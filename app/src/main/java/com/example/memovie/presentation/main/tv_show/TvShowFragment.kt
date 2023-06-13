package com.example.memovie.presentation.main.tv_show

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memovie.core.data.model.GenreModel
import com.example.memovie.core.utils.NetworkConstant
import com.example.memovie.databinding.FragmentTvShowBinding
import com.example.memovie.presentation.base.BaseFragment
import com.example.memovie.presentation.components.adapter.AdapterGenre
import com.example.memovie.presentation.components.adapter.MovieAdapter
import com.example.memovie.presentation.main.GenreState
import com.example.memovie.presentation.main.genreDetail.GenreDetailBottomSheet
import com.example.memovie.presentation.utils.hideView
import com.example.memovie.presentation.utils.scheduledEvent
import com.example.memovie.presentation.utils.setToolbarEvent
import com.example.memovie.presentation.utils.showView
import com.example.memovie.presentation.utils.textListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Timer


@AndroidEntryPoint
class TvShowFragment : BaseFragment<FragmentTvShowBinding>() {
    private val timer: Timer = Timer()
    private val viewModel: TvShowViewModel by viewModels()
    private val adapterTvShow: MovieAdapter by lazy { MovieAdapter() }
    private val genre: AdapterGenre by lazy { AdapterGenre() }
    private val adapterTrendingTvShow: MovieAdapter by lazy { MovieAdapter() }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvShowBinding
        get() = FragmentTvShowBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            initUI()
            initController()
            initObserve()
        }
    }

    override fun FragmentTvShowBinding.initUI() {
        toolbar.setToolbarEvent {
            if (toolbar.llcSearchBar.visibility == View.VISIBLE) {
                showHideListAll(true)
                clCategories.hideView()
            } else {
                showHideListAll(false)
            }
        }
        setupAdapter()
    }


    override fun FragmentTvShowBinding.initController() {
        toolbar.edtSearch.textListener {
            scheduledEvent(timer) {
                if (!it.isNullOrEmpty()) viewModel.searchMovie(it.toString())
                else viewModel.getTvShowList()
            }
        }
        iError.tvTryAgain.setOnClickListener {
            viewModel.execute()
        }
        genre.setOnClickListener { data ->
            lifecycleScope.launch {
                viewModel.setOrRemoveSelectedPosition(data ?: GenreModel())
                delay(300)
                viewModel.getTvShowList()
                rvGenres.smoothScrollToPosition(0)
            }
        }
        tvSeeAllGenre.setOnClickListener {
            GenreDetailBottomSheet(state = viewModel.genreState.value) {
                lifecycleScope.launch {
                    viewModel.updateGenreState(it as GenreState)
                    delay(300)
                    viewModel.getTvShowList()
                    rvGenres.smoothScrollToPosition(0)
                }
            }.show(requireActivity().supportFragmentManager, "Bottom Sheet Genre")
        }
        tvSeeAllTvShow.setOnClickListener {
            showHideListAll(true)
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (llcSeeAll.visibility == View.VISIBLE) {
                        if (toolbar.llcSearchBar.visibility == View.VISIBLE) toolbar.llcSearchBar.hideView()
                        showHideListAll(false)
                    }
                }
            })

        srlMain.setOnRefreshListener {
            if (srlMain.isRefreshing) {
                viewModel.execute()
                lifecycleScope.launch {
                    delay(1000)
                    srlMain.isRefreshing = false
                }
            }
        }
        rvSeeAll.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!recyclerView.canScrollVertically(1) && dy > 0 && !viewModel.tvState.value.loadingNextPage) {
                    if (toolbar.llcSearchBar.visibility != View.VISIBLE) viewModel.loadMoreActivities()
                }
                if (dy > 0) { // scrolling down
                    lifecycleScope.launch {
                        srlMain.isEnabled = false
                    }
                } else if (dy < 0) { // scrolling up
                    if (layoutManager?.findFirstVisibleItemPosition() == 0) srlMain.isEnabled = true
                }
            }
        })
        rvTvShow.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (dy > 0) { // scrolling down
                    lifecycleScope.launch {
                        srlMain.isEnabled = false
                    }
                } else if (dy < 0) { // scrolling up
                    if (layoutManager?.findFirstVisibleItemPosition() == 0) srlMain.isEnabled = true
                }
            }
        })
    }

    override fun FragmentTvShowBinding.initObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tvState.collectLatest {
                    launch {
                        adapterTvShow.setViewLoading(it.loading)
                        adapterTrendingTvShow.setViewLoading(it.loadingTrendingTV)
                        showNextPageLoading(it.loadingNextPage)
                    }
                    launch {
                        showHideError(it.errorTvShow ?: it.errorTrendingTv)
                    }
                    launch {
                        if (it.listTvShow.isNotEmpty()) {
                            adapterTvShow.differ.submitList(it.listTvShow)
                            adapterTvShow.setViewError(false)
                        }
                        else{
                            adapterTvShow.setViewError(true, NetworkConstant.NOT_FOUND)
                        }
                    }
                    launch {
                        if (it.listTrendingTV.isNotEmpty()) {
                            adapterTrendingTvShow.differ.submitList(it.listTrendingTV)
                            adapterTrendingTvShow.setViewError(false)
                        } else {
                            adapterTvShow.setViewError(true, NetworkConstant.NOT_FOUND)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.genreState.collectLatest {
                    lifecycleScope.launch {
                        genre.setViewLoading(it.loading)
                    }
                    lifecycleScope.launch {
                        if (it.listGenre.isNotEmpty()) genre.differ.submitList(it.listGenre.take(10))
                        else genre.setViewError(!it.errorGenre.isNullOrEmpty(), it.errorGenre)
                    }
                    lifecycleScope.launch {
                        if (!it.selectedGenre.isNullOrEmpty()) showHideListAll(true)
                    }
                }
            }
        }
    }

    private fun showNextPageLoading(state: Boolean) {
        binding.loadingNextPages.rlMain.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun showHideListAll(state: Boolean) {
        if (state) {
            if (viewModel.tvState.value.listTvShow.isNotEmpty()) {
                binding.rvSeeAll.adapter = adapterTvShow
                binding.llcSeeAll.showView()
                binding.llcMain.hideView()
            }
        } else {
            viewModel.execute()
            binding.llcSeeAll.hideView()
            binding.clCategories.showView()
            binding.llcMain.showView()
            showNextPageLoading(false)
        }
    }

    private fun setupAdapter() {
        binding.rvGenres.adapter = genre
        binding.rvTrendingTV.adapter = adapterTrendingTvShow
        binding.rvTvShow.adapter = adapterTvShow
        binding.rvTvShow.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvSeeAll.layoutManager = GridLayoutManager(requireActivity(), 2)
    }
    private fun FragmentTvShowBinding.showHideError(errorText: String?) {
        errorText?.let {
            if (errorText != NetworkConstant.NOT_FOUND) {
                srlMain.hideView()
                iError.llcError.showView()
                iError.tvMessage.text = errorText
                iError.tvTryAgain.visibility = View.VISIBLE
            }
            return
        }
        srlMain.showView()
        iError.llcError.hideView()
    }
}