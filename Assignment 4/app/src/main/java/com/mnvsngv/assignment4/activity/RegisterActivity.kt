package com.mnvsngv.assignment4.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.mnvsngv.assignment4.R
import com.mnvsngv.assignment4.backend.FirebaseBackend
import com.mnvsngv.assignment4.backend.IBackendListener
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), IBackendListener {
    private val backend = FirebaseBackend(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton.setOnClickListener {
            if (areInputsValid()) {
                progressBar.visibility = View.VISIBLE
                backend.register(
                    emailInput.text.toString(), userIDInput.text.toString(),
                    nameInput.text.toString(), passwordInput.text.toString()
                )
            }
        }
    }

    private fun areInputsValid(): Boolean {

        var isValid = validate(emailInput, R.string.invalid_email) {
            TextUtils.isEmpty(it) || !Patterns.EMAIL_ADDRESS.matcher(it).matches()
        }

        val emptyValidationInputs =
            mapOf(userIDInput to R.string.invalid_user_id,
                nameInput to R.string.invalid_name,
                passwordInput to R.string.invalid_password,
                confirmPasswordInput to R.string.invalid_confirm_password)

        for ((input, errorID) in emptyValidationInputs) {
            isValid = validate(input, errorID) {
                TextUtils.isEmpty(it)
            } && isValid
        }

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

    override fun onRegisterSuccess() {
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show()
    }

    override fun onRegisterFailure(messageID: Int) {
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, messageID, Toast.LENGTH_SHORT).show()
    }
}
