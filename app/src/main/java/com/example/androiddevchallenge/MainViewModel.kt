package com.example.androiddevchallenge

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class MainViewModel : ViewModel() {

    enum class TimeUnit {
        FULL, HOUR, MINUTE, SECOND
    }

    var curTimeUnit: Enum<TimeUnit> by mutableStateOf(TimeUnit.FULL)


    var curHour: Int by mutableStateOf(0)

    var curMinute: Int by mutableStateOf(0)

    var curSecond: Int by mutableStateOf(0)


    val fullTimeArray: ArrayList<String> by mutableStateOf(ArrayList())

    val hourArray: ArrayList<String> by mutableStateOf(ArrayList())

    val minuteArray: ArrayList<String> by mutableStateOf(ArrayList())

    val secondArray: ArrayList<String> by mutableStateOf(ArrayList())

    var isTicking: Boolean by mutableStateOf(false)

    var progress: Float by mutableStateOf(0f)

    var tickTimer: CountDownTimer? = null



    fun formatTime(time: Int): String {
        if (time < 10) {
            return "0$time"
        } else {
            return "$time"
        }
    }

    fun updateSecondTimeArray() {
        if (secondArray.isNullOrEmpty()) {
            curSecond = 0
        } else {
            if (secondArray.size == 1) {
                curSecond = secondArray[0].toInt()
            } else if (secondArray.size == 2) {
                curSecond = secondArray[0].toInt()*10 + secondArray[1].toInt()
            }
        }
    }

    fun updateMinuteTimeArray() {
        if (minuteArray.isNullOrEmpty()) {
            curMinute = 0
        } else {
            if (minuteArray.size == 1) {
                curMinute = minuteArray[0].toInt()
            } else if (minuteArray.size == 2) {
                curMinute = minuteArray[0].toInt()*10 + minuteArray[1].toInt()
            }
        }
    }

    fun updateHourTimeArray() {
        if (hourArray.isNullOrEmpty()) {
            curHour = 0
        } else {
            if (hourArray.size == 1) {
                curHour = hourArray[0].toInt()
            } else if (hourArray.size == 2) {
                curHour = hourArray[0].toInt()*10 + hourArray[1].toInt()
            }
        }
    }

    fun updateFullTimeArray() {
        if (fullTimeArray.isNullOrEmpty()) {
            curHour = 0
            curMinute = 0
            curSecond = 0
        } else {
            if (fullTimeArray.size <= 2) {
                // 计算秒
                var second = 0
                if (fullTimeArray.size == 1) {
                    second = fullTimeArray[0].toInt()
                } else {
                    second = fullTimeArray[0].toInt() * 10 + fullTimeArray[1].toInt()
                }

                curSecond = second
                curMinute = 0
                curHour = 0
            } else if (fullTimeArray.size <= 4) {
                // 计算 分秒
                var minute = 0
                var second = 0

                if (fullTimeArray.size == 3) {
                    minute = fullTimeArray[0].toInt()
                    second = fullTimeArray[1].toInt() * 10 + fullTimeArray[2].toInt()
                } else {
                    minute = fullTimeArray[0].toInt() * 10 + fullTimeArray[1].toInt()
                    second = fullTimeArray[2].toInt() * 10 + fullTimeArray[3].toInt()
                }

                curSecond = second
                curMinute = minute
                curHour = 0
            } else if (fullTimeArray.size <= 6) {
                // 计算时分秒
                var hour = 0
                var minute = 0
                var second = 0
                if (fullTimeArray.size == 5) {
                    hour = fullTimeArray[0].toInt()
                    minute = fullTimeArray[1].toInt() * 10 + fullTimeArray[2].toInt()
                    second = fullTimeArray[3].toInt() * 10 + fullTimeArray[4].toInt()
                } else {
                    hour = fullTimeArray[0].toInt() * 10 + fullTimeArray[1].toInt()
                    minute = fullTimeArray[2].toInt() * 10 + fullTimeArray[3].toInt()
                    second = fullTimeArray[4].toInt() * 10 + fullTimeArray[5].toInt()
                }

                curSecond = second
                curMinute = minute
                curHour = hour
            }
        }


    }

    fun startTick() {
        curTimeUnit = TimeUnit.FULL
        isTicking = true

        var totalSecond = 0
        if (curHour != 0) {
            totalSecond += curHour*60*60
        }

        if (curMinute != 0) {
            totalSecond += curMinute*60
        }

        if (curSecond != 0) {
            totalSecond += curSecond
        }

        // millseconds 需要加上1秒的多余时间
        val tickTime = (totalSecond * 1000).toLong()

        if (curSecond >= 60) {
            curMinute += curSecond / 60
            curSecond %= 60
        }

        if (curMinute >= 60) {
            curHour += curMinute / 60
            curMinute %= 60
        }

        tickTimer = object : CountDownTimer(tickTime, 1) {
            override fun onTick(millisUntilFinished: Long) {
                // 剩余时间需要转换为时分秒
                mills2HMS(millisUntilFinished)
//                Log.d("jimbray", "${millisUntilFinished}")
//                val percent = ((tickTime - millisUntilFinished) / tickTime).toFloat() / 1000f
//                progress = percent
//                Log.d("jimbray", "tickTime : ${tickTime/1000} | mulllisUtilFinished : ${millisUntilFinished/1000} | percent : $percent | progress: $progress")
//                val percentageCompleted = 100 - millisUntilFinished / (tickTime / 100)
                val percentageCompleted = 100 - millisUntilFinished * 100 / tickTime
                progress = percentageCompleted / 100f
                Log.d("jimbray", "${millisUntilFinished / (tickTime / 100)}")
            }

            override fun onFinish() {

                Timer().schedule(1000) {
                    isTicking = false
                }
                progress = 1f
                curHour = 0
                curMinute = 0
                curSecond = 0
                fullTimeArray.clear()
                hourArray.clear()
                minuteArray.clear()
                secondArray.clear()
            }

        }

        progress = 0f

        tickTimer?.start()
    }


    fun stopTick() {
        isTicking = false
        progress = 0f
        tickTimer?.cancel()
    }

    fun mills2HMS(mills: Long) {
        val totalSecond = mills / 1000
        curSecond = (totalSecond % 60).toInt()
        var minute = totalSecond / 60
        curMinute = (minute % 60).toInt()
        curHour = (minute / 60).toInt()
    }
}