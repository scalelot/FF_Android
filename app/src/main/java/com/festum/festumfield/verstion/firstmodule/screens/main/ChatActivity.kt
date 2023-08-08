package com.festum.festumfield.verstion.firstmodule.screens.main

import android.annotation.SuppressLint
import android.os.Handler
import android.util.Log
import android.view.View
import com.app.easyday.screens.base.BaseActivity
import com.festum.festumfield.MyApplication
import com.festum.festumfield.databinding.ChatActivityBinding
import com.festum.festumfield.verstion.firstmodule.screens.adapters.ChatMessageAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListItem
import com.festum.festumfield.verstion.firstmodule.sources.local.model.ListSection
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.*
import com.festum.festumfield.verstion.firstmodule.utils.DateTimeUtils
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@AndroidEntryPoint
class ChatActivity : BaseActivity<ChatViewModel>() {

    private lateinit var binding: ChatActivityBinding
    private lateinit var receiverUserId: String
    var format = SimpleDateFormat()
    var mSocket: Socket? = null
    private val listItems = ArrayList<ListItem>()

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

        format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")


        if (userName.isNotEmpty() && receiverUserId.isNotEmpty()) {
            binding.userName.text = userName
            viewModel.getChatMessageHistory(receiverUserId, 1, Int.MAX_VALUE)
        }

        binding.back.setOnClickListener {
            finish()
        }

        binding.btnSend.setOnClickListener {

            val msg = binding.edtChating.text.toString()

            if (msg.isNotEmpty()) {
                viewModel.sendMessage(receiverUserId, msg)
            }

        }

    }

    override fun setupObservers() {

        viewModel.chatData.observe(this) { chatList ->

            chatMessageAdapter = ChatMessageAdapter(
                this@ChatActivity,
                arrayListOf(),
                receiverUserId
            )
            binding.msgRV.adapter = chatMessageAdapter

            sortList(chatList?.docs as ArrayList<DocsItem>)

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
                        val titleItem = DateTimeUtils.getChatDate(date.time, this@ChatActivity, true)
                        val day = DateTimeUtils.getChatDate(date.time, this@ChatActivity, true)
                        val isToday = day == today

                        val listSection = ListSection(titleItem, code, isToday, !isToday && date.time < now)
                        listSection.sectionName = code
                        listItems.add(listSection)
                        prevCode = code
                    }
                    it.sectionName = prevCode
                    listItems.add(it)
                }
            }
            chatMessageAdapter?.setItems(listItems)
            Handler().postDelayed(Runnable { binding.msgRV.scrollToPosition(listItems.size - 1) }, 50)

        }
    }

    fun getMessage() {

        mSocket?.on("newMessage") { args ->

            val data = args[0] as JSONObject

            Log.e("TAG", "getMessage: $data")

            val contentList = data.getJSONObject("content")
            val messageList = contentList.getJSONObject("text")

            val product = Product(contentList.optString("productid"))
            val media = Media(
                path = contentList.optString("path"),
                mime = contentList.optString("mime"),
                name = contentList.optString("name"),
                type = contentList.optString("type")
            )

            val text = Text(messageList.optString("message"))

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

            val date = format.parse(newItem.timestamp?.let { convertLongToTime(it) })

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

                Log.e("TAG", "getMessage: $newItem")
                chatMessageAdapter?.addItem(newItem)
                chatMessageAdapter?.itemCount?.let { it1 ->
                    binding.msgRV.smoothScrollToPosition(
                        it1
                    )
                }
            }
        }
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return format.format(date)
    }

}