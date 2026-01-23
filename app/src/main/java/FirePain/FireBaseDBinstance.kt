package firepain

import com.google.firebase.database.*
import android.util.Log
import API_Handling.Book

class FireBaseDBinstance {

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

    // Check if a book exists in the database
    fun bookExists(book: Book, onResult: (Boolean) -> Unit) {
        booksRef.get()
            .addOnSuccessListener { snapshot ->
                var found = false
                for (childSnapshot in snapshot.children) {
                    val dbTitle = childSnapshot.child("title").value as? String
                    val dbIsbn = childSnapshot.child("isbn").value as? String

                    // Match by ISBN if available, otherwise by title
                    if (book.isbn.isNotEmpty() && dbIsbn == book.isbn) {
                        found = true
                        break
                    } else if (book.isbn.isEmpty() && dbTitle == book.title) {
                        found = true
                        break
                    }
                }
                onResult(found)
            }
            .addOnFailureListener { e ->
                Log.e("RealtimeDB", "Failed to check book existence", e)
                onResult(false)
            }
    }

    // Check multiple books at once (more efficient)
    fun checkBooksExist(books: List<Book>, onResult: (Map<Book, Boolean>) -> Unit) {
        booksRef.get()
            .addOnSuccessListener { snapshot ->
                val resultMap = mutableMapOf<Book, Boolean>()
                val dbBooks = mutableListOf<Pair<String, String>>() // title, isbn pairs

                // Collect all database books
                for (childSnapshot in snapshot.children) {
                    val title = childSnapshot.child("title").value as? String ?: ""
                    val isbn = childSnapshot.child("isbn").value as? String ?: ""
                    dbBooks.add(Pair(title, isbn))
                }

                // Check each book
                books.forEach { book ->
                    val exists = dbBooks.any { (dbTitle, dbIsbn) ->
                        if (book.isbn.isNotEmpty()) {
                            dbIsbn == book.isbn
                        } else {
                            dbTitle == book.title
                        }
                    }
                    resultMap[book] = exists
                }

                onResult(resultMap)
            }
            .addOnFailureListener { e ->
                Log.e("RealtimeDB", "Failed to check books existence", e)
                // Return all as false on error
                onResult(books.associateWith { false })
            }
    }

    // Delete book by title
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