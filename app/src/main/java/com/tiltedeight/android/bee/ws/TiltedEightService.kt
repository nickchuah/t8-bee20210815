package com.tiltedeight.android.bee.ws

import com.tiltedeight.android.bee.ws.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TiltedEightService {

    @POST("/register/contact")
    fun registerDevice_contact(@Body model: RegisterDeviceContactReq): Call<RegisterDeviceContactResp>

    @POST("/register/bio")
    fun registerDevice_bio(@Body model: RegisterDeviceBioReq): Call<RegisterDeviceBioResp>

    @POST("/register/verifyOtp")
    fun registerDevice_verifyOtp(@Body model: RegisterDeviceVerifyOtpReq): Call<RegisterDeviceVerifyOtpResp>
}