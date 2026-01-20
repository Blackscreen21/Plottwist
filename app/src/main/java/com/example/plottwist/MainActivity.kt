package com.example.plottwist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import API_Handling.parseUserInput
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.foundation.layout.Arrangement.Center

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
    var searchHandler by remember { mutableStateOf(ApiCaller(BookQuery.Title(""))) }

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
            label = { Text("ISBN") },
            placeholder = { Text("Search for the ISBN/Author/Title") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Book,
                    contentDescription = "Book icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    // parse the input and update the handler
                    val query = parseUserInput(userInput)
                    searchHandler = ApiCaller(query)
                    // trigger API call here
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )
        Spacer(modifier = Modifier.weight(1f))

        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .background(Color(0xFFF8AACD))
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Center
        ) {
            Text(
                text = "© 2026 Plottwist App",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }

}