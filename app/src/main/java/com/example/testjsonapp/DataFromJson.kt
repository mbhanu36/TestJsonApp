package com.example.testjsonapp

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class DataFromJson : ComponentActivity() {
    @RequiresApi(26)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoadJsonScreen()
        }
    }
}

@RequiresApi(26)
@Composable
fun LoadJsonScreen() {
    var filePath by remember { mutableStateOf("/storage/emulated/0/Android/data/com.example.testjsonapp/files/form_data.json") }
    var entries by remember { mutableStateOf(emptyList<Entry>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = filePath,
            onValueChange = { filePath = it },
            label = { Text("Enter JSON File Path") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                entries = loadJsonFromFile(filePath)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Load JSON Data")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            items(entries) { entry ->
                Column {
                    Text("Email: ${entry.email}")
                    Text("Name: ${entry.name}")
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


data class Entry(val email: String, val name: String)

fun loadJsonFromFile(filePath: String): List<Entry> {
    try {
        val jsonFile = File(filePath)
        val jsonString = jsonFile.readText()

        val gson = Gson()
        return gson.fromJson(jsonString, object : TypeToken<List<Entry>>() {}.type)
    } catch (e: Exception) {
        // Handle any exceptions during file reading
        return emptyList()
    }
}
