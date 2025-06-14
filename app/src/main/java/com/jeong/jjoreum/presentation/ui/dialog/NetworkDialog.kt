package com.jeong.jjoreum.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.jeong.jjoreum.databinding.DialogNetworkBinding

/**
 * 네트워크 연결이 없을 때 다시 시도를 요청하는 다이얼로그
 */
class NetworkDialog : DialogFragment() {

    private lateinit var binding: DialogNetworkBinding
    private var myDialogInterface: DialogListener? = null

    /**
     * 다이얼로그 인터페이스 - 확인 버튼 클릭 시 호출
     */
    interface DialogListener {
        fun onDialogPositiveClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNetworkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btn.setOnClickListener {
            myDialogInterface?.onDialogPositiveClick()
        }
    }
}
