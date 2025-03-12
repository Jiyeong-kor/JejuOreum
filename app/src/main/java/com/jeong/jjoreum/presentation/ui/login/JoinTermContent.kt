package com.jeong.jjoreum.presentation.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding4.view.clicks
import com.jeong.jjoreum.databinding.DialogJoinTermContentBinding
import java.util.concurrent.TimeUnit

class JoinTermContent : AppCompatActivity() {

    private lateinit var binding: DialogJoinTermContentBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogJoinTermContentBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        binding.joinTermContentBack.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                finish()
            }

        binding.joinTermContentWebView.loadUrl("file:///android_asset/term.html")
    }
}