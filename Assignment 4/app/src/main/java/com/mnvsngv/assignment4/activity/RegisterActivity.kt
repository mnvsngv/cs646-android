package com.mnvsngv.assignment4.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
            backend.register(emailInput.text.toString(), userIDInput.text.toString(),
                nameInput.text.toString(), passwordInput.text.toString())
        }
    }

    override fun onRegisterSuccess() {
        Toast.makeText(this, "Created your account! Log in now!", Toast.LENGTH_SHORT).show()
    }
}
