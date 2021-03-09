package com.example.androiddevchallenge.controller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.LinearInterpolator
import com.example.androiddevchallenge.constant.ANIMATOR_SPEED
import com.example.androiddevchallenge.status.CompletedStatus
import com.example.androiddevchallenge.status.NotStartedStatus
import com.example.androiddevchallenge.status.PausedStatus
import com.example.androiddevchallenge.status.StartedStatus
import com.example.androiddevchallenge.viewmodel.TimerViewModel



class AnimatorController(private val viewModel: TimerViewModel) {

    private var valueAnimator: ValueAnimator? = null

    fun start() {
        if (viewModel.totalTime == 0L) return
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofInt(viewModel.totalTime.toInt() * ANIMATOR_SPEED, 0)
            valueAnimator?.interpolator = LinearInterpolator()
            valueAnimator?.addUpdateListener {
                viewModel.animValue = (it.animatedValue as Int) / ANIMATOR_SPEED.toFloat()
                viewModel.timeLeft = (it.animatedValue as Int).toLong() / ANIMATOR_SPEED
            }
            valueAnimator?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    complete()
                }
            })
        } else {
            valueAnimator?.setIntValues(viewModel.totalTime.toInt() * ANIMATOR_SPEED, 0)
        }
        // 间隔1秒
        valueAnimator?.duration = viewModel.totalTime * 1000L
        valueAnimator?.start()
        viewModel.status = StartedStatus(viewModel)
    }

    fun pause() {
        valueAnimator?.pause()
        viewModel.status = PausedStatus(viewModel)
    }

    fun resume() {
        valueAnimator?.resume()
        viewModel.status = StartedStatus(viewModel)
    }

    fun stop() {
        valueAnimator?.cancel()
        viewModel.timeLeft = 0
        viewModel.status = NotStartedStatus(viewModel)
    }

    fun complete() {
        viewModel.totalTime = 0
        viewModel.status = CompletedStatus(viewModel)
    }
}