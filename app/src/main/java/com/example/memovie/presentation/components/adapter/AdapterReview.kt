package com.example.memovie.presentation.components.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.memovie.R
import com.example.memovie.core.data.model.ReviewDataModel
import com.example.memovie.core.utils.IMAGE_URL
import com.example.memovie.databinding.ItemViewListSkeletonBinding
import com.example.memovie.databinding.ItemViewLoadingPagesBinding
import com.example.memovie.databinding.ListItemReviewBinding
import com.example.memovie.presentation.utils.DUMMY
import com.example.memovie.presentation.utils.RECYCLER_VIEW_LOADING
import com.example.memovie.presentation.utils.RECYCLER_VIEW_LOADING_NEXT
import com.example.memovie.presentation.utils.RECYCLER_VIEW_SUCCESS
import com.example.memovie.presentation.utils.convertDateFormat
import com.example.memovie.presentation.utils.hideView
import com.example.memovie.presentation.utils.invisible
import com.example.memovie.presentation.utils.loadImage
import com.example.memovie.presentation.utils.showView

class AdapterReview :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoading = false
    private var isNextLoading = false

    private val diffCallBack = object : DiffUtil.ItemCallback<ReviewDataModel>() {
        override fun areItemsTheSame(
            oldItem: ReviewDataModel,
            newItem: ReviewDataModel
        ): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(
            oldItem: ReviewDataModel,
            newItem: ReviewDataModel
        ): Boolean =
            oldItem == newItem

        override fun getChangePayload(
            oldItem: ReviewDataModel,
            newItem: ReviewDataModel
        ): Any {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallBack)

    inner class Loading(
        val parent: ViewGroup,
        private val binding: ItemViewListSkeletonBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                shimmerFrame.startShimmer()
            }
        }
    }

    inner class LoadingNextItem(
        val parent: ViewGroup,
        binding: ItemViewLoadingPagesBinding
    ) : RecyclerView.ViewHolder(binding.root)

    inner class Item(val parent: ViewGroup, private val binding: ListItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ReviewDataModel) {
            binding.apply {
                parent.context.let { ctx ->
                    /** set data to view **/
                    if (data.author == DUMMY) {
                        tvUserName.text = parent.context.getString(R.string.end_of_page)
                        profileImage.invisible()
                        tvReview.hideView()
                        return
                    }
                    profileImage.showView()
                    tvReview.showView()
                    tvUserName.text = ctx.getString(
                        R.string.authorName, data.author, convertDateFormat(
                            data.createdAt.toString(), "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy"
                        )
                    )
                    tvReview.text = data.content
                    if (!data.authorDetails?.avatarPath.isNullOrEmpty()) profileImage.loadImage(ctx, IMAGE_URL + data.authorDetails?.avatarPath)
                    else profileImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            ctx,
                            R.drawable.ic_profile
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
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
                return Item(
                    parent,
                    ListItemReviewBinding.inflate(
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
                holder.bind()
            }

            is LoadingNextItem -> {
                holder.adapterPosition
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size


    @SuppressLint("NotifyDataSetChanged")
    fun changeIsLoading(value: Boolean) {
        isNextLoading = value
        notifyDataSetChanged()
    }

}