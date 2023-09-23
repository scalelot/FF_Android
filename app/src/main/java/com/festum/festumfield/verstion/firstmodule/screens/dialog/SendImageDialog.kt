package com.festum.festumfield.verstion.firstmodule.screens.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.festum.festumfield.R
import com.festum.festumfield.databinding.SendImageItemBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseDialogFragment
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.SendImageInterface
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendProductViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class SendImageDialog(
    private val file: File,
    private val receiverUserName: String,
    private val onSend: SendImageInterface
): BaseDialogFragment<FriendProductViewModel>() {

    private lateinit var binding: SendImageItemBinding

    override fun getContentView(): View {
        binding = SendImageItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        val image = file

        Glide.with(requireActivity())
            .load(image)
            .placeholder(R.mipmap.ic_app_logo)
            .into(binding.imageView)

        binding.sendButton.setOnClickListener {
            onSend.onSendImage(file, binding.sendText.text.toString())
            dismiss()
        }

        binding.receiverUserName.text = receiverUserName

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it1 ->
                val behaviour = BottomSheetBehavior.from(it1)
                setupFullHeight(it1)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.isDraggable = false
            }
        }

        return dialog

    }

    private fun setupFullHeight(bottomSheet: View) {

        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams

    }

    override fun setObservers() {

    }
}