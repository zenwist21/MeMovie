package com.example.memovie.presentation.components.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.memovie.R
import com.example.memovie.core.data.model.TmDbModel
import com.example.memovie.core.utils.IMAGE_URL
import com.example.memovie.core.utils.NetworkConstant.NOT_FOUND
import com.example.memovie.databinding.ItemViewListBinding
import com.example.memovie.databinding.ItemViewListErrorBinding
import com.example.memovie.databinding.ItemViewListSkeletonBinding
import com.example.memovie.databinding.ItemViewLoadingPagesBinding
import com.example.memovie.presentation.utils.RECYCLER_VIEW_ERROR
import com.example.memovie.presentation.utils.RECYCLER_VIEW_LOADING
import com.example.memovie.presentation.utils.RECYCLER_VIEW_LOADING_NEXT
import com.example.memovie.presentation.utils.RECYCLER_VIEW_SUCCESS
import com.example.memovie.presentation.utils.hideView
import com.example.memovie.presentation.utils.loadImage
import com.example.memovie.presentation.utils.showView

class MovieAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClick: ((Any?) -> Unit)? = null
    private var isLoading = false
    private var isNextLoading = false
    private var isError = false

    private val diffCallBack = object : DiffUtil.ItemCallback<TmDbModel>() {
        override fun areItemsTheSame(
            oldItem: TmDbModel,
            newItem: TmDbModel
        ): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: TmDbModel,
            newItem: TmDbModel
        ): Boolean =
            oldItem == newItem

        override fun getChangePayload(
            oldItem: TmDbModel,
            newItem: TmDbModel
        ): Any {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    inner class Loading(
        val parent: ViewGroup,
        binding: ItemViewListSkeletonBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class LoadingNextItem(
        val parent: ViewGroup,
        binding: ItemViewLoadingPagesBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class Item(private val parent: ViewGroup, private val binding: ItemViewListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TmDbModel) {
            binding.apply {
                parent.context.let {
                    /** set data to view **/
                    tvTitle.text =
                        if (data.title.isNullOrEmpty()) data.originalName else data.title
                    ivPoster.loadImage(it, IMAGE_URL + data.posterPath)
                    cvMain.setOnClickListener {
                        onItemClick?.invoke(data)
                    }
                }
            }
        }
    }

    inner class ItemEmpty(
        private val parent: ViewGroup,
        private val binding: ItemViewListErrorBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TmDbModel) {
            binding.apply {
                parent.context.let { ctx ->
                    /** set data to view **/
                    lavLoading.hideView()
                    llcError.showView()
                    tvMessage.text = if (data.message == NOT_FOUND) ctx.getString(R.string.no_data_exist)
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
                    ItemViewListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            RECYCLER_VIEW_LOADING -> {
                return Loading(
                    parent,
                    ItemViewListSkeletonBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            RECYCLER_VIEW_LOADING_NEXT -> {
                return LoadingNextItem(
                    parent,
                    ItemViewLoadingPagesBinding.inflate(
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
        } else if (position == differ.currentList.lastIndex && isNextLoading) {
            RECYCLER_VIEW_LOADING_NEXT
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

            is LoadingNextItem -> {
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
        notifyItemRangeChanged(0, differ.currentList.size)
    }


    fun setViewError(state: Boolean, message: String? = "") {
        isError = state
        if (isError) {
            differ.submitList(mutableListOf(TmDbModel(message = message)))
        }
    }

    fun setOnClickListener(listener: (Any?) -> Unit) {
        onItemClick = listener
    }
}