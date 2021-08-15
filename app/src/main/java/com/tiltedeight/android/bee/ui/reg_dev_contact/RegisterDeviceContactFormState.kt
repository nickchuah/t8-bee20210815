package com.tiltedeight.android.bee.ui.reg_dev_contact

/**
 * Data validation state of the login form.
 */
data class RegisterDeviceContactFormState(val emailError: Int? = null,
                                          val phoneNumError: Int? = null,
                                          val isDataValid: Boolean = false)