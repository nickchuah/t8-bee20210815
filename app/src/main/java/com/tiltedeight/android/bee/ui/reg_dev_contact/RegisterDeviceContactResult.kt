package com.tiltedeight.android.bee.ui.reg_dev_contact

/**
 * Authentication result : success (user details) or error message.
 */
data class RegisterDeviceContactResult(
        val success: RegisterDeviceContactView? = null,
        val error: Int? = null
)