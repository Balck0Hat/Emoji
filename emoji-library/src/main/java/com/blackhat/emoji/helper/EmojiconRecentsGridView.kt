package com.blackhat.emoji.helper

import android.content.Context
import android.widget.GridView

import com.blackhat.emoji.R
import com.blackhat.emoji.emoji.Emojicon


class EmojiconRecentsGridView internal
constructor(context: Context, emojicons: Array<Emojicon>?, recents: EmojiconRecents, emojiconsPopup: EmojiconsPopup,
            useSystemDefault: Boolean) : EmojiconGridView(context, emojicons, recents, emojiconsPopup, useSystemDefault), EmojiconRecents {

    private val mAdapter: EmojiAdapter?

    init {
        val recents1 = EmojiconRecentsManager
                .getInstance(rootView.context)
        mAdapter = EmojiAdapter(rootView.context, recents1, useSystemDefault)
        mAdapter.setEmojiClickListener(object : OnEmojiconClickedListener {
            override fun onEmojiconClicked(emojicon: Emojicon) {
                if (mEmojiconPopup.onEmojiconClickedListener != null) {
                    mEmojiconPopup.onEmojiconClickedListener!!.onEmojiconClicked(emojicon)
                }
            }
        })
        val gridView = rootView.findViewById<GridView>(R.id.Emoji_GridView)
        gridView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    override fun addRecentEmoji(context: Context, emojicon: Emojicon) {
        val recents = EmojiconRecentsManager.getInstance(context)
        recents.push(emojicon)

        // notify dataset changed
        mAdapter?.notifyDataSetChanged()
    }

}