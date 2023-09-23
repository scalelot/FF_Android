package com.festum.festumfield.verstion.firstmodule.screens.main.group

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.festum.festumfield.R
import com.festum.festumfield.databinding.ActivityHomeBinding
import com.festum.festumfield.databinding.ActivityNewGroupBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseActivity
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import com.festum.festumfield.verstion.firstmodule.viemodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewGroupActivity : BaseActivity<ChatViewModel>() {

    private lateinit var binding: ActivityNewGroupBinding
    override fun getContentView(): View {
        binding = ActivityNewGroupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun setupUi() {

        binding.icBack.setOnClickListener {
            finish()
        }

    }

    override fun setupObservers() {

    }
}