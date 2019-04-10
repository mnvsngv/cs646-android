package com.mnvsngv.assignment4.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.backend.FirebaseBackend
import com.mnvsngv.assignment4.backend.IBackendListener
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity


class LoginActivity : AppCompatActivity(), IBackendListener, TextView.OnEditorActionListener {
    private val backend = FirebaseBackend(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        if (backend.isUserLoggedIn()) onLoginSuccess()

        loginButton.setOnClickListener {
            if (areInputsValid()) {
                progressBar.visibility = View.VISIBLE
                backend.login(emailInput.text.toString(), passwordInput.text.toString())
            }
        }

        registerButton.setOnClickListener {
            startActivity<RegisterActivity>()
        }

        passwordInput.setOnEditorActionListener(this)
    }

    private fun areInputsValid(): Boolean {

        var isValid = validate(emailInput, R.string.invalid_email) {
            TextUtils.isEmpty(it) || !Patterns.EMAIL_ADDRESS.matcher(it).matches()
        }

        isValid = validate(passwordInput, R.string.invalid_password) {
            TextUtils.isEmpty(it)
        } && isValid

        return isValid
    }

    private fun validate(view: TextView, errorMessageID: Int, isInvalid: (String) -> Boolean): Boolean {
        val textToValidate = view.text.toString()
        if (isInvalid(textToValidate)) {
            view.error = getString(errorMessageID)
            return false
        }

        return true
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            progressBar.visibility = View.VISIBLE
            backend.login(emailInput.text.toString(), passwordInput.text.toString())
            return true  // Event handled!
        }

        return false  // Event not handled!
    }

    override fun onLoginSuccess() {
        progressBar.visibility = View.INVISIBLE
//        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show()
        startActivity(intentFor<InstaPostActivity>().newTask().clearTop())
        finish()
    }

    override fun onLoginFailure(messageID: Int) {
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, messageID, Toast.LENGTH_SHORT).show()
    }
}
