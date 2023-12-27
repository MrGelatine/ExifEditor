package com.example.exifreader

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.exifreader.ui.theme.ExifReaderTheme
import org.jetbrains.annotations.Nullable
import java.io.File
import java.nio.file.Files.setAttribute


val ExifTags:Array<String> = arrayOf(
    "FNumber",
    "ApertureValue",
    "Artist",
    "BitsPerSample",
    "BrightnessValue",
    "CFAPattern",
    "ColorSpace",
    "ComponentsConfiguration",
    "CompressedBitsPerPixel",
    "Compression",
    "Contrast",
    "Copyright",
    "CustomRendered",
    "DateTime",
    "DateTimeDigitized",
    "DateTimeOriginal",
    "DefaultCropSize",
    "DeviceSettingDescription",
    "DigitalZoomRatio",
    "DNGVersion",
    "ExifVersion",
    "ExposureBiasValue",
    "ExposureIndex",
    "ExposureMode",
    "ExposureProgram",
    "ExposureTime",
    "FileSource",
    "Flash",
    "FlashpixVersion",
    "FlashEnergy",
    "FocalLength",
    "FocalLengthIn35mmFilm",
    "FocalPlaneResolutionUnit",
    "FocalPlaneXResolution",
    "FocalPlaneYResolution",
    "FNumber",
    "GainControl",
    "GPSAltitude",
    "GPSAltitudeRef",
    "GPSAreaInformation",
    "GPSDateStamp",
    "GPSDestBearing",
    "GPSDestBearingRef",
    "GPSDestDistance",
    "GPSDestDistanceRef",
    "GPSDestLatitude",
    "GPSDestLatitudeRef",
    "GPSDestLongitude",
    "GPSDestLongitudeRef",
    "GPSDifferential",
    "GPSDOP",
    "GPSImgDirection",
    "GPSImgDirectionRef",
    "GPSLatitude",
    "GPSLatitudeRef",
    "GPSLongitude",
    "GPSLongitudeRef",
    "GPSMapDatum",
    "GPSMeasureMode",
    "GPSProcessingMethod",
    "GPSSatellites",
    "GPSSpeed",
    "GPSSpeedRef",
    "GPSStatus",
    "GPSTimeStamp",
    "GPSTrack",
    "GPSTrackRef",
    "GPSVersionID",
    "ImageDescription",
    "ImageLength",
    "ImageUniqueID",
    "ImageWidth",
    "InteroperabilityIndex",
    "ISOSpeedRatings",
    "ISOSpeedRatings",
    "JPEGInterchangeFormat",
    "JPEGInterchangeFormatLength",
    "LightSource",
    "Make",
    "MakerNote",
    "MaxApertureValue",
    "MeteringMode",
    "Model",
    "NewSubfileType",
    "OECF",
    "AspectFrame",
    "PreviewImageLength",
    "PreviewImageStart",
    "ThumbnailImage",
    "Orientation",
    "PhotometricInterpretation",
    "PixelXDimension",
    "PixelYDimension",
    "PlanarConfiguration",
    "PrimaryChromaticities",
    "ReferenceBlackWhite",
    "RelatedSoundFile",
    "ResolutionUnit",
    "RowsPerStrip",
    "ISO",
    "JpgFromRaw",
    "SensorBottomBorder",
    "SensorLeftBorder",
    "SensorRightBorder",
    "SensorTopBorder",
    "SamplesPerPixel",
    "Saturation",
    "SceneCaptureType",
    "SceneType",
    "SensingMethod",
    "Sharpness",
    "ShutterSpeedValue",
    "Software",
    "SpatialFrequencyResponse",
    "SpectralSensitivity",
    "StripByteCounts",
    "StripOffsets",
    "SubfileType",
    "SubjectArea",
    "SubjectDistance",
    "SubjectDistanceRange",
    "SubjectLocation",
    "SubSecTime",
    "SubSecTimeDigitized",
    "SubSecTimeDigitized",
    "SubSecTimeOriginal",
    "SubSecTimeOriginal",
    "ThumbnailImageLength",
    "ThumbnailImageWidth",
    "TransferFunction",
    "UserComment",
    "WhiteBalance",
    "WhitePoint",
    "XResolution",
    "YCbCrCoefficients",
    "YCbCrPositioning",
    "YCbCrSubSampling",
    "YResolution"
)
class MainActivity : ComponentActivity() {
    private val PERMISSIONS =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
            )
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!checkPermission()){
            requestPermission()
        }


        val viewModel: MainActivityViewModel by viewModels()
        setContent {
            ExifReaderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "ChooseButton") {
                        composable("ChooseButton") { Greeting(navController, viewModel) }
                        composable("imageInfo")
                        {
                            ImageInfo(navController = navController, screenSize = LocalConfiguration.current,contentResolver = contentResolver, viewModel = viewModel)
                        }
                        composable("exifInfo")
                        {
                            ExifEditor(screenSize = LocalConfiguration.current,contentResolver = contentResolver, viewModel = viewModel, activity = this@MainActivity)
                        }
                    }
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(this@MainActivity, WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            requestPermissions(
                this@MainActivity,
                arrayOf<String>(WRITE_EXTERNAL_STORAGE),
                100
            )
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageInfo(navController: NavController, screenSize: Configuration, contentResolver:ContentResolver,viewModel:MainActivityViewModel, modifier: Modifier = Modifier){
    val screen_height = screenSize.screenHeightDp
    val screen_width = screenSize.screenWidthDp
    val exifInfo = getExifInfo(viewModel.imageUrl, contentResolver)
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
                    onClick = { navController.navigate("exifInfo") }) {
                    Text(text = "Change Exif")
                }
            }

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExifEditor(screenSize: Configuration, contentResolver:ContentResolver,viewModel:MainActivityViewModel, activity:Activity, modifier: Modifier = Modifier){
    val exifInfo = getExifInfo(viewModel.imageUrl, contentResolver)

    Card() {
        var gpsLatitude:String by remember{ mutableStateOf(exifInfo[ExifInterface.TAG_GPS_LATITUDE]?: "") }
        var gpsLongtitude:String by remember{ mutableStateOf(exifInfo[ExifInterface.TAG_GPS_LONGITUDE]?: "") }
        var exifVersion:String by remember{ mutableStateOf(exifInfo[ExifInterface.TAG_EXIF_VERSION]?: "") }
        OutlinedTextField(
            value = gpsLatitude,
            onValueChange = {
                gpsLatitude = it
                setExitInfo(viewModel.imageUrl, contentResolver, ExifInterface.TAG_GPS_LATITUDE, it, activity)
            },
            label = { Text("GPS Latitude") }
        )
        OutlinedTextField(
            value = gpsLongtitude,
            onValueChange = {
                gpsLongtitude = it
                setExitInfo(viewModel.imageUrl, contentResolver, ExifInterface.TAG_GPS_LONGITUDE, it, activity)
            },
            label = { Text("GPS Longitude") }
        )
        OutlinedTextField(
            value = exifVersion,
            onValueChange = {
                exifVersion = it
                setExitInfo(viewModel.imageUrl, contentResolver, ExifInterface.TAG_EXIF_VERSION, it, activity)
            },
            label = { Text("Exif Version") }
        )
    }
}
@Composable
fun Greeting(navController:NavController, viewModel:MainActivityViewModel, modifier: Modifier = Modifier) {
    val pickMedia = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent(), onResult = { image_uri ->
        viewModel.imageUrl = image_uri!!
        navController.navigate("imageInfo")
    })
    ElevatedButton(onClick = { pickMedia.launch("image/*") }) {
        Text(
            text = "Choose image!",
            modifier = modifier
        )
    }
}

fun getImageMediaId(resolver: ContentResolver, uri: Uri): Int{
    val collection =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        }else{
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE
    )

    val selection = null
    val selectionArgs = null
    val sordOrder = null
    val query = resolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
        sordOrder
    )
    var id = -1
    val dataColumn = query?.getColumnIndexOrThrow(projection[0])
    if (query != null) {
        if (query.moveToFirst()){
            id = query.getInt(dataColumn!!)
        }
    }
    return query?.count ?: 0
}

fun getPathUri(resolver:ContentResolver, uri:Uri):String? {

    var path: String? = null
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    resolver.query(uri, proj, null, null, null)?.use {cursor->
        val dataColumn = cursor.getColumnIndexOrThrow(proj[0])
        if (cursor.moveToFirst()){
            path = cursor.getString(dataColumn)
        }
        cursor.close()
    }
    return path
}

fun setExitInfo(imageUri: Uri, resolver: ContentResolver, tag: String, value: String, activity: Activity){
    val granted = ContextCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.ACCESS_MEDIA_LOCATION)
    if(granted != PackageManager.PERMISSION_GRANTED) {
        requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION), 0)
    }
    val path = getPathUri(resolver,imageUri)
    ExifInterface(File(path)).run {
        setAttribute(tag, value)
        saveAttributes()
    }

}
fun getExifInfo(imageUri: Uri, resolver: ContentResolver): MutableMap<String, String>{
    val inputResolver = resolver.openInputStream(imageUri)!!
    val exifInterface: ExifInterface =
        ExifInterface(inputResolver)
    val tagsInfo: MutableMap<String, String> = mutableMapOf()
    for (tag in ExifTags){
        if(exifInterface.hasAttribute(tag)){
            exifInterface.getAttribute(tag)?.let { tagsInfo.put(tag, it) }
        }
    }
    return tagsInfo
}
