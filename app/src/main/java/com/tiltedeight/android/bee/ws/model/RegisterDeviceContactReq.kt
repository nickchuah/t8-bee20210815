package com.tiltedeight.android.bee.ws.model

data class RegisterDeviceContactReq(
    val vendorKey: String?,
    val appBundleId: String,
    val email: String?,
    val phoneNum: String,
    val deviceId: String,
    val gaid: String?,
    val fcmToken: String?,
    val deviceType: String = "android",
    val extra: Map<String, Object>?
) {
    val apple_deviceToken: String? = null
    val apple_idForVendor: String? = null
    val idfa: String? = null
}