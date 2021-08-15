package com.tiltedeight.android.bee.ws

import android.content.Context
import android.os.AsyncTask
import com.tiltedeight.android.bee.data.model.RegisterDeviceData
import com.tiltedeight.android.bee.ws.model.RegisterDeviceVerifyOtpReq
import com.tiltedeight.android.bee.ws.model.RegisterDeviceVerifyOtpResp


class RegDeviceVerifyOtpAsyncTask(
    val registerDeviceData: RegisterDeviceData, val context: Context,
    val postExecuteAction: (RegisterDeviceVerifyOtpResp?) -> Unit
) :
    AsyncTask<Void, Void, RegisterDeviceVerifyOtpResp>() {

    override fun doInBackground(vararg params: Void?): RegisterDeviceVerifyOtpResp? {
        val regDeviceVerifyOtp = RegisterDeviceVerifyOtpReq(
                registrationId = registerDeviceData.registrationId,
                otp = registerDeviceData.otp
        )
        val req = WS.TiltedEight.registerDevice_verifyOtp(regDeviceVerifyOtp)

        val resp = req.execute()

        return resp.body()
    }

    override fun onPostExecute(result: RegisterDeviceVerifyOtpResp?) {
        super.onPostExecute(result)
        postExecuteAction(result)
    }
}