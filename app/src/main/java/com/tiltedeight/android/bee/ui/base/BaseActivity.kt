/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tiltedeight.android.bee.ui.base

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.tiltedeight.android.bee.R
import com.tiltedeight.android.bee.feat.windowpreferences.WindowPreferencesManager
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

/** Base Activity class that provides a demo screen structure for a single demo.  */
abstract class BaseActivity : AppCompatActivity(), HasAndroidInjector {
    private var toolbar: Toolbar? = null
    private var demoContainer: ViewGroup? = null

    @Inject
    var androidInjector: DispatchingAndroidInjector<Any>? = null
    override fun onCreate(bundle: Bundle?) {
        if (shouldSetUpContainerTransform()) {
            val transitionName = intent.getStringExtra(EXTRA_TRANSITION_NAME)
            findViewById<View>(android.R.id.content).transitionName = transitionName
            setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
            window.sharedElementEnterTransition = buildContainerTransform( /* entering= */true)
            window.sharedElementReturnTransition = buildContainerTransform( /* entering= */false)
        }
        safeInject()
        super.onCreate(bundle)
        val windowPreferencesManager = WindowPreferencesManager(this)
        windowPreferencesManager.applyEdgeToEdgePreference(window)
        setContentView(R.layout.cat_demo_activity)
        toolbar = findViewById(R.id.toolbar)
        demoContainer = findViewById(R.id.cat_demo_activity_container)
        initDemoActionBar()
        demoContainer!!.addView(onCreateDemoView(LayoutInflater.from(this), demoContainer, bundle))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @get:StringRes
    open val demoTitleResId: Int
        get() = 0

    protected fun shouldShowDefaultDemoActionBar(): Boolean {
        return true
    }

    protected fun shouldShowDefaultDemoActionBarCloseButton(): Boolean {
        return true
    }

    protected fun shouldSetUpContainerTransform(): Boolean {
        return (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP
                && intent.getStringExtra(EXTRA_TRANSITION_NAME) != null)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector!!
    }

    private fun safeInject() {
        try {
            AndroidInjection.inject(this)
        } catch (e: Exception) {
            // Ignore exception, not all DemoActivity subclasses need to inject
        }
    }

    @RequiresApi(VERSION_CODES.LOLLIPOP)
    private fun buildContainerTransform(entering: Boolean): MaterialContainerTransform {
        val transform = MaterialContainerTransform(this, entering)
        transform.addTarget(android.R.id.content)
        transform.containerColor = MaterialColors.getColor(findViewById(android.R.id.content), R.attr.colorSurface)
        transform.fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
        return transform
    }

    private fun initDemoActionBar() {
        if (shouldShowDefaultDemoActionBar()) {
            setSupportActionBar(toolbar)
            setDemoActionBarTitle(supportActionBar)
            if (shouldShowDefaultDemoActionBarCloseButton()) {
                supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_vd_theme_24px)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            }
        } else {
            toolbar!!.visibility = View.GONE
        }
    }

    private fun setDemoActionBarTitle(actionBar: ActionBar?) {
        if (demoTitleResId != 0) {
            actionBar!!.setTitle(demoTitleResId)
        } else {
            actionBar!!.title = defaultDemoTitle
        }
    }

    private val defaultDemoTitle: String
        private get() {
            val extras = intent.extras
            return if (extras != null) {
                extras.getString(EXTRA_DEMO_TITLE, "")
            } else {
                ""
            }
        }

    abstract fun onCreateDemoView(
            layoutInflater: LayoutInflater?, viewGroup: ViewGroup?, bundle: Bundle?): View?

    companion object {
        const val EXTRA_DEMO_TITLE = "demo_title"
        const val EXTRA_TRANSITION_NAME = "EXTRA_TRANSITION_NAME"
    }
}