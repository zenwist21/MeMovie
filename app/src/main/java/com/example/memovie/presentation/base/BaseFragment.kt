package com.example.memovie.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    val TAG = this.javaClass.simpleName

    private var _binding: VB? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    protected val binding: VB get() = _binding!!
    private var onClearBinding: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }

    open fun VB.initUI() {

    }

    open fun VB.initController() {

    }

    open fun VB.initObserve() {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}