package com.example.memovie.presentation.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.memovie.R
import com.example.memovie.core.data.model.TmDbModel
import com.example.memovie.core.utils.IMAGE_URL
import com.example.memovie.core.utils.NetworkConstant.NOT_FOUND
import com.example.memovie.databinding.ActivityDetailBinding
import com.example.memovie.presentation.components.adapter.AdapterGenre
import com.example.memovie.presentation.review.ReviewActivity
import com.example.memovie.presentation.utils.hideView
import com.example.memovie.presentation.utils.itemID
import com.example.memovie.presentation.utils.itemTYPE
import com.example.memovie.presentation.utils.loadImage
import com.example.memovie.presentation.utils.showView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailActivityViewModel by viewModels()
    private val adapterGenre: AdapterGenre by lazy { AdapterGenre() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
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

    private fun initAdapter() {
        binding.rvGenre.adapter = adapterGenre
    }

    private fun initListener() {
        binding.iToolbar.ivBack.setOnClickListener {
            this.finish()
        }
        binding.btnYt.setOnClickListener {
            when (viewModel.state.value.type) {
                DetailType.MOVIE -> {
                    viewModel.getMovieVideos()
                }

                else -> {
                    viewModel.getTvVideos()
                }
            }
        }
        binding.iError.tvTryAgain.setOnClickListener {
            viewModel.getData()
        }
        binding.tvSeeReview.setOnClickListener {
            Intent(this, ReviewActivity::class.java).also {
                it.putExtra(itemID, viewModel.state.value.itemId)
                it.putExtra(itemTYPE, viewModel.state.value.type)
                startActivity(it)
            }
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest {state->
                    lifecycleScope.launch {
                        showInitialLoading(state.initialLoading)
                        loadingMovies(state)
                    }
                    lifecycleScope.launch {
                        setToView(state.result)
                    }
                    lifecycleScope.launch {
                        showErrorView(state)
                    }
                }
            }
        }
    }

    private fun loadingMovies(state: DetailUIState) {
        if (state.loadingMovies) {
            binding.progressBar.showView()
            return
        }
        if (state.errorVideo.isNullOrEmpty() && state.resultVideo.isNotEmpty()) {
            binding.progressBar.hideView()
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("vnd.youtube://${state.resultVideo[0].key}")
            )
            startActivity(intent)
            viewModel.clearLink()
            return
        }
        if (!state.errorVideo.isNullOrEmpty()) {
            binding.progressBar.hideView()
            Toast.makeText(
                this@DetailActivity,
                if (state.errorVideo == NOT_FOUND) getString(R.string.no_video)
                else state.errorVideo.toString().ifEmpty { getString(R.string.unknown_error) },
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        binding.progressBar.hideView()
    }


    private fun setToView(result: TmDbModel?) {
        if (result == null) return
        binding.apply {
            tvTitle.text = if (!result.title.isNullOrEmpty()) result.title else result.name
            tvDescription.text = result.overview
            imageView.loadImage(
                this@DetailActivity,
                IMAGE_URL + result.backdropPath
            )
            profileImage.loadImage(
                this@DetailActivity,
                IMAGE_URL + result.posterPath
            )
            adapterGenre.differ.submitList(result.genres)
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

    private fun showErrorView(state: DetailUIState) {
        if (!state.errorResult.isNullOrEmpty()) {
            binding.iError.llcError.showView()
            binding.llcMain.hideView()
            binding.iLoading.llcLoading.hideView()
            binding.iError.tvMessage.text = state.errorResult
            binding.iError.tvTryAgain.showView()
            return
        }
        binding.iError.llcError.hideView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}