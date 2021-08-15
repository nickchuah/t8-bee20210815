package com.tiltedeight.android.bee.ui.reg_dev_contact

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.tiltedeight.android.bee.data.RegisterDeviceRepository
import com.tiltedeight.android.bee.ws.Result

import com.tiltedeight.android.bee.R
import com.tiltedeight.android.bee.ws.model.RegisterDeviceContactResp

class RegisterDeviceContactViewModel(private val registerDeviceRepository: RegisterDeviceRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<RegisterDeviceContactFormState>()
    val registerDeviceContactFormState: LiveData<RegisterDeviceContactFormState> = _loginForm

    private val _loginResult = MutableLiveData<RegisterDeviceContactResult>()
    val registerDeviceContactResult: LiveData<RegisterDeviceContactResult> = _loginResult

    fun submitContact(email: String, phoneNum: String, context: Context, postExecuteAction: (RegisterDeviceContactResp?) -> Unit) {
        // can be launched in a separate asynchronous job
        val result = registerDeviceRepository.registerDeviceContact(phoneNum, email, context, postExecuteAction)

        if (result is Result.Success) {
            _loginResult.value = RegisterDeviceContactResult(success = RegisterDeviceContactView(displayName = result?.data?.fullName?: "Unknown"))
        } else {
            _loginResult.value = RegisterDeviceContactResult(error = R.string.login_failed)
        }
    }

    fun contactDataChanged(contactNum: String?, email: String?): Boolean {

        var phoneNumErr: Int? = null
        var emailErr: Int? = null
        var isDataValid = false

        if(contactNum.isNullOrBlank()){
            phoneNumErr = R.string.mandatory_phone_num

        } else if (! isPhoneNumValid(contactNum)) {
            phoneNumErr = R.string.invalid_phone_num
        }

        if (email.isNullOrBlank()){
            emailErr = R.string.mandatory_email

        } else if (!isEmailValid(email)) {
            emailErr = R.string.invalid_email
        }

        listOfNotNull(phoneNumErr, emailErr).ifEmpty { isDataValid = true }

        _loginForm.value = RegisterDeviceContactFormState(phoneNumError = phoneNumErr, emailError = emailErr, isDataValid = isDataValid)

        return isDataValid
    }

    private fun isPhoneNumValid(phoneNum: String): Boolean = Patterns.PHONE.matcher(phoneNum).matches()

    private fun isEmailValid(email: String): Boolean {
        return if (email.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }
}