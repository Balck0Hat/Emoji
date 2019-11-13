package com.blackhat.emoji.actions;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.blackhat.emoji.helper.EmojiconEditText;
import com.blackhat.emoji.helper.EmojiconsPopup;
import com.blackhat.emoji.R;


public class EmojIconActions implements View.OnFocusChangeListener {

    private boolean useSystemEmoji = false;
    private EmojiconsPopup popup;
    private Context context;
    private ImageView emojiButton;
    private int KeyBoardIcon = R.drawable.ic_action_keyboard;
    private int SmileyIcons = R.drawable.smiley;
    private KeyboardListener keyboardListener;
    private List<EmojiconEditText> emojiconEditTextList = new ArrayList<>();
    private EmojiconEditText emojiconEditText;


    public EmojIconActions(Context ctx, View rootView, EmojiconEditText emojiconEditText, ImageView emojiButton) {
        this.emojiButton = emojiButton;
        this.context = ctx;
        addEmojiconEditTextList(emojiconEditText);
        this.popup = new EmojiconsPopup(rootView, ctx, useSystemEmoji);
    }

    public void addEmojiconEditTextList(EmojiconEditText... emojiconEditText) {
        Collections.addAll(emojiconEditTextList, emojiconEditText);
        for (EmojiconEditText editText : emojiconEditText) {
            editText.setOnFocusChangeListener(this);
        }
    }

    public EmojIconActions(Context ctx, View rootView, EmojiconEditText emojiconEditText,
                           ImageView emojiButton, String iconPressedColor, String tabsColor,
                           String backgroundColor) {
        addEmojiconEditTextList(emojiconEditText);
        this.emojiButton = emojiButton;
        this.context = ctx;
        this.popup = new EmojiconsPopup(rootView, ctx, useSystemEmoji, iconPressedColor,
                tabsColor, backgroundColor);
    }

    public void setIconsIds(int keyboardIcon, int smileyIcon) {
        this.KeyBoardIcon = keyboardIcon;
        this.SmileyIcons = smileyIcon;
    }

    public void setUseSystemEmoji(boolean useSystemEmoji) {
        this.useSystemEmoji = useSystemEmoji;
        for (EmojiconEditText editText : emojiconEditTextList) {
            editText.setUseSystemDefault(useSystemEmoji);
        }
        refresh();
    }


    private void refresh() {
        popup.updateUseSystemDefault(useSystemEmoji);
    }

    public void ShowEmojIcon() {
        if (emojiconEditText == null)
            emojiconEditText = emojiconEditTextList.get(0);
        popup.setSizeForSoftKeyboard();

        popup.setOnDismissListener(() -> changeEmojiKeyboardIcon(emojiButton, SmileyIcons));

        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup
                .OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {
                if (keyboardListener != null)
                    keyboardListener.onKeyboardOpen();
            }

            @Override
            public void onKeyboardClose() {
                if (keyboardListener != null)
                    keyboardListener.onKeyboardClose();
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(emojicon -> {

            int start = emojiconEditText.getSelectionStart();
            int end = emojiconEditText.getSelectionEnd();
            if (start < 0) {
                emojiconEditText.append(emojicon.getEmoji());
            } else {
                emojiconEditText.getText().replace(Math.min(start, end),
                        Math.max(start, end), emojicon.getEmoji(), 0,
                        emojicon.getEmoji().length());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(v -> {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            emojiconEditText.dispatchKeyEvent(event);
        });

        showForEditText();
    }

    private void showForEditText() {

        emojiButton.setOnClickListener(v -> {
            if (emojiconEditText == null)
                emojiconEditText = emojiconEditTextList.get(0);
            if (!popup.isShowing()) {

                if (popup.isKeyBoardOpen()) {
                    popup.showAtBottom();
                    changeEmojiKeyboardIcon(emojiButton, KeyBoardIcon);
                } else {
                    emojiconEditText.setFocusableInTouchMode(true);
                    emojiconEditText.requestFocus();
                    final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(inputMethodManager).showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                    popup.showAtBottomPending();
                    changeEmojiKeyboardIcon(emojiButton, KeyBoardIcon);
                }
            } else {
                popup.dismiss();
            }


        });
    }

    public void closeEmojIcon() {
        if (popup != null && popup.isShowing())
            popup.dismiss();
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            if (view instanceof EmojiconEditText) {
                emojiconEditText = (EmojiconEditText) view;
            }
        }
    }


    public interface KeyboardListener {
        void onKeyboardOpen();

        void onKeyboardClose();
    }

    public void setKeyboardListener(KeyboardListener listener) {
        this.keyboardListener = listener;
    }

}