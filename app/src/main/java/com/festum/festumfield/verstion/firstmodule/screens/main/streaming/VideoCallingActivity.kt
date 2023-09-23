package com.festum.festumfield.verstion.firstmodule.screens.main.streaming

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ActivityVideoCallingBinding
import com.festum.festumfield.databinding.ChatActivityBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel

class VideoCallingActivity : BaseActivity<ChatViewModel>() {

    private lateinit var binding: ActivityVideoCallingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_calling)
    }

    override fun getContentView(): View {
        binding = ActivityVideoCallingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

    }

    override fun setupObservers() {

    }
}