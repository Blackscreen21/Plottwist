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
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import firepain.FireBaseDBinstance

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
    var bookAvailability by remember { mutableStateOf<Map<Book, Boolean>>(emptyMap()) }
    var searchResult by remember { mutableStateOf("Results will appear here...") }
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
            Text(text = "Search Books", style = MaterialTheme.typography.headlineSmall)
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
                        bookAvailability = emptyMap()
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

        if (isCheckingAvailability) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Checking availability...", color = Color.Gray)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        ) {
            items(books) { book ->
                val isAvailable = bookAvailability[book] ?: false

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
                            Text(
                                text = "ISBN: ${book.isbn}",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Availability indicator
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isAvailable) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                                contentDescription = if (isAvailable) "Available" else "Not available",
                                tint = if (isAvailable) Color(0xFF42F647) else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAvailable) "Available for trade" else "Not in database",
                                color = if (isAvailable) Color(0xFF42F647) else Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
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
                                if (isAvailable) {
                                    // Show trade request dialog or perform trade action
                                    searchResult = "Trade request sent for '${book.title}'!"
                                    db.deleteBookByTitle(book.title)
                                    //ToDo: A list and page of books the user has selected to trade (List of books he wants)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAvailable) Color(0xFF4A90E2) else Color.Gray,
                                disabledContainerColor = Color.Gray
                            ),
                            enabled = isAvailable,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = if (isAvailable) "Tauschen" else "Nicht verfügbar",
                                color = if (isAvailable) Color.White else Color.DarkGray
                            )
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