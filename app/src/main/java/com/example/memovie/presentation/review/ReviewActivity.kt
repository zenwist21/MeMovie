package com.example.memovie.presentation.review

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.memovie.R
import com.example.memovie.core.utils.NetworkConstant.NOT_FOUND
import com.example.memovie.databinding.ActivityReviewBinding
import com.example.memovie.presentation.components.adapter.AdapterReview
import com.example.memovie.presentation.detail.DetailType
import com.example.memovie.presentation.utils.hideView
import com.example.memovie.presentation.utils.itemID
import com.example.memovie.presentation.utils.itemTYPE
import com.example.memovie.presentation.utils.showView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewActivity : AppCompatActivity() {
    private var _binding: ActivityReviewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReviewViewModel by viewModels()
    private val adapter: AdapterReview by lazy { AdapterReview() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityReviewBinding.inflate(layoutInflater)
        initController()
        setContentView(binding.root)
    }

    private fun initController() {
        viewModel.setMovieId(
            intent.getIntExtra(itemID, 0),
            DetailType.valueOf(intent.extras?.getString(itemTYPE) ?: DetailType.MOVIE.name)
        )
        viewModel.getData()
        initListener()
        initAdapter()
        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.apply {
                    state.collectLatest {
                        launch {
                            showInitialLoading(it.initialLoading)
                            adapter.changeIsLoading(it.loadingNextPage)
                        }
                        launch {
                            if (it.listReview.isNotEmpty()) adapter.differ.submitList(it.listReview)
                        }
                        launch {
                            showErrorView(state = state.value)
                        }
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        binding.rvList.adapter = adapter
        binding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && dy > 0 && !viewModel.state.value.loadingNextPage) {
                    viewModel.loadMoreActivities()
                }
                if (dy > 0) { // scrolling down
                    lifecycleScope.launch {
                        delay(5000)
                        binding.btnToTop.hideView()
                    }
                } else if (dy < 0) { // scrolling up
                    binding.btnToTop.showView()
                }
            }
        })
    }

    private fun initListener() {
        binding.iToolbar.ivBack.setOnClickListener {
            this.finish()
        }
        binding.btnToTop.setOnClickListener {
            binding.rvList.smoothScrollToPosition(0)
        }
        binding.iError.tvTryAgain.setOnClickListener {
            if (viewModel.state.value.error == NOT_FOUND) this.finish()
            else {
                viewModel.getData()
            }
        }
        binding.srlMain.setOnRefreshListener {
            if (binding.srlMain.isRefreshing) {
                viewModel.getData()
                lifecycleScope.launch {
                    delay(1000)
                    binding.srlMain.isRefreshing = false
                }
            }
        }
    }

    private fun showInitialLoading(state: Boolean) {
        if (state) {
            binding.llcMain.hideView()
            binding.iLoading.llcLoading.showView()
            return
        }
        binding.llcMain.showView()
        binding.iLoading.llcLoading.hideView()
    }

    private fun showErrorView(state: ReviewUiState) {
        if (!state.error.isNullOrEmpty()) {
            if (state.currentPage == 1 && state.error == NOT_FOUND || state.currentPage >= 1 && state.error != NOT_FOUND) {
                if (state.error == NOT_FOUND) binding.iError.tvTryAgain.text = getString(R.string.exit) else binding.iError.tvTryAgain.text = getString(R.string.try_again)
                binding.iError.llcError.showView()
                binding.llcMain.hideView()
                binding.iLoading.llcLoading.hideView()
                binding.iError.tvMessage.text = if (state.error == NOT_FOUND) getString(R.string.still_no_reviews) else state.error
                binding.iError.tvTryAgain.showView()
            }
            return
        }
        binding.iError.llcError.hideView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}