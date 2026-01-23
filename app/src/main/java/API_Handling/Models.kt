package API_Handling


data class GoogleBooksResponse(
    val items: List<BookItem>?
)

data class BookItem(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val industryIdentifiers: List<Identifier>?,
    val imageLinks: ImageLinks?
)

data class Identifier(
    val type: String,
    val identifier: String
)

data class ImageLinks(
    val thumbnail: String?
)

data class Book(
    val title: String,
    val author: String,
    val isbn: String,
    val coverUrl: String?
)