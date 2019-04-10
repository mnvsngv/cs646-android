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
import org.jetbrains.anko.startActivity


class LoginActivity : AppCompatActivity(), IBackendListener, TextView.OnEditorActionListener {
    private val backend = FirebaseBackend(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (backend.isUserLoggedIn()) onLoginSuccess()

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
        var isValid = true

        val emailText = emailInput.text.toString().trim()
        if (TextUtils.isEmpty(emailText) || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            emailInput.error = getString(R.string.invalid_email)
            isValid = false
        }

        val passwordText = passwordInput.text.toString().trim()
        if (TextUtils.isEmpty(passwordText)) {
            passwordInput.error = getString(R.string.invalid_password)
            isValid = false
        }

        return isValid
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            backend.login(emailInput.text.toString(), passwordInput.text.toString())
            return true  // Event handled!
        }

        return false  // Event not handled!
    }

    override fun onLoginSuccess() {
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show()
    }

    override fun onLoginFailure(message: String) {
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
