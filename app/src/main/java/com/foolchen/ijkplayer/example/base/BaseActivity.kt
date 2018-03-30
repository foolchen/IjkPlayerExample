package com.foolchen.ijkplayer.example.base

import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import com.foolchen.ijkplayer.example.permissions.IRxPermission

open class BaseActivity : AppCompatActivity(), IRxPermission {
    private val mRxPermissions: RxPermissions by lazy { RxPermissions(this) }

    override fun getRxPermissions(): RxPermissions = mRxPermissions
}