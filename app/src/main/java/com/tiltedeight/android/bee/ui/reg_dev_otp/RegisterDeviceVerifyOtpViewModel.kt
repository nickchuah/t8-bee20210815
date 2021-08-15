package com.tiltedeight.android.bee.ui.reg_dev_bio

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiltedeight.android.bee.data.RegisterDeviceRepository
import com.tiltedeight.android.bee.ws.Result

import com.tiltedeight.android.bee.R
import com.tiltedeight.android.bee.ws.model.RegisterDeviceBioResp
import com.tiltedeight.android.bee.ws.model.RegisterDeviceVerifyOtpResp

class RegisterDeviceVerifyOtpViewModel(private val registerDeviceRepository: RegisterDeviceRepository) : ViewModel() {

    private val _otpForm = MutableLiveData<RegisterDeviceVerifyOtpFormState>()
    val registerDeviceVerifyOtpFormState: LiveData<RegisterDeviceVerifyOtpFormState> = _otpForm

    private val _loginResult = MutableLiveData<RegisterDeviceVerifyOtpResult>()
    val registerDeviceVerifyOtpResult: LiveData<RegisterDeviceVerifyOtpResult> = _loginResult

    fun submitOtp(otp: String, context: Context, postExecuteAction: (RegisterDeviceVerifyOtpResp?) -> Unit) {
        // can be launched in a separate asynchronous job
        val result = registerDeviceRepository.registerDeviceVerifyOtp(otp, context, postExecuteAction)

        if (result is Result.Success) {
            _loginResult.value = RegisterDeviceVerifyOtpResult(success = RegisterDeviceVerifyOtpView(displayName = result.data.fullName?: "Unknown"))
        } else {
            _loginResult.value = RegisterDeviceVerifyOtpResult(error = R.string.login_failed)
        }
    }

    fun otpDataChanged(otp: String): Boolean {

        if (otp.isBlank()) {
            _otpForm.value = RegisterDeviceVerifyOtpFormState(otpError = R.string.empty_otp)
        } else if(otp.length < 6){
            _otpForm.value = RegisterDeviceVerifyOtpFormState(otpError = R.string.invalid_otp)
        } else {
            _otpForm.value = RegisterDeviceVerifyOtpFormState(otpError = null, isDataValid = true)
        }

        return _otpForm.value?.isDataValid?: false
    }
}