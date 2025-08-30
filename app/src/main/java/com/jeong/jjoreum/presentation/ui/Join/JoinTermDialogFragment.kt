package com.jeong.jjoreum.presentation.ui.Join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import com.jeong.jjoreum.R
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme

class JoinTermDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JJOreumTheme {
                    JoinTermDialogContent(onClose = { dismiss() })
                }
            }
        }
    }

    override fun getTheme(): Int = R.style.Theme_JJOreum

    companion object {
        const val TAG = "JoinTermDialog"
        fun newInstance(): JoinTermDialogFragment = JoinTermDialogFragment()
    }
}