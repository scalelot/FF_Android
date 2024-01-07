package com.festum.festumfield.verstion.firstmodule.screens.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.festum.festumfield.R
import com.festum.festumfield.verstion.firstmodule.screens.adapters.CountryCodePickerAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.PhoneCodeModel
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CodeDialog (
    val context: Activity,
    private val phoneModelList: ArrayList<PhoneCodeModel>,
    val mListner: CountyPickerItems
) :
    BottomSheetDialogFragment() {

    private lateinit var bottomSheet: ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    interface CountyPickerItems {
        fun pickCountry(countries: PhoneCodeModel)
    }

    override fun onStart() {
        super.onStart()
        bottomSheet =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup // notice the R root package
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewItems: View = inflater.inflate(R.layout.country_code_dialog, container, false)

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        val recyclerView = viewItems.findViewById<RecyclerView>(R.id.country_dialog_lv)
        val close = viewItems.findViewById<ImageView>(R.id.mClose)
        val mSearch = viewItems.findViewById<SearchView>(R.id.mSearch)

        val adapter = CountryCodePickerAdapter(
            context, phoneModelList,

            countryPick = object : CountryCodePickerAdapter.CountyrPick {
                override fun pick(countries: PhoneCodeModel) {
                    mListner.pickCountry(countries)
                    dismiss()
                }
            })
        recyclerView?.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter

        close.setOnClickListener {
            dismiss()
            DeviceUtils.hideKeyboard(context)
        }

        val phoneArray = phoneModelList
        phoneArray.sortBy { it.value }
        adapter.AddAll(phoneArray)


        mSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.e("newText", newText.toString())
                adapter.filter.filter(newText)

                return true
            }
        })

        mSearch.setOnCloseListener {
            DeviceUtils.hideKeyboard(context)
            false
        }

        return viewItems

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

                behaviour.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                    override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                        // React to dragging events
                    }
                })
            }
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
}