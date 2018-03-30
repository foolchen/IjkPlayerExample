package com.foolchen.ijkplayer.example.widget.media

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.MediaController

/**
 * 自定义的播放器控制器
 *
 * @author chenchong
 * 2018/3/29
 * 上午10:06
 */
class IjkMediaController(context: Context) : FrameLayout(context), IMediaController {

    private var isShowing = false

    override fun onFinishInflate() {
        super.onFinishInflate()

        // 根布局加载完成，开始初始化控制器布局

    }


    override fun show(timeout: Int) {
    }

    override fun show() {
    }

    override fun hide() {
    }

    override fun isShowing(): Boolean = isShowing

    override fun setAnchorView(view: View?) {
    }

    override fun setEnabled(enabled: Boolean) {
    }

    override fun setMediaPlayer(player: MediaController.MediaPlayerControl?) {
    }

    override fun showOnce(view: View?) {
    }
}