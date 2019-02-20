package com.mnvsngv.assignment2.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.mnvsngv.assignment2.R
import com.mnvsngv.assignment2.data_classes.Question
import kotlinx.android.synthetic.main.fragment_question.*
import kotlinx.android.synthetic.main.fragment_question.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick


private const val QUESTION_PARAM = "question"
private const val CHECKED_RADIO_BUTTON_KEY = "checkedRadioButtonId"


class QuestionFragment : Fragment() {
    private var question: Question? = null
    private var listener: QuestionFragmentListener? = null
    private var answer: Int = -1
    private var checkedRadioButtonId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            question = it.getSerializable(QUESTION_PARAM) as Question
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_question, container, false)

        fragmentView.questionText.text = question?.question

        // Set up all the radio buttons in our options
        for (i in 0 until fragmentView.optionsGroup.childCount) {
            val radioButton = fragmentView.optionsGroup.getChildAt(i) as RadioButton

            radioButton.text = question?.options?.get(i) ?: "Default"
            radioButton.onClick {
                if (it != null) onRadioButtonChecked(it)
            }
        }

        return fragmentView
    }

    // Store the user's selection & update the parent activity
    private fun onRadioButtonChecked(radioButton: View) {
        answer = radioButton.tag.toString().toInt()
        listener?.onOptionSelect()
        checkedRadioButtonId = radioButton.id
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is QuestionFragmentListener) {
            listener = context
        }
    }

    // Need to save the user's selection
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CHECKED_RADIO_BUTTON_KEY, checkedRadioButtonId)
    }

    // The Fragment equivalent of onRestoreInstanceState: need to restore the user's radio button selection here
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            checkedRadioButtonId = savedInstanceState.getInt(CHECKED_RADIO_BUTTON_KEY)
            if (checkedRadioButtonId != -1) {
                optionsGroup.clearCheck()
                val checkedRadioButton = view?.findViewById<RadioButton>(checkedRadioButtonId)
                checkedRadioButton?.isChecked = true
                onRadioButtonChecked(checkedRadioButton as RadioButton)
            }
        }
    }

    fun getSelectedAnswer(): Int {
        return answer
    }

    interface QuestionFragmentListener {
        fun onOptionSelect()
    }

    companion object {
        @JvmStatic
        fun newInstance(questionParam: Question) =
            QuestionFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(QUESTION_PARAM, questionParam)
                }
            }
    }
}
