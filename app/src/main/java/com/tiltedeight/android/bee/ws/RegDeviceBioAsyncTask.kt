package com.tiltedeight.android.bee.ws

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.ads.identifier.AdvertisingIdClient
import com.tiltedeight.android.bee.BuildConfig
import com.tiltedeight.android.bee.data.model.RegisterDeviceData
import com.tiltedeight.android.bee.util.DeviceIdUtils
import com.tiltedeight.android.bee.ws.model.RegisterDeviceBioReq
import com.tiltedeight.android.bee.ws.model.RegisterDeviceBioResp
import com.tiltedeight.android.bee.ws.model.RegisterDeviceContactReq
import com.tiltedeight.android.bee.ws.model.RegisterDeviceContactResp
import java.util.*


class RegDeviceBioAsyncTask(
    val registerDeviceData: RegisterDeviceData, val context: Context,
    val postExecuteAction: (RegisterDeviceBioResp?) -> Unit
) :
    AsyncTask<Void, Void, RegisterDeviceBioResp>() {

    override fun doInBackground(vararg params: Void?): RegisterDeviceBioResp? {
        val regDeviceBio = RegisterDeviceBioReq(
            registrationId = registerDeviceData.registrationId,
            fullName = registerDeviceData.fullName,
            dob = registerDeviceData.dob,
            gender = registerDeviceData.gender,
            recaptchaHash = registerDeviceData.recaptchaHash
        )
        val req = WS.TiltedEight.registerDevice_bio(regDeviceBio)

        val resp = req.execute()

        return resp.body()
    }

    override fun onPostExecute(result: RegisterDeviceBioResp?) {
        super.onPostExecute(result)
        postExecuteAction(result)
    }
}