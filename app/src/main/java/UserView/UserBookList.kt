package UserView

import android.content.Context
import API_Handling.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserBookList(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_books", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_USER_BOOKS = "user_books_list"
    }

    // Add a book to user's local list
    fun addBook(book: Book) {
        val books = getUserBooks().toMutableList()
        // Check if book already exists (by ISBN or title)
        if (books.none { it.isbn == book.isbn || (it.isbn.isEmpty() && it.title == book.title) }) {
            books.add(book)
            saveBooks(books)
        }
    }

    // Remove a book from user's local list
    fun removeBook(book: Book) {
        val books = getUserBooks().toMutableList()
        books.removeAll { it.isbn == book.isbn || (it.isbn.isEmpty() && it.title == book.title) }
        saveBooks(books)
    }

    // Get all user's books
    fun getUserBooks(): List<Book> {
        val json = sharedPreferences.getString(KEY_USER_BOOKS, null) ?: return emptyList()
        val type = object : TypeToken<List<Book>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Clear all books
    fun clearAllBooks() {
        sharedPreferences.edit().remove(KEY_USER_BOOKS).apply()
    }

    // Check if a book is in user's list
    fun hasBook(book: Book): Boolean {
        return getUserBooks().any {
            it.isbn == book.isbn || (it.isbn.isEmpty() && it.title == book.title)
        }
    }

    private fun saveBooks(books: List<Book>) {
        val json = gson.toJson(books)
        sharedPreferences.edit().putString(KEY_USER_BOOKS, json).apply()
    }
}