package com.example.tipcalculator

import android.content.ContentValues.TAG
import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipcalculator.components.InputField
import com.example.tipcalculator.components.RoundButton
import com.example.tipcalculator.ui.theme.TipCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                Column() {
                    Center()
                }


            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    TipCalculatorTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            color = MaterialTheme.colors.background
        ) {
            content(

            )
        }
    }
}

@Composable
fun Header(totalPerPerson: Double = 0.0){
    Surface(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .height(150.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(25.dp))),
        color = Color(0xFFE7D7F1)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)

            Text(text = "Каждый должен заплатить:",
            style = MaterialTheme.typography.h5)
            Text(text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Center(){
    BillForm(){
        Log.d("TEST1", "Center: $it")

    }

}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValChange: (String) -> (Unit) = {}
             ){
    val context = LocalContext.current
    val totalState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalState.value) {
        totalState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    var sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val procents = (sliderPositionState.value * 100).toInt()

    var persons by remember {
        mutableStateOf(1)
    }

    val totalPersonState = remember {
        mutableStateOf(0.0)
    }

    Header(totalPerPerson = totalPersonState.value)


    Surface(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = CircleShape.copy(all = CornerSize(12.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)) {

        Column(modifier = Modifier.padding(6.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {

            InputField(
                valueState = totalState ,
                labelId = "Введите сумму" ,
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if (!validState) return@KeyboardActions


                    onValChange(totalState.value.trim())

                    keyboardController?.hide()

                })

            if (validState){



                Row(modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.Start){
                    Text(text = "Персоны:",
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ),
                    fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End) {

                        RoundButton(imageVector = Icons.Default.Remove, onClick = {
                            if (persons <= 1){
                                persons -= 0
                            }else{
                                persons -= 1
                                totalPersonState.value = calculatePerPerson(bill = totalState.value.toDouble(),persons = persons,procents = procents)
                            }

                        })
                        Text(text = "$persons",modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .align(alignment = Alignment.CenterVertically), fontSize = 22.sp)
                        RoundButton(imageVector = Icons.Default.Add, onClick = {
                            persons += 1
                            totalPersonState.value = calculatePerPerson(bill = totalState.value.toDouble(),persons = persons,procents = procents)
                        })
                    }
                }
                Row(modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.Start){
                    Text(text = "Чаевые:", modifier = Modifier.align(alignment = Alignment.CenterVertically), fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(168.dp))
                    Text(text = "$ ${tipAmountState.value}", modifier = Modifier.align(alignment = Alignment.CenterVertically), fontSize = 20.sp)
                }
                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Spacer(modifier = Modifier.height(15.dp))
                    Text(text = "$procents %", fontSize = 19.sp)
                    Spacer(modifier = Modifier.height(5.dp))
                    Slider(value = sliderPositionState.value,
                        onValueChange = {newVal ->

                            sliderPositionState.value = newVal
                            tipAmountState.value = calculateTip(totalState.value.toDouble(),procents)
                            totalPersonState.value = calculatePerPerson(bill = totalState.value.toDouble(),persons = persons,procents = procents)

                        }, modifier = Modifier.padding(horizontal = 16.dp))

                }
            }else{
                Box(){
                }
            }

        }

    }


}

fun calculateTip(bill: Double, procents: Int): Double {
    return if (bill > 1 && bill.toString().isNotEmpty())
        ((bill * procents) / 100)  else 0.0


}

fun calculatePerPerson(bill: Double, persons: Int,procents: Int): Double {
    val totalbill = calculateTip(bill = bill, procents = procents) + bill
    return if (bill > 1 && bill.toString().isNotEmpty())
        (totalbill / persons) else 0.0
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipCalculatorTheme {
        MyApp{
            Column() {

                Center()
            }
        }
    }
}

