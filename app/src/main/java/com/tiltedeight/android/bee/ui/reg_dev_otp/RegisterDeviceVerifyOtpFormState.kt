package com.tiltedeight.android.bee.ui.reg_dev_bio

/**
 * Data validation state of the login form.
 */
data class RegisterDeviceVerifyOtpFormState(val otpError: Int? = null,
                                            val isDataValid: Boolean = false)