package com.jeong.jjoreum.presentation.ui.Join

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.jakewharton.rxbinding4.view.clicks
import com.jeong.jjoreum.databinding.DialogJoinTermBinding
import java.util.concurrent.TimeUnit
import com.jeong.jjoreum.R

class JoinTermDialogFragment : DialogFragment() {

    private var _binding: DialogJoinTermBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogJoinTermBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.joinTermContentWebView.loadUrl("file:///android_asset/term.html")
        binding.joinTermContentClose.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                dismiss()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int {
        return R.style.Theme_JJOreum
    }

    companion object {
        const val TAG = "JoinTermDialog"
        fun newInstance(): JoinTermDialogFragment {
            return JoinTermDialogFragment()
        }
    }
}