package com.foolchen.ijkplayer.example

import android.os.Bundle
import android.util.Log
import com.foolchen.ijkplayer.example.base.BaseActivity
import com.foolchen.ijkplayer.example.utils.getScreenWidth
import com.foolchen.ijkplayer.example.utils.resize
import com.foolchen.ijkplayer.example.widget.media.AndroidMediaController
import com.xcar.ijkplayerexample.R
import kotlinx.android.synthetic.main.activity_video_player.*

/**
 * 视频播放页面
 *
 * @author chenchong
 * 2018/3/28
 * 上午11:35
 */
class VideoPlayerActivity : BaseActivity() {
    private val TAG: String by lazy { this@VideoPlayerActivity::class.java.simpleName }
    private lateinit var mMediaController: AndroidMediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        video_player.apply {
            resize()
            mMediaController = AndroidMediaController(this@VideoPlayerActivity, false)
            mMediaController.setSupportActionBar(supportActionBar)
            setMediaController(mMediaController)
            setHudView(hud_view)
            setVideoPath("http://vn.xcar.com.cn/xtv/qiniu/video/converted/mp4/2017/11/30/mp4_o_1c05e8d2qo1knlk2ai1eq9dq81j_720p.mp4")
            setOnPreparedListener {
                // 初始化完成，开始播放
                Log.d(TAG, "初始化完成，开始播放")
                it.start()
            }
        }

    }

    // 限制播放器为16：9
    private fun resize() {
        val width = getScreenWidth()
        val height = (width / 16F * 9F).toInt()
        video_player.resize(width, height)
    }
}