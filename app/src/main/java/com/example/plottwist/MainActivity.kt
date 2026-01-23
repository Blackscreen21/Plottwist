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
import API_Handling.BookQuery
import API_Handling.ApiCaller
import API_Handling.Book
import API_Handling.parseGoogleBooksResponse
import API_Handling.parseUserInput
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import firepain.OwbDB

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    var userInput by remember { mutableStateOf("") }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var searchResult by remember { mutableStateOf("Results will appear here...") }
    var shouldSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Initialize database
    val db = remember { OwbDB() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

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

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(books) { book ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${book.title} - ${book.author}",
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            // Add book to your "want to trade" list
                            db.addBook(
                                book = book,
                                onSuccess = {
                                    searchResult = "Book added to trade list!"
                                },
                                onFailure = { e ->
                                    searchResult = "Failed to add book: ${e.message}"
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC8E6C9)),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = "Hochladen")
                    }
                }
                HorizontalDivider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Spacer(modifier = Modifier.weight(1f))

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