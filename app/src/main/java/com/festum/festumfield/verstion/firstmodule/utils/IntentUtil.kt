package com.festum.festumfield.verstion.firstmodule.utils

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.io.File
import java.util.*

class IntentUtil {

    companion object {

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        var storge_permissions_33 = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.CAMERA
        )

        var PICK_IMAGE_CHOOSER_REQUEST_CODE = 200
        var PICK_IMAGE_VIDEO_CHOOSER_REQUEST_CODE = 300
        var PRODUCT_REQUEST_CODE = 100
        var IMAGE_PICKER_SELECT = 201
        var DOCUMENT_PICKER_SELECT = 301
        var imgPath: File? = null
        var vidPath: File? = null
        fun getPickImageChooserIntent(
            context: Context,
            activity: Activity
        ): Intent? {
            val allIntents: MutableList<Intent> = ArrayList()
            val packageManager = context.packageManager

            // collect all camera intents if Camera cameraPermission is available
            if (cameraPermission(activity)) {
                val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri())
                allIntents.add(captureIntent)
            }
            var galleryIntents =
                getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT)
            if (galleryIntents.isEmpty()) {
                // if no intents found for get-content try pick intent action (Huawei P9).
                galleryIntents =
                    getGalleryIntents(packageManager, Intent.ACTION_PICK)
            }
            allIntents.addAll(galleryIntents)
            val target: Intent
            if (allIntents.isEmpty()) {
                target = Intent()
            } else {
                target = allIntents[allIntents.size - 1]
                allIntents.removeAt(allIntents.size - 1)
            }

            // Create a chooser from the main  intent
            val chooserIntent = Intent.createChooser(target, "Choose source")

            // Add all other intents
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>()
            )
            return chooserIntent
        }

        fun getGalleryIntents(
            packageManager: PackageManager, action: String
        ): List<Intent> {
            val intents: MutableList<Intent> = ArrayList()
            val galleryIntent =
                if (action === Intent.ACTION_GET_CONTENT) Intent(action) else Intent(
                    action,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            galleryIntent.type = "image/*"
            val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
            for (res in listGallery) {
                val intent = Intent(galleryIntent)
                intent.component =
                    ComponentName(res.activityInfo.packageName, res.activityInfo.name)
                intent.setPackage(res.activityInfo.packageName)
                intents.add(intent)
            }

            return intents
        }

        fun cameraPermission(activity: Activity): Boolean {

            return ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun readAudioPermission(activity: Activity): Boolean {

            return ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.READ_MEDIA_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED

        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun readVideoPermission(activity: Activity): Boolean {

            return ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.READ_MEDIA_VIDEO
                    ) == PackageManager.PERMISSION_GRANTED

        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun readImagesPermission(activity: Activity): Boolean {

            return ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

        }

        private fun setImageUri(): Uri {
            // Store image in dcim
            val file = File(
                Environment.getExternalStorageDirectory().toString() + "/DCIM/",
                "image" + Date().time.toString() + ".png"
            )
            val imgUri: Uri = Uri.fromFile(file)
            this.imgPath = file
            return imgUri
        }

        fun readPermission(activity: Activity): Boolean {

            return ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

        }

        fun writePermission(activity: Activity): Boolean {

            return ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

        }

        fun readMapPermission(activity: Activity): Boolean {

            return ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        }

        fun readMapFinePermission(activity: Activity): Boolean {

            return ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        }

        fun getCaptureImageVideoIntent(
            activity: Activity
        ): Intent {

            val allIntents: MutableList<Intent> = ArrayList()

            // collect all camera intents if Camera cameraPermission is available
            if (cameraPermission(activity)) {
                val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri())
                allIntents.add(captureIntent)

                val captureIntent1 = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                allIntents.add(captureIntent1)
            }

            val target: Intent
            if (allIntents.isEmpty()) {
                target = Intent()
            } else {
                target = allIntents[allIntents.size - 1]
                allIntents.removeAt(allIntents.size - 1)
            }

            // Create a chooser from the main  intent
            val chooserIntent = Intent.createChooser(target, "Choose source")

            // Add all other intents
            chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>()
            )
            return chooserIntent
        }

        fun getPickImageResultUri(context: Context, data: Intent?): Uri? {
            var isCamera = true
            if (data != null && data.data != null) {
                val action = data.action
                isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
            }
            return if (isCamera || data?.data == null) {
//            --------1---------
//            getCaptureImageOutputUri(context)
//            --------2---------
                /* val photo = data?.extras?.get("data") as Bitmap?
                 getImageUri(context, photo)*/
//            --------3---------.
                Uri.fromFile(getImagePath())
            } else data.data
        }

        fun getImagePath(): File? {
            return imgPath
        }

    }
}