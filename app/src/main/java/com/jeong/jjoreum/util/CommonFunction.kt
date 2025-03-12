package com.jeong.jjoreum.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.jeong.jjoreum.JJOreumApplication
import com.jeong.jjoreum.presentation.ui.dialog.LoadingDialog

/**
 * 토스트 메시지를 표시하는 함수
 * @param message 화면에 표시할 메시지
 */
fun toastMessage(message: String) {
    Toast.makeText(JJOreumApplication.getInstance(), message, Toast.LENGTH_SHORT).show()
}

/**
 * 로그 메시지를 출력하는 함수
 * @param message 출력할 로그 메시지
 */
fun logMessage(message: String) {
    Log.e("OreumTag", message)
}

lateinit var mLoadingDialog: LoadingDialog

/**
 * 로딩 다이얼로그를 표시하는 함수
 * @param context 다이얼로그를 띄울 컨텍스트
 */
fun showLoadingDialog(context: Context) {
    mLoadingDialog = LoadingDialog(context)
    mLoadingDialog.show()
}

/**
 * 로딩 다이얼로그를 닫는 함수
 */
fun dissmissLoadingDialog() {
    if (mLoadingDialog.isShowing) {
        mLoadingDialog.dismiss()
    }
}
