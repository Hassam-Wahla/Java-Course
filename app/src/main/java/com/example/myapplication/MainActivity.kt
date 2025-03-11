package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Calculator(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Calculator(modifier: Modifier = Modifier) {
    var currentInput by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Center content (input/result) in the middle of the screen
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top, // Pushes the result at the top initially
            horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
        ) {
            // Display Result or current input
            Text(
                text = result.ifEmpty { currentInput },
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically),
                maxLines = 1
            )

            // Spacer to push the buttons to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // Buttons for calculator
            Column {
                val buttonLabels = listOf(
                    listOf("7", "8", "9", "/"),
                    listOf("4", "5", "6", "*"),
                    listOf("1", "2", "3", "-"),
                    listOf("0", ".", "=", "+"),
                    listOf("C") // Clear button at the bottom
                )
                for (row in buttonLabels) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp), // Add space between rows
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Add spacing between buttons
                    ) {
                        for (label in row) {
                            Button(
                                onClick = { onButtonClick(label, currentInput, result, onResultUpdate = { result = it }, onInputUpdate = { currentInput = it }) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp), // Ensuring consistent button height
                                shape = MaterialTheme.shapes.small.copy(CornerSize(16.dp)),
                            ) {
                                Text(text = label, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun onButtonClick(label: String, currentInput: String, result: String, onResultUpdate: (String) -> Unit, onInputUpdate: (String) -> Unit) {
    when (label) {
        "=" -> {
            // Evaluate the input expression
            try {
                val calculatedResult = calculateExpression(currentInput)
                onResultUpdate(calculatedResult)
            } catch (e: Exception) {
                onResultUpdate("Error")
            }
            onInputUpdate("") // Reset input after showing result
        }
        "C" -> {
            // Clear the input or result
            onInputUpdate("")
            onResultUpdate("")
        }
        else -> {
            // If there is already a result, replace it with the new input
            if (result.isNotEmpty()) {
                // Reset input with the result and append the new operator
                onInputUpdate(result + label)
                onResultUpdate("") // Reset result so that further calculations work
            } else {
                onInputUpdate(currentInput + label)
            }
        }
    }
}

fun calculateExpression(expression: String): String {
    return try {
        val result = evalArithmeticExpression(expression)
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun evalArithmeticExpression(expression: String): Double {
    var result = expression
    result = result.replace("÷", "/")
    result = result.replace("×", "*")

    // Add handling for negative numbers: fix issue when the expression starts with a minus sign
    if (result.startsWith("-")) {
        result = "0$result"
    }

    val evalResult = try {
        val tokens = result.split(Regex("(?<=\\d)(?=[-+*/])|(?<=[-+*/])(?=\\d)"))
        var res = tokens[0].toDouble()
        for (i in 1 until tokens.size step 2) { // Process the operators and operands
            val operand = tokens[i + 1].toDouble()
            when (tokens[i]) {
                "+" -> res += operand
                "-" -> res -= operand
                "*" -> res *= operand
                "/" -> res /= operand
            }
        }
        res
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid expression")
    }
    return evalResult
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    MyApplicationTheme {
        Calculator()
    }
}