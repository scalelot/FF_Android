package com.festum.festumfield.verstion.firstmodule.screens.dialog


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.festum.festumfield.databinding.ActivityChatProductSelectBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseDialogFragment
import com.festum.festumfield.verstion.firstmodule.screens.adapters.ProductListAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.GetFriendProduct
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ProductItemInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsProducts
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendProductViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductItemsDialog(private val receiverUserId : String,
                         private val chatProduct :ProductItemInterface)  : BaseDialogFragment<FriendProductViewModel>(), ProductItemInterface {

    private lateinit var binding: ActivityChatProductSelectBinding
    private var productListAdapter: ProductListAdapter? = null

    override fun getContentView(): View {
        binding = ActivityChatProductSelectBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        val getFriendProduct = GetFriendProduct(
            friendid = receiverUserId,
            page = 1,
            limit = Int.MAX_VALUE,
            search = "",
            sortoption = -1,
            sortfield = "name"
        )
        viewModel.friendProductItems(getFriendProduct)

        binding.icProductBack.setOnClickListener {
            dismiss()
        }

    }

    override fun setObservers() {

        viewModel.friendProductData.observe(this){
                friendsProductList ->

            Log.e("TAG", "setObservers" + friendsProductList.toString() )

            if (friendsProductList != null){

                productListAdapter = ProductListAdapter(requireActivity(), friendsProductList, this)
                val linearLayoutManager = GridLayoutManager(requireActivity(), 2)
                binding.recycleChatProduct.layoutManager = linearLayoutManager
                binding.recycleChatProduct.adapter = productListAdapter

                binding.idPBLoading.visibility = View.GONE

            }

        }

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

    override fun singleProduct(item: FriendsProducts, productId: String, sendProduct: Boolean) {
        chatProduct.chatProduct(item)
        dismiss()
    }

    override fun chatProduct(item: FriendsProducts) {

    }

}