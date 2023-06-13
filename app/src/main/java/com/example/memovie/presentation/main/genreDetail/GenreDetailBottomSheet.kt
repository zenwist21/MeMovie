package com.example.memovie.presentation.main.genreDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memovie.R
import com.example.memovie.core.data.model.GenreModel
import com.example.memovie.databinding.ViewGenreBottomSheetBinding
import com.example.memovie.presentation.components.adapter.AdapterGenre
import com.example.memovie.presentation.main.GenreState
import com.example.memovie.presentation.utils.setGenre
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GenreDetailBottomSheet(
    private var state: GenreState = GenreState(),
    private val onClick: (Any?) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: ViewGenreBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val adapterGenre by lazy { AdapterGenre() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewGenreBottomSheetBinding.inflate(inflater, container, false)
        initController()
        return binding.root
    }

    private fun initController() {
        initUI()
        initListener()
    }

    private fun initListener() {
        binding.apply {
            adapterGenre.setOnClickListener { data ->
                setOrRemoveSelectedPosition(data ?: GenreModel())
            }
            tvClearSelected.setOnClickListener {
                clearSelection()
            }
            btnConfirm.setOnClickListener {
                onClick.invoke(state)
                this@GenreDetailBottomSheet.dismiss()
            }
        }
    }

    private fun initUI() {
        binding.rvGenres.adapter = adapterGenre
        binding.rvGenres.layoutManager = GridLayoutManager(requireActivity(), 2)
        adapterGenre.differ.submitList(state.listGenre)
    }

    override fun getTheme() = R.style.BottomSheetRoundedDialogTheme

    private fun updateStateList(
        data: GenreState
    ) {
        state = state.copy(
            listGenre = data.listGenre,
            selectedGenre = data.selectedGenre
        )
    }

    private fun setOrRemoveSelectedPosition(data: GenreModel) {
        val listSelected = mutableListOf<GenreModel>()
        val temp = mutableListOf<GenreModel>()
        listSelected.addAll(state.listGenre.filter { it.isSelected })
        temp.addAll(state.listGenre.filter { !it.isSelected })
        if (listSelected.isNotEmpty()) {
            if (listSelected.contains(data)) {
                listSelected.remove(data)
                temp.add(data.copy(isSelected = false))
            } else {
                listSelected.add(data.copy(isSelected = true))
                temp.remove(data)
            }
        } else {
            listSelected.add(data.copy(isSelected = true))
            temp.remove(data)
        }
        adapterGenre.differ.submitList(listSelected + temp)
        updateStateList(
            state.copy(
                listGenre = listSelected + temp,
                selectedGenre = setGenre(listSelected + temp)
            )
        )
    }

    private fun clearSelection() {
      lifecycleScope.launch {
          adapterGenre.differ.submitList(adapterGenre.differ.currentList.map { it.copy(isSelected = false) }.sortedBy { it.name })
          delay(200)
          updateStateList(data = state.copy(listGenre = adapterGenre.differ.currentList, selectedGenre = null))
      }
    }
}