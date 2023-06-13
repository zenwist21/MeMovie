package com.example.memovie.presentation.main.movie

import android.content.Intent
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
import com.example.memovie.core.data.model.TmDbModel
import com.example.memovie.core.utils.NetworkConstant.NOT_FOUND
import com.example.memovie.databinding.FragmentMovieBinding
import com.example.memovie.presentation.base.BaseFragment
import com.example.memovie.presentation.components.adapter.AdapterGenre
import com.example.memovie.presentation.components.adapter.MovieAdapter
import com.example.memovie.presentation.detail.DetailActivity
import com.example.memovie.presentation.detail.DetailType
import com.example.memovie.presentation.main.GenreState
import com.example.memovie.presentation.main.genreDetail.GenreDetailBottomSheet
import com.example.memovie.presentation.utils.getDummyMovie
import com.example.memovie.presentation.utils.hideView
import com.example.memovie.presentation.utils.itemID
import com.example.memovie.presentation.utils.itemTYPE
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
class MovieFragment : BaseFragment<FragmentMovieBinding>() {
    private val timer: Timer = Timer()
    private val viewModel: MovieViewModel by viewModels()
    private val adapterMovie: MovieAdapter by lazy { MovieAdapter() }
    private val genre: AdapterGenre by lazy { AdapterGenre() }
    private val adapterTrendingMovie: MovieAdapter by lazy { MovieAdapter() }

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

    override fun FragmentMovieBinding.initUI() {
        toolbar.setToolbarEvent {
            if (toolbar.llcSearchBar.visibility == View.VISIBLE) {
                showHideListAll(true)
                clCategories.hideView()
            } else {
                showHideListAll(false)
            }
        }
        setupAdapter()
        adapterMovie.differ.submitList(getDummyMovie())
    }


    override fun FragmentMovieBinding.initController() {
        toolbar.edtSearch.textListener {
            scheduledEvent(timer) {
                if (!it.isNullOrEmpty()) viewModel.searchMovie(it.toString())
                else viewModel.getMoviesList()
            }
        }
        iError.tvTryAgain.setOnClickListener {
            viewModel.execute()
        }
        adapterMovie.setOnClickListener { data ->
            Intent(requireActivity(), DetailActivity::class.java).also {
                it.putExtra(itemID, (data as TmDbModel).id)
                it.putExtra(itemTYPE, DetailType.MOVIE.name)
                startActivity(it)
            }
        }
        adapterTrendingMovie.setOnClickListener { data ->
            Intent(requireActivity(), DetailActivity::class.java).also {
                it.putExtra(itemID, (data as TmDbModel).id)
                it.putExtra(itemTYPE, DetailType.MOVIE.name)
                startActivity(it)
            }
        }
        genre.setOnClickListener { data ->
            lifecycleScope.launch {
                viewModel.setOrRemoveSelectedPosition(data ?: GenreModel())
                delay(300)
                viewModel.getMoviesList()
                rvGenres.smoothScrollToPosition(0)
            }
        }
        tvSeeAllGenre.setOnClickListener {
            GenreDetailBottomSheet(state = viewModel.genreState.value) {
                lifecycleScope.launch {
                    viewModel.updateGenreState(it as GenreState)
                    delay(300)
                    viewModel.getMoviesList()
                    rvGenres.smoothScrollToPosition(0)
                }
            }.show(requireActivity().supportFragmentManager, "Bottom Sheet Genre")
        }
        tvSeeAllMovies.setOnClickListener {
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
                if (!recyclerView.canScrollVertically(1) && dy > 0 && !viewModel.movieState.value.loadingNextPage) {
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
        rvMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

    override fun FragmentMovieBinding.initObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieState.collectLatest {
                    launch {
                        adapterMovie.setViewLoading(it.loading)
                        adapterTrendingMovie.setViewLoading(it.loadingTrendingMovies)
                        showNextPageLoading(it.loadingNextPage)
                    }
                    launch {
                        showHideError(it.errorMovie ?: it.errorTrendingMovie)
                    }
                    launch {
                        if (it.listMovie.isNotEmpty()) {
                            adapterMovie.differ.submitList(it.listMovie)
                            adapterMovie.setViewError(false)
                        }
                        else{
                            adapterMovie.setViewError(true, NOT_FOUND)
                        }
                    }
                    launch {
                        if (it.listTrendingMovie.isNotEmpty()) {
                            adapterTrendingMovie.differ.submitList(it.listTrendingMovie)
                            adapterTrendingMovie.setViewError(false)
                        } else {
                            adapterTrendingMovie.setViewError(true, NOT_FOUND)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.genreState.collectLatest {
                    launch {
                        genre.setViewLoading(it.loading)
                    }
                    launch {
                        showHideError(it.errorGenre)
                    }
                    launch {
                        if (it.listGenre.isNotEmpty()) genre.differ.submitList(it.listGenre.take(10))
                        else genre.setViewError(!it.errorGenre.isNullOrEmpty(), it.errorGenre)
                    }
                    launch {
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
            if (viewModel.movieState.value.listMovie.isNotEmpty()) {
                binding.rvSeeAll.adapter = adapterMovie
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
        binding.rvTrendingMovies.adapter = adapterTrendingMovie
        binding.rvMovies.adapter = adapterMovie
        binding.rvMovies.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding.rvSeeAll.layoutManager = GridLayoutManager(requireActivity(), 2)
    }

    private fun FragmentMovieBinding.showHideError(errorText: String?) {
        errorText?.let {
            if (errorText != NOT_FOUND) {
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