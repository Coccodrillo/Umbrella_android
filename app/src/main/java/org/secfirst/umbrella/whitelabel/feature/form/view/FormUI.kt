package org.secfirst.umbrella.whitelabel.feature.form.view


import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.tintedCheckBox
import org.jetbrains.anko.appcompat.v7.tintedRadioButton
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.form.Answer
import org.secfirst.umbrella.whitelabel.data.database.form.Screen
import org.secfirst.umbrella.whitelabel.feature.form.FieldType
import org.secfirst.umbrella.whitelabel.feature.form.hasAnswer
import org.secfirst.umbrella.whitelabel.feature.form.view.controller.FormController


class FormUI(private val screen: Screen, private val answers: List<Answer>?) : AnkoComponent<FormController>, Step {

    override fun createView(ui: AnkoContext<FormController>) = ui.apply {
        val size = 16f
        val formTextColor = ContextCompat.getColor(ui.ctx, R.color.ms_black)

        scrollView {
            background = ColorDrawable(ContextCompat.getColor(context, R.color.form_background))
            verticalLayout {
                padding = dip(20)
                textView(screen.title){
                    textSize = 20f
                    textColor = R.color.umbrella_purple
                }.lparams{gravity = Gravity.CENTER_HORIZONTAL}

                screen.items.forEach { item ->
                    when (item.type) {

                        FieldType.LABEL.value ->
                            textView(item.label) {
                                textSize = 18f
                                textColor = formTextColor
                                padding = dip(10)
                            }.lparams { gravity = Gravity.CENTER }

                        FieldType.TEXT_AREA.value -> {
                            val answer = item.hasAnswer(answers)
                            textView(item.label) {
                                textSize = size
                                textColor = formTextColor
                            }.lparams { topMargin = dip(10) }
                            val editText = editText {
                                hint = item.hint
                                setText(answer.textInput)
                                textColor = formTextColor

                            }.lparams(width = matchParent)
//                            if (Build.VERSION.SDK_INT < 24) {
//                                editText.backgroundTintList = ColorStateList.valueOf(formTextColor)
//                            }
                            answer.itemId = item.id
                            bindEditText(answer, editText, ui)
                        }
                        FieldType.TEXT_INPUT.value -> {
                            val answer = item.hasAnswer(answers)
                            textView(item.label) {
                                textSize = size
                                textColor = ContextCompat.getColor(context, R.color.ms_black)
                            }.lparams { topMargin = dip(10) }
                            val editText = editText {
                                hint = item.hint
                                setText(answer.textInput)
                                hintTextColor = R.color.immersive_background
                                textColor = formTextColor
                            }.lparams(width = matchParent)
//                            if (Build.VERSION.SDK_INT < 24) {
//                                editText.backgroundTintList = ColorStateList.valueOf(formTextColor)
//                            }
                            answer.itemId = item.id
                            answer.run { bindEditText(answer, editText, ui) }
                        }
                        FieldType.MULTIPLE_CHOICE.value -> {
                            textView(item.label) {
                                textSize = size
                                textColor = ContextCompat.getColor(context, R.color.ms_black)
                            }.lparams { topMargin = dip(10) }
                            item.options.forEach { formOption ->
                                val answer = formOption.hasAnswer(answers)
                                val checkBox = tintedCheckBox {
                                    text = formOption.label
                                    textColor = formTextColor
                                    isChecked = answer.choiceInput
                                }
                                answer.optionId = formOption.id
//                                if (Build.VERSION.SDK_INT < 24) {
//                                    checkBox.buttonTintList = ColorStateList.valueOf(formTextColor)
//                                }
                                bindCheckBox(answer, checkBox, ui)
                            }
                        }
                        FieldType.SINGLE_CHOICE.value -> {
                            textView(item.label) {
                                textSize = size
                                textColor = formTextColor
                            }
                            item.options.forEach { formOption ->
                                val answer = formOption.hasAnswer(answers)
                                val radioButton = tintedRadioButton {
                                    text = formOption.label
                                    isChecked = answer.choiceInput
                                    textSize = size
                                    textColor = formTextColor
                                }
                                answer.optionId = formOption.id
//                                if (Build.VERSION.SDK_INT < 24) {
//                                    radioButton.buttonTintList = ColorStateList.valueOf(formTextColor)
//                                }
                                bindRadioButton(answer, radioButton, ui)
                            }
                        }
                    }
                }

            }.lparams(width = matchParent, height = matchParent)
        }

    }.view


    private fun bindRadioButton(answer: Answer, radioButton: RadioButton, ui: AnkoContext<FormController>) {
        val radioButtonMap = hashMapOf<RadioButton, Answer>()
        radioButtonMap[radioButton] = answer
        ui.owner.radioButtonList.add(radioButtonMap)
    }

    private fun bindCheckBox(answer: Answer, checkBox: CheckBox, ui: AnkoContext<FormController>) {
        val checkboxMap = hashMapOf<CheckBox, Answer>()
        checkboxMap[checkBox] = answer
        ui.owner.checkboxList.add(checkboxMap)
    }

    private fun bindEditText(answer: Answer, editText: EditText, ui: AnkoContext<FormController>) {
        val editTextMap = hashMapOf<EditText, Answer>()
        editTextMap[editText] = answer
        ui.owner.editTextList.add(editTextMap)
    }

    override fun onSelected() {}

    override fun verifyStep(): Nothing? = null

    override fun onError(error: VerificationError) {}

}