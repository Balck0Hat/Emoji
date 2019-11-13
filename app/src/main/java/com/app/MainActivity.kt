package com.app

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView

import com.blackhat.emoji.actions.EmojIconActions
import com.blackhat.emoji.helper.EmojiconEditText
import com.blackhat.emoji.helper.EmojiconTextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emojIcon = EmojIconActions(this, root_view, emojicon_edit_text, emoji_btn)
        emojIcon.ShowEmojIcon()
        emojIcon.setKeyboardListener(object : EmojIconActions.KeyboardListener {
            override fun onKeyboardOpen() {
                Log.e("Keyboard", "open")
            }

            override fun onKeyboardClose() {
                Log.e("Keyboard", "close")
            }
        })

        use_system_default.setOnCheckedChangeListener { _, b ->
            emojIcon.setUseSystemEmoji(b)
            textView.setUseSystemDefault(b)
        }
        emojIcon.addEmojiconEditTextList(emojicon_edit_text2)

        submit_btn.setOnClickListener {
            val newText = emojicon_edit_text.text.toString()
            textView.text = newText
        }
    }


}
