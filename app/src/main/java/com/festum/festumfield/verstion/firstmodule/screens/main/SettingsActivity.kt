package com.festum.festumfield.verstion.firstmodule.screens.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.festum.festumfield.Activity.BlockedContactActivity
import com.festum.festumfield.Activity.ChangeNumberActivity
import com.festum.festumfield.Activity.Contact_Us_Activity
import com.festum.festumfield.Activity.ConversationActivity
import com.festum.festumfield.Activity.HelpActivity
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ActivitySettingsBinding
import com.festum.festumfield.databinding.ChatBackupDialogBinding
import com.festum.festumfield.databinding.UploadExcelDialogBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.main.authorization.LoginActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates.Companion.get
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager.disconnectSocket
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

@AndroidEntryPoint
class SettingsActivity : BaseActivity<ProfileViewModel>() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var excelDialogBinding : UploadExcelDialogBinding
    override fun getContentView(): View {

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun setupUi() {

        init()

    }

    private fun init(){

        binding.notiSwitch.isChecked = get().isNotificationOn

        binding.darkModeSwitch.isChecked = get().isNightModeOn

        binding.icBackArrow.setOnClickListener {

            val intent = Intent()
            intent.putExtra("darkTheme", get().isNightModeOn)
            setResult(1, intent)
            finish()

        }

        binding.notiSwitch.setOnCheckedChangeListener { compoundButton, b ->
            get().isNotificationOn = compoundButton.isChecked
        }

        binding.chatBackup.setOnClickListener { chatBackupDialog() }

        binding.uploadExcel.setOnClickListener{ uploadExcelDialog() }

        binding.blockContact.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, BlockedContactActivity::class.java))
        }

        binding.changeNumber.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, ChangeNumberActivity::class.java))
        }

        binding.contactUs.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, Contact_Us_Activity::class.java))
        }

        binding.help.setOnClickListener{
            startActivity(Intent(this@SettingsActivity, HelpActivity::class.java))
        }

        binding.conversation.setOnClickListener {
            startActivity(Intent(this@SettingsActivity, ConversationActivity::class.java))
        }

        binding.signOut.setOnClickListener {

            val sharedPreferences = getSharedPreferences("AuthTokens", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            get().channelId = ""
            get().token = ""
            get().userName = ""
            get().businessProfile = false
            get().personalProfile = false
            get().onLineUser = ""
            runOnUiThread { disconnectSocket() }
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                get().isNightModeOn = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                this@SettingsActivity.recreate()
            } else {
                get().isNightModeOn = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                this@SettingsActivity.recreate()
            }
        }

    }

    override fun setupObservers() {

    }

    private fun chatBackupDialog() {
        val dialog = Dialog(this)
        val viewDialog = ChatBackupDialogBinding.inflate(layoutInflater)
        dialog.setContentView(viewDialog.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val back = ColorDrawable(Color.TRANSPARENT)
        val insetDrawable = InsetDrawable(back, 50)
        dialog.window?.setBackgroundDrawable(insetDrawable)
        viewDialog.btnClose.setOnClickListener { dialog.dismiss() }
        viewDialog.buttonCancle.setOnClickListener { dialog.dismiss() }
        viewDialog.buttonBackup.setOnClickListener {
            Toast.makeText(this@SettingsActivity, "BackUp Done", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun uploadExcelDialog() {
        val dialog = Dialog(this@SettingsActivity)
        excelDialogBinding = UploadExcelDialogBinding.inflate(layoutInflater)
        dialog.setContentView(excelDialogBinding.root)
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val back = ColorDrawable(Color.TRANSPARENT)
        val insetDrawable = InsetDrawable(back, 50)
        dialog.window?.setBackgroundDrawable(insetDrawable)
        val dialogNo = dialog.findViewById<AppCompatButton>(R.id.dialog_no)
        val dialogYes = dialog.findViewById<AppCompatButton>(R.id.dialog_yes)
        excelDialogBinding.btnSelectFile
        excelDialogBinding.btnSelectFile.setOnClickListener(View.OnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this@SettingsActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@SettingsActivity,
                    arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                selectExcel()
            }
        })
        excelDialogBinding.icClose.setOnClickListener(View.OnClickListener {
            excelDialogBinding.btnSelectFile.visibility = View.VISIBLE
            excelDialogBinding.llSampleTxt.visibility = View.VISIBLE
            excelDialogBinding.llPhoneEmail.visibility = View.GONE
            excelDialogBinding.llTxt.visibility = View.GONE
            excelDialogBinding.icClose.visibility = View.GONE
        })
        excelDialogBinding.llSampleTxt.setOnClickListener(View.OnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(this@SettingsActivity, R.style.CustomBottomSheetDialogTheme)
            val inflate: View =
                LayoutInflater.from(this@SettingsActivity).inflate(R.layout.bottom_dialog, null)
            bottomSheetDialog.setContentView(inflate)
            val bottom_img = inflate.findViewById<ImageView>(R.id.bottom_img)
            try {
                val ims = assets.open("sample_exe_file.png")
                val d = Drawable.createFromStream(ims, null)
                bottom_img.setImageDrawable(d)
            } catch (ex: IOException) {
                return@OnClickListener
            }
            bottomSheetDialog.show()
        })
        dialogNo.setOnClickListener { dialog.dismiss() }
        excelDialogBinding.imgClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun selectExcel() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        startActivityForResult(intent, 1)
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    val uri = data?.data
                    val uriString: String = uri.toString()
                    val myFile = File(uri?.path.toString())
                    var displayName: String? = null
                    onReadClick(uri)
                    if (uriString.startsWith("content://")) {
                        var cursor: Cursor? = null
                        try {
                            cursor = this.contentResolver.query(uri ?: Uri.EMPTY, null, null, null, null)
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) ?: 0)
                                excelDialogBinding.txtFileName.text = displayName
                                excelDialogBinding.llTxt.visibility = View.VISIBLE
                                excelDialogBinding.llPhoneEmail.visibility = View.VISIBLE
                                excelDialogBinding.llSampleTxt.visibility = View.GONE
                                excelDialogBinding.icClose.visibility = View.VISIBLE
                                excelDialogBinding.btnSelectFile.visibility = View.GONE
                            }
                        } finally {
                            cursor?.close()
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.name
                        Log.e("ExcelSheetName>>>>  ", displayName)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun onReadClick(uriString: Uri?) {
        try {
            val stream = contentResolver.openInputStream(uriString ?: Uri.EMPTY)
            val workbook = XSSFWorkbook(stream)
            val sheet = workbook.getSheetAt(0)
            val email = ArrayList<String>()
            val pass = ArrayList<String>()
            var columnIndex2 = 0
            var columnIndex3 = 0
            val row: Row = sheet.getRow(0)
            for (cell in row) {
                if (cell.stringCellValue == "Email") {
                    columnIndex2 = cell.columnIndex
                    for (r in sheet) {
                        if (r.rowNum == 0) continue
                        val c2 = r.getCell(columnIndex2)
                        if (c2 != null) {
                            print("" + c2)
                            email.add(c2.toString())
                            excelDialogBinding.txtEmailId.text = email.size.toString()
                        }
                    }
                } else if (cell.stringCellValue == "Phone Number") {
                    columnIndex3 = cell.columnIndex
                    for (r1 in sheet) {
                        if (r1.rowNum == 0) continue
                        val c3 = r1.getCell(columnIndex3)
                        if (c3 != null) {
                            print("" + c3)
                            pass.add(c3.toString())
                            excelDialogBinding.txtPhNo.text = pass.size.toString()
                        }
                    }
                }
            }
        } catch (fileNotFoundException: FileNotFoundException) {
            fileNotFoundException.printStackTrace()
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectExcel()
        } else {
            Toast.makeText(this@SettingsActivity, resources.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("darkTheme", get().isNightModeOn)
        setResult(1, intent)
        finish()
        super.onBackPressed()
    }

}