package com.mnvsngv.assignment4.singleton

import android.app.Activity
import android.util.Log
import com.mnvsngv.assignment4.backend.FirebaseBackend
import com.mnvsngv.assignment4.backend.IBackend
import com.mnvsngv.assignment4.backend.IBackendListener

// TODO fix having to recreate the backend instance for each activity
object BackendInstance {
    private var backend: IBackend? = null

    fun getInstance(baseActivity: Activity, listener: IBackendListener): IBackend {
        Log.i("BackendInstance", "Using listener $listener")
//        if (backend == null) {
            backend = FirebaseBackend(baseActivity, listener)
//        }
//        else {
//            (backend as FirebaseBackend).listener = listener
//        }
        return backend as IBackend
    }
}