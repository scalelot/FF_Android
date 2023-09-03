package com.festum.festumfield.verstion.firstmodule.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.festum.festumfield.R
import com.festum.festumfield.verstion.firstmodule.navigation.UiEvent
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : BaseViewModel> : Fragment() {
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[getViewModelClass()]
        viewModel.uiEventStream.observe(this) { uiEvent -> processUiEvent(uiEvent) }

    }


    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity?.window?.statusBarColor = getStatusBarColor()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = getContentView()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        setObservers()

        viewModel.onFragmentCreated()

    }

    abstract fun getContentView(): View
    abstract fun initUi()
    abstract fun setObservers()

    open fun getStatusBarColor() = ContextCompat.getColor(requireContext(), R.color.colorAccent)

    private fun getViewModelClass(): Class<VM> {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        return type as Class<VM>
    }

    protected open fun processUiEvent(uiEvent: UiEvent) {
        (activity as? BaseActivity<*>)?.processUiEvent(uiEvent)
    }


}