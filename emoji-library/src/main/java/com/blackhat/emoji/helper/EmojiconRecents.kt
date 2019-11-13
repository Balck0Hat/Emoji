package com.blackhat.emoji.helper

import android.content.Context

import com.blackhat.emoji.emoji.Emojicon

interface EmojiconRecents {
    fun addRecentEmoji(context: Context, emojicon: Emojicon)
}