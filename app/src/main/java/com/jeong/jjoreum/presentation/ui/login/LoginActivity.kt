package com.jeong.jjoreum.presentation.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding4.view.clicks
import com.jeong.jjoreum.databinding.ActivityLoginBinding
import com.jeong.jjoreum.presentation.ui.main.MainActivity
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var emailEdit = ""
    private var passwordEdit = ""

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        with(binding) {
            loginBtnJoin.clicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    startActivity(Intent(this@LoginActivity, JoinFormActivity::class.java))
                    finish()
                }
            loginBtnLogin.clicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    loginBtnClick()
                }
            loginBtnBack.clicks()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    finish()
                }
        }
    }

    private fun loginBtnClick() {
        emailEdit = binding.loginEditEmail.text.toString()
        passwordEdit = binding.loginEditPassword.text.toString()

        if (emailEdit.isEmpty()) {
            binding.loginLayoutEmail.error = "이메일을 입력해주세요"
        } else if (passwordEdit.isEmpty()) {
            binding.loginLayoutPassword.error = "비밀번호를 입력해주세요"
        } else {
//            joinRef.child("user").addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (dataSnapshot in snapshot.children) {
//                        dataSnapshot.getValue(JoinItem::class.java)?.let {
//                            if (it.email == emailEdt) {
//                                if (it.password == passwordEdt) {
//                                    if (binding.loginCheckboxAutoLogin.isChecked) {
//                                        getloginSPEditor().putInt("userId", it.userId).commit()
//                                        setUserId(getLoginSP().getInt("userId", -1))
//                                        findNickname()
//                                        binding.loginError.visibility = View.GONE
            startActivity(
                Intent(
                    this@LoginActivity,
                    MainActivity::class.java
                )
            )
            finish()
//                                        return // for문 종료
//                                    }
//                                }
//                            } else {
//                                binding.loginError.visibility = View.VISIBLE
//                            }
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    logMessage(error.message)
//                }
//            })
        }
    }
}