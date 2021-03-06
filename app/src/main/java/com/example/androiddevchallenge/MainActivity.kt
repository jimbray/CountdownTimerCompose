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
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
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
@ExperimentalAnimationApi
@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        Main()
    }
}

@ExperimentalAnimationApi
@Composable
fun Main() {
    Column {
        Title(title = "Countdown")
        SettingArea()
        OperationArea()
        ColorBlock()
    }
}

@Composable
fun ColorBlock() {
    val viewModel: MainViewModel = viewModel()

    val animatedProgress by animateFloatAsState(
        targetValue = viewModel.progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    Box(
        Modifier
            .background(Color(0xFFD4237A).convert(ColorSpaces.CieXyz))
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(32.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column(Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Crossfade(targetState = viewModel.isTicking) { isTicking ->
                if (!isTicking) {
                    // 没开始
                    Text(text = "Ready?", Modifier.fillMaxWidth(), fontSize = 48.sp, color = Color.White)
                } else {
                    CircularProgressIndicator(
                        progress = animatedProgress,
                        strokeWidth = 16.dp,
                        color = Color.White,
                        modifier = Modifier.size(320.dp)
                    )

                }
            }

        }



    }
}

@Composable
fun OperationArea() {
    val viewModel: MainViewModel = viewModel()
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 16.dp), horizontalArrangement = Arrangement.Center
    ) {

        Button(onClick = {
            if (viewModel.isTicking) {
                // stop
                viewModel.stopTick()
            } else {
                viewModel.startTick()
            }
        }, Modifier.clip(RoundedCornerShape(8.dp))) {
            Text(text = if (viewModel.isTicking) "Stop" else "Start")
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun SettingArea() {
    val viewModel: MainViewModel = viewModel()

    Column {
        TimeArea(
            viewModel.curHour,
            viewModel.curMinute,
            viewModel.curSecond
        )
        AnimatedVisibility(!viewModel.isTicking) {
            NumberArea()
        }

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

                            MainViewModel.TimeUnit.HOUR -> {
                                if (viewModel.hourArray.isNullOrEmpty()) {
                                    // 没有填写数字
                                    viewModel.curTimeUnit = MainViewModel.TimeUnit.FULL
                                } else {
                                    viewModel.hourArray.removeAt(viewModel.hourArray.size - 1)
                                    viewModel.updateHourTimeArray()
                                }
                            }

                            MainViewModel.TimeUnit.MINUTE -> {
                                if (viewModel.minuteArray.isNullOrEmpty()) {
                                    // 没有填写数字
                                    viewModel.curTimeUnit = MainViewModel.TimeUnit.FULL
                                } else {
                                    viewModel.minuteArray.removeAt(viewModel.minuteArray.size - 1)
                                    viewModel.updateMinuteTimeArray()
                                }
                            }

                            MainViewModel.TimeUnit.SECOND -> {
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
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = viewModel.formatTime(hour), Modifier.clickable {
                    viewModel.curTimeUnit = MainViewModel.TimeUnit.HOUR
                    if (viewModel.curHour == 0) {
                        viewModel.hourArray.clear()
                    } else {
                        viewModel.curHour.toString()
                            .forEach { it -> viewModel.hourArray.add(it.toString()) }
                    }
                },
                fontSize = 72.sp,
                color = if (viewModel.curTimeUnit == MainViewModel.TimeUnit.HOUR || viewModel.curTimeUnit == MainViewModel.TimeUnit.FULL) {
                    Color(0xFFCD1247).convert(ColorSpaces.CieXyz)
                } else Color.Black
            )
            Text(
                text = "Hour",
                Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = ":", fontSize = 72.sp, color = Color(0xFFCD1247).convert(
                ColorSpaces.CieXyz
            )
        )
        Column(Modifier.weight(1f)) {
            Text(
                text = viewModel.formatTime(minute),
                Modifier.clickable {
                    viewModel.curTimeUnit = MainViewModel.TimeUnit.MINUTE
                    if (viewModel.curMinute == 0) {
                        viewModel.minuteArray.clear()
                    } else {
                        viewModel.curMinute.toString()
                            .forEach { it -> viewModel.minuteArray.add(it.toString()) }
                    }
                },
                fontSize = 72.sp,
                color = if (viewModel.curTimeUnit == MainViewModel.TimeUnit.MINUTE || viewModel.curTimeUnit == MainViewModel.TimeUnit.FULL) Color(
                    0xFFCD1247
                ).convert(ColorSpaces.CieXyz) else Color.Black
            )
            Text(
                text = "Minute",
                Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }


        Text(
            text = ":", fontSize = 72.sp, color = Color(0xFFCD1247).convert(
                ColorSpaces.CieXyz
            )
        )
        Column(Modifier.weight(1f)) {
            Text(
                text = viewModel.formatTime(second),
                Modifier.clickable {
                    viewModel.curTimeUnit = MainViewModel.TimeUnit.SECOND
                    if (viewModel.curSecond == 0) {
                        viewModel.secondArray.clear()
                    } else {
                        viewModel.curSecond.toString()
                            .forEach { it -> viewModel.secondArray.add(it.toString()) }
                    }
                },
                fontSize = 72.sp,
                color = if (viewModel.curTimeUnit == MainViewModel.TimeUnit.SECOND || viewModel.curTimeUnit == MainViewModel.TimeUnit.FULL) Color(
                    0xFFCD1247
                ).convert(ColorSpaces.CieXyz) else Color.Black
            )
            Text(
                text = "Second",
                Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

    }
}

@Composable
fun Title(title: String) {
    Text(text = title, Modifier.padding(8.dp), fontSize = 24.sp)
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
