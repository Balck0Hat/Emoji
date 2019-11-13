package com.blackhat.emoji.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow

import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

import java.util.Arrays
import java.util.Objects

import com.blackhat.emoji.R
import com.blackhat.emoji.emoji.Cars
import com.blackhat.emoji.emoji.Electr
import com.blackhat.emoji.emoji.Emojicon
import com.blackhat.emoji.emoji.Food
import com.blackhat.emoji.emoji.Nature
import com.blackhat.emoji.emoji.People
import com.blackhat.emoji.emoji.Sport
import com.blackhat.emoji.emoji.Symbols

class EmojiconsPopup : PopupWindow, ViewPager.OnPageChangeListener, EmojiconRecents {
    private var mEmojiTabLastSelectedIndex = -1
    private lateinit var mEmojiTabs: Array<View>
    private var mEmojisAdapter: PagerAdapter? = null
    private var mRecentsManager: EmojiconRecentsManager? = null
    private var keyBoardHeight = 0
    private var pendingOpen: Boolean? = false
    var isKeyBoardOpen: Boolean? = false
        private set
    var onEmojiconClickedListener: EmojiconGridView.OnEmojiconClickedListener? = null

    private var onEmojiconBackspaceClickedListener: OnEmojiconBackspaceClickedListener? = null
    private var onSoftKeyboardOpenCloseListener: OnSoftKeyboardOpenCloseListener? = null
    private var rootView: View? = null
    private var mContext: Context? = null
    private var mUseSystemDefault: Boolean = false
    private var view: View? = null
    private var iconPressedColor = "#495C66"
    private var tabsColor = "#DCE1E2"
    private var backgroundColor = "#E6EBEF"

    private var emojisPager: ViewPager? = null

    private val usableScreenHeight: Int
        get() {
            val metrics = DisplayMetrics()

            val windowManager = mContext!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            Objects.requireNonNull(windowManager).defaultDisplay.getMetrics(metrics)

            return metrics.heightPixels

        }

    constructor(rootView: View, mContext: Context, useSystemDefault: Boolean, iconPressedColor: String, tabsColor: String, backgroundColor: String) : super(mContext) {
        this.backgroundColor = backgroundColor
        this.iconPressedColor = iconPressedColor
        this.tabsColor = tabsColor
        this.mUseSystemDefault = useSystemDefault
        this.mContext = mContext
        this.rootView = rootView
        val customView = createCustomView()
        contentView = customView
        softInputMode = LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        setSize(255)
        setBackgroundDrawable(null)


    }

    constructor(rootView: View, mContext: Context, useSystemDefault: Boolean) : super(mContext) {
        this.mUseSystemDefault = useSystemDefault
        this.mContext = mContext
        this.rootView = rootView
        val customView = createCustomView()
        contentView = customView
        softInputMode = LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        setSize(255)
        setBackgroundDrawable(null)

    }

    fun setOnSoftKeyboardOpenCloseListener(listener: OnSoftKeyboardOpenCloseListener) {
        this.onSoftKeyboardOpenCloseListener = listener
    }

    fun setOnEmojiconBackspaceClickedListener(listener: OnEmojiconBackspaceClickedListener) {
        this.onEmojiconBackspaceClickedListener = listener
    }

    fun showAtBottom() {
        showAtLocation(rootView, Gravity.BOTTOM, 0, 0)
    }

    fun showAtBottomPending() {
        if (isKeyBoardOpen!!)
            showAtBottom()
        else
            pendingOpen = true
    }

    override fun dismiss() {
        super.dismiss()
        EmojiconRecentsManager
                .getInstance(mContext!!).saveRecents()
    }

