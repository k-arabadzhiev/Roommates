package org.kagami.roommate.chat.presentation.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.io.InputStream

@Composable
fun PickImages(
    modifier: Modifier,
    onPhotosSelect: (List<ByteArray>) -> Unit
) {
    val contentResolver = LocalContext.current.contentResolver
    var imageUri by remember { mutableStateOf<List<Uri?>>(listOf()) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            imageUri = uris
            if (uris.isNotEmpty()) {
                val byteArray = uris.map { uri ->
                    val inputStream = contentResolver.openInputStream(uri)!!
                    val byteArray = inputStream.readBytes()
                    inputStream.close()
                    byteArray
                }
                onPhotosSelect(byteArray)
            }
        }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.padding(vertical = 8.dp),
            onClick = {
                launcher.launch("image/*")
            }
        ) {
            Text(text = "Load Images")
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 96.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            userScrollEnabled = false
        ) {
            items(imageUri) { uri ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .build(),
                    contentDescription = "Image to upload"
                )
            }
        }
    }
}