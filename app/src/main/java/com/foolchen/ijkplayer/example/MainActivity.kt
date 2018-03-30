package com.foolchen.ijkplayer.example

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.foolchen.ijkplayer.example.base.BaseActivity
import com.xcar.ijkplayerexample.R
import kotlinx.android.synthetic.main.activity_main.*
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class MainActivity : BaseActivity() {
    private var mPermissionDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        IjkMediaPlayer.loadLibrariesOnce(null)
        IjkMediaPlayer.native_profileBegin("libijkplayer.so")

        val requestPermissions = getRxPermissions()
                .request(Manifest.permission.INTERNET,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)

        fun callback(result: Boolean) {
            if (result) {

            } else {
                mPermissionDialog = AlertDialog.Builder(this@MainActivity)
                        .setMessage(R.string.text_permission_tips)
                        .setPositiveButton(R.string.text_require_permissions, { _, _
                            ->
                            requestPermissions.subscribe { result -> callback(result) }
                        })
                        .setNegativeButton(R.string.text_deny_permissions, { dialog, _ ->
                            dialog.cancel()
                        })
                        .create()
                mPermissionDialog!!.show()
            }
        }
        requestPermissions
                .subscribe { result ->
                    callback(result)
                }

        listeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPermissionDialog?.cancel()
    }

    private fun listeners() {
        btn_video_player.setOnClickListener {
            startActivity(Intent(this@MainActivity, VideoPlayerActivity::class.java))
        }
    }
}
