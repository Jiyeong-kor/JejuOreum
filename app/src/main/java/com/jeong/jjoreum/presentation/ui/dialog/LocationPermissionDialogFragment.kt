package com.jeong.jjoreum.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jeong.jjoreum.databinding.DialogLocationPermissionBinding
import com.jeong.jjoreum.presentation.viewmodel.LocationPermissionViewModel
import com.jeong.jjoreum.presentation.viewmodel.PermissionEvent

class LocationPermissionDialogFragment : DialogFragment() {

    private var _binding: DialogLocationPermissionBinding? = null
    private val binding get() = _binding!!

    private val locationPermissionViewModel: LocationPermissionViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            locationPermissionViewModel.handlePermissionResult(permissions)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogLocationPermissionBinding.inflate(inflater, container, false)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationPermissionViewModel.permissionEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is PermissionEvent.RequestLocationPermission -> {
                    requestPermissionLauncher.launch(locationPermissionViewModel.getRequiredPermissions())
                }

                is PermissionEvent.PermissionGranted -> {
                    dismiss()
                }

                is PermissionEvent.PermissionDenied -> {
                    dismiss()
                }

                else -> {
                }
            }
        }

        binding.dialogPermissionGrantButton.setOnClickListener {
            locationPermissionViewModel.requestLocationPermission()
        }

        binding.dialogPermissionDenyButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        locationPermissionViewModel.checkLocationPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}