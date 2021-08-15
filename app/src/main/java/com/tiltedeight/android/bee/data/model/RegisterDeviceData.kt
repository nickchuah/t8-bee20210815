package com.tiltedeight.android.bee.data.model

import java.util.*

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class RegisterDeviceData (
        var registrationId: String? = null,
        var fullName: String? = null,
        var mobileNum: String? = null,
        var email: String? = null,
        var dob: Calendar? = null,
        var gender: Char? = null,
        var vendorKey: String? = null,
        var appBundleId: String? = null,
        var deviceId: String? = null,
        var gaid: String? = null,
        var deviceType: String = "android",
        var recaptchaHash: String? = null,
        var otp: String? = null,
        var fcmToken: String? = null
)