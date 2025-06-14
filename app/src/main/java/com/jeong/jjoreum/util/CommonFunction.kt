package com.jeong.jjoreum.util

import android.widget.Toast
import com.jeong.jjoreum.JJOreumApplication

/**
 * 토스트 메시지를 표시하는 함수
 * @param message 화면에 표시할 메시지
 */
fun toastMessage(message: String) {
    Toast.makeText(JJOreumApplication.getInstance(), message, Toast.LENGTH_SHORT).show()
}