package com.example.exifreader.ui.screens

import android.app.Activity
import android.content.res.Configuration
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.exifreader.MainActivityViewModel
import com.example.exifreader.exifTags
import java.io.File

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ExifInfo(navController: NavController, screenSize: Configuration, activity: Activity, viewModel: MainActivityViewModel, modifier: Modifier = Modifier){
    val screen_height = screenSize.screenHeightDp
    val screen_width = screenSize.screenWidthDp
    val exifInfo = getExifInfo(viewModel.imageUrl, activity)
    Card(modifier = Modifier
        .height(screen_height.dp)
        .width(screen_width.dp)) {
        ConstraintLayout(modifier = Modifier
            .height(screen_height.dp)
            .width(screen_width.dp)) {
            val (image,tags,infoButton) = createRefs()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(image) { top.linkTo(parent.top, margin = 5.dp) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                GlideImage(
                    modifier = Modifier
                        .height((4 * (screen_height / 10)).dp),
                    model = viewModel.imageUrl, contentDescription = ""
                )
            }
            LazyColumn(
                modifier = Modifier
                    .height((4 * (screen_height / 10)).dp)
                    .constrainAs(tags) { top.linkTo(image.bottom, margin = 5.dp) }
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            )
            {
                exifInfo.forEach { keyValue ->
                    item {
                        Text(
                            text = "${keyValue.key}: ${keyValue.value}")
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(infoButton) { top.linkTo(tags.bottom, margin = 5.dp) },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("ExifInfo") }) {
                    Text(text = "Change Exif")
                }
            }

        }
    }
}

internal fun setExitInfo(imageUri: Uri, changeExif: Map<String, String>, activity: Activity){
    var path = FileHelper.getRealPathFromURI(activity.applicationContext,imageUri)
    ExifInterface(File(path)).run {
        changeExif.forEach { pair ->
            setAttribute(pair.key, pair.value)
        }
        saveAttributes()
    }

}
internal fun getExifInfo(imageUri: Uri, activity: Activity): MutableMap<String, String>{
    val inputResolver = activity.contentResolver.openInputStream(imageUri)!!
    val exifInterface  =
        ExifInterface(inputResolver)
    val tagsInfo: MutableMap<String, String> = mutableMapOf()
    for (tag in exifTags){
        if(exifInterface.hasAttribute(tag)){
            exifInterface.getAttribute(tag)?.let { tagsInfo.put(tag, it) }
        }
    }
    return tagsInfo
}