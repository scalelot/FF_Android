package com.festum.festumfield.verstion.firstmodule.screens.dialog


import android.R
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.doOnAttach
import com.festum.festumfield.Model.ImageSliderModel
import com.festum.festumfield.databinding.ActivityProductDetailsBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseDialogFragment
import com.festum.festumfield.verstion.firstmodule.screens.adapters.ProductViewPagerAdapter
import com.festum.festumfield.verstion.firstmodule.sources.remote.interfaces.ProductItemInterface
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.FriendsProducts
import com.festum.festumfield.verstion.firstmodule.sources.remote.model.ProductItem
import com.festum.festumfield.verstion.firstmodule.viemodels.FriendProductViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProductDetailDialog(private val productId : String,
                          private val chatProduct :ProductItemInterface)  : BaseDialogFragment<FriendProductViewModel>(), ProductItemInterface {

    private lateinit var binding: ActivityProductDetailsBinding
    private var productItemData: ProductItem? = null

    var viewPagerAdapter: ProductViewPagerAdapter? = null

    override fun getContentView(): View {
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initUi() {

        viewModel.getProduct(productId)

        binding.btnInquiryMessage.visibility = View.VISIBLE
        binding.icPEdit.visibility = View.GONE
        binding.icDelete.visibility = View.GONE

        binding.icBack.setOnClickListener {
            dismiss()
        }

        binding.btnInquiryMessage.setOnClickListener{
            chatProduct.singleProduct(FriendsProducts(),productId,true)
            dismiss()
        }

    }

    @SuppressLint("SetTextI18n")
    override fun setObservers() {

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

            if (productItemData != null){

                binding.txtPName.text = productItemData?.name
                binding.txtPPrice.text = "${"$" + productItemData?.price.toString() + ".00"}"

                if (productItemData?.offer.isNullOrBlank()){
                    binding.txtPOffer.visibility = View.GONE
                }else{
                    binding.txtPOffer.visibility = View.VISIBLE
                    binding.txtPOffer.text = "${"$" + productItemData?.offer.toString() + "% Off"}"
                }

                binding.txtPDes.text = productItemData?.description
                binding.txtPCode.text = productItemData?.itemCode

                val imageSliderModels = ArrayList<ImageSliderModel>()

                /*if (productItemData?.images?.isNotEmpty() == true){
                    for (i in productItemData?.images?.indices!!) {
                        imageSliderModels.add(
                            ImageSliderModel(
                                productItemData?.images?.get(i)
                            )
                        )
                    }
                }*/

                binding.viewPager.visibility = View.GONE
                binding.productPager.visibility = View.VISIBLE
                binding.pagerDotes.visibility = View.VISIBLE

                viewPagerAdapter = ProductViewPagerAdapter(requireActivity())

                binding.productPager.apply {
                    adapter = viewPagerAdapter?.apply { submitList(productItemData?.images) }
                }

                binding.pagerDotes.attachTo(binding.productPager)


               /* binding.dotsLayout.setWithViewPager2(binding.productPager, false)
                binding.dotsLayout.itemCount = productItemData?.images?.size ?: 0
                binding.dotsLayout.setAnimationMode(CircleIndicator.AnimationMode.SLIDE)*/


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

    }

    override fun chatProduct(item: FriendsProducts) {

    }

}