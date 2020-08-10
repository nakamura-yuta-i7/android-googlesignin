package com.dena.googlesignin

import android.util.Log
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient

open class MyGoogleCalendar(
    val googleApi: GoogleApiClient?
) {
    init {
        if (googleApi == null) {
            throw Exception("GoogleApi Not Valid.")
        }
        if (!canAuthorized()) {
            throw Exception("GoogleApi Not Authorized.")
        }
    }

    fun canAuthorized(): Boolean {
        Log.d("canAuthorized", "koko1")
        val optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(googleApi)
        Log.d("canAuthorized", "koko2")
        return optionalPendingResult.isDone
    }


}