package com.tiltedeight.android.bee.ui.reg_dev_contact

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.Constants.TAG
import com.google.firebase.messaging.ktx.messaging
import com.hbb20.CountryCodePicker
import com.tiltedeight.android.bee.R
import com.tiltedeight.android.bee.data.RegisterDeviceRepository
import com.tiltedeight.android.bee.ui.base.BaseActivity
import com.tiltedeight.android.bee.ui.base.SharedBackNextHelper
import com.tiltedeight.android.bee.ui.reg_dev_bio.RegisterDeviceBioActivity
import com.tiltedeight.android.bee.util.AndroidUtils

class RegisterDeviceContactActivity : BaseActivity() {

    companion object {
        val SHARED_AXIS_KEY = "activity_shared_axis_axis"
    }

    private lateinit var registerDeviceContactViewModel: RegisterDeviceContactViewModel

    private lateinit var sharedBackNextHelper: SharedBackNextHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        window.allowReturnTransitionOverlap = true

        super.onCreate(savedInstanceState)

        sharedBackNextHelper = SharedBackNextHelper(findViewById(R.id.controls_layout))

        val countryCodePicker = findViewById<CountryCodePicker>(R.id.countryCodePicker)
        val contactNum = findViewById<EditText>(R.id.contactNum)
        countryCodePicker.registerCarrierNumberEditText(contactNum)

        val email = findViewById<EditText>(R.id.email)
        val registerDevice_contact = findViewById<Button>(R.id.next_button)
        val loading = findViewById<View>(R.id.loading)

        registerDeviceContactViewModel = ViewModelProvider(
                this,
                RegisterDeviceContactViewModelFactory()
        ).get(RegisterDeviceContactViewModel::class.java)

        registerDeviceContactViewModel.registerDeviceContactFormState.observe(
                this@RegisterDeviceContactActivity,
                Observer {
                    val contactState = it ?: return@Observer

                    if (contactState.phoneNumError != null) {
                        contactNum.error = getString(contactState.phoneNumError)
                    }
                    if (contactState.emailError != null) {
                        email.error = getString(contactState.emailError)
                    }
                })

        fun registerDeviceContact() {
            if(registerDeviceContactViewModel.contactDataChanged(countryCodePicker.fullNumberWithPlus, email.text.toString())) {

                AndroidUtils.animateView(loading, View.VISIBLE, 0.4F, 200)

                registerDeviceContactViewModel.submitContact(
                        email.text.toString(),
                        countryCodePicker.fullNumberWithPlus,
                        this@RegisterDeviceContactActivity,
                        postExecuteAction = {

                            AndroidUtils.animateView(loading, View.GONE, 0F, 200);

                            if (it?.registrationId.isNullOrBlank()) {
                                showRegisterDeviceContactFailed(R.string.service_error)

                            } else {
                                navigateToNextActivity()
                            }
                        })
            }
        }

        sharedBackNextHelper.setNextButtonOnClickListener { v: View? -> registerDeviceContact() }

        email.apply {
            afterTextChanged {
                registerDeviceContactViewModel.contactDataChanged(
                        countryCodePicker.fullNumberWithPlus,
                        email.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        registerDeviceContact()
                    }
                }
                false
            }

            registerDevice_contact.setOnClickListener {
                registerDeviceContact()
            }
        }

        Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()

            RegisterDeviceRepository.instance.setFcmToken(token)
        })
    }

    override fun onCreateDemoView(
            layoutInflater: LayoutInflater?,
            viewGroup: ViewGroup?,
            bundle: Bundle?
    ): View {
        return layoutInflater!!.inflate(
                R.layout.reg_dev_contact_activity, viewGroup, false /* attachToRoot */
        )
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data

            if(data?.getBooleanExtra("SUCCESSFUL_REGISTRATION", false)?: false){
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
    }

    private fun navigateToNextActivity() {

        val axis: Int = sharedBackNextHelper.transitionAxis
        val exitTransition = MaterialSharedAxis(axis,  /* forward= */true)
        exitTransition.addTarget(R.id.start_activity)
        window.exitTransition = exitTransition
        val reenterTransition = MaterialSharedAxis(axis,  /* forward= */false)
        reenterTransition.addTarget(R.id.start_activity)
        window.reenterTransition = reenterTransition
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        val intent = Intent(this, RegisterDeviceBioActivity::class.java)
        intent.putExtra(SHARED_AXIS_KEY, axis)
        startForResult.launch(intent, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data?.getBooleanExtra("SUCCESSFUL_REGISTRATION", false)?: false){
            finish();
        }
    }

    private fun showRegisterDeviceContactFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override val demoTitleResId: Int
        get() = R.string.registration
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun RadioGroup.afterRadioSelected(afterRadioSelected: (Int) -> Unit) {
    this.setOnCheckedChangeListener { group, checkedId -> afterRadioSelected.invoke(checkedId) }
}