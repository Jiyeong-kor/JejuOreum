package com.jeong.jjoreum.presentation.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.jakewharton.rxbinding4.view.clicks
import com.jeong.jjoreum.R
import com.jeong.jjoreum.databinding.DialogJoinTermBinding
import java.util.concurrent.TimeUnit

class JoinTermFragment : Fragment() {

    private var _binding: DialogJoinTermBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogJoinTermBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.termArrow.clicks()
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .subscribe {
                findNavController().navigate(R.id.action_joinTermFragment_to_joinTermContent)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}