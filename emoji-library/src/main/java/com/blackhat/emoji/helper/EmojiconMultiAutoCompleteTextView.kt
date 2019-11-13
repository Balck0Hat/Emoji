package com.blackhat.emoji.helper

import android.content.Context
import android.text.style.DynamicDrawableSpan
import android.util.AttributeSet
import android.widget.MultiAutoCompleteTextView

import com.blackhat.emoji.R

class EmojiconMultiAutoCompleteTextView : MultiAutoCompleteTextView {
    private var mEmojiconSize: Int = 0
    private var mEmojiconAlignment: Int = 0
    private var mEmojiconTextSize: Int = 0
    private var mUseSystemDefault = false

    constructor(context: Context) : super(context) {
        mEmojiconSize = textSize.toInt()
        mEmojiconTextSize = textSize.toInt()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Emojicon)
        mEmojiconSize = a.getDimension(R.styleable.Emojicon_emojiconSize, textSize).toInt()
        mEmojiconAlignment = a.getInt(R.styleable.Emojicon_emojiconAlignment, DynamicDrawableSpan.ALIGN_BASELINE)
        mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiconUseSystemDefault, false)
        a.recycle()
        mEmojiconTextSize = textSize.toInt()
        text = text
    }

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        updateText()
    }

    private fun updateText() {
        EmojiconHandler.addEmojis(context, text, mEmojiconSize, mEmojiconAlignment, mEmojiconTextSize, mUseSystemDefault)
    }

}
