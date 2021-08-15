package com.tiltedeight.android.bee.ws.model

data class RegisterDeviceVerifyOtpReq (
    var registrationId: String? = null,
    var otp: String? = null
)