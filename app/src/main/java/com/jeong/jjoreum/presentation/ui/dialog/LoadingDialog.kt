package com.jeong.jjoreum.presentation.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import com.jeong.jjoreum.R

/**
 * 여러 화면에서 공통으로 사용하는 로딩 다이얼로그 클래스
 * @param context 다이얼로그를 띄울 컨텍스트
 */
class LoadingDialog(context: Context) : Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 제거
        setContentView(R.layout.dialog_loading) // 레이아웃 설정
        setCancelable(false) // 다이얼로그 외부 터치로 닫히지 않도록 설정

        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경을 투명하게 설정
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) // 배경 어둡게 하지 않음
        }
    }
}
