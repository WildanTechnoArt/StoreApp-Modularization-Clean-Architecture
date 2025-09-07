package com.wildan.storeapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wildan.core.extension.clearAppDataStore
import com.wildan.core.extension.showAlertDialog
import com.wildan.storeapp.databinding.BottomSheetLayoutBinding
import com.wildan.storeapp.ui.activity.LoginActivity
import com.wildan.storeapp.ui.viewmodel.DatabaseViewModel
import com.wildan.storeapp.ui.viewmodel.LocalDataViewModelFactory
import kotlinx.coroutines.launch

class ProfileBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLayoutBinding? = null
    private val binding get() = _binding
    private lateinit var viewModelDatabase: DatabaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetLayoutBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = LocalDataViewModelFactory.getInstance(requireActivity())

        viewModelDatabase =
            ViewModelProvider(requireActivity(), factory)[DatabaseViewModel::class.java]
        binding?.btnLogout?.setOnClickListener {
            requireActivity().showAlertDialog("Are you sure you want to leave?") {
                lifecycleScope.launch {
                    requireActivity().clearAppDataStore()
                    viewModelDatabase.clearCart()
                    requireActivity().startActivity(
                        Intent(
                            requireActivity(),
                            LoginActivity::class.java
                        )
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}