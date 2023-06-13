package com.example.memovie.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.memovie.R
import com.example.memovie.core.data.model.GenreModel
import com.example.memovie.core.data.model.TmDbModel
import com.example.memovie.databinding.ViewMainAppBarBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import java.util.TimerTask

fun getDummyMovie(): MutableList<TmDbModel> {
    return mutableListOf(
        TmDbModel(),
        TmDbModel(),
        TmDbModel(),
        TmDbModel(),
    )
}

fun getDummyGenres(): MutableList<GenreModel> {
    return mutableListOf(
        GenreModel(
            id = 1,
            name = "",
            message = "",
            isSelected = false
        ),
        GenreModel(
            id = 1,
            name = "",
            message = "",
            isSelected = false
        ),
        GenreModel(
            id = 1,
            name = "",
            message = "",
            isSelected = false
        ),
        GenreModel(
            id = 1,
            name = "",
            message = "",
            isSelected = false
        ),
        GenreModel(
            id = 1,
            name = "",
            message = "",
            isSelected = false
        )

    )
}

fun ImageView.loadImage(context: Context, url: String) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.ic_dummy_background)
        .error(R.color.colorSoftGrey)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .thumbnail(
            Glide.with(context)
                .load(R.drawable.ic_dummy_background)
        )
        .into(this)

}

fun setGenre(data: List<GenreModel>): List<Int> {
    val temp: MutableList<Int> = mutableListOf()
    mutableListOf<GenreModel>().let {
        it.addAll(data.filter { data -> data.isSelected })
        if (it.isNotEmpty()) temp.addAll(it.map { data -> data.id ?: 0 })
    }
    return temp
}

fun View.showView() {
    visibility = View.VISIBLE
}

fun View.hideView() {
    visibility = View.GONE
}
fun View.invisible() {
    visibility = View.INVISIBLE
}

@SuppressLint("SimpleDateFormat")
fun convertDateFormat(
    current: String,
    oldFormat: String, newFormat: String
): String {
    return try {
        var dateFormat = SimpleDateFormat(oldFormat)
        val newDate = dateFormat.parse(current)
        dateFormat = SimpleDateFormat(newFormat)
        dateFormat.format(newDate ?: Date())
    } catch (e: Exception) {
        ""
    }

}

private fun View.toggleAnimation() {
    val transition: Transition = Slide(Gravity.BOTTOM)
    transition.duration = 300
    transition.addTarget(this)
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup?, transition)
    this.visibility = if (this.visibility == View.GONE) View.VISIBLE else View.GONE
}

fun ViewMainAppBarBinding.setToolbarEvent(
    searchHint: Int = R.string.search_movies,
    unit: (() -> Unit)? = null
) {
    tilSearch.hint = this.root.context.getString(searchHint)
    ivSearch.setOnClickListener {
        llcSearchBar.toggleAnimation()
        unit?.invoke()
    }
}

fun scheduledEvent(timer: Timer, event: (() -> Unit)? = null) {
    var pTimer = timer
    pTimer.cancel()
    pTimer = Timer()
    pTimer.schedule(object : TimerTask() {
        override fun run() {
            event?.invoke()
        }
    }, 1000)
}


fun EditText.textListener(
    beforeOnTextChanged: ((s: CharSequence?) -> Unit)? = null,
    isOnTextChanged: ((s: CharSequence?) -> Unit)? = null,
    isAfterTextChanged: ((s: Editable?) -> Unit)? = null
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (beforeOnTextChanged == null) return
            beforeOnTextChanged.invoke(s)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isOnTextChanged == null) return
            isOnTextChanged.invoke(s)
        }

        override fun afterTextChanged(s: Editable?) {
            if (isAfterTextChanged == null) return
            isAfterTextChanged.invoke(s)
        }

    })
}

