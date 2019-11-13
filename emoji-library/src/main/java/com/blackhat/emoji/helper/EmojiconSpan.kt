package com.blackhat.emoji.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan

import java.lang.ref.WeakReference

internal class EmojiconSpan(private val mContext: Context, private val mResourceId: Int, size: Int, alignment: Int, private val mTextSize: Int) : DynamicDrawableSpan(alignment) {

    private val mSize: Int = size

    private var mHeight: Int = 0

    private var mWidth: Int = 0

    private var mTop: Int = 0

    private var mDrawable: Drawable? = null

    private var mDrawableRef: WeakReference<Drawable>? = null

    private val cachedDrawable: Drawable
        get() {
            if (mDrawableRef == null || mDrawableRef!!.get() == null) {
                mDrawableRef = WeakReference<Drawable>(drawable)
            }
            return mDrawableRef!!.get()!!
        }

    init {
        mHeight = mSize
        mWidth = mHeight
    }

    override fun getDrawable(): Drawable? {
        if (mDrawable == null) {
            try {
                mDrawable = mContext.resources.getDrawable(mResourceId)
                mHeight = mSize
                mWidth = mHeight * mDrawable!!.intrinsicWidth / mDrawable!!.intrinsicHeight
                mTop = (mTextSize - mHeight) / 2
                mDrawable!!.setBounds(0, mTop, mWidth, mTop + mHeight)
            } catch (e: Exception) {
                // swallow
            }

        }
        return mDrawable
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        //super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        val b = cachedDrawable
        canvas.save()

        var transY = bottom - b.bounds.bottom
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY = top + (bottom - top) / 2 - (b.bounds.bottom - b.bounds.top) / 2 - mTop
        }

        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }
}