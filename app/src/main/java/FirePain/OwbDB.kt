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

    fun deleteBook(book: Book, onSuccess: () -> Unit = {}) {
        // Find and delete by ISBN (unique identifier)
        booksRef.orderByChild("isbn").equalTo(book.isbn).get()
            .addOnSuccessListener { snapshot ->
                for (childSnapshot in snapshot.children) {
                    childSnapshot.ref.removeValue()
                        .addOnSuccessListener {
                            Log.d("RealtimeDB", "Book deleted: ${book.title}")
                            onSuccess()
                        }
                }
            }
    }
}