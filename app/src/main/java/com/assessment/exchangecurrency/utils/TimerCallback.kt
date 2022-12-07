package com.assessment.exchangecurrency.utils

import androidx.lifecycle.LifecycleOwner

interface TimerCallback: LifecycleOwner {

    fun onTick(millisUntilFinished: Long)

    fun onTimeOut()
}
