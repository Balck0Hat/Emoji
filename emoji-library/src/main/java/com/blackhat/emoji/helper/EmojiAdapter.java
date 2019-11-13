package com.blackhat.emoji.helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.Objects;

import com.blackhat.emoji.R;
import com.blackhat.emoji.emoji.Emojicon;

import org.jetbrains.annotations.NotNull;

class EmojiAdapter extends ArrayAdapter<Emojicon> {
    private boolean mUseSystemDefault;
    private EmojiconGridView.OnEmojiconClickedListener emojiClickListener;

    EmojiAdapter(Context context, List<Emojicon> data, boolean useSystemDefault) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = useSystemDefault;
    }

    EmojiAdapter(Context context, Emojicon[] data, boolean useSystemDefault) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = useSystemDefault;
    }


    void setEmojiClickListener(EmojiconGridView.OnEmojiconClickedListener listener) {
        this.emojiClickListener = listener;
    }

    @NotNull
    @Override
    public View getView(final int position, View convertView, @NotNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.emojicon_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = v.findViewById(R.id.emojicon_icon);
            holder.icon.setUseSystemDefault(mUseSystemDefault);

            v.setTag(holder);
        }

        Emojicon emoji = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.icon.setText(Objects.requireNonNull(emoji).getEmoji());
        holder.icon.setOnClickListener(v1 -> emojiClickListener.onEmojiconClicked(Objects.requireNonNull(getItem(position))));

        return v;
    }

    class ViewHolder {
        EmojiconTextView icon;
    }
}