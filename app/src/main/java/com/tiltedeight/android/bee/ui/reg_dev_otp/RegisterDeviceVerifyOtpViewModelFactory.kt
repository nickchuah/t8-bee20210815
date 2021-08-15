package com.tiltedeight.android.bee.ui.reg_dev_bio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiltedeight.android.bee.data.RegisterDeviceRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class RegisterDeviceVerifyOtpViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterDeviceVerifyOtpViewModel::class.java)) {
            return RegisterDeviceVerifyOtpViewModel(
                registerDeviceRepository = RegisterDeviceRepository.instance
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}