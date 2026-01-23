package com.example.plottwist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import API_Handling.*
import coil.compose.AsyncImage
import com.example.plottwist.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlottwistTheme {
                HomeScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // State preserved on rotation
    var userInput by rememberSaveable { mutableStateOf("") }
    var searchResult by rememberSaveable { mutableStateOf("") }
    var shouldSearch by remember { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    
    // Re-trigger search after rotation
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty() && books.isEmpty() && !shouldSearch) {
            shouldSearch = true
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
            // Scrollable content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Top decorative border with dots
                FiligreeBorder(color = RichGold)
                Spacer(modifier = Modifier.height(8.dp))

                // Title with side decorations
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SideDiamond(color = RichGold)
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "PLOT TWIST",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 4.sp,
                                color = RichGold
                            )
                        )
                        Text(
                            text = "BOOK EXCHANGE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                color = CreamWhite.copy(alpha = 0.7f)
                            )
                        )
                    }

                    SideDiamond(color = RichGold)
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
                        label = { Text("ISBN / Author / Title", color = CreamWhite.copy(0.7f), fontSize = 12.sp) },
                        placeholder = { Text("Search for books...", color = CreamWhite.copy(0.5f), fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Rounded.Book, contentDescription = "Book", tint = RichGold, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                if (books.isNotEmpty()) {
                                    userInput = ""
                                    books = emptyList()
                                    searchResult = ""
                                    searchQuery = ""
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

                // API call logic
                if (shouldSearch) {
                    LaunchedEffect(searchQuery) {
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
                            color = if (searchResult.startsWith("Error")) 
                                Color(0xFFCF6679) else CreamWhite.copy(0.8f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Divider before results
                if (books.isNotEmpty()) {
                    OrnateDivider(color = RichGold)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Book results list
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    books.forEach { book ->
                        BookCard(book = book)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Fixed footer section
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
fun BookCard(book: Book) {
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
                    .height(140.dp),
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
                }

                // Action buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { 
                            /*ToDo: Hier dann womöglich UserDatabase mit Büchern die man hochgeladen hat um aus
                            * denen auszuwählen gegen was man tauschen will
                            * Das wäre dann auch FireStore -> UserDB
                            */
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkGold,
                            contentColor = DeepNavy
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Bücher tauschen", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { 
                            //ToDo: Wenn UserDB dann hier in UserDB und in die generelle verfügbarkeitsDB einfügen
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RichGold,
                            contentColor = DeepNavy
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Bücher anbieten", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
