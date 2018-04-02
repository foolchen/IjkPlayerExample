package com.foolchen.ijkplayer.example.widget.media

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.widget.AppCompatImageButton
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.*
import com.xcar.ijkplayerexample.R

/**
 * 自定义的播放器控制器
 *
 * @author chenchong
 * 2018/3/29
 * 上午10:06
 */
class IjkMediaController(context: Context) : FrameLayout(context), IMediaController {

    private val sDefaultTimeout = 3000

    private var mAnchorView: View? = null
    private var mControllerView: View? = null
    private var mPlayer: MediaController.MediaPlayerControl? = null

    private val mLayoutChangeListener: AnchorLayoutChangeListener by lazy { AnchorLayoutChangeListener() }
    private var mBtnPlay: AppCompatImageButton? = null
    private var mBtnFullscreen: AppCompatImageButton? = null
    private var mSeekBar: SeekBar? = null

    private var isShowing = false
    private var isDragging = false

    private val mShowProgressRunnable = Runnable {
        val progress = setProgress()
        if (!isDragging && isShowing && mPlayer?.isPlaying == true) {
            delayProgress((1000 - progress % 1000).toLong())
        }
    }

    private val mPlayPauseListener = OnClickListener {
        doPauseResume()
        show(sDefaultTimeout)
    }

    private val mFullscreenListener = OnClickListener {
        // TODO: 2018/4/2 chenchong
    }

    private val mSeekListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (!fromUser) return // 如果不是用户操作的，则直接跳出，不进行处理

            // 如果为用户胡改变了进度，则跳转到新的进度
            mPlayer?.apply {
                val duration = duration
                val newPosition = duration * progress / 1000L
                seekTo(newPosition.toInt())
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            // 正在拖动进度条
            show(3600000)
            isDragging = true
            removeCallbacks(mShowProgressRunnable)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            // 停止拖动进度条
            isDragging = false
            setProgress()
            updatePausePlay()
            show(sDefaultTimeout)

            // 保证进度条正确更新
            post(mShowProgressRunnable)
        }

    }

    // 用于监听anchor布局变化
    private inner class AnchorLayoutChangeListener : OnLayoutChangeListener {
        override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int,
            oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            // TODO: 2018/3/30 chenchong 此处监听布局的变化
        }
    }

    init {
        id = R.id.ijk_media_controller_id
    }

    override fun show(timeout: Int) {
        if (!isShowing && mAnchorView != null) {
            setProgress()
            mBtnPlay?.requestFocus()
            disableUnsupportedButtons()

            isShowing = true
        }

        updatePausePlay()

        post(mShowProgressRunnable)
    }

    override fun show() {
        show(sDefaultTimeout)
    }

    override fun hide() {
    }

    override fun isShowing(): Boolean = isShowing

    /**
     * 设置控制器的锚定View
     * @param view 控制器的锚定View，一般为播放器本身
     */
    override fun setAnchorView(view: View?) {
        // 如果新设置的anchor与旧的相同，则直接取消处理
        if (view == mAnchorView) return

        // 如果已经有了anchor view（之前设置过anchor view），则将原先的anchor view与布局监听解除联系，防止错误回调
        mAnchorView?.removeOnLayoutChangeListener(mLayoutChangeListener)
        mAnchorView = view
        // 为新的anchor添加布局监听
        mAnchorView?.addOnLayoutChangeListener(mLayoutChangeListener)

        // 如果player的布局本身为FrameLayout，则直接将controller添加到播放器中
        tryEmbed()

        // 在当前容器中添加真正的控制器布局
        removeAllViews()
        val v = makeControllerView()
        var layoutParams = v.layoutParams
        if (layoutParams == null) {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
        }
        (layoutParams as FrameLayout.LayoutParams).gravity = Gravity.BOTTOM
        addView(v, layoutParams)
    }

    override fun setMediaPlayer(player: MediaController.MediaPlayerControl?) {
        mPlayer = player
        updatePausePlay()
    }


    override fun setEnabled(enabled: Boolean) {
        mBtnPlay?.isEnabled = enabled
        mBtnFullscreen?.isEnabled = enabled
        mSeekBar?.isEnabled = enabled
        disableUnsupportedButtons()
    }


    override fun showOnce(view: View?) {
        // 暂未明确作用，留空
    }

    // 尝试将控制器嵌入到播放器中
    private fun tryEmbed() {
        (mAnchorView as ViewGroup).let { anchor ->
            val layoutParams: ViewGroup.LayoutParams
            when (anchor) {
                is FrameLayout -> {
                    layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                    layoutParams.gravity = Gravity.BOTTOM
                }
                is RelativeLayout -> {
                    layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                }
                is LinearLayout -> {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.gravity = Gravity.BOTTOM
                }
                is ConstraintLayout -> {
                    layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.bottomToBottom = ConstraintSet.PARENT_ID
                }
                else -> {
                    throw IllegalArgumentException("${anchor::class.java} is not support yet.")
                }
            }

            anchor.addView(this@IjkMediaController, layoutParams)
        }
    }

    // 创建播放器布局
    private fun makeControllerView(): View {
        mControllerView = LayoutInflater.from(context).inflate(R.layout.layout_ijk_media_controller,
            this, false)

        // 初始化控制器，添加点击事件等
        initControllerView(mControllerView!!)

        return mControllerView!!
    }

    private fun initControllerView(controllerView: View) {
        mBtnPlay = controllerView.findViewById(R.id.btn_play)
        mBtnFullscreen = controllerView.findViewById(R.id.btn_fullscreen)
        mSeekBar = controllerView.findViewById(R.id.sb_progress)

        // 为各个控件添加事件监听
        mBtnPlay?.apply {
            requestFocus()// 不明为何需要获取焦点，暂时存疑
            setOnClickListener(mPlayPauseListener)
        }

        mBtnFullscreen?.setOnClickListener(mFullscreenListener)

        mSeekBar?.apply {
            setOnSeekBarChangeListener(mSeekListener)
            max = 1000
        }
    }

    // 更新播放/暂停的状态
    private fun updatePausePlay() {
        if (mBtnPlay == null || mPlayer == null) return

        mBtnPlay?.apply {
            if (mPlayer?.isPlaying == true) {
                setImageResource(R.drawable.ic_media_pause)
            } else {
                setImageResource(R.drawable.ic_media_play)
            }
        }
    }

    // 禁用不应该可用的按钮，例如：在直播时"暂停"按钮无法使用
    private fun disableUnsupportedButtons() {
        try {
            if (mPlayer?.canPause() != true) {
                mBtnPlay?.isEnabled = false
            }

            if (mPlayer?.canSeekBackward() != true && mPlayer?.canSeekForward() != true) {
                mSeekBar?.isEnabled = false
            }

        } catch (ex: IncompatibleClassChangeError) {
            // 在sdk使用了旧版接口时，不存在canPause等方法
            // 则处理失败，默认不禁用按钮
        }
    }

    // 切换播放/暂停状态
    private fun doPauseResume() {
        mPlayer?.apply {
            if (isPlaying) {
                pause()
            } else {
                start()
            }

            updatePausePlay()
        }
    }

    // 设定当前的进度
    private fun setProgress(): Int {
        var progress = 0
        if (isDragging) return progress

        mPlayer?.apply {
            val position = currentPosition
            val duration = duration
            if (mSeekBar != null) {
                if (duration > 0) {
                    val pos = 1000L * position / duration
                    mSeekBar!!.progress = pos.toInt()
                }

                val percent = bufferPercentage
                mSeekBar!!.secondaryProgress = percent * 10
            }

            progress = position
        }
        return progress
    }

    private fun delayProgress(delay: Long) {
        postDelayed(mShowProgressRunnable, delay)
    }
}