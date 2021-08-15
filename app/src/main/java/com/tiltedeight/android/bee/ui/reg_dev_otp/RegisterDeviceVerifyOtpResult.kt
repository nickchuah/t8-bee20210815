package com.tiltedeight.android.bee.ui.reg_dev_bio

/**
 * Authentication result : success (user details) or error message.
 */
data class RegisterDeviceVerifyOtpResult(
    val success: RegisterDeviceVerifyOtpView? = null,
    val error: Int? = null
)