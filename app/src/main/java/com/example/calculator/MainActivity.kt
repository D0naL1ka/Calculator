package com.example.calculator
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var display: TextView
    private lateinit var buttonZero: Button
    private lateinit var buttonOne: Button
    private lateinit var buttonTwo: Button
    private lateinit var buttonThree: Button
    private lateinit var buttonFour: Button
    private lateinit var buttonFive: Button
    private lateinit var buttonSix: Button
    private lateinit var buttonSeven: Button
    private lateinit var buttonEight: Button
    private lateinit var buttonNine: Button
    private lateinit var buttonAdd: Button
    private lateinit var buttonSubtract: Button
    private lateinit var buttonMultiply: Button
    private lateinit var buttonDivide: Button
    private lateinit var buttonAllClear: Button
    private lateinit var buttonDecimal: Button
    private lateinit var buttonPlusMinus: Button
    private lateinit var buttonPercent: Button
    private lateinit var buttonEquals: Button
    private lateinit var buttonExit: Button

    private var firstOperand: Double? = null
    private var pendingOperation = ""
    private var operatorJustClicked = false

    private var isResultDisplayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        display = findViewById(R.id.textViewDisplay)
        buttonZero = findViewById(R.id.buttonZero)
        buttonOne = findViewById(R.id.buttonOne)
        buttonTwo = findViewById(R.id.buttonTwo)
        buttonThree = findViewById(R.id.buttonThree)
        buttonFour = findViewById(R.id.buttonFour)
        buttonFive = findViewById(R.id.buttonFive)
        buttonSix = findViewById(R.id.buttonSix)
        buttonSeven = findViewById(R.id.buttonSeven)
        buttonEight = findViewById(R.id.buttonEight)
        buttonNine = findViewById(R.id.buttonNine)
        buttonAdd = findViewById(R.id.buttonAdd)
        buttonSubtract = findViewById(R.id.buttonSubtract)
        buttonMultiply = findViewById(R.id.buttonMultiply)
        buttonDivide = findViewById(R.id.buttonDivide)
        buttonAllClear = findViewById(R.id.buttonAllClear)
        buttonDecimal = findViewById(R.id.buttonDecimal)
        buttonPlusMinus = findViewById(R.id.buttonPlusMinus)
        buttonPercent = findViewById(R.id.buttonPercent)
        buttonEquals = findViewById(R.id.buttonEquals)
        buttonExit = findViewById(R.id.buttonExit)
    }

    private fun setupListeners() {
        val numberButtons = listOf(
            buttonZero, buttonOne, buttonTwo, buttonThree, buttonFour, buttonFive,
            buttonSix, buttonSeven, buttonEight, buttonNine
        )
        numberButtons.forEach { button ->
            button.setOnClickListener { onNumberClick(button.text.toString()) }
        }

        val operatorButtons = listOf(
            buttonAdd, buttonSubtract, buttonMultiply, buttonDivide, buttonPercent
        )
        operatorButtons.forEach { button ->
            button.setOnClickListener { onOperatorClick(button.text.toString()) }
        }

        buttonAllClear.setOnClickListener { onClearClick() }
        buttonDecimal.setOnClickListener { onDecimalClick() }
        buttonEquals.setOnClickListener { onEqualsClick() }
        buttonExit.setOnClickListener { finish() }
        buttonPlusMinus.setOnClickListener { onSignChangeClick() }
    }

    private fun onNumberClick(number: String) {
        if (isResultDisplayed) {
            display.text = number
            isResultDisplayed = false
        } else {
            val currentText = display.text.toString()
            if (currentText == "0") {
                display.text = number
            } else {
                display.append(number)
            }
        }
        operatorJustClicked = false
    }

    private fun onOperatorClick(operator: String) {
        if (operatorJustClicked) return

        if (pendingOperation.isNotEmpty()) {
            onEqualsClick()
        }
        isResultDisplayed = false

        firstOperand = display.text.toString().replace(',', '.').toDoubleOrNull()
        pendingOperation = operator
        display.append(operator)
        operatorJustClicked = true
    }

    private fun onEqualsClick() {
        if (firstOperand == null || pendingOperation.isEmpty() || operatorJustClicked) {
            return
        }

        val expression = display.text.toString()
        val secondOperandString = expression.substringAfter(pendingOperation)
        if (secondOperandString.isEmpty()) return
        val secondOperand = secondOperandString.replace(',', '.').toDouble()

        val result = performCalculation(firstOperand!!, secondOperand, pendingOperation)
        displayResult(result)

        pendingOperation = ""
        firstOperand = null
        isResultDisplayed = true
    }

    @SuppressLint("SetTextI18n")
    private fun performCalculation(num1: Double, num2: Double, operator: String): Double? {
        return when (operator) {
            "+" -> num1 + num2
            "−", "-" -> num1 - num2
            "×" -> num1 * num2
            "÷" -> {
                if (num2 == 0.0) {
                    display.text = "Error"
                    return null
                }
                num1 / num2
            }
            "%" -> (num1 * num2) / 100.0 // (X * Y) / 100
            else -> null
        }
    }

    private fun onClearClick() {
        firstOperand = null
        pendingOperation = ""
        operatorJustClicked = false
        isResultDisplayed = false
        display.text = "0"
    }

    private fun onDecimalClick() {
        if (isResultDisplayed) {
            display.text = "0,"
            isResultDisplayed = false
            return
        }
        val currentExpression = display.text.toString()
        val currentNumber = currentExpression.substringAfter(pendingOperation)
        if (!currentNumber.contains(",")) {
            display.append(",")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onSignChangeClick() {
        if (isResultDisplayed || operatorJustClicked) return

        val expression = display.text.toString()
        if (pendingOperation.isEmpty()) {
            if (expression.startsWith("-")) {
                display.text = expression.removePrefix("-")
            } else if (expression != "0") {
                display.text = "-$expression"
            }
        } else {
            val firstPart = expression.substringBefore(pendingOperation) + pendingOperation
            var secondPart = expression.substringAfter(pendingOperation)

            if (secondPart.startsWith("-")) {
                secondPart = secondPart.removePrefix("-")
            } else if (secondPart.isNotEmpty()) {
                secondPart = "-$secondPart"
            }
            display.text = firstPart + secondPart
        }
    }

    private fun displayResult(result: Double?) {
        if (result == null) return
        val formatter = DecimalFormat("0.########")
        val formattedResult = formatter.format(result)
        display.text = formattedResult.replace('.', ',')
    }
}

