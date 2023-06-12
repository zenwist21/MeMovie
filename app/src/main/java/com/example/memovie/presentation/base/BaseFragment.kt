package com.example.memovie.presentation.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    val TAG = this.javaClass.simpleName

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    open fun VB.initUI(){

    }

    open fun VB.initController(){

    }

    open fun VB.initObserve(){

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