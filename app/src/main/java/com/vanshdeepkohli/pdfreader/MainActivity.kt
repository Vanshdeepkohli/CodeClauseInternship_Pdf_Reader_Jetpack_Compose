package com.vanshdeepkohli.pdfreader

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vanshdeepkohli.pdfreader.ui.theme.PDFReaderTheme
import java.io.File

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PDFReaderTheme {
                val context = LocalContext.current
                val pdfPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument(),
                    onResult = { uri ->
                        if (uri != null) {
                            val fileName = uri.lastPathSegment
                            val recentFiles = loadRecentFiles(context)
                            val newRecentFiles = recentFiles.toMutableList()
                            newRecentFiles.add(
                                0,
                                Pair(fileName, uri.toString())
                            )
//                            recentFiles = newRecentFiles.take(5).toList()

                            saveRecentFile(context, fileName, uri.toString())

                            println("!!!!! $uri")

                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(
                                Uri.parse(uri.toString()),
                                "application/pdf"
                            )
                            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            startActivity(intent)
                        }
                    }
                )
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                colors = topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                title = {
                                    Text("PDF Reader")
                                }
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(onClick = {
                                pdfPickerLauncher.launch(arrayOf("application/pdf"))
                            }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                )
                            }
                        }
                    ) {
                        Column(modifier = Modifier.padding(it)) {
                            Recent()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PDFReaderTheme {
        Recent()
    }
}

@Composable
fun Recent() {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize()
    ) {
//        RecentFilesList(recentFiles = listOf("Hello", "World", "Vansh"), onFileClicked = {})
        val context = LocalContext.current
        val recentFiles by remember {
            mutableStateOf(loadRecentFiles(context))
        }

        println("!!! recent files == $recentFiles")
        RecentFilesList(context = context, recentFiles = recentFiles)
    }
}

@Composable
fun RecentFilesList(
    context: Context,
    recentFiles: List<Pair<String?, String>>
) {
    if (recentFiles.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No recent files found.",
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column {
            Text("Recent Files:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(3.dp))
            LazyColumn {
//                println("!! $recentFiles")
                items(recentFiles.size) { index ->
                    RecentFileItem(context = context,file = recentFiles[index])
//                    RecentFileItem(recentFiles[index]) { clickedFile ->
//                        println("!! clicked File == ${clickedFile.second}")
////                        val uri = Uri.fromFile(File(clickedFile.second))
////                        val intent = Intent(Intent.ACTION_VIEW)
////                        intent.setDataAndType(uri, "application/pdf")
////                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
////                        context.startActivity(intent)
//
////                        val intent = Intent(Intent.ACTION_VIEW)
////                        intent.setDataAndType(
////                            Uri.parse(clickedFile.second),
////                            "application/pdf"
////                        )
////                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
////
////                        context.startActivity(intent)
//                    }
                }
            }
        }
    }
}

@Composable
fun RecentFileItem(
    context: Context,
    file: Pair<String?, String>,
//    onFileClicked: (Pair<String?, String>) -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(Color.LightGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 8.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_picture_as_pdf_24),
                contentDescription = "PDF file",
                tint = Color.White,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                file.first ?: "File name unknown",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        val uri =
                            Uri.parse(file.second) // Use Uri.parse for direct string conversion
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(uri, "application/pdf")
                        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        context.startActivity(intent)
                    }
            )
        }
    }
}

private fun saveRecentFile(context: Context, filename: String? = null, filePath: String) {
    val sharedPref = context.getSharedPreferences("recent_files", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("recent_files", "$filename,$filePath")
        apply()
    }
}


private fun loadRecentFiles(context: Context): List<Pair<String?, String>> {
    val sharedPref = context.getSharedPreferences("recent_files", Context.MODE_PRIVATE)
    val savedFiles = sharedPref.getString("recent_files", "")?.split(",") ?: emptyList()

    return savedFiles.mapIndexed { index, filePath ->
        val filename = filePath.substringAfterLast("/") // Extract filename
        Pair(filename, filePath)
    }
}

