package com.example.plottwist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import UserView.UserBookList
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.plottwist.ui.theme.*
import firepain.FireBaseDBinstance

@Composable
fun MyBooksScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val localStorage = remember { UserBookList(context) }
    val db = remember { FireBaseDBinstance() }

    var userBooks by remember { mutableStateOf(localStorage.getUserBooks()) }
    var statusMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy)
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
                    tint = RichGold
                )
            }
            Text(
                text = "My Books (${userBooks.size})",
                style = MaterialTheme.typography.headlineSmall,
                color = CreamWhite
            )
            Spacer(modifier = Modifier.width(48.dp)) // Balance the layout
        }

        AnimatedVisibility(
            visible = statusMessage.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = statusMessage,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                color = if (statusMessage.contains("Error") || statusMessage.contains("failed"))
                    Color.Red else RichGold,
                style = MaterialTheme.typography.bodyMedium
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
                    Card (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MidNavy),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            // Gradient accent bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(
                                        Brush.horizontalGradient(listOf(RichGold, DarkGold))
                                    )
                            )

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
                                        style = MaterialTheme.typography.titleMedium,
                                        color = CreamWhite
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = book.author,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = CreamWhite.copy(alpha = 0.7f)
                                    )
                                    if (book.isbn.isNotEmpty()) {
                                        Text(
                                            text = "ISBN: ${book.isbn}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CreamWhite.copy(alpha = 0.6f)
                                        )
                                    }
                                }

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
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
                                            tint = DarkGold
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
                                            tint = Color(0xFFF55555)
                                        )
                                    }
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGold)
            ) {
                Text(text = "Upload All Books to Central List")
            }
        }

        Row(
            modifier = Modifier
                .background(DeepNavy)
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "© 2026 Plottwist App",
                modifier = Modifier.padding(bottom = 16.dp),
                color = DarkGold.copy(alpha = 0.8f)
            )
        }
    }
}