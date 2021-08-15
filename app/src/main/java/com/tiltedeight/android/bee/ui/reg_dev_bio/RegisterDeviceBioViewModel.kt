package com.tiltedeight.android.bee.ui.reg_dev_bio

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiltedeight.android.bee.data.RegisterDeviceRepository
import com.tiltedeight.android.bee.ws.Result

import com.tiltedeight.android.bee.R
import com.tiltedeight.android.bee.ws.model.RegisterDeviceBioResp
import java.util.*

class RegisterDeviceBioViewModel(private val registerDeviceRepository: RegisterDeviceRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<RegisterDeviceBioFormState>()
    val registerDeviceBioFormState: LiveData<RegisterDeviceBioFormState> = _loginForm

    private val _loginResult = MutableLiveData<RegisterDeviceBioResult>()
    val registerDeviceBioResult: LiveData<RegisterDeviceBioResult> = _loginResult

    fun submitBio(name: String?, selectedDob: Calendar?, gender: Char?, context: Context, postExecuteAction: (RegisterDeviceBioResp?) -> Unit) {
        val result = registerDeviceRepository.registerDeviceBio(name, selectedDob, gender, context, postExecuteAction)

        if (result is Result.Success) {
            _loginResult.value = RegisterDeviceBioResult(success = RegisterDeviceBioView(displayName = result.data.fullName?: "Unknown"))
        } else {
            _loginResult.value = RegisterDeviceBioResult(error = R.string.login_failed)
        }
    }

    fun bioDataChanged(name: String?, selectedDob: Calendar?, gender: Char?): Boolean {
        var isDataValid = false

        var nameErr: Int? = null
        var dobErr: Int? = null
        var genderErr: Int? = null

        if(name.isNullOrBlank()){
            nameErr = R.string.mandatory_name
        }

        listOfNotNull(nameErr, dobErr, genderErr).ifEmpty { isDataValid = true }

        _loginForm.value = RegisterDeviceBioFormState(nameError = nameErr, dobError = dobErr,
            genderError = genderErr, isDataValid = isDataValid)

        return isDataValid
    }
}