package com.foolchen.ijkplayer.example.permissions

import com.tbruyelle.rxpermissions2.RxPermissions

interface IRxPermission {
  fun getRxPermissions(): RxPermissions
}