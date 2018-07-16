package com.frolov.nikita.circlemenulayout

import android.animation.Animator

open class SimpleAnimatorListener(
        private val actionAnimationRepeat: (animation: Animator) -> Unit = {},
        private val actionAnimationEnd: (animation: Animator) -> Unit = {},
        private val actionAnimationCancel: (animation: Animator) -> Unit = {},
        private val actionAnimationStart: (animation: Animator) -> Unit = {}) : Animator.AnimatorListener {

    override fun onAnimationRepeat(animation: Animator) {
        actionAnimationRepeat(animation)
    }

    override fun onAnimationEnd(animation: Animator) {
        actionAnimationEnd(animation)
    }

    override fun onAnimationCancel(animation: Animator) {
        actionAnimationCancel(animation)
    }

    override fun onAnimationStart(animation: Animator) {
        actionAnimationStart(animation)
    }

}