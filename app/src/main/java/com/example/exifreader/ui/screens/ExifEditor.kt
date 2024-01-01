package com.example.exifreader.ui.screens

import android.app.Activity
import android.media.ExifInterface
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.exifreader.MainActivityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExifEditor(navController: NavController, viewModel: MainActivityViewModel, activity: Activity, modifier: Modifier = Modifier){
    val exifInfo = getExifInfo(viewModel.imageUrl, activity)

    Card() {
        var datetime:String by remember{ mutableStateOf(exifInfo[ExifInterface.TAG_DATETIME]?: "") }
        var gpsLatitude:String by remember{ mutableStateOf(exifInfo[ExifInterface.TAG_GPS_LATITUDE]?: "") }
        var gpsLongtitude:String by remember{ mutableStateOf(exifInfo[ExifInterface.TAG_GPS_LONGITUDE]?: "") }
        var make:String by remember{ mutableStateOf(exifInfo[ExifInterface.TAG_MAKE]?: "") }
        var model:String by remember{ mutableStateOf(exifInfo[ExifInterface.TAG_MODEL]?: "") }
        val changeExif:MutableMap<String, String> by remember{ mutableStateOf(mutableMapOf()) }
        OutlinedTextField(
            value = datetime,
            onValueChange = {
                datetime = it
                changeExif[ExifInterface.TAG_DATETIME] = datetime
            },
            label = { Text("Date Time") }
        )
        OutlinedTextField(
            value = gpsLatitude,
            onValueChange = {
                gpsLatitude = it
                changeExif[ExifInterface.TAG_GPS_LATITUDE] = gpsLatitude
            },
            label = { Text("GPS Latitude") }
        )
        OutlinedTextField(
            value = gpsLongtitude,
            onValueChange = {
                gpsLongtitude = it
                changeExif[ExifInterface.TAG_GPS_LONGITUDE] = gpsLongtitude
            },
            label = { Text("GPS Longitude") }
        )
        OutlinedTextField(
            value = make,
            onValueChange = {
                make = it
                changeExif[ExifInterface.TAG_MAKE] = make
            },
            label = { Text("Make") }
        )
        OutlinedTextField(
            value = model,
            onValueChange = {
                model = it
                changeExif[ExifInterface.TAG_MODEL] = model
            },
            label = { Text("Model") }
        )
        FloatingActionButton(onClick = {
            setExitInfo(viewModel.imageUrl, changeExif, activity)
            navController.navigate("MainMenu"){
                popUpTo("MainMenu")
            }

        }) {
            Text(text = "Change")
        }
    }
}
