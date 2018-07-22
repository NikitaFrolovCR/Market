package com.frolov.nikita.circlemenulayout

import android.animation.ValueAnimator
import android.content.Context
import android.support.annotation.IdRes
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.frolov.nikita.circlemenulayout.CircleMenuLayout.FirstChildPosition.EAST_
import com.frolov.nikita.circlemenulayout.CircleMenuLayout.FirstChildPosition.SOUTH
import com.frolov.nikita.circlemenulayout.CircleMenuLayout.StatusChild.CHOOSE
import com.frolov.nikita.circlemenulayout.CircleMenuLayout.StatusChild.NOT_USE

public interface CircleMenuInterface {
    fun onClickItemBefore(@IdRes id: Int)
    fun onClickItemAfter(@IdRes id: Int)
}

class CircleMenuLayout : ViewGroup {

    companion object {
        private const val DEFAULT_RADIUS = -1f
        private const val RADIUS_SIZE_C = 3
        private const val CHOOSE_ITEM = 0
        private const val DURATION_CIRCLES_GROUP = 300L
        private const val DURATION_CIRCLE_CHOOSE = 150L
    }

    private enum class StatusChild {
        CHOOSE, NOT_USE;
    }

    private enum class FirstChildPosition(private val angle: Int) {
        EAST(0), EAST_(360), SOUTH(90), WEST(180), NORTH(270);

        operator fun invoke() = angle
    }

    private class CircleItemMenu(var view: View, var status: StatusChild, var angel: Float)

    private var circleWidth: Int = 0
    private var circleHeight: Int = 0
    private var radius = DEFAULT_RADIUS
    private val child: MutableList<CircleItemMenu> = mutableListOf()
    private var isAnimate: Boolean = false
    private var valueAnimator: ValueAnimator? = null
    var viewChoose: Int = CHOOSE_ITEM

    private var listeners: MutableList<CircleMenuInterface> = mutableListOf()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        attrs?.let {
            //TODO set attribute from xml
        }
    }

    public fun addListener(listener: CircleMenuInterface) {
        listeners.add(listener)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        circleHeight = height
        circleWidth = width

        if (radius == DEFAULT_RADIUS) {
            radius = (if (circleWidth <= circleHeight) circleWidth else circleHeight).div(RADIUS_SIZE_C).toFloat()
        }

        setChildAngles()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxChildWidth = 0
        var maxChildHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue

            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)

            maxChildWidth = Math.max(maxChildWidth, child.measuredWidth)
            maxChildHeight = Math.max(maxChildHeight, child.measuredHeight)
        }

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = resolveSize(widthMode, widthSize, Math.min(widthSize, heightSize), maxChildWidth * RADIUS_SIZE_C)
        val height = resolveSize(heightMode, heightSize, Math.min(heightSize, widthSize), maxChildHeight * RADIUS_SIZE_C)

        setMeasuredDimension(View.resolveSize(width, widthMeasureSpec), View.resolveSize(height, heightMeasureSpec))
    }

    private fun resolveSize(mode: Int, exactly: Int, atMost: Int, unspecified: Int) = when (mode) {
        MeasureSpec.EXACTLY -> exactly
        MeasureSpec.AT_MOST -> atMost
        else -> unspecified
    }

    private fun setChildAngles() {
        val angleDelay = EAST_() / (childCount - 1)
        var localAngle = SOUTH().toFloat()
        child.clear()

        for (index in 0 until childCount) {
            getChildAt(index).takeIf { it.visibility != View.GONE }?.let {
                if (index == viewChoose) {
                    val childWidth = it.measuredWidth
                    val childHeight = it.measuredHeight
                    val left = circleWidth.half() - childWidth.half()
                    val top = circleHeight.half() - childHeight.half()
                    it.layout(left, top, left + childWidth, top + childHeight)
                    child.add(CircleItemMenu(it, CHOOSE, localAngle))
                } else {
                    layout(it, localAngle)
                    child.add(CircleItemMenu(it, NOT_USE, localAngle))
                    localAngle += angleDelay
                }
                it.tag = index
                it.setOnClickListener { view ->
                    (view.tag as? Int)?.let { child[it] }
                            ?.takeIf { it.status == NOT_USE && !isAnimate }?.let {
                                isAnimate = true
                                viewChoose = index
                                listeners.forEach { it.onClickItemBefore(view.id) }
                                animateDivide(index)
                            }
                }
            }
        }

    }

    private fun animateDivide(viewChoose: Int) {
        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator?.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            val shift = EAST_() / childCount
            var localAngle = SOUTH().toFloat()
            child.forEach {
                it.angel = it.angel - (it.angel - localAngle) * animatedValue
                if (it.status == NOT_USE) layout(it.view, it.angel)
                localAngle += shift
            }
        }
        valueAnimator?.duration = DURATION_CIRCLES_GROUP
        valueAnimator?.start()
        valueAnimator?.addListener(object : SimpleAnimatorListener(actionAnimationEnd = { animateToCenter(viewChoose) }) {})
    }

    private fun animateJoin(viewChoose: Int) {
        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator?.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            val shift = EAST_() / (childCount - 1)
            var localAngle = SOUTH().toFloat()
            child.forEach {
                it.angel = it.angel + (localAngle - it.angel) * animatedValue
                if (it.status == NOT_USE) {
                    layout(it.view, it.angel)
                    localAngle += shift
                }
            }
        }
        valueAnimator?.duration = DURATION_CIRCLES_GROUP
        valueAnimator?.start()

        valueAnimator?.addListener(object : SimpleAnimatorListener(actionAnimationEnd = {
            isAnimate = false
            listeners.forEach { it.onClickItemAfter(child[viewChoose].view.id) }
        }) {})
    }

    private fun animateToCenter(viewChoose: Int) {
        child.first { it.status == CHOOSE }.let {
            it.status = NOT_USE
            chooseAnimate(it.view, false) {
                with(child[viewChoose]) {
                    status = CHOOSE
                    chooseAnimate(view, true) { animateJoin(viewChoose) }
                }
            }
        }
    }

    private fun chooseAnimate(view: View, choose: Boolean, actionAnimateEnd: () -> Unit) {
        var shift = EAST_() / childCount
        shift *= view.tag as? Int ?: 0
        shift += SOUTH()

        valueAnimator = if (choose) ValueAnimator.ofFloat(1f, 0f) else ValueAnimator.ofFloat(0f, 1f)
        valueAnimator?.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            layout(radius * animatedValue, view, shift.toFloat())
        }
        valueAnimator?.duration = DURATION_CIRCLE_CHOOSE
        valueAnimator?.start()
        valueAnimator?.addListener(object : SimpleAnimatorListener(actionAnimationEnd = { actionAnimateEnd() }) {})
    }

    private fun layout(view: View, localAngle: Float? = null) = layout(radius, view, localAngle)

    private fun layout(radius: Float, view: View, localAngle: Float? = null) {
        val childWidth = view.measuredWidth
        val childHeight = view.measuredHeight

        val leftShift = localAngle?.let { radius * Math.cos(Math.toRadians(it.toDouble())) } ?: .0
        val left = Math.round((circleWidth.half() - childWidth.half() + leftShift).toFloat())

        val rightShift = localAngle?.let { radius * Math.sin(Math.toRadians(it.toDouble())) } ?: .0
        val top = Math.round((circleHeight.half() - childHeight.half() + rightShift).toFloat())

        view.layout(left, top, left + childWidth, top + childHeight)
    }

    private fun Int.half() = div(2)

    override fun onDetachedFromWindow() {
        valueAnimator?.cancel()
        super.onDetachedFromWindow()
    }

}