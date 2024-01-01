package com.example.exifreader.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.exifreader.MainActivityViewModel

@Composable
fun MainMenu(navController: NavController, viewModel: MainActivityViewModel, modifier: Modifier = Modifier) {
    val pickMedia = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(), onResult = { image_uri ->
        viewModel.imageUrl = image_uri!!
        navController.navigate("ImageInfo")
    })
    ElevatedButton(onClick = { pickMedia.launch("image/*") }) {
        Text(
            text = "Choose image!",
            modifier = modifier
        )
    }
}