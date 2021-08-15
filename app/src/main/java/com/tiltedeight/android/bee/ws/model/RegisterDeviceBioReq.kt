package com.tiltedeight.android.bee.ws.model

import java.util.*

data class RegisterDeviceBioReq (
    var registrationId: String? = null,
    var fullName: String? = null,
    var dob: Calendar? = null,
    var gender: Char? = null,
    var recaptchaHash: String? = null
)