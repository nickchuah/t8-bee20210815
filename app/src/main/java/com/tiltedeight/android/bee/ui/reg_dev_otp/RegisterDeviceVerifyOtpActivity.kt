package com.tiltedeight.android.bee.ui.reg_dev_bio

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.tiltedeight.android.bee.R
import com.tiltedeight.android.bee.ui.base.BaseActivity
import com.tiltedeight.android.bee.ui.base.SharedBackNextHelper
import com.tiltedeight.android.bee.ui.reg_dev_contact.RegisterDeviceContactActivity
import com.tiltedeight.android.bee.ui.reg_dev_contact.afterTextChanged
import com.tiltedeight.android.bee.util.AndroidUtils

class RegisterDeviceVerifyOtpActivity : BaseActivity() {

    private lateinit var registerDeviceVerifyOtpViewModel: RegisterDeviceVerifyOtpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // Remove the transition name to avoid the super class, DemoActivity, from configuring the
        // window with its own transition setup.

        // Remove the transition name to avoid the super class, DemoActivity, from configuring the
        // window with its own transition setup.
        window.allowEnterTransitionOverlap = true

        val axis = intent.getIntExtra(
            RegisterDeviceContactActivity.SHARED_AXIS_KEY,
            MaterialSharedAxis.X
        )

        val enterTransition = MaterialSharedAxis(axis,  /* forward= */true)
        enterTransition.addTarget(R.id.end_activity)
        window.enterTransition = enterTransition

        val returnTransition = MaterialSharedAxis(axis,  /* forward= */false)
        returnTransition.addTarget(R.id.end_activity)
        window.returnTransition = returnTransition

        super.onCreate(savedInstanceState)

        val sharedBackNextHelper = SharedBackNextHelper(findViewById(R.id.controls_layout))

        sharedBackNextHelper.setBackButtonOnClickListener { v: View? -> onBackPressed() }

        val otp = findViewById<EditText>(R.id.otp)
        val resendOtp = findViewById<Button>(R.id.resend_otp)
        val registerDevice_verifyOtp = findViewById<Button>(R.id.next_button)
        val loading = findViewById<View>(R.id.loading)

        registerDeviceVerifyOtpViewModel = ViewModelProvider(
            this,
            RegisterDeviceVerifyOtpViewModelFactory()
        )
                .get(RegisterDeviceVerifyOtpViewModel::class.java)

        registerDeviceVerifyOtpViewModel.registerDeviceVerifyOtpFormState.observe(
            this@RegisterDeviceVerifyOtpActivity,
            Observer {
                val contactState = it ?: return@Observer

                // disable login button unless both username / password is valid
//                registerDevice_contact.isEnabled = contactState.isDataValid
            })

        fun registerOtp() {
            if(registerDeviceVerifyOtpViewModel.otpDataChanged(otp.text.toString())) {

                AndroidUtils.animateView(loading, View.VISIBLE, 0.4F, 200)

                registerDeviceVerifyOtpViewModel.submitOtp(
                        otp.text.toString(),
                        this@RegisterDeviceVerifyOtpActivity,
                        postExecuteAction = {

                            AndroidUtils.animateView(loading, View.GONE, 0F, 200);

                            if (it?.status == null) {
                                showRegisterDeviceVerifyOtpFailed(R.string.service_error)

                            } else if(it.status != 'S') {
                                showRegisterDeviceVerifyOtpFailed(R.string.bad_otp)

                            } else {
                                updateUiWithUser(R.string.successfully_registered)
                                intent.putExtra("SUCCESSFUL_REGISTRATION", true)
                                setResult(Activity.RESULT_OK, intent)

                                //Complete and destroy login activity once successful
                                finish()
                            }
                        })
            }
        }

        otp.apply {
            afterTextChanged {
                registerDeviceVerifyOtpViewModel.otpDataChanged(
                    otp.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> registerOtp()
                }
                false
            }

            registerDevice_verifyOtp.setOnClickListener {
                registerOtp()
            }
        }
    }

    override fun onCreateDemoView(
        layoutInflater: LayoutInflater?,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        return layoutInflater!!.inflate(
            R.layout.reg_dev_verify_otp_activity, viewGroup, false /* attachToRoot */
        )
    }

    private fun updateUiWithUser(@StringRes message: Int) {
        Toast.makeText(
            applicationContext,
            getString(message),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showRegisterDeviceVerifyOtpFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override val demoTitleResId: Int
        get() = R.string.otp_verification
}
