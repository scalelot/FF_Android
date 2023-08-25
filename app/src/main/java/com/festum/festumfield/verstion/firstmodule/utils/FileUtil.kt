package com.festum.festumfield.verstion.firstmodule.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import com.festum.festumfield.BuildConfig.DEBUG
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class FileUtil {
    companion object {
        @SuppressLint("NewApi")
        fun getPath(uri: Uri, context: Context): String? {
            var uri: Uri = uri
            val needToCheckUri = Build.VERSION.SDK_INT >= 19
            var selection: String? = null
            var selectionArgs: Array<String>? = null
            var type: String? = null
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            // deal with different Uris.
            if (needToCheckUri && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if (isDownloadsDocument(uri)) {
                    val fileName: String? = getFilePath(context, uri)
                    if (fileName != null) {
                        return Environment.getExternalStorageDirectory()
                            .toString() + "/Download/" + fileName
                    }

                    var id = DocumentsContract.getDocumentId(uri)
                    if (id.startsWith("raw:")) {
                        id = id.replaceFirst("raw:".toRegex(), "")
                        val file = File(id)
                        if (file.exists()) return id
                    }

                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    type = split[0]
                    when (type) {
                        "image" -> uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(
                        split[1]
                    )
                }
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                val projection = arrayOf(
                    MediaStore.Images.Media.DATA
                )
                try {
                    context.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                        .use { cursor ->
                            if (cursor != null && cursor.moveToFirst()) {
                                val columnIndex: Int =
                                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                                return cursor.getString(columnIndex)
                            }
                        }
                } catch (e: Exception) {
                }
            } else {

                return uri.path

            }

            /* when (type) {
                 "video" -> {
                     val projection = arrayOf(
                         MediaStore.Video.VideoColumns.DATA
                     )
                     val cursor: Cursor? = context.contentResolver.query(
                         uri, projection, selection, selectionArgs,
                         null
                     )
                     return if (cursor == null) uri.path else {
                         cursor.moveToFirst()
                         val idx = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA)
                         try {
                             Log.e("cursor.getString(idx)", cursor.getString(idx))
                             cursor.getString(idx)
                         } catch (exception: java.lang.Exception) {
                             Log.e("exception", exception.message.toString())
                             null
                         }
                     }
                 }
                 "image" -> {
                     val projection = arrayOf(
                         MediaStore.Images.Media.DATA
                     )
                     try {
                         context.contentResolver
                             .query(uri, projection, selection, selectionArgs, null)
                             .use { cursor ->
                                 if (cursor != null && cursor.moveToFirst()) {
                                     val columnIndex: Int =
                                         cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                                     return cursor.getString(columnIndex)
                                 }
                             }
                     } catch (e: Exception) {
                     }
                 }
                 else ->
                     return uri.path
             }*/
            return null
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        private fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        private fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        private fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }


        private fun getFilePath(context: Context, uri: Uri): String? {
            var cursor: Cursor? = null
            val projection = arrayOf(
                MediaStore.MediaColumns.DISPLAY_NAME
            )
            try {
                cursor = context.contentResolver.query(
                    uri, projection, null, null,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val index: Int =
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                    return cursor.getString(index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        private fun getDataColumn(
            context: Context, uri: Uri, selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(
                column
            )
            try {
                cursor = context.contentResolver.query(
                    uri, projection, selection, selectionArgs,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    if (DEBUG) DatabaseUtils.dumpCursor(cursor)
                    val column_index: Int = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            return null
        }

        fun getDocumentFilePath(context: Context, uri: Uri): String {
            var absolutePath = ""
            try {
                val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
                val pdfInBytes = ByteArray(inputStream.available())
                inputStream.read(pdfInBytes)
                val encodePdf: String = Base64.encodeToString(pdfInBytes, Base64.DEFAULT)
                var offset = 0
                var numRead = 0
                while (offset < pdfInBytes.size && inputStream.read(
                        pdfInBytes,
                        offset,
                        pdfInBytes.size - offset
                    ).also {
                        numRead = it
                    } >= 0
                ) {
                    offset += numRead
                }
                var mPath = ""
                mPath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                    (context.getExternalFilesDir(Environment.DIRECTORY_DCIM).toString()  + ".pdf")
                } else {
                    Environment.getExternalStorageDirectory().toString() + ".pdf"
                }
                val pdfFile = File(mPath)
                val op: OutputStream = FileOutputStream(pdfFile)
                op.write(pdfInBytes)
                absolutePath = pdfFile.path
            } catch (ae: Exception) {
                ae.printStackTrace()
            }
            return absolutePath
        }
    }
}