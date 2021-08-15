package com.tiltedeight.android.bee.ui.reg_dev_contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiltedeight.android.bee.data.RegisterDeviceRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class RegisterDeviceContactViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterDeviceContactViewModel::class.java)) {
            return RegisterDeviceContactViewModel(
                registerDeviceRepository = RegisterDeviceRepository.instance
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}