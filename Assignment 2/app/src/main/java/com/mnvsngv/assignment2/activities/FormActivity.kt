package com.mnvsngv.assignment2.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mnvsngv.assignment2.R
import kotlinx.android.synthetic.main.activity_form.*
import org.jetbrains.anko.startActivityForResult
import org.json.JSONException
import org.json.JSONObject
import java.io.File


private const val LOG_TAG = "mnvsngv_logs"
private const val INTENT_REQUEST = 1
private const val USER_INFO_FILE_NAME = "user_information.json"
private const val FIRST_NAME_KEY = "firstName"
private const val FAMILY_NAME_KEY = "familyName"
private const val NICKNAME_KEY = "nickname"
private const val AGE_KEY = "age"
private const val SCORE_KEY = "score"


class FormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        retrieveUserInformation()

        submitFormButton.setOnClickListener {
            if (areValidTextInputs()) {  // If the user has filled all form fields, start the quiz
                saveUserInformation(resultTextValue.text.toString())
                startActivityForResult<QuizActivity>(INTENT_REQUEST)
            }
        }
    }

    // Save user info
    override fun onDestroy() {
        super.onDestroy()
        saveUserInformation(resultTextValue.text.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != INTENT_REQUEST) {  // Ensure this is for the intended intent!
            return
        }
        when (resultCode) {
            Activity.RESULT_OK -> {  // Show the score from the received intent
                val correctAnswers = data?.getIntExtra("result", 0)
                if (correctAnswers != null) showScore(correctAnswers.toString())
            }
            Activity.RESULT_CANCELED -> {  // Show a toast to indicate the quiz was cancelled
                Toast.makeText(this, "Come back to the quiz again soon!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Validates that the user has filled in all the fields
    private fun areValidTextInputs(): Boolean {
        var isValid = true

        if (firstNameInput.text.toString().trim() == "") {
            firstNameInput.error = "Please enter your first name!"
            isValid = false
        }
        if (familyNameInput.text.toString().trim() == "") {
            familyNameInput.error = "Please enter your family name!"
            isValid = false
        }
        if (nicknameInput.text.toString().trim() == "") {
            nicknameInput.error = "Please enter your nickname!"
            isValid = false
        }
        if (ageInput.text.toString().trim() == "") {
            ageInput.error = "Please enter your age!"
            isValid = false
        }

        return isValid
    }

    // Save user info to file
    private fun saveUserInformation(score: String) {
        val firstName = firstNameInput.text.toString()
        val familyName = familyNameInput.text.toString()
        val nickname = nicknameInput.text.toString()
        val age = ageInput.text.toString()

        val infoJsonObject = JSONObject()
        infoJsonObject.put(FIRST_NAME_KEY, firstName)
        infoJsonObject.put(FAMILY_NAME_KEY, familyName)
        infoJsonObject.put(NICKNAME_KEY, nickname)
        infoJsonObject.put(AGE_KEY, age)
        if (score.isNotEmpty()) infoJsonObject.put(SCORE_KEY, score)

        openFileOutput(USER_INFO_FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(infoJsonObject.toString().toByteArray())
        }
    }

    // Restore user info from file
    private fun retrieveUserInformation() {
        if (File(filesDir, USER_INFO_FILE_NAME).exists()) {
            openFileInput(USER_INFO_FILE_NAME).use {
                val jsonDataString = String(it.readBytes())

                if (jsonDataString.isNotEmpty()) {
                    val infoJsonObject = JSONObject(jsonDataString)

                    firstNameInput.setText(infoJsonObject.getString(FIRST_NAME_KEY))
                    familyNameInput.setText(infoJsonObject.getString(FAMILY_NAME_KEY))
                    nicknameInput.setText(infoJsonObject.getString(NICKNAME_KEY))
                    ageInput.setText(infoJsonObject.getString(AGE_KEY))

                    try {
                        val score = infoJsonObject.getInt(SCORE_KEY)
                        showScore(score.toString())
                    } catch (e: JSONException) {
                        Log.i(LOG_TAG, "No score yet for this user.")
                    }
                }

            }
        }
    }

    // Convenience method to update the score on the UI
    private fun showScore(score: String) {
        resultTextValue.text = score
        resultTextLabel.visibility = View.VISIBLE
        verticalDivider.visibility = View.VISIBLE
    }

}
