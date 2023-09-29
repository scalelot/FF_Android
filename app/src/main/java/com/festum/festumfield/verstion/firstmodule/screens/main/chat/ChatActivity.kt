package com.festum.festumfield.verstion.firstmodule.screens.main.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.festum.festumfield.Activity.ReelsActivity
import com.festum.festumfield.R
import com.festum.festumfield.Utils.Constans
import com.festum.festumfield.databinding.ChatActivityBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.screens.adapters.ChatMessageAdapter
import com.festum.festumfield.verstion.firstmodule.screens.dialog.AppPermissionDialog
import com.festum.festumfield.verstion.firstmodule.screens.dialog.ProductDetailDialog
import com.festum.festumfield.verstion.firstmodule.screens.dialog.ProductItemsDialog
import com.festum.festumfield.verstion.firstmodule.screens.dialog.SendImageDialog
import com.festum.festumfield.verstion.firstmodule.screens.main.group.GroupDetailsActivity
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListItem
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListSection
import com.festum.festumfield.verstion.firstmodule.sources.local.prefrences.AppPreferencesDelegates
import com.festum.festumfield.verstion.firstmodule.sources.remote.apis.SocketManager
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ProductItemInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.SendImageInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import com.festum.festumfield.verstion.firstmodule.utils.DateTimeUtils
import com.festum.festumfield.verstion.firstmodule.utils.DateTimeUtils.FORMAT_API_DATETIME
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import com.festum.festumfield.verstion.firstmodule.utils.FileUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil
import com.festum.festumfield.verstion.firstmodule.utils.IntentUtil.Companion.IMAGE_PICKER_SELECT
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.google.gson.Gson
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
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@SuppressLint("SimpleDateFormat")
@AndroidEntryPoint
class ChatActivity : BaseActivity<ChatViewModel>(), ProductItemInterface, SendImageInterface {

    private lateinit var binding: ChatActivityBinding


    private lateinit var receiverUserId: String
    private lateinit var receiverUserName: String
    private lateinit var receiverUserImage: String
    private lateinit var friendsItem: FriendsListItems

    var format = SimpleDateFormat()
    var mSocket: Socket? = null
    private val listItems = ArrayList<ListItem>()
    private var productItemData: ProductItem? = null
    private var productId: String? = null
    private var sendProductId: String? = null

    private var chatMessageAdapter: ChatMessageAdapter? = null
//    private var productListAdapter: ProductListAdapter? = null


    override fun getContentView(): View {
        binding = ChatActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {


        getMessage()

        getUserStatus()


        val intent = intent.extras

        val jsonList = intent?.getString("friendsList")

        friendsItem = Gson().fromJson(jsonList, FriendsListItems::class.java)
        receiverUserName = friendsItem.fullName.toString()
        receiverUserImage = friendsItem.profileimage.toString()
        receiverUserId = friendsItem.id.toString()
        productId = intent?.getString("productId").toString()

        Log.e("TAG", "setupUi: $receiverUserId")

        val jsonObject = JSONObject(AppPreferencesDelegates.get().onLineUser)
        val onlineUserChannelId = jsonObject.keys()
        while (onlineUserChannelId.hasNext()) {
            val key = onlineUserChannelId.next()
            if (receiverUserId == key.toString().lowercase()) {
                binding.textOnline.text = getString(R.string.online)
            }
        }

        format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")

        if (receiverUserImage.isNotEmpty()) {

            Glide.with(this@ChatActivity).load(Constans.Display_Image_URL + receiverUserImage)
                .placeholder(R.drawable.ic_user_img).into(binding.imgUser)

        }



        if (receiverUserName.isNotEmpty() && receiverUserId.isNotEmpty()) {
            binding.userName.text = receiverUserName
            viewModel.getChatMessageHistory(receiverUserId, 1, Int.MAX_VALUE)
        }

        if (friendsItem.members != null){

            val groupMembers = friendsItem.members as? List<MembersList>

            if (groupMembers != null){

                binding.userName.text = friendsItem.name

                for (i in groupMembers.indices){
                    val userNamesList = groupMembers.mapNotNull { it.membersList?.fullName }
                    val commaSeparatedUserNames = userNamesList.joinToString(", ")
                    binding.textOnline.text = commaSeparatedUserNames
                }
            }

        }

        binding.rlUser.setOnClickListener {

            if (friendsItem.members?.isNotEmpty() == true){

                val intent = Intent(this@ChatActivity, GroupDetailsActivity::class.java)
                val jsonItem = Gson().toJson(friendsItem)
                intent.putExtra("groupMembersList", jsonItem)
                startActivity(intent)

            }else{
                return@setOnClickListener
            }

        }

        binding.back.setOnClickListener {
            finish()
        }

        /* Message */
        binding.btnSend.setOnClickListener {

            val msg = binding.edtChating.text.toString()

            if (msg.isNotEmpty() || sendProductId?.isNotEmpty() == true) {
                viewModel.sendMessage(null, receiverUserId, msg, sendProductId)
            }

            binding.relReplay.visibility = View.GONE

            DeviceUtils.hideKeyboard(this@ChatActivity)

        }

        /* Photos */
        binding.imgGallery.setOnClickListener {
            if (IntentUtil.readPermission(
                    this@ChatActivity
                ) && IntentUtil.writePermission(
                    this@ChatActivity
                )
            ) {
                openIntent(binding.imgGallery.id)
            } else
                onMediaPermission(binding.imgGallery.id)

        }

        /* Camera */
        binding.imgCamera.setOnClickListener {
            if (IntentUtil.cameraPermission(this@ChatActivity)
            ) {
                openIntent(binding.imgCamera.id)
            } else
                onCameraPermission(binding.imgCamera.id)

        }

        /* Product */
        binding.imgProduct.setOnClickListener {

            /* ProductDialog */

            val dialog = ProductItemsDialog(receiverUserId, this)
            dialog.show(supportFragmentManager, "product")

        }

        binding.ivClose.setOnClickListener {

            binding.relReplay.visibility = View.GONE

            sendProductId = ""

        }

        /* Product View */

        if (AppPreferencesDelegates.get().businessProfile) {
            binding.imgProduct.visibility = View.VISIBLE
        } else {
            binding.imgProduct.visibility = View.GONE
        }

    }

    override fun setupObservers() {

        viewModel.chatData.observe(this) { chatList ->

            chatList?.reverse()

            chatMessageAdapter = ChatMessageAdapter(
                this@ChatActivity,
                chatList as ArrayList<ListItem>,
                receiverUserId
            )
            binding.msgRV.adapter = chatMessageAdapter

            var prevCode = ""
            val now = DateTimeUtils.getNowSeconds()
            val today = DateTimeUtils.getChatDate(now, this@ChatActivity, true)
            chatList.forEach {
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
            binding.msgRV.scrollToPosition(listItems.size - 1)

            binding.idPBLoading.visibility = View.GONE

        }

        viewModel.sendData.observe(this) { sendData ->

            sendData?.content?.product?.productid?.let { viewModel.getProduct(it) }

            Handler(Looper.getMainLooper()).postDelayed({

                val from = From(id = sendData?.from)
                val to = To(id = sendData?.to)

                val text = Text(sendData?.content?.text?.message)
                val media = Media(
                    path = sendData?.content?.media?.path,
                    mime = sendData?.content?.media?.mime,
                    name = sendData?.content?.media?.name,
                    type = sendData?.content?.media?.type
                )

                val newItem = DocsItem(
                    createdAt = sendData?.createdAt,
                    v = sendData?.v,
                    context = sendData?.context,
                    from = from,
                    mainId = sendData?.id,
                    to = to,
                    id = sendData?.id,
                    contentType = sendData?.contentType,
                    content = Content(
                        text = text,
                        product = Product(productItemData),
                        media = media
                    ),
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
            }, 500)

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

            Log.e("TAG", "setupObservers:$productItemData")

        }

    }

    fun getMessage() {

        SocketManager.mSocket?.on("newMessage") { args ->

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

            val productItem = Product(productid = productId)
            val text = Text(messageList.optString("message"))

            if (productItem.productid?.id?.isNotEmpty() == true) {
                viewModel.getProduct(productItem.productid.id)

                Executors.newSingleThreadScheduledExecutor().schedule({
                    if (productItemData != null) {
                        newItemList(data, text, media, Product(productid = productItemData))
                    }
                }, 2, TimeUnit.SECONDS)

            } else {
                newItemList(data, text, media, productItem)
            }


        }
    }

    private fun getUserStatus() {

        SocketManager.mSocket?.on("userConnected") { args ->

            val data = args[0] as JSONObject

            AppPreferencesDelegates.get().onLineUser =
                data.optJSONObject("onlineUsers")?.toString() ?: ""

            runOnUiThread {

                val jsonObject = JSONObject(AppPreferencesDelegates.get().onLineUser)
                val onlineUserChannelId = jsonObject.keys()
                while (onlineUserChannelId.hasNext()) {
                    val key = onlineUserChannelId.next()
                    if (receiverUserId == key.toString().lowercase()) {
                        binding.textOnline.text = getString(R.string.online)
                    }
                }

            }

            Log.e("TAG", "getUserStatus:$data")

        }?.on("offline") { args ->

            val data = args[0] as JSONObject
            Log.e("TAG", "offline:$data")

            AppPreferencesDelegates.get().onLineUser =
                data.optJSONObject("onlineUsers")?.toString() ?: ""

            runOnUiThread {
                binding.textOnline.text = getString(R.string.offline)
            }

        }?.on("typingReceive") { args ->
            val data = args[0] as JSONObject
            Log.e("TAG", "typingReceive:$data")

            runOnUiThread {
                binding.textOnline.text = getString(R.string.typing)

                Handler(Looper.getMainLooper()).postDelayed(
                    Runnable { binding.textOnline.text = getString(R.string.online) },
                    3000
                )

            }

//            val i = Intent()
//            i.type = "image/*"
//            i.action = Intent.ACTION_GET_CONTENT
//
//            // pass the constant to compare it
//            // with the returned requestCode
//
//            // pass the constant to compare it
//            // with the returned requestCode
//            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)

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

    private fun onCameraPermission(actionID: Int) {

        Dexter.withContext(this@ChatActivity)
            .withPermissions(
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                    if (permission?.areAllPermissionsGranted() == true) {
                        openIntent(actionID)
                    } else {
                        AppPermissionDialog.showPermission(
                            this@ChatActivity,
                            getString(R.string.media_permission),
                            getString(R.string.media_permission_title)
                        )
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).withErrorListener {}

            .check()
    }

    private fun onMediaPermission(actionID: Int) {

        Dexter.withContext(this@ChatActivity)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(permission: MultiplePermissionsReport?) {
                    if (permission?.areAllPermissionsGranted() == true) {
                        openIntent(actionID)
                    } else {
                        AppPermissionDialog.showPermission(
                            this@ChatActivity,
                            getString(R.string.media_permission),
                            getString(R.string.media_permission_title)
                        )
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
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

                val dialog = SendImageDialog(file, receiverUserName, this)
                dialog.show(supportFragmentManager, "productDetails")

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
                viewModel.sendMessage(file = file, receiverId = receiverUserId, "", "")
            }
        }

        if (requestCode == IntentUtil.PRODUCT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val productId = productId
            viewModel.sendMessage(null, receiverUserId, "", productId)
        }

    }

    private fun newItemList(data: JSONObject, text: Text, media: Media, product: Product) {

        val content = Content(text = text, media = media, product = product)

        val from = From(
            id = data.getString("from"),
            profileimage = receiverUserImage,
            fullName = receiverUserName
        )
        val to = To(id = data.getString("to"))
        val newItem = DocsItem(
            v = data.optInt("v"),
            context = data.optString("context"),
            from = from,
            to = to,
            contentType = data.getString("contentType"),
            content = content,
            timestamp = data.getLong("timestamp"),
            status = data.getString("status"),
            createdAt = getCurrentUTCTime()
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

    override fun singleProduct(item: FriendsProducts, productId: String, sendProduct: Boolean) {

        /* Ready to product sending */

        binding.relReplay.visibility = View.VISIBLE

        val options = RequestOptions()
        binding.ivProImage.clipToOutline = true

        val product = item.images?.get(0)
        Glide.with(this@ChatActivity)
            .load(product)
            .apply(
                options.centerCrop()
                    .skipMemoryCache(true)
                    .priority(Priority.HIGH)
                    .format(DecodeFormat.PREFER_ARGB_8888)
            )
            .into(binding.ivProImage)

        binding.txtProName.text = item.name
        binding.txtProDes.text = item.description
        binding.txtProPrice.text = "${"$" + item.price.toString() + ".00"}"

        sendProductId = item.id

    }

    override fun chatProduct(item: FriendsProducts) {

        /* ProductDetailsDialog */
        Log.e("TAG", "chatProduct: $item")

        val dialog = item.id?.let { ProductDetailDialog(productId = it, chatProduct = this, item = item) }
        dialog?.show(supportFragmentManager, "productDetails")

    }

    private fun getCurrentUTCTime(): String {
        val nowInUtc = OffsetDateTime.now(UTC)
        nowInUtc.format(DateTimeFormatter.ofPattern(FORMAT_API_DATETIME))
        Log.e("TAG", "getCurrentUTCTime:--- $nowInUtc")
        return nowInUtc.toString()
    }

    override fun onSendImage(file: File, message: String) {
        /*  Send Image */
        viewModel.sendMessage(file = file, receiverId = receiverUserId, message = message, "")
    }


}