package com.tiltedeight.android.bee.ui.reg_dev_bio

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.*
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.tiltedeight.android.bee.R
import com.tiltedeight.android.bee.ui.base.BaseActivity
import com.tiltedeight.android.bee.ui.base.SharedBackNextHelper
import com.tiltedeight.android.bee.ui.reg_dev_contact.RegisterDeviceContactActivity
import com.tiltedeight.android.bee.ui.reg_dev_contact.afterRadioSelected
import com.tiltedeight.android.bee.ui.reg_dev_contact.afterTextChanged
import com.tiltedeight.android.bee.util.AndroidUtils
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class RegisterDeviceBioActivity : BaseActivity() {

    private lateinit var registerDeviceBioViewModel: RegisterDeviceBioViewModel

    private lateinit var sharedBackNextHelper: SharedBackNextHelper

    private var selectedDob: Calendar? = null
    private var selectedGender: Char? = null

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

        sharedBackNextHelper = SharedBackNextHelper(
            findViewById(R.id.controls_layout),
            MaterialSharedAxis.Z
        )
//        sharedAxisHelper.setAxisButtonGroupEnabled(false)

        sharedBackNextHelper.setBackButtonOnClickListener { v: View? -> onBackPressed() }

        val name = findViewById<EditText>(R.id.full_name)
        val dob = findViewById<EditText>(R.id.dob)
        val gender = findViewById<RadioGroup>(R.id.radio_button_gender)
        val loading = findViewById<View>(R.id.loading)
        val dialogTheme: Int = resolveOrThrow(
            this,
            R.attr.materialCalendarTheme
        )

        dob.setOnClickListener {
            val builder: MaterialDatePicker.Builder<*> =
                setupDateSelectorBuilder()
            val constraintsBuilder: CalendarConstraints.Builder =
                setupConstraintsBuilder()

            builder.setTheme(dialogTheme)

            try {
                builder.setCalendarConstraints(constraintsBuilder.build())
                val picker = builder.build()

                picker.addOnPositiveButtonClickListener { selection: Any? ->
                    val cal = GregorianCalendar()
                    cal.timeInMillis = selection as Long
                    selectedDob = cal

                    dob.setText(picker.headerText)
                }

                picker.show(supportFragmentManager, picker.toString())

            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        registerDeviceBioViewModel = ViewModelProvider(
            this,
            RegisterDeviceBioViewModelFactory()
        )
                .get(RegisterDeviceBioViewModel::class.java)

        registerDeviceBioViewModel.registerDeviceBioFormState.observe(
            this@RegisterDeviceBioActivity,
            Observer {
                val bioState = it ?: return@Observer

                if(bioState.nameError != null){
                    name.error = getString(bioState.nameError)
                }

                if(bioState.dobError != null){
                    dob.error = getString(bioState.dobError)
                }

//                if(bioState.genderError != null){
//                    gender.error = getString(bioState.genderError)
//                }
            })

        fun registerDeviceBio() {
            if(registerDeviceBioViewModel.bioDataChanged(name.text.toString(), selectedDob, selectedGender)) {

                AndroidUtils.animateView(loading, View.VISIBLE, 0.4F, 200)

                registerDeviceBioViewModel.submitBio(
                        name.text.toString(), selectedDob, selectedGender,
                        this@RegisterDeviceBioActivity,
                        postExecuteAction = {

                            AndroidUtils.animateView(loading, View.GONE, 0F, 200);

                            if (it?.status != 'S') {
                                showRegisterDeviceBioFailed(R.string.service_error)

                            } else {
                                navigateToVerifyOtpActivity()
                            }
                        })
            }
        }

        sharedBackNextHelper.setNextButtonOnClickListener { registerDeviceBio() }

        name.apply {
            afterTextChanged {
                registerDeviceBioViewModel.bioDataChanged(
                    name.text.toString(),
                        selectedDob,
                        selectedGender
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> registerDeviceBio()
                }
                false
            }

//            registerDevice_contact.setOnClickListener {
//                registerDeviceContact()
//            }
        }

        gender.apply {
            afterRadioSelected {
                selectedGender = if(it == R.id.gender_male){
                    'M'
                } else if(it == R.id.gender_female){
                    'F'
                } else {
                    null
                }

                registerDeviceBioViewModel.bioDataChanged(
                        name.text.toString(),
                        selectedDob,
                        selectedGender
                )
            }
        }
    }

    private fun setupDateSelectorBuilder(): MaterialDatePicker.Builder<*> {

        val openAt = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        openAt.add(Calendar.YEAR, -20)

        val inputMode = MaterialDatePicker.INPUT_MODE_CALENDAR
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setSelection(selectedDob?.timeInMillis ?: openAt.timeInMillis)
        builder.setInputMode(inputMode)
        return builder
    }

    private fun setupConstraintsBuilder(): CalendarConstraints.Builder {
        val constraintsBuilder = CalendarConstraints.Builder()

        val openAt = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        openAt.add(Calendar.YEAR, -20)
        constraintsBuilder.setOpenAt(openAt.timeInMillis)

        val lowerBoundCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        lowerBoundCalendar.add(Calendar.YEAR, -100)
        val lowerBound = lowerBoundCalendar.timeInMillis
        val upperBoundCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        upperBoundCalendar.add(Calendar.YEAR, -5)
        val upperBound = upperBoundCalendar.timeInMillis
        constraintsBuilder.setStart(lowerBound)
        constraintsBuilder.setEnd(upperBound)
        val validators: MutableList<DateValidator> = ArrayList()
        validators.add(DateValidatorPointForward.from(lowerBound))
        validators.add(DateValidatorPointBackward.before(upperBound))
        constraintsBuilder.setValidator(CompositeDateValidator.allOf(validators))
        return constraintsBuilder
    }

    private fun resolveOrThrow(context: Context, @AttrRes attributeResId: Int): Int {
        val typedValue = TypedValue()
        if (context.theme.resolveAttribute(attributeResId, typedValue, true)) {
            return typedValue.data
        }
        throw java.lang.IllegalArgumentException(context.resources.getResourceName(attributeResId))
    }

    override fun onCreateDemoView(
        layoutInflater: LayoutInflater?,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        return layoutInflater!!.inflate(
            R.layout.reg_dev_bio_activity, viewGroup, false /* attachToRoot */
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

    private fun navigateToVerifyOtpActivity() {

        val axis: Int = sharedBackNextHelper.transitionAxis
        val exitTransition = MaterialSharedAxis(axis,  /* forward= */true)
        exitTransition.addTarget(R.id.start_activity)
        window.exitTransition = exitTransition
        val reenterTransition = MaterialSharedAxis(axis,  /* forward= */false)
        reenterTransition.addTarget(R.id.start_activity)
        window.reenterTransition = reenterTransition
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
        val intent = Intent(this, RegisterDeviceVerifyOtpActivity::class.java)
        intent.putExtra(RegisterDeviceContactActivity.SHARED_AXIS_KEY, axis)
        startForResult.launch(intent, options)
    }

    private fun showRegisterDeviceBioFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override val demoTitleResId: Int
        get() = R.string.basic_info
}