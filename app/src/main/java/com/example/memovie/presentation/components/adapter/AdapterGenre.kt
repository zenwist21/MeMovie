package com.example.memovie.presentation.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.memovie.R
import com.example.memovie.core.data.model.GenreModel
import com.example.memovie.core.utils.NetworkConstant.NOT_FOUND
import com.example.memovie.databinding.ItemViewCategoriesBinding
import com.example.memovie.databinding.ItemViewGenreSkeletonBinding
import com.example.memovie.databinding.ItemViewListErrorBinding
import com.example.memovie.presentation.utils.RECYCLER_VIEW_ERROR
import com.example.memovie.presentation.utils.RECYCLER_VIEW_LOADING
import com.example.memovie.presentation.utils.RECYCLER_VIEW_SUCCESS
import com.example.memovie.presentation.utils.getDummyGenres

class AdapterGenre : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClick: ((GenreModel?) -> Unit)? = null
    private var isLoading = false
    private var isError = false
    private val diffCallBack = object : DiffUtil.ItemCallback<GenreModel>() {
        override fun areItemsTheSame(
            oldItem: GenreModel,
            newItem: GenreModel
        ): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: GenreModel,
            newItem: GenreModel
        ): Boolean =
            oldItem == newItem

        override fun getChangePayload(
            oldItem: GenreModel,
            newItem: GenreModel
        ): Any {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    inner class Loading(
        val parent: ViewGroup,
        binding: ItemViewGenreSkeletonBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class Item(val parent: ViewGroup, private val binding: ItemViewCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GenreModel) {
            binding.apply {
                parent.context.let { ctx ->
                    /** set data to view **/
                    llcMain.background = ContextCompat.getDrawable(
                        ctx,
                        if (data.isSelected) R.drawable.card_round_shape_grey_blue_stroke else R.drawable.card_round_shape_grey
                    )
                    tvGenre.text = data.name
                    llcMain.setOnClickListener {
                        onItemClick?.invoke(data)
                        return@setOnClickListener
                    }
                }
            }
        }
    }

    inner class ItemEmpty(val parent: ViewGroup, private val binding: ItemViewListErrorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GenreModel) {
            binding.apply {
                parent.context.let { ctx ->
                    /** set data to view **/
                    tvMessage.text =
                        if (data.message == NOT_FOUND) ctx.getString(R.string.no_data_exist)
                        else data.message
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            RECYCLER_VIEW_SUCCESS -> {
                return Item(
                    parent,
                    ItemViewCategoriesBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            RECYCLER_VIEW_LOADING -> {
                return Loading(
                    parent,
                    ItemViewGenreSkeletonBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                return ItemEmpty(
                    parent,
                    ItemViewListErrorBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) {
            RECYCLER_VIEW_LOADING
        } else if (isError) {
            RECYCLER_VIEW_ERROR
        } else {
            RECYCLER_VIEW_SUCCESS
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Item -> {
                holder.bind(differ.currentList[position])
            }

            is Loading -> {
                holder.adapterPosition
            }

            is ItemEmpty -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size


    fun setViewLoading(state: Boolean) {
        isLoading = state
        if (isLoading) differ.submitList(getDummyGenres())
    }

    fun setViewError(state: Boolean, message: String? = "") {
        isError = state
        if (isError) {
            differ.submitList(mutableListOf(GenreModel(message = message)))
        }
    }


    fun setOnClickListener(listener: (GenreModel?) -> Unit) {
        onItemClick = listener
    }

}