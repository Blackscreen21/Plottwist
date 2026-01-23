package com.example.plottwist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import API_Handling.ApiCaller
import API_Handling.Book
import API_Handling.parseGoogleBooksResponse
import API_Handling.parseUserInput
import UserView.Nav
import UserView.UserBookList
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.ui.platform.LocalContext
import firepain.OwbDB

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Nav()
        }
    }
}

@Composable
fun SearchScreen(onNavigateToMyBooks: () -> Unit) {
    var userInput by remember { mutableStateOf("") }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var searchResult by remember { mutableStateOf("Results will appear here...") }
    var shouldSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current
    val localStorage = remember { UserBookList(context) }
    val db = remember { OwbDB() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Search Books", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
            IconButton(onClick = onNavigateToMyBooks) {
                Icon(
                    Icons.Rounded.LibraryBooks,
                    contentDescription = "My Books",
                    tint = Color(0xFF42F647)
                )
            }
        }

        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("ISBN / Author / Title") },
            placeholder = { Text("Enter search query") },
            leadingIcon = { Icon(Icons.Rounded.Book, contentDescription = "Book Icon") },
            trailingIcon = {
                IconButton(onClick = {
                    if (books.isNotEmpty()) {
                        userInput = ""
                        books = emptyList()
                        searchResult = "Results will appear here..."
                    } else {
                        searchQuery = userInput
                        shouldSearch = true
                    }
                }) {
                    val icon = if (books.isNotEmpty()) Icons.Rounded.Close else Icons.Rounded.Search
                    val description = if (books.isNotEmpty()) "Clear" else "Search"
                    Icon(icon, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        if (shouldSearch) {
            LaunchedEffect(Unit) {
                try {
                    val query = parseUserInput(searchQuery)
                    val apiCaller = ApiCaller(query)

                    searchResult = "Searching..."
                    val result = apiCaller.fetchBook()
                    books = parseGoogleBooksResponse(result)
                    searchResult = "Found ${books.size} books"
                } catch (e: Exception) {
                    searchResult = "Error: ${e.message}"
                }
                shouldSearch = false
            }
        }

        if (searchResult.isNotEmpty()) {
            Text(
                text = searchResult,
                modifier = Modifier.padding(16.dp),
                color = if (searchResult.startsWith("Error")) Color.Red else Color.Gray
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        ) {
            items(books) { book ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = book.title)
                        Text(text = book.author, color = Color.Gray)
                        if (book.isbn.isNotEmpty()) {
                            Text(text = "ISBN: ${book.isbn}", color = Color.Gray, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                        }
                    }

                    Column {
                        Button(
                            onClick = {
                                localStorage.addBook(book)
                                searchResult = "'${book.title}' added to your list!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42F647)),
                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                        ) {
                            Text(text = "Meine Liste")
                        }

                        Button(
                            onClick = {
                                db.deleteBookByTitle(book.title) {
                                    searchResult = "Owner of ${book.title} will be messaged. If they are interested in any of the books in your List they will let you know."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(text = "Will ich haben")
                        }
                    }
                }
                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .background(Color(0xFFF8AACD))
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "© 2026 Plottwist App",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}