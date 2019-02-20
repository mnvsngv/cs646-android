package com.mnvsngv.assignment2.activities

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Xml
import android.view.View
import com.mnvsngv.assignment2.R
import com.mnvsngv.assignment2.data_classes.Question
import com.mnvsngv.assignment2.fragments.QuestionFragment
import kotlinx.android.synthetic.main.activity_quiz.*
import org.xmlpull.v1.XmlPullParser
import java.io.InputStreamReader


private const val QUESTIONS_FILE_PATH = "questions.xml"
private const val QUESTION_FRAGMENT_TAG = "questionFragment"
private const val PARSER_CURRENT_LINE_KEY = "parserLine"
private const val QUESTION_KEY = "question"
private const val CORRECT_ANSWERS_KEY = "correctAnswers"


class QuizActivity : AppCompatActivity(), QuestionFragment.QuestionFragmentListener {

    private val parser: XmlPullParser = Xml.newPullParser()
    private var correctAnswers = 0
    private var currentQuestion: Question? = Question()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // We're going to parse the questions file with an XmlPullParser so set its input
        parser.setInput(InputStreamReader(assets.open(QUESTIONS_FILE_PATH)))
        var currentFragment: QuestionFragment

        if (savedInstanceState != null) {  // we need to restore the current question into the existing fragment
            val previousParserLine = savedInstanceState.getInt(PARSER_CURRENT_LINE_KEY)

            // Move the XmlPullParser to the line number that it was at previously
            while (parser.lineNumber < previousParserLine) {
                parser.next()
            }

            // Now restore the rest of the values that the activity needs to track its current state
            currentFragment = supportFragmentManager.findFragmentByTag(QUESTION_FRAGMENT_TAG) as QuestionFragment
            correctAnswers = savedInstanceState.getInt(CORRECT_ANSWERS_KEY)
            currentQuestion = savedInstanceState.getSerializable(QUESTION_KEY) as Question

        } else {  // No bundle, so we have to create a new fragment with a new question
            currentQuestion = parseForNextQuestion()
            currentFragment = QuestionFragment.newInstance(currentQuestion as Question)
            setQuestionFragment(currentFragment)
        }

        nextQuestionButton.setOnClickListener {
            val answer = currentFragment.getSelectedAnswer()  // Get what the user selected

            if (answer == currentQuestion?.answer) {  // Record the answer as necessary
                correctAnswers++
            }

            // Try to get a new question
            currentQuestion = parseForNextQuestion()
            if (currentQuestion != null) {  // We've got a new question so put it into a new fragment & display
                currentFragment = QuestionFragment.newInstance(currentQuestion as Question)
                setQuestionFragment(currentFragment)
                nextQuestionButton.visibility = View.INVISIBLE
            } else {  // We're done with all questions so send back the results to the calling activity
                val intentToPass = intent
                intentToPass.putExtra("result", correctAnswers)
                setResult(Activity.RESULT_OK, intentToPass)
                finish()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(PARSER_CURRENT_LINE_KEY, parser.lineNumber)
        outState?.putSerializable(QUESTION_KEY, currentQuestion)
        outState?.putInt(CORRECT_ANSWERS_KEY, correctAnswers)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, null)
        finish()
    }

    override fun onOptionSelect() {
        nextQuestionButton.visibility = View.VISIBLE
    }


    // Convenience method to replace the activity's existing fragment with a new instance
    private fun setQuestionFragment(fragment: QuestionFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.questionFragment, fragment, QUESTION_FRAGMENT_TAG)
        transaction.commit()
    }

    private fun parseForNextQuestion(): Question? {
        var eventType = parser.eventType
        val question = Question()
        var isQuestionFinished = false

        // We'll go through the XML file, element by element, till we are at the end of the document.
        while (eventType != XmlPullParser.END_DOCUMENT) {

            // By inspecting start and end tags, we can construct the question object piece by piece.
            // When we find a start tag, then we'll add that piece to the question.
            // When we find an end tag, we'll check to see if we've finished creating the question.
            when (eventType) {
                XmlPullParser.START_TAG -> processStartTag(question)
                XmlPullParser.END_TAG -> isQuestionFinished = processEndTag()
            }

            // Ensure that we'll use the next element in the next call to this function.
            eventType = parser.next()

            // If we've successfully created a question then we've got to to stop parsing the XML file for now.
            if (isQuestionFinished) break
        }

        return if (isQuestionFinished) question else null
    }

    private fun processStartTag(question: Question) {
        when (parser.name) {
            "text" -> {
                parser.next()  // Move from the tag to the text
                question.question = parser.text
            }
            "option" -> {
                if (parser.attributeCount > 0) question.answer = question.options.size + 1
                parser.next()  // Move from the tag to the text
                question.options.add(parser.text)
            }
        }
    }

    private fun processEndTag(): Boolean {
        return if (parser.name == "question") {
            parser.next()  // Move from the tag to the text
            true
        } else false
    }
}
