package com.mnvsngv.assignment2.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
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

        if (savedInstanceState != null) checkedRadioButtonId = savedInstanceState.getInt(CHECKED_RADIO_BUTTON_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_question, container, false)

        fragmentView.questionText.text = question?.question

        fragmentView.radioButton1.text = question?.options?.get(0) ?: "Default"
        fragmentView.radioButton1.onClick {
            if (it != null) onRadioButtonChecked(it)
        }

        fragmentView.radioButton2.text = question?.options?.get(1) ?: "Default"
        fragmentView.radioButton2.onClick {
            if (it != null) onRadioButtonChecked(it)
        }

        fragmentView.radioButton3.text = question?.options?.get(2) ?: "Default"
        fragmentView.radioButton3.onClick {
            if (it != null) onRadioButtonChecked(it)
        }

        fragmentView.radioButton4.text = question?.options?.get(3) ?: "Default"
        fragmentView.radioButton4.onClick {
            if (it != null) onRadioButtonChecked(it)
        }

        return fragmentView
    }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CHECKED_RADIO_BUTTON_KEY, checkedRadioButtonId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            Log.i("tag", "Hey.")
            checkedRadioButtonId = savedInstanceState.getInt(CHECKED_RADIO_BUTTON_KEY)
            if (checkedRadioButtonId != -1) {
                Log.i("tag", "Sup.")
                optionsGroup.clearCheck()
                val checkedRadioButton = view?.findViewById<RadioButton>(checkedRadioButtonId)
//                view?.fragmentView.optionsGroup.clearCheck()
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
