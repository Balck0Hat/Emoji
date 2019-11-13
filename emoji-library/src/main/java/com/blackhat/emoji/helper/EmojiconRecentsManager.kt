package com.blackhat.emoji.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

import java.util.ArrayList
import java.util.StringTokenizer

import com.blackhat.emoji.emoji.Emojicon

class EmojiconRecentsManager private constructor(context: Context) : ArrayList<Emojicon>() {

    private val mContext: Context = context.applicationContext

    var recentPage: Int
        get() = preferences.getInt(PREF_PAGE, 0)
        set(page) = preferences.edit().putInt(PREF_PAGE, page).apply()

    private val preferences: SharedPreferences
        get() = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    init {
        loadRecents()
    }

    fun push(`object`: Emojicon) {
        // FIXME totally inefficient way of adding the emoji to the adapter
        // TODO this should be probably replaced by a deque
        if (contains(`object`)) {
            super.remove(`object`)
        }
        add(0, `object`)
    }

    private fun loadRecents() {
        val prefs = preferences
        val str = prefs.getString(PREF_RECENTS, "")
        val tokenizer = StringTokenizer(str, "~")
        while (tokenizer.hasMoreTokens()) {
            try {
                add(Emojicon(tokenizer.nextToken()))
            } catch (e: NumberFormatException) {
                // ignored
            }

        }
    }

    fun saveRecents() {
        val str = StringBuilder()
        val c = size
        for (i in 0 until c) {
            val e = get(i)
            str.append(e.emoji)
            if (i < c - 1) {
                str.append('~')
            }
        }
        val prefs = preferences
        prefs.edit().putString(PREF_RECENTS, str.toString()).apply()
    }

    companion object {

        private const val PREFERENCE_NAME = "emojicon"
        private const val PREF_RECENTS = "recent_emojis"
        private const val PREF_PAGE = "recent_page"

        private val LOCK = Any()
        @SuppressLint("StaticFieldLeak")
        private var sInstance: EmojiconRecentsManager? = null

        fun getInstance(context: Context): EmojiconRecentsManager {
            if (sInstance == null) {
                synchronized(LOCK) {
                    if (sInstance == null) {
                        sInstance = EmojiconRecentsManager(context)
                    }
                }
            }
            return this.sInstance!!
        }
    }

}
