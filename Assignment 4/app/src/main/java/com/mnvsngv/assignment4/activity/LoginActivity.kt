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
import com.mnvsngv.assignment4.backend.IBackendListener
import com.mnvsngv.assignment4.singleton.BackendInstance
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class LoginActivity : AppCompatActivity(), IBackendListener, TextView.OnEditorActionListener {

    private val backend = BackendInstance.getInstance(this, this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // If the previous user is still logged in then don't show the login screen.
        // This can be the case when the app is destroyed & recreated when the user presses the back button
        // after logging in.
        if (backend.getCurrentUser() != null) onLoginSuccess()

        loginButton.setOnClickListener {
            if (areInputsValid()) {
                progressBar.visibility = View.VISIBLE
                backend.login(emailInput.text.toString(), passwordInput.text.toString())
            }
        }

        registerButton.setOnClickListener {
            startActivity<RegisterActivity>()
        }

        // Initiate login when the user presses enter when on the password field
        passwordInput.setOnEditorActionListener(this)
    }

    // Handle ENTER button pressed on the password field
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
        startActivity(intentFor<InstaPostActivity>().newTask().clearTop())
        finish()
    }

    override fun onLoginFailure(messageID: Int) {
        progressBar.visibility = View.INVISIBLE
        Toast.makeText(this, messageID, Toast.LENGTH_SHORT).show()
    }


    private fun areInputsValid(): Boolean {
        var isValid = validate(emailInput, R.string.invalid_email) {
            TextUtils.isEmpty(it) || !Patterns.EMAIL_ADDRESS.matcher(it).matches()
        }

        // The validate() call is done before the AND with isValid.
        // This is because if isValid is false, then validate() is not called as a "shortcut".
        // In that case the user would not see all the invalid inputs at the same time.
        isValid = validate(passwordInput, R.string.invalid_password) {
            TextUtils.isEmpty(it)
        } && isValid

        return isValid
    }

    // Run an invalidation test on a given text field
    private fun validate(view: TextView, errorMessageID: Int, isInvalid: (String) -> Boolean): Boolean {
        val textToValidate = view.text.toString()
        return if (isInvalid(textToValidate)) {
            view.error = getString(errorMessageID)
            false
        } else true
    }
}
