//package com.tiltedeight.android.bee.data
//
//import com.tiltedeight.android.bee.data.model.RegisterDeviceData
//import com.tiltedeight.android.bee.ws.Result
//import java.io.IOException
//
//
///**
// * Class that handles authentication w/ login credentials and retrieves user information.
// */
//class LoginDataSource {
//
////    private val tiltedEightService: TiltedEightService
//
//    constructor() {
//    }
//
//    fun login(username: String, password: String): Result<RegisterDeviceData> {
//        try {
//            // TODO: handle loggedInUser authentication
//            val fakeUser = RegisterDeviceData(java.util.UUID.randomUUID().toString(), "Jane Doe")
//            return Result.Success(fakeUser)
//        } catch (e: Throwable) {
//            return Result.Error(IOException("Error logging in", e))
//        }
//    }
//
//    fun logout() {
//        // TODO: revoke authentication
//    }
//}