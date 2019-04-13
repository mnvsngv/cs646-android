package com.mnvsngv.assignment4.singleton

import android.app.Activity
import com.mnvsngv.assignment4.backend.FirebaseBackend
import com.mnvsngv.assignment4.backend.IBackend
import com.mnvsngv.assignment4.backend.IBackendListener

object BackendInstance {
    fun getInstance(baseActivity: Activity, listener: IBackendListener): IBackend {
        return FirebaseBackend(baseActivity, listener)
    }
}