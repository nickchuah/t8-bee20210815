package com.tiltedeight.android.bee.ws

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.ads.identifier.AdvertisingIdClient
import com.tiltedeight.android.bee.BuildConfig
import com.tiltedeight.android.bee.data.model.RegisterDeviceData
import com.tiltedeight.android.bee.util.DeviceIdUtils
import com.tiltedeight.android.bee.ws.model.RegisterDeviceContactReq
import com.tiltedeight.android.bee.ws.model.RegisterDeviceContactResp


class RegDeviceContactAsyncTask(
        val registerDeviceData: RegisterDeviceData, val context: Context,
        val postExecuteAction: (RegisterDeviceContactResp?) -> Unit
) : AsyncTask<Void, Void, RegisterDeviceContactResp>() {

    private var vendorKey: String? = null
    private var gaid: String? = null

    init {
        fun getVendorKey(): String? {
            val ai: ApplicationInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(),
                    PackageManager.GET_META_DATA
            )
            val bundle = ai.metaData
            return bundle.getString("te_vendor_key")
        }

        fun getGaid(): String? {
            try {
                return AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext()).get().id
            } catch (ex: Exception){
                ex.printStackTrace()
            }
            return null;
        }

        vendorKey = getVendorKey()
//        gaid = getGaid()
    }

    override fun doInBackground(vararg params: Void?): RegisterDeviceContactResp? {
        val regDeviceContact = RegisterDeviceContactReq(
            vendorKey = vendorKey,
            appBundleId = "",//BuildConfig.APPLICATION_ID,
            email = registerDeviceData.email,
            phoneNum = registerDeviceData.mobileNum!!,
            deviceId = DeviceIdUtils.uniquePsuedoID,
            gaid = gaid,
            fcmToken = registerDeviceData.fcmToken,
            extra = null
        )
        val req = WS.TiltedEight.registerDevice_contact(regDeviceContact)

        val resp = req.execute()

        return resp.body()
    }

    override fun onPostExecute(result: RegisterDeviceContactResp?) {
        super.onPostExecute(result)
        registerDeviceData.registrationId = result?.registrationId
        postExecuteAction(result)
    }
}