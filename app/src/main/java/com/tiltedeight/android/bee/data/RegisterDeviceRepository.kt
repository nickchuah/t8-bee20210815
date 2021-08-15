package com.tiltedeight.android.bee.data

import android.content.Context
import com.tiltedeight.android.bee.data.model.RegisterDeviceData
import com.tiltedeight.android.bee.ws.RegDeviceBioAsyncTask
import com.tiltedeight.android.bee.ws.RegDeviceContactAsyncTask
import com.tiltedeight.android.bee.ws.RegDeviceVerifyOtpAsyncTask
import com.tiltedeight.android.bee.ws.Result
import com.tiltedeight.android.bee.ws.model.RegisterDeviceBioResp
import com.tiltedeight.android.bee.ws.model.RegisterDeviceContactResp
import com.tiltedeight.android.bee.ws.model.RegisterDeviceVerifyOtpResp
import java.util.*

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class RegisterDeviceRepository {

    // in-memory cache of the loggedInUser object
    val registerDeviceData = RegisterDeviceData()

    private constructor()

    companion object {
        private val _this: RegisterDeviceRepository = RegisterDeviceRepository()

        val instance: RegisterDeviceRepository
            get() {
                return _this
            }
    }

    fun setFcmToken(fcmToken: String?) {
        registerDeviceData.fcmToken = fcmToken
    }

    fun registerDeviceContact(phoneNum: String, email: String,context: Context, postExecuteAction: (RegisterDeviceContactResp?) -> Unit): Result<RegisterDeviceData> {

        registerDeviceData.mobileNum = phoneNum
        registerDeviceData.email = email

        RegDeviceContactAsyncTask(
                registerDeviceData!!,
                context,
                postExecuteAction
        ).execute()
        return Result.Success(registerDeviceData!!)
    }

    fun registerDeviceBio(name: String?, selectedDob: Calendar?, gender: Char?, context: Context, postExecuteAction: (RegisterDeviceBioResp?) -> Unit): Result<RegisterDeviceData> {

        registerDeviceData.fullName = name
        registerDeviceData.dob = selectedDob
        registerDeviceData.gender = gender

        RegDeviceBioAsyncTask(
                registerDeviceData!!,
                context,
                postExecuteAction
        ).execute()
        return Result.Success(registerDeviceData!!)
    }

    fun registerDeviceVerifyOtp(otp: String?, context: Context, postExecuteAction: (RegisterDeviceVerifyOtpResp?) -> Unit): Result<RegisterDeviceData> {

        registerDeviceData.otp = otp

        RegDeviceVerifyOtpAsyncTask(
                registerDeviceData!!,
                context,
                postExecuteAction
        ).execute()
        return Result.Success(registerDeviceData!!)
    }
}