package com.jeong.jjoreum.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.jeong.jjoreum.R
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
import com.jeong.jjoreum.presentation.viewmodel.LocationPermissionViewModel
import com.jeong.jjoreum.presentation.viewmodel.PermissionEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationPermissionDialogFragment : DialogFragment() {

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
    ): View = ComposeView(requireContext()).apply {
        setContent {
            JJOreumTheme {
                LocationPermissionDialogContent(
                    onGrantClick = { locationPermissionViewModel.requestLocationPermission() },
                    onDenyClick = { dismiss() }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationPermissionViewModel.permissionEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is PermissionEvent.RequestLocationPermission -> {
                    requestPermissionLauncher.launch(
                        locationPermissionViewModel.getRequiredPermissions()
                    )
                }

                is PermissionEvent.PermissionGranted,
                is PermissionEvent.PermissionDenied,
                is PermissionEvent.PermissionDeniedPermanently -> dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        locationPermissionViewModel.checkLocationPermission()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    @Composable
    private fun LocationPermissionDialogContent(
        onGrantClick: () -> Unit,
        onDenyClick: () -> Unit
    ) {
        Card {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.permission_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(R.string.permission_dialog_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDenyClick,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = stringResource(R.string.permission_dialog_deny))
                    }
                    Button(onClick = onGrantClick) {
                        Text(text = stringResource(R.string.permission_dialog_grant))
                    }
                }
            }
        }
    }
}