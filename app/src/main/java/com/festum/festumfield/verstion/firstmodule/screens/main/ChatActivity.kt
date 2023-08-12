package com.festum.festumfield.verstion.firstmodule.screens.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import com.app.easyday.screens.base.BaseActivity
import com.festum.festumfield.Activity.ChatProductSelectActivity
import com.festum.festumfield.MyApplication
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ChatActivityBinding
import com.festum.festumfield.verstion.firstmodule.screens.adapters.ChatMessageAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListItem
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListSection
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import com.festum.festumfield.verstion.firstmodule.utils.DateTimeUtils
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil.Companion.IMAGE_PICKER_SELECT
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
@AndroidEntryPoint
class ChatActivity : BaseActivity<ChatViewModel>() {

    private lateinit var binding: ChatActivityBinding
    private lateinit var receiverUserId: String
    var format = SimpleDateFormat()
    var mSocket: Socket? = null
    private val listItems = ArrayList<ListItem>()
    private var productItemData: ProductItem? = null
    private var productId: String? = null

    private var chatMessageAdapter: ChatMessageAdapter? = null


    override fun getContentView(): View {
        binding = ChatActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        mSocket = MyApplication.mSocket

        if (mSocket?.connected() == true) {

            getMessage()

        } else {

            mSocket?.connected()

        }

        val intent = intent.extras

        val userName = intent?.getString("userName").toString()
        receiverUserId = intent?.getString("id").toString()
        productId = intent?.getString("productId").toString()

        format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")


        if (userName.isNotEmpty() && receiverUserId.isNotEmpty()) {
            binding.userName.text = userName
            viewModel.getChatMessageHistory(receiverUserId, 1, Int.MAX_VALUE)
        }

        binding.back.setOnClickListener {
            finish()
        }

        /* Message */
        binding.btnSend.setOnClickListener {

            val msg = binding.edtChating.text.toString()

            if (msg.isNotEmpty()) {
                viewModel.sendMessage(null, receiverUserId, msg,"")
            }

        }

        /* Photos */
        binding.imgGallery.setOnClickListener {
            if (IntentUtil.cameraPermission(this@ChatActivity) && IntentUtil.readPermission(
                    this@ChatActivity
                ) && IntentUtil.writePermission(
                    this@ChatActivity
                )
            ) {
                openIntent(binding.imgGallery.id)
            } else
                onPermission(binding.imgGallery.id)

        }

        /* Camera */
        binding.imgCamera.setOnClickListener {
            if (IntentUtil.cameraPermission(this@ChatActivity) && IntentUtil.readPermission(
                    this@ChatActivity
                ) && IntentUtil.writePermission(
                    this@ChatActivity
                )
            ) {
                openIntent(binding.imgCamera.id)
            } else
                onPermission(binding.imgCamera.id)

        }

        /* Product */
        binding.imgProduct.setOnClickListener {
            val chatProductIntent = Intent(this@ChatActivity, ChatProductSelectActivity::class.java)
            chatProductIntent.putExtra("friendid", receiverUserId)
            startActivity(chatProductIntent)
        }

    }

    override fun setupObservers() {

        viewModel.chatData.observe(this) { chatList ->

            chatList?.reverse()

            chatMessageAdapter = ChatMessageAdapter(
                this@ChatActivity,
                chatList as ArrayList<ListItem> /* = java.util.ArrayList<com.festum.festumfield.verstion.firstmodule.sources.local.model.ListItem> */,
                receiverUserId
            )
            binding.msgRV.adapter = chatMessageAdapter
            binding.msgRV.scrollToPosition(chatList.size - 1)

            /*sortList(chatList?.docs as ArrayList<DocsItem>)*/

            /*val from = From(id = sendData?.from)
            val to = To(id = sendData?.to)

            val newItem = DocsItem(
                createdAt = sendData?.createdAt,
                v = sendData?.v,
                context = sendData?.context,
                from = from,
                mainId = sendData?.id,
                to = to,
                id = sendData?.id,
                contentType = sendData?.contentType,
                content = sendData?.content,
                timestamp = sendData?.timestamp,
                status = sendData?.status,
                updatedAt = sendData?.updatedAt

            )

            chatMessageAdapter?.addItem(newItem)
            chatMessageAdapter?.itemCount?.let { it1 ->
                binding.msgRV.smoothScrollToPosition(
                    it1
                )
            }*/



            binding.idPBLoading.visibility = View.GONE

        }

        viewModel.sendData.observe(this) { sendData ->

            val from = From(id = sendData?.from)
            val to = To(id = sendData?.to)

            val newItem = DocsItem(
                createdAt = sendData?.createdAt,
                v = sendData?.v,
                context = sendData?.context,
                from = from,
                mainId = sendData?.id,
                to = to,
                id = sendData?.id,
                contentType = sendData?.contentType,
                content = sendData?.content,
                timestamp = sendData?.timestamp,
                status = sendData?.status,
                updatedAt = sendData?.updatedAt

            )

            val date = format.parse(newItem.createdAt)

            val code = date?.time?.let { it1 ->

                DateTimeUtils.getChatDate(
                    it1,
                    this@ChatActivity,
                    true
                )

            }

            val listItem = listItems.find { it.sectionName == code }

            binding.edtChating.setText("")

            if (listItem == null) {
                //chat has today section
                val now = DateTimeUtils.getNowSeconds()
                val today =
                    DateTimeUtils.getChatDate(now, this@ChatActivity, true)
                val titleItem =
                    DateTimeUtils.getChatDate(
                        date.time,
                        this@ChatActivity,
                        true
                    )
                val day = DateTimeUtils.getChatDate(
                    date.time,
                    this@ChatActivity,
                    true
                )
                val isToday = day == today

                val listSection =
                    code?.let { it1 ->
                        ListSection(
                            titleItem,
                            it1, isToday, !isToday && date.time < now
                        )
                    }
                listSection?.sectionName = code
                if (listSection != null) {
                    listItems.add(listSection)
                    chatMessageAdapter?.addItem(listSection)
                }
            } else {
                newItem.sectionName = code
                listItems.add(newItem)
                //There is no today section
            }

            chatMessageAdapter?.addItem(newItem)
            chatMessageAdapter?.itemCount?.let { it1 ->
                binding.msgRV.smoothScrollToPosition(
                    it1
                )
            }

        }

        viewModel.productData.observe(this) { productData ->

            productItemData = ProductItem(
                offer = productData?.offer,
                subCategory = productData?.subCategory,
                images = productData?.images,
                price = productData?.price,
                itemCode = productData?.itemCode,
                name = productData?.name,
                description = productData?.description,
                id = productData?.id,
                category = productData?.category
            )

        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun sortList(logList: ArrayList<DocsItem>) {

        logList.sortWith { o1, o2 ->

            val date1 = format.parse(o1.createdAt)
            val date2 = format.parse(o2.createdAt)
            if (o1.createdAt != null && o2.createdAt != null) {
                val dateStr1 = DateTimeUtils.addStringTimeToDate(date1)
                val dateStr2 = DateTimeUtils.addStringTimeToDate(date2)
                dateStr2.compareTo(dateStr1)
            } else {
                date2.compareTo(date1)
            }
        }

        logList.reverse()

        if (chatMessageAdapter != null) {
            chatMessageAdapter?.clearAll()
            listItems.clear()
            var prevCode = ""
            val now = DateTimeUtils.getNowSeconds()
            val today = DateTimeUtils.getChatDate(now, this@ChatActivity, true)
            logList.forEach {
                if (it.createdAt != null) {
                    val date =
                        format.parse(
                            it.createdAt
                        )
                    val code = DateTimeUtils.getChatDate(date.time, this@ChatActivity, true)

                    if (code != prevCode) {
                        val titleItem =
                            DateTimeUtils.getChatDate(date.time, this@ChatActivity, true)
                        val day = DateTimeUtils.getChatDate(date.time, this@ChatActivity, true)
                        val isToday = day == today

                        val listSection =
                            ListSection(titleItem, code, isToday, !isToday && date.time < now)
                        listSection.sectionName = code
                        listItems.add(listSection)
                        prevCode = code
                    }
                    it.sectionName = prevCode
                    listItems.add(it)
                }
            }
            chatMessageAdapter?.setItems(listItems)
            Handler().postDelayed(
                Runnable { binding.msgRV.scrollToPosition(listItems.size - 1) },
                50
            )

        }
    }

    fun getMessage() {

        mSocket?.on("newMessage") { args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "getMessage: $data")

            val contentList = data.getJSONObject("content")
            val messageList = contentList.getJSONObject("text")
            val mediaList = contentList.getJSONObject("media")
            val product = contentList.getJSONObject("product")

            val media = Media(
                path = mediaList.optString("path"),
                mime = mediaList.optString("mime"),
                name = mediaList.optString("name"),
                type = mediaList.optString("type")
            )
            val productId = ProductItem(id = product.optString("productid"))

            val productItem = Product(productId)

            val text = Text(messageList.optString("message"))

            if (productItem.productid?.id?.isNotEmpty() == true) {
                viewModel.getProduct(productItem.productid.id)

                Executors.newSingleThreadScheduledExecutor().schedule({
                    if (productItemData != null) {
                        newItemList(data, text, media, Product(productItemData))
                    }
                }, 2, TimeUnit.SECONDS)

            } else {
                newItemList(data, text, media, productItem)
            }


        }
    }

    fun openIntent(actionID: Int) {
        when (actionID) {
            R.id.img_gallery -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "image/*"
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                startActivityForResult(intent, IMAGE_PICKER_SELECT)
            }
            R.id.img_camera -> {
                IntentUtil.getCaptureImageVideoIntent(this@ChatActivity).let {
                    startActivityForResult(
                        it,
                        IntentUtil.PICK_IMAGE_VIDEO_CHOOSER_REQUEST_CODE
                    )
                }
            }
        }
    }

    private fun onPermission(actionID: Int) {

        Dexter.withContext(this@ChatActivity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0?.areAllPermissionsGranted() == true)
                        openIntent(actionID)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                }

            }).withErrorListener {}

            .check()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK) {

            val uri = IntentUtil.getPickImageResultUri(baseContext, data)

            if (uri != null) {
                val mImageFile = uri.let { it ->
                    FileUtil.getPath(Uri.parse(it.toString()), this@ChatActivity)
                        ?.let { File(it) }
                }
                val file = File(mImageFile.toString())
                viewModel.sendMessage(file = file, receiverId = receiverUserId,"","")
            }

        }

        if (requestCode == IntentUtil.PICK_IMAGE_VIDEO_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            var uri = data?.data
            if (uri == null) {
                uri = Uri.fromFile(IntentUtil.getImagePath())
            }
            if (uri != null) {

                val mImageFile = uri.let { it ->
                    FileUtil.getPath(Uri.parse(it.toString()), this@ChatActivity)
                        ?.let { File(it) }
                }
                val file = File(mImageFile.toString())
                viewModel.sendMessage(file = file, receiverId = receiverUserId,"","")
            }
        }

        if (requestCode == IntentUtil.PRODUCT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val productId = productId
            viewModel.sendMessage(null, receiverUserId, "",productId)
        }

    }

    private fun newItemList(data: JSONObject, text: Text, media: Media, product: Product) {

        val content = Content(text = text, media = media, product = product)

        val from = From(id = data.getString("from"))
        val to = To(id = data.getString("to"))
        val newItem = DocsItem(
            v = data.optInt("v"),
            context = data.optString("context"),
            from = from,
            to = to,
            contentType = data.getString("contentType"),
            content = content,
            timestamp = data.getLong("timestamp"),
            status = data.getString("status")
        )

        val date = format.parse(newItem.timestamp?.let { convertLongToTime(it) }.toString())

        val code = date?.time?.let { it1 ->

            DateTimeUtils.getChatDate(
                it1,
                this@ChatActivity,
                true
            )

        }

        val listItem = listItems.find { it.sectionName == code }

        runOnUiThread {

            if (listItem == null) {
                //chat has today section
                val now = DateTimeUtils.getNowSeconds()
                val today =
                    DateTimeUtils.getChatDate(now, this@ChatActivity, true)
                val titleItem =
                    DateTimeUtils.getChatDate(
                        date?.time ?: 0,
                        this@ChatActivity,
                        true
                    )
                val day = DateTimeUtils.getChatDate(
                    date?.time ?: 0,
                    this@ChatActivity,
                    true
                )
                val isToday = day == today

                val listSection =
                    code?.let { it1 ->
                        ListSection(
                            titleItem,
                            it1, isToday, !isToday && date.time < now
                        )
                    }
                listSection?.sectionName = code
                if (listSection != null) {
                    listItems.add(listSection)
                    chatMessageAdapter?.addItem(listSection)
                }
            } else {
                newItem.sectionName = code
                listItems.add(newItem)
                //There is no today section
            }

            Log.e("TAG", "getMessage: $newItem")
            chatMessageAdapter?.addItem(newItem)
            chatMessageAdapter?.itemCount?.let { it1 ->
                binding.msgRV.smoothScrollToPosition(
                    it1
                )
            }
        }

    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return format.format(date)
    }

}