package com.example.testjsonapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter

// Entry point of the application
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channel before setting content
        createNotificationChannel()

        // Set the content of the activity with Compose UI
        setContent {
            MyScreen(context = this)
        }
    }

    // Function to create notification channel
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "my_channel_id"
        val channelName = "My Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(channel)
    }
}

// Function to send a notification
@RequiresApi(Build.VERSION_CODES.O)
private fun sendNotification(context: Context) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channelId = "my_channel_id"
    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("My Notification")
        .setContentText("Hello! This is my notification.")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .build()

    notificationManager.notify(1, notification)
}


// Data class to represent form data
data class FormData(val name: String, val email: String)

// Form composable that collects user input
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyScreen(context: Context) {
    var formData by remember { mutableStateOf(FormData("", "")) }
    var entries by remember { mutableStateOf(emptyMap<String, Entry>()) }
    var showJsonData by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxHeight(1f)
    ) {
        //Textfield for Name
        OutlinedTextField(
            value = formData.name,
            onValueChange = { formData = formData.copy(name = it) },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        //Textfield for Email
        OutlinedTextField(
            value = formData.email,
            onValueChange = { formData = formData.copy(email = it) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Row {
            // Button to submit the form and save data to a file
            Button(
                onClick = {
                    //To store the formdata in the entries
                    entries = entries + (System.currentTimeMillis().toString() to Entry(
                        formData.email,
                        formData.name
                    ))
                    // Save form data as JSON to a file
                    saveJsonToFile(context, formData)

                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Submit")
            }
            //Button to Switch to DataFromJson Activity
            Button(
                onClick = {
                    showJsonData = !showJsonData
                },
                modifier = Modifier.padding(start = 8.dp, top = 16.dp)
            ) {
                Text("Load JSON Data")
            }
        }

        if (showJsonData) {
            DisplayJsonEntries(entries = entries)
        }
    }
}

// New composable to display JSON entries
@Composable
fun DisplayJsonEntries(entries: Map<String, Entry>) {
    LazyColumn {
        items(entries.entries.toList()) { (_, entry) ->
            Column {
                Text("Email: ${entry.email}")
                Text("Name: ${entry.name}")
                Spacer(modifier = Modifier.height(16.dp)) // Add spacing between entries
            }
        }
    }
}


// Function to save form data as JSON to a file
@RequiresApi(Build.VERSION_CODES.O)
fun saveJsonToFile(context: Context, formData: FormData) {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val json = gson.toJson(formData)

    try {
        val fileName = "form_data.json"
        val file = File(context.getExternalFilesDir(null), fileName)
        val fileWriter = FileWriter(file)
        fileWriter.write(json)
        fileWriter.flush()
        fileWriter.close()
        Log.d("SaveJsonToFile", "JSON saved to file: $file")
        val file2 = File(context.getExternalFilesDir(null), fileName)
        Log.d("File Path", file2.absolutePath)
    } catch (e: Exception) {
        // Handle any exceptions during file saving
        Log.e("SaveJsonToFile", "Error saving JSON to file: ${e.message}")
    }
}