    fun setSizeForSoftKeyboard() {
        rootView!!.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView!!.getWindowVisibleDisplayFrame(r)

            val screenHeight = usableScreenHeight
            var heightDifference = screenHeight - (r.bottom - r.top)
            val resourceId = mContext!!.resources
                    .getIdentifier("status_bar_height",
                            "dimen", "android")
            if (resourceId > 0) {
                heightDifference -= mContext!!.resources
                        .getDimensionPixelSize(resourceId)
            }
            if (heightDifference > 100) {
                keyBoardHeight = heightDifference
                setSize(keyBoardHeight)
                if ((!isKeyBoardOpen!!)) {
                    if (onSoftKeyboardOpenCloseListener != null)
                        onSoftKeyboardOpenCloseListener!!.onKeyboardOpen(keyBoardHeight)
                }
                isKeyBoardOpen = true
                if (pendingOpen!!) {
                    showAtBottom()
                    pendingOpen = false
                }
            } else {
                isKeyBoardOpen = false
                if (onSoftKeyboardOpenCloseListener != null)
                    onSoftKeyboardOpenCloseListener!!.onKeyboardClose()
            }
        }
    }

    private fun setSize(height: Int) {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        setHeight(height)
    }

    fun updateUseSystemDefault(mUseSystemDefault: Boolean) {
        if (view != null) {
            mEmojisAdapter = null
            val positionPager = emojisPager!!.currentItem
            dismiss()

            this.mUseSystemDefault = mUseSystemDefault
            contentView = createCustomView()
            //mEmojisAdapter.notifyDataSetChanged();
            mEmojiTabs[positionPager].isSelected = true
            emojisPager!!.currentItem = positionPager
            onPageSelected(positionPager)
            if (!isShowing) {

                //If keyboard is visible, simply show the emoji popup
                if (isKeyBoardOpen!!) {
                    showAtBottom()
                    // changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                } else {
                    showAtBottomPending()
                    // changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                }//else, open the text keyboard first and immediately after that show the emoji popup
            }


        }
    }


    @SuppressLint("InflateParams")
    private fun createCustomView(): View {
        val inflater = mContext!!.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = Objects.requireNonNull(inflater).inflate(R.layout.emojicons, null, false)
        emojisPager = view!!.findViewById(R.id.emojis_pager)
        val tabs = view!!.findViewById<LinearLayout>(R.id.emojis_tab)

        emojisPager!!.addOnPageChangeListener(this)
        mEmojisAdapter = EmojisPagerAdapter(
                Arrays.asList(
                        EmojiconRecentsGridView(mContext!!, null, this, this, mUseSystemDefault),
                        EmojiconGridView(mContext!!, People.DATA, this, this, mUseSystemDefault),
                        EmojiconGridView(mContext!!, Nature.DATA, this, this, mUseSystemDefault),
                        EmojiconGridView(mContext!!, Food.DATA, this, this, mUseSystemDefault),
                        EmojiconGridView(mContext!!, Sport.DATA, this, this, mUseSystemDefault),
                        EmojiconGridView(mContext!!, Cars.DATA, this, this, mUseSystemDefault),
                        EmojiconGridView(mContext!!, Electr.DATA, this, this, mUseSystemDefault),
                        EmojiconGridView(mContext!!, Symbols.DATA, this, this, mUseSystemDefault)

                )
        )
        emojisPager!!.adapter = mEmojisAdapter
        mEmojiTabs = arrayOf(
                view!!.findViewById(R.id.emojis_tab_0_recents),
                view!!.findViewById(R.id.emojis_tab_1_people),
                view!!.findViewById(R.id.emojis_tab_2_nature),
                view!!.findViewById(R.id.emojis_tab_3_food),
                view!!.findViewById(R.id.emojis_tab_4_sport),
                view!!.findViewById(R.id.emojis_tab_5_cars),
                view!!.findViewById(R.id.emojis_tab_6_elec),
                view!!.findViewById(R.id.emojis_tab_7_sym)
        )

//        mEmojiTabs[0] = view!!.findViewById(R.id.emojis_tab_0_recents)
//        mEmojiTabs[1] = view!!.findViewById(R.id.emojis_tab_1_people)
//        mEmojiTabs[2] = view!!.findViewById(R.id.emojis_tab_2_nature)
//        mEmojiTabs[3] = view!!.findViewById(R.id.emojis_tab_3_food)
//        mEmojiTabs[4] = view!!.findViewById(R.id.emojis_tab_4_sport)
//        mEmojiTabs[5] = view!!.findViewById(R.id.emojis_tab_5_cars)
//        mEmojiTabs[6] = view!!.findViewById(R.id.emojis_tab_6_elec)
//        mEmojiTabs[7] = view!!.findViewById(R.id.emojis_tab_7_sym)
        for (i in mEmojiTabs.indices) {
            mEmojiTabs[i].setOnClickListener { v -> emojisPager!!.currentItem = i }
        }


        emojisPager!!.setBackgroundColor(Color.parseColor(backgroundColor))
        tabs.setBackgroundColor(Color.parseColor(tabsColor))
        for (mEmojiTab in mEmojiTabs!!) {
            val btn = mEmojiTab as ImageButton
            btn.setColorFilter(Color.parseColor(iconPressedColor))
        }

        val imgBtn = view!!.findViewById<ImageButton>(R.id.emojis_backspace)
        imgBtn.setColorFilter(Color.parseColor(iconPressedColor))
        imgBtn.setBackgroundColor(Color.parseColor(backgroundColor))


        view!!.findViewById<View>(R.id.emojis_backspace).setOnTouchListener(RepeatListener(500, 50, OnClickListener { v ->
            if (onEmojiconBackspaceClickedListener != null)
                v?.let { onEmojiconBackspaceClickedListener!!.onEmojiconBackspaceClicked(it) }
        }))

        // get last selected page
        mRecentsManager = EmojiconRecentsManager.getInstance(view!!.context)
        var page = mRecentsManager!!.recentPage
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (page == 0 && mRecentsManager!!.size == 0) {
            page = 1
        }

        if (page == 0) {
            onPageSelected(page)
        } else {
            emojisPager!!.setCurrentItem(page, false)
        }
        return view as View
    }

    override fun addRecentEmoji(context: Context, emojicon: Emojicon) {
        val fragment = (Objects.requireNonNull<PagerAdapter>(emojisPager!!.adapter) as EmojisPagerAdapter).recentFragment
        fragment!!.addRecentEmoji(context, emojicon)
    }


    override fun onPageScrolled(i: Int, v: Float, i2: Int) {}

    override fun onPageSelected(i: Int) {
        if (mEmojiTabLastSelectedIndex == i) {
            return
        }
        when (i) {
            0, 1, 2, 3, 4, 5, 6, 7 -> {

                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs!!.size) {
                    mEmojiTabs!![mEmojiTabLastSelectedIndex].isSelected = false
                }
                mEmojiTabs!![i].isSelected = true
                mEmojiTabLastSelectedIndex = i
                mRecentsManager!!.recentPage = i
            }
        }
    }

    override fun onPageScrollStateChanged(i: Int) {}

    private class EmojisPagerAdapter internal constructor(private val views: List<EmojiconGridView>) : PagerAdapter() {

        internal val recentFragment: EmojiconRecentsGridView?
            get() {
                for (it in views) {
                    if (it is EmojiconRecentsGridView)
                        return it
                }
                return null
            }

        override fun getCount(): Int {
            return views.size
        }


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val v = views[position].rootView
            container.addView(v, 0)
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            container.removeView(view as View)
        }

        override fun isViewFromObject(view: View, key: Any): Boolean {
            return key === view
        }
    }

    class RepeatListener internal constructor(private val initialInterval: Int, private val normalInterval: Int, private val clickListener: OnClickListener?) : View.OnTouchListener {

        private val handler = Handler()

        private val handlerRunnable = object : Runnable {
            override fun run() {
                if (downView == null) {
                    return
                }
                handler.removeCallbacksAndMessages(downView)
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval)
                clickListener?.onClick(downView)
            }
        }

        private var downView: View? = null

        init {
            requireNotNull(clickListener) { "null runnable" }
            require(!(initialInterval < 0 || normalInterval < 0)) { "negative interval" }
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    downView = view
                    handler.removeCallbacks(handlerRunnable)
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval)
                    clickListener!!.onClick(view)
                    return true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> {
                    handler.removeCallbacksAndMessages(downView)
                    downView = null
                    return true
                }
            }
            return false
        }
    }

    interface OnEmojiconBackspaceClickedListener {
        fun onEmojiconBackspaceClicked(v: View)
    }

    interface OnSoftKeyboardOpenCloseListener {
        fun onKeyboardOpen(keyBoardHeight: Int)

        fun onKeyboardClose()
    }
}