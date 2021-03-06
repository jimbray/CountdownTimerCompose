/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        Main()
    }
}

@Composable
fun Main() {
    Column {
        Title(title = "Countdown")
        OperationArea()
    }
}

@Composable
fun OperationArea() {
    val viewModel: MainViewModel = viewModel()
    Column {
        TimeArea(
            viewModel.curHour,
            viewModel.curMinute,
            viewModel.curSecond
        )
        NumberArea()
    }
}

@Composable
fun NumberArea() {
    val viewModel: MainViewModel = viewModel()
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            NumberText(text = "1", Modifier.weight(1f))
            NumberText(text = "2", Modifier.weight(1f))
            NumberText(text = "3", Modifier.weight(1f))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            NumberText(text = "4", Modifier.weight(1f))
            NumberText(text = "5", Modifier.weight(1f))
            NumberText(text = "6", Modifier.weight(1f))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            NumberText(text = "7", Modifier.weight(1f))
            NumberText(text = "8", Modifier.weight(1f))
            NumberText(text = "9", Modifier.weight(1f))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            NumberText(text = "0", Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "delete",
                Modifier
                    .weight(1f)
                    .size(28.dp)
                    .clickable {
                            when (viewModel.curTimeUnit) {
                                MainViewModel.TimeUnit.FULL -> {
                                    if (viewModel.fullTimeArray.isNullOrEmpty()) {
                                        viewModel.curHour = 0
                                        viewModel.curMinute = 0
                                        viewModel.curSecond = 0
                                    } else {
                                        viewModel.fullTimeArray.removeAt(viewModel.fullTimeArray.size - 1)
                                        viewModel.updateFullTimeArray()
                                    }

                                }

                                MainViewModel.TimeUnit.HOUR ->  {
                                    if (viewModel.hourArray.isNullOrEmpty()) {
                                        // 没有填写数字
                                        viewModel.curTimeUnit = MainViewModel.TimeUnit.FULL
                                    } else {
                                        viewModel.hourArray.removeAt(viewModel.hourArray.size - 1)
                                        viewModel.updateHourTimeArray()
                                    }
                                }

                                MainViewModel.TimeUnit.MINUTE ->  {
                                    if (viewModel.minuteArray.isNullOrEmpty()) {
                                        // 没有填写数字
                                        viewModel.curTimeUnit = MainViewModel.TimeUnit.FULL
                                    } else {
                                        viewModel.minuteArray.removeAt(viewModel.minuteArray.size - 1)
                                        viewModel.updateMinuteTimeArray()
                                    }
                                }

                                MainViewModel.TimeUnit.SECOND ->  {
                                    if (viewModel.secondArray.isNullOrEmpty()) {
                                        // 没有填写数字
                                        viewModel.curTimeUnit = MainViewModel.TimeUnit.FULL
                                    } else {
                                        viewModel.secondArray.removeAt(viewModel.secondArray.size - 1)
                                        viewModel.updateSecondTimeArray()
                                    }
                                }
                            }

                    }
            )
        }
    }
}

@Composable
fun NumberText(text: String, modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()
    Text(text = text, modifier = modifier.pointerInteropFilter { event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            Log.d("jimbray", "Down!!!!->${viewModel.curTimeUnit}")
            when (viewModel.curTimeUnit) {

                MainViewModel.TimeUnit.SECOND -> {
                    viewModel.secondArray.let { timeArray ->
                        if (timeArray.size < 2) {
                            // 还能继续输入
                            timeArray.add(text)
                            viewModel.updateSecondTimeArray()
                        }
                    }
                }

                MainViewModel.TimeUnit.MINUTE -> {
                    viewModel.minuteArray.let { timeArray ->
                        if (timeArray.size < 2) {
                            // 还能继续输入
                            timeArray.add(text)
                            viewModel.updateMinuteTimeArray()
                        }
                    }
                }

                MainViewModel.TimeUnit.HOUR -> {
                    viewModel.hourArray.let { timeArray ->
                        if (timeArray.size < 2) {
                            // 还能继续输入
                            timeArray.add(text)
                            viewModel.updateHourTimeArray()
                        }
                    }
                }

                MainViewModel.TimeUnit.FULL -> {
                    viewModel.fullTimeArray.let { timeArray ->
                        if (timeArray.size < 6) {
                            // 还能继续输入
                            timeArray.add(text)
                            viewModel.updateFullTimeArray()
                        }
                    }

                }
            }
        }
        true
    }, fontSize = 32.sp, color = Color.Black, textAlign = TextAlign.Center)
}

@Composable
fun TimeArea(hour: Int, minute: Int, second: Int) {
    val viewModel: MainViewModel = viewModel()
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = viewModel.formatTime(hour), Modifier.clickable {
                viewModel.curTimeUnit = MainViewModel.TimeUnit.HOUR
                if (viewModel.curHour == 0) {
                    viewModel.hourArray.clear()
                } else {
                    viewModel.curHour.toString().forEach { it-> viewModel.hourArray.add(it.toString()) }
                }
            },
            fontSize = 72.sp,
            color = if (viewModel.curTimeUnit == MainViewModel.TimeUnit.HOUR || viewModel.curTimeUnit == MainViewModel.TimeUnit.FULL) {
                Color(0xFFCD1247).convert(ColorSpaces.CieXyz)
            } else Color.Black)
        Text(
            text = ":", fontSize = 72.sp, color = Color(0xFFCD1247).convert(
                ColorSpaces.CieXyz
            )
        )
        Text(
            text = viewModel.formatTime(minute),
            Modifier.clickable {
                viewModel.curTimeUnit = MainViewModel.TimeUnit.MINUTE
                if (viewModel.curMinute == 0) {
                    viewModel.minuteArray.clear()
                } else {
                    viewModel.curMinute.toString().forEach { it-> viewModel.minuteArray.add(it.toString()) }
                }
            },
            fontSize = 72.sp,
            color = if (viewModel.curTimeUnit == MainViewModel.TimeUnit.MINUTE || viewModel.curTimeUnit == MainViewModel.TimeUnit.FULL) Color(0xFFCD1247).convert(ColorSpaces.CieXyz) else Color.Black)

        Text(
            text = ":", fontSize = 72.sp, color = Color(0xFFCD1247).convert(
                ColorSpaces.CieXyz
            )
        )
        Text(
            text = viewModel.formatTime(second),
            Modifier.clickable {
                viewModel.curTimeUnit = MainViewModel.TimeUnit.SECOND
                if (viewModel.curSecond == 0) {
                    viewModel.secondArray.clear()
                } else {
                    viewModel.curSecond.toString().forEach { it-> viewModel.secondArray.add(it.toString()) }
                }
            },
            fontSize = 72.sp,
            color = if (viewModel.curTimeUnit == MainViewModel.TimeUnit.SECOND || viewModel.curTimeUnit == MainViewModel.TimeUnit.FULL) Color(0xFFCD1247).convert(ColorSpaces.CieXyz) else Color.Black
        )
    }
}

@Composable
fun Title(title: String) {
    Text(text = title, Modifier.padding(8.dp), fontSize = 24.sp)
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
