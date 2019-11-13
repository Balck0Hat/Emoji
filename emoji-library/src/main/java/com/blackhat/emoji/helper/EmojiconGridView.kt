package com.blackhat.emoji.helper

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.GridView

import com.blackhat.emoji.R
import com.blackhat.emoji.emoji.Emojicon
import com.blackhat.emoji.emoji.People

import java.util.Objects

open class EmojiconGridView internal constructor(context: Context, emojicons: Array<Emojicon>?, recents: EmojiconRecents, var mEmojiconPopup: EmojiconsPopup, useSystemDefault: Boolean) {
    var rootView: View
    private var mRecents: EmojiconRecents? = null
    private var mData: Array<Emojicon>? = null


    init {
        val inflater = context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        rootView = Objects.requireNonNull(inflater).inflate(R.layout.emojicon_grid, null)
        setRecents(recents)
        val gridView = rootView.findViewById<GridView>(R.id.Emoji_GridView)
        if (emojicons == null) {
            mData = People.DATA
        } else {
            mData = emojicons.clone()
        }
        val mAdapter = EmojiAdapter(rootView.context, mData, useSystemDefault)
        mAdapter.setEmojiClickListener(object : OnEmojiconClickedListener {
            override fun onEmojiconClicked(emojicon: Emojicon) {
                if (mEmojiconPopup.onEmojiconClickedListener != null) {
                    mEmojiconPopup.onEmojiconClickedListener!!.onEmojiconClicked(emojicon)
                }
                if (mRecents != null) {
                    mRecents!!.addRecentEmoji(rootView.context, emojicon)
                }
            }
        })
        gridView.adapter = mAdapter
    }

    private fun setRecents(recents: EmojiconRecents) {
        mRecents = recents
    }

    interface OnEmojiconClickedListener {
        fun onEmojiconClicked(emojicon: Emojicon)
    }

}