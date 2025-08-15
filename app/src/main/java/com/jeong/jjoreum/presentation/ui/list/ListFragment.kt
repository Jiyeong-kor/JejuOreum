package com.jeong.jjoreum.presentation.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.jeong.jjoreum.R
import com.jeong.jjoreum.presentation.ui.theme.JJOreumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JJOreumTheme {
                    ListScreen(
                        viewModel = viewModel,
                        onItemClick = { oreum ->
                            val bundle = Bundle().apply {
                                putParcelable("oreumData", oreum)
                            }
                            findNavController().navigate(
                                R.id.action_listFragment_to_detailFragment, bundle
                            )
                        },
                        showToast = { msg ->
                            Toast.makeText(
                                requireContext(),
                                msg, Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}