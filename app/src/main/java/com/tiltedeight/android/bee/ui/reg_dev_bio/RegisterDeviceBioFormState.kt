package com.tiltedeight.android.bee.ui.reg_dev_bio

/**
 * Data validation state of the login form.
 */
data class RegisterDeviceBioFormState(val nameError: Int? = null,
                                      val dobError: Int? = null,
                                      val genderError: Int? = null,
                                      val isDataValid: Boolean = false)