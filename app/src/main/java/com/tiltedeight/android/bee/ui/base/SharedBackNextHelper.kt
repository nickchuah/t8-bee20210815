/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tiltedeight.android.bee.ui.base

import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.transition.MaterialSharedAxis
import com.tiltedeight.android.bee.R

/** A helper class that sets up and manages shared axis demo controls.  */
class SharedBackNextHelper(controlsLayout: ViewGroup) {
    var transitionAxis = MaterialSharedAxis.X
        private set
    private val backButton: Button
    private val nextButton: Button

    constructor(controlsLayout: ViewGroup, transitionAxis: Int) : this(controlsLayout) {
        this.transitionAxis = transitionAxis
    }

    fun setNextButtonOnClickListener(onClickListener: View.OnClickListener?) {
        nextButton.setOnClickListener(onClickListener)
    }

    fun setBackButtonOnClickListener(onClickListener: View.OnClickListener?) {
        backButton.setOnClickListener(onClickListener)
    }

    companion object {
        private val BUTTON_AXIS_MAP = SparseIntArray()
    }

    init {
        backButton = controlsLayout.findViewById(R.id.back_button)
        nextButton = controlsLayout.findViewById(R.id.next_button)
    }
}