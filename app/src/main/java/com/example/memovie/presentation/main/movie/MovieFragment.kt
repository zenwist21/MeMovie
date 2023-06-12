package com.example.memovie.presentation.main.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.memovie.databinding.FragmentMovieBinding
import com.example.memovie.presentation.base.BaseFragment
import com.example.memovie.presentation.main.MainViewModel

class MovieFragment : BaseFragment<FragmentMovieBinding>() {
    private val viewModel: MainViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMovieBinding
        get() = FragmentMovieBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            initUI()
            initController()
            initObserve()
        }
    }

}