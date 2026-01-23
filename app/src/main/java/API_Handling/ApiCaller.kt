package API_Handling
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


/**
 * We are using <a href = "https://www.googleapis.com/books/v1/volumes?q=isbn:">Google Rest Api as ressource</a>
 */
sealed class BookQuery {
    data class Isbn(val value: String) : BookQuery()
    data class Author(val name: String) : BookQuery()
    data class Title(val title: String) : BookQuery()
}


class ApiCaller(private val query: BookQuery) {
    private val client = OkHttpClient()

    fun buildQueryString(): String = when(query) {
        is BookQuery.Isbn -> "isbn:${query.value}"
        is BookQuery.Author -> "inauthor:${query.name}"
        is BookQuery.Title -> "intitle:${query.title}"
    }

    suspend fun fetchBook(): String = withContext(Dispatchers.IO) {
        val url = "https://www.googleapis.com/books/v1/volumes?q=${buildQueryString()}"
        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                return@withContext response.body?.string() ?: ""
            }
        } catch (e: Exception) {
            return@withContext "Error: ${e.message}"

        }
    }
    /* ToDo: A method that takes the output of fetchBook and prettifies it.
        AKA we only want the results of the json that interest us
        Goodreads has roundabout a similar layout as i am trying to achieve for sarch
        Picute: Title, Author,
    * */
}
fun parseUserInput(input: String): BookQuery {
    return when {
        input.contains(":") -> {
            val parts = input.split(":", limit = 2)

    //    require(parts.size == 2) { "Invalid input format" }

            val key = parts[0].trim().lowercase()
            val value = parts[1].trim()

            when (key) {
                "isbn" -> BookQuery.Isbn(value)
                "author" -> BookQuery.Author(value)
                "book", "title" -> BookQuery.Title(value)
                else -> throw IllegalArgumentException("Unknown search type")
            }
        }
        else -> BookQuery.Title(input)
    }
}