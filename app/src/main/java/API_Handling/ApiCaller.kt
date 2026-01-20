package API_Handling

/**
 * We are using <a href = "https://www.googleapis.com/books/v1/volumes?q=isbn:">Google Rest Api as ressource</a>
 */
sealed class BookQuery {
    data class Isbn(val value: String) : BookQuery()
    data class Author(val name: String) : BookQuery()
    data class Title(val title: String) : BookQuery()
}

class ApiCaller(
    private val query: BookQuery
) {
    fun buildQueryString(): String {
        return when (query) {
            is BookQuery.Isbn -> "isbn:${query.value}"
            is BookQuery.Author -> "inauthor:${query.name}"
            is BookQuery.Title -> "intitle:${query.title}"
        }
    }
}

fun parseUserInput(input: String): BookQuery {
    val parts = input.split(":", limit = 2)
    require(parts.size == 2) { "Invalid input format" }

    val key = parts[0].trim().lowercase()
    val value = parts[1].trim()

    return when (key) {
        "isbn" -> BookQuery.Isbn(value)
        "author" -> BookQuery.Author(value)
        "book", "title" -> BookQuery.Title(value)
        else -> throw IllegalArgumentException("Unknown search type")
    }
}