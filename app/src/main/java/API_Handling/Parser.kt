package API_Handling

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

fun parseGoogleBooksResponse(json: String): List<Book> {
    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    val adapter = moshi.adapter(GoogleBooksResponse::class.java)
    val response = adapter.fromJson(json)

    return response?.items?.map { item ->
        Book(
            title = item.volumeInfo.title,
            author = item.volumeInfo.authors?.firstOrNull() ?: "Unknown",
            isbn = item.volumeInfo.industryIdentifiers?.find { it.type == "ISBN_13" }?.identifier ?: "",
            coverUrl = item.volumeInfo.imageLinks?.thumbnail
        )
    } ?: emptyList()
}