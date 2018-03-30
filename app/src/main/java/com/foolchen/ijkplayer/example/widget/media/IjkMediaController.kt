package com.foolchen.ijkplayer.example.widget.media

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.RelativeLayout
import com.xcar.ijkplayerexample.R

/**
 * 自定义的播放器控制器
 *
 * @author chenchong
 * 2018/3/29
 * 上午10:06
 */
class IjkMediaController(context: Context) : FrameLayout(context), IMediaController {

    private var isShowing = false
    private val mLayoutChangeListener: AnchorLayoutChangeListener by lazy { AnchorLayoutChangeListener() }
    private var mAnchorView: View? = null
    private var mControllerView: View? = null

    init {
        id = R.id.ijk_media_controller_id
    }

    override fun show(timeout: Int) {
    }

    override fun show() {
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


    override fun setEnabled(enabled: Boolean) {
    }

    override fun setMediaPlayer(player: MediaController.MediaPlayerControl?) {
    }

    override fun showOnce(view: View?) {
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

    }

    // 用于监听anchor布局变化
    private inner class AnchorLayoutChangeListener : OnLayoutChangeListener {
        override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int,
            oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            // TODO: 2018/3/30 chenchong 此处监听布局的变化
        }
    }
}