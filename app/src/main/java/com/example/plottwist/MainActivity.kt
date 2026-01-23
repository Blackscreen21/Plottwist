package com.example.plottwist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import API_Handling.*
import UserView.Nav
import coil.compose.AsyncImage
import com.example.plottwist.ui.theme.*
import firepain.FireBaseDBinstance
import UserView.UserBookList
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlottwistTheme {
                Nav()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onNavigateToMyBooks: () -> Unit) {
    var userInput by remember { mutableStateOf("") }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var bookAvailability by remember { mutableStateOf<Map<Book, Boolean>>(emptyMap()) }
    var searchResult by remember { mutableStateOf("") }
    var shouldSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isCheckingAvailability by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val localStorage = remember { UserBookList(context) }
    val db = remember { FireBaseDBinstance() }

    // Check book availability when books list changes
    LaunchedEffect(books) {
        if (books.isNotEmpty()) {
            isCheckingAvailability = true
            db.checkBooksExist(books) { availabilityMap ->
                bookAvailability = availabilityMap
                isCheckingAvailability = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepNavy, MidNavy, DeepNavy)
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top decorative border
                FiligreeBorder(color = RichGold)
                Spacer(modifier = Modifier.height(8.dp))

                // Title with navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SideDiamond(color = RichGold)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "PLOTTWIST",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 4.sp,
                                color = RichGold
                            )
                        )
                        Text(
                            text = "DISCOVER & EXCHANGE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                color = CreamWhite.copy(alpha = 0.7f)
                            )
                        )
                    }

                    IconButton(
                        onClick = onNavigateToMyBooks,
                        modifier = Modifier
                            .size(40.dp)
                            .border(2.dp, RichGold, RoundedCornerShape(8.dp))
                            .background(MidNavy, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.LibraryBooks,
                            contentDescription = "My Books",
                            tint = RichGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search input field
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(10.dp))
                        .border(
                            width = 2.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(DarkGold, RichGold, BrightGold, RichGold, DarkGold)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(MidNavy, RoundedCornerShape(10.dp))
                        .padding(2.dp)
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        label = {
                            Text(
                                "ISBN / Author / Title",
                                color = CreamWhite.copy(0.7f),
                                fontSize = 12.sp
                            )
                        },
                        placeholder = {
                            Text(
                                "Search for books...",
                                color = CreamWhite.copy(0.5f),
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Book,
                                contentDescription = "Book",
                                tint = RichGold,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                if (books.isNotEmpty()) {
                                    userInput = ""
                                    books = emptyList()
                                    bookAvailability = emptyMap()
                                    searchResult = ""
                                } else {
                                    searchQuery = userInput
                                    shouldSearch = true
                                }
                            }) {
                                Icon(
                                    if (books.isNotEmpty()) Icons.Rounded.Close else Icons.Rounded.Search,
                                    contentDescription = if (books.isNotEmpty()) "Clear" else "Search",
                                    tint = RichGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CreamWhite,
                            unfocusedTextColor = CreamWhite,
                            cursorColor = RichGold,
                            focusedBorderColor = RichGold,
                            unfocusedBorderColor = DarkGold
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                }

                // Search logic
                if (shouldSearch) {
                    LaunchedEffect(Unit) {
                        try {
                            val query = parseUserInput(searchQuery)
                            val apiCaller = ApiCaller(query)
                            searchResult = "Searching ancient tomes..."
                            val result = apiCaller.fetchBook()
                            books = parseGoogleBooksResponse(result)
                            searchResult = if (books.isNotEmpty()) "Found ${books.size} volumes" else "No books found"
                        } catch (e: Exception) {
                            searchResult = "Error: ${e.message}"
                        }
                        shouldSearch = false
                    }
                }

                // Search result message
                if (searchResult.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = searchResult,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = if (searchResult.startsWith("Error")) Color(0xFFCF6679)
                            else CreamWhite.copy(0.8f)
                        )
                    )
                }

                // Availability checking indicator
                if (isCheckingAvailability) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = RichGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Checking availability...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CreamWhite.copy(0.7f),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Divider before results
                if (books.isNotEmpty()) {
                    OrnateDivider(color = RichGold)
                }
            }

            // Book results list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(books) { book ->
                    val isAvailable = bookAvailability[book] ?: false
                    StyledBookCard(
                        book = book,
                        isAvailable = isAvailable,
                        onAddToList = {
                            localStorage.addBook(book)
                            searchResult = "'${book.title}' added to your list!"
                        },
                        onTrade = {
                            if (isAvailable) {
                                searchResult = "Trade request sent for '${book.title}'!"
                                db.deleteBookByTitle(book.title)
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OrnateDivider(color = RichGold)
                Spacer(modifier = Modifier.height(4.dp))
                ButtonDots(color = RichGold, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(DarkGold, RichGold, DarkGold)
                            )
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "© MMXXVI · PLOTTWIST",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Medium,
                            color = DeepNavy,
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StyledBookCard(
    book: Book,
    isAvailable: Boolean,
    onAddToList: () -> Unit,
    onTrade: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(6.dp))
            .border(2.dp, RichGold, RoundedCornerShape(6.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(MidNavy, DeepNavy.copy(alpha = 0.8f), MidNavy)
                ),
                RoundedCornerShape(6.dp)
            )
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Book cover image
            Box(
                modifier = Modifier
                    .size(100.dp, 140.dp)
                    .border(2.dp, RichGold, RoundedCornerShape(4.dp))
                    .background(DeepNavy, RoundedCornerShape(4.dp))
            ) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = "Book cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Book information and actions
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrightGold,
                            fontSize = 14.sp
                        ),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "by ${book.author}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = CreamWhite.copy(0.7f),
                            fontSize = 12.sp
                        ),
                        maxLines = 1
                    )
                    if (book.isbn.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ISBN: ${book.isbn}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CreamWhite.copy(0.5f),
                                fontSize = 10.sp
                            )
                        )
                    }

                    // Availability indicator
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAvailable) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                            contentDescription = if (isAvailable) "Available" else "Not available",
                            tint = if (isAvailable) Color(0xFF42F647) else CreamWhite.copy(0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAvailable) "Available" else "Not in database",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isAvailable) Color(0xFF42F647) else CreamWhite.copy(0.5f),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Action buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onAddToList,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkGold,
                            contentColor = DeepNavy
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Meine Liste",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onTrade,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAvailable) RichGold else CreamWhite.copy(0.3f),
                            contentColor = DeepNavy,
                            disabledContainerColor = CreamWhite.copy(0.2f),
                            disabledContentColor = CreamWhite.copy(0.5f)
                        ),
                        enabled = isAvailable,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isAvailable) "Tauschen" else "Nicht verfügbar",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}