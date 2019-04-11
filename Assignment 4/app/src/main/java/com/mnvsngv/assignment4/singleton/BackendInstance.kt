package com.mnvsngv.assignment4.singleton

import android.app.Activity
import com.mnvsngv.assignment4.backend.FirebaseBackend
import com.mnvsngv.assignment4.backend.IBackend
import com.mnvsngv.assignment4.backend.IBackendListener

object BackendInstance {
    private var backend: IBackend? = null

    fun getInstance(baseActivity: Activity, listener: IBackendListener): IBackend {
        if (backend == null) {
            backend = FirebaseBackend(baseActivity, listener)
        }
        else {
            (backend as FirebaseBackend).listener = listener
        }
        return backend as IBackend
    }
}