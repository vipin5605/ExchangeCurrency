package com.assessment.exchangecurrency.utils

import android.os.CountDownTimer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class LifecycleAwareTimer(
    private val duration: Long,
    private val interval: Long,
    private val callback: TimerCallback
) : DefaultLifecycleObserver {

    private val stopAt: Long = System.currentTimeMillis() + duration
    private var timer: CountDownTimer? = null
    private val expired: Boolean
        get() = (stopAt - System.currentTimeMillis()) <= 0

    init {
        callback.lifecycle.addObserver(this)
    }

    /**
     * Create and start a CountDownTimer if needed. Also discards the previous timer (since timer
     * cannot be resumed and always start at the initial eta).
     */
    fun startTimer() {
        timer?.cancel()
        timer = null

        val eta = stopAt - System.currentTimeMillis()
        timer = object : CountDownTimer(
            eta, interval) {
            override fun onTick(millisUntilFinished: Long) {
                callback.onTick(millisUntilFinished)
            }

            override fun onFinish() {
                callback.onTimeOut()
                callback.lifecycle.removeObserver(this@LifecycleAwareTimer)
            }
        }
        timer?.start()
    }

    /**
     * Cancels the timer and off-hook from lifecycle callbacks
     */
    fun discardTimer() {
        timer?.cancel()
        callback.lifecycle.removeObserver(this)
    }


    override fun onResume(owner: LifecycleOwner) {
        if (expired) {
            callback.onTimeOut()
            discardTimer()
        } else {
            startTimer()
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        timer?.cancel()
    }


    override fun onDestroy(owner: LifecycleOwner) {
        discardTimer()
    }
}