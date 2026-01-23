package com.example.plottwist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import API_Handling.Book
import UserView.UserBookList
import firepain.OwbDB

@Composable
fun MyBooksScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val localStorage = remember { UserBookList(context) }
    val db = remember { OwbDB() }

    var userBooks by remember { mutableStateOf(localStorage.getUserBooks()) }
    var statusMessage by remember { mutableStateOf("") }

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
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF42F647)
                )
            }
            Text(
                text = "My Books (${userBooks.size})",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.width(48.dp)) // Balance the layout
        }

        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                modifier = Modifier.padding(16.dp),
                color = if (statusMessage.contains("Error") || statusMessage.contains("failed"))
                    Color.Red else Color(0xFF42F647)
            )
        }

        if (userBooks.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No books in your list yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Search for books and add them to your list!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                items(userBooks) { book ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = book.author,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                if (book.isbn.isNotEmpty()) {
                                    Text(
                                        text = "ISBN: ${book.isbn}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Column {
                                IconButton(
                                    onClick = {
                                        db.addBook(
                                            book = book,
                                            onSuccess = {
                                                statusMessage = "'${book.title}' uploaded to central list!"
                                            },
                                            onFailure = { e ->
                                                statusMessage = "Upload failed: ${e.message}"
                                            }
                                        )
                                    }
                                ) {
                                    Icon(
                                        Icons.Rounded.Upload,
                                        contentDescription = "Upload to central list",
                                        tint = Color(0xFF4A90E2)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        localStorage.removeBook(book)
                                        userBooks = localStorage.getUserBooks()
                                        statusMessage = "'${book.title}' removed from your list"
                                    }
                                ) {
                                    Icon(
                                        Icons.Rounded.Delete,
                                        contentDescription = "Remove from list",
                                        tint = Color(0xFFFF6B6B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bulk upload button
            Button(
                onClick = {
                    var successCount = 0
                    var failCount = 0
                    val totalBooks = userBooks.size

                    userBooks.forEach { book ->
                        db.addBook(
                            book = book,
                            onSuccess = {
                                successCount++
                                if (successCount + failCount == totalBooks) {
                                    statusMessage = "Uploaded $successCount books. Failed: $failCount"
                                }
                            },
                            onFailure = {
                                failCount++
                                if (successCount + failCount == totalBooks) {
                                    statusMessage = "Uploaded $successCount books. Failed: $failCount"
                                }
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text(text = "Upload All Books to Central List")
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