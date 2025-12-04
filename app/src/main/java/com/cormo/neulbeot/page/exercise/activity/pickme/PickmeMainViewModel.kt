/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cormo.neulbeot.page.exercise.activity.pickme

import androidx.lifecycle.ViewModel

/**
 * Game mode: fixed settings for HandLandmarkerHelper
 */
class PickmeMainViewModel : ViewModel() {

    // Always use GPU
    val currentDelegate: Int = PickmeHandLandmarkerHelper.DELEGATE_GPU

    // Fixed confidence thresholds
    val currentMinHandDetectionConfidence: Float = 0.5f
    val currentMinHandTrackingConfidence: Float = 0.5f
    val currentMinHandPresenceConfidence: Float = 0.5f

    // Track only one hand for game
    val currentMaxHands: Int = 2
}
