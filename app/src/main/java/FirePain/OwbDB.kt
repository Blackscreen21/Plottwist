package firepain

import com.google.firebase.database.*
import android.util.Log
import API_Handling.Book

class OwbDB {

    private val database = FirebaseDatabase.getInstance()
    private val booksRef = database.getReference("books")

    // Add a book from your Book data class
    fun addBook(book: Book, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        val bookId = booksRef.push().key
        val bookData = mapOf(
            "title" to book.title,
            "author" to book.author,
            "isbn" to book.isbn,
            "coverUrl" to book.coverUrl,
            "timestamp" to System.currentTimeMillis()
        )

        bookId?.let {
            booksRef.child(it).setValue(bookData)
                .addOnSuccessListener {
                    Log.d("RealtimeDB", "Book added: ${book.title}")
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e("RealtimeDB", "Failed to add book", e)
                    onFailure(e)
                }
        }
    }

    // Get all books with a callback
    fun getAllBooks(onResult: (List<Book>) -> Unit) {
        booksRef.get().addOnSuccessListener { snapshot ->
            val booksList = mutableListOf<Book>()
            for (childSnapshot in snapshot.children) {
                val title = childSnapshot.child("title").getValue(String::class.java) ?: ""
                val author = childSnapshot.child("author").getValue(String::class.java) ?: ""
                val isbn = childSnapshot.child("isbn").getValue(String::class.java) ?: ""
                val coverUrl = childSnapshot.child("coverUrl").getValue(String::class.java) ?: ""
                booksList.add(Book(title, author, isbn, coverUrl))
            }
            onResult(booksList)
        }.addOnFailureListener { e ->
            Log.e("RealtimeDB", "Failed to read books", e)
            onResult(emptyList())
        }
    }

    // Listen to real-time updates
    fun listenToBooks(onUpdate: (List<Book>) -> Unit) {
        booksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val booksList = mutableListOf<Book>()
                for (childSnapshot in snapshot.children) {
                    val title = childSnapshot.child("title").getValue(String::class.java) ?: ""
                    val author = childSnapshot.child("author").getValue(String::class.java) ?: ""
                    val isbn = childSnapshot.child("isbn").getValue(String::class.java) ?: ""
                    val coverUrl = childSnapshot.child("coverUrl").getValue(String::class.java) ?: ""
                    booksList.add(Book(title, author, isbn, coverUrl))
                }
                onUpdate(booksList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("RealtimeDB", "Failed to read books", error.toException())
            }
        })
    }

    // Option 1: Get all books and filter in-memory (only good for small datasets)
    fun deleteBookByTitle(title: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        booksRef.get()
            .addOnSuccessListener { snapshot ->
                val tasks = mutableListOf<com.google.android.gms.tasks.Task<Void>>()

                for (childSnapshot in snapshot.children) {
                    val bookTitle = childSnapshot.child("title").value as? String
                    if (bookTitle == title) {
                        tasks.add(childSnapshot.ref.removeValue())
                    }
                }

                if (tasks.isEmpty()) {
                    Log.w("RealtimeDB", "No book found with title: $title")
                    onFailure(Exception("No book found with title: $title"))
                } else {
                    com.google.android.gms.tasks.Tasks.whenAll(tasks)
                        .addOnSuccessListener {
                            Log.d("RealtimeDB", "Book(s) deleted: $title")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("RealtimeDB", "Failed to delete book(s): $title", e)
                            onFailure(e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("RealtimeDB", "Failed to query books", e)
                onFailure(e)
            }
    }
}