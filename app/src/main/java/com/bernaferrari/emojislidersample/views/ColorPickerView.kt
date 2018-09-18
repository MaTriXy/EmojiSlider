package com.bernaferrari.emojislidersample.views

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.bernaferrari.emojislidersample.extensions.dpToPixels

class ColorPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0, defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var progress: Float = ANIM_MIN_VALUE
    var circlePaint: Paint? = null
    var outlinePaint: Paint? = null

    var outLineColor: Int
        get() = outlinePaint?.color ?: Color.TRANSPARENT
        set(value) {
            outlinePaint?.color = value
            invalidate()
        }

    var colors = Pair(Color.YELLOW, Color.RED)

    fun invalidateColors() {
        // If the value is set here, it risks getting a solid color if width is blue.
        // This way, it will be refreshed on onDraw.
        circlePaint = null
        outlinePaint = null
        invalidate()
    }

    fun areColorsSet(): Boolean = circlePaint != null && outlinePaint != null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (circlePaint == null) {
            circlePaint = createPaintInside()
        }

        if (outlinePaint == null) {
            outlinePaint = createPaintOutside()
        }

        canvas.drawCircle(
            width.toFloat() / 2.0f,
            height.toFloat() / 2.0f,
            (Math.min(width, height) / 2f - dpToPixels(OUTER_PADDING, context)) * progress,
            outlinePaint!!
        )

        canvas.drawCircle(
            width.toFloat() / 2.0f,
            height.toFloat() / 2.0f,
            Math.min(width, height) / 2f - dpToPixels(INNER_PADDING, context) * progress,
            circlePaint!!
        )
    }

    fun reverseSelection() {
        if (this.isSelected) {
            deselectIfSelected(true)
        } else {
            selectIfDeselected(true)
        }
    }

    fun selectIfDeselected(animated: Boolean) {
        if (!this.isSelected) {
            startAnimation(ANIM_MIN_VALUE, ANIM_MAX_VALUE, animated)
            this.isSelected = true
        }
    }

    fun deselectIfSelected(animated: Boolean) {
        if (this.isSelected) {
            startAnimation(ANIM_MAX_VALUE, ANIM_MIN_VALUE, animated)
            this.isSelected = false
        }
    }

    private fun startAnimation(fromValue: Float, toValue: Float, isAnimated: Boolean) {
        if (isAnimated) {
            val ofFloat = ObjectAnimator.ofFloat(fromValue, toValue)
            ofFloat.duration = ANIM_DURATION
            ofFloat.interpolator = AccelerateDecelerateInterpolator()
            ofFloat.addUpdateListener(updateListener())
            ofFloat.start()
            return
        }
        this.progress = toValue
        invalidate()
    }

    private fun updateListener(): ValueAnimator.AnimatorUpdateListener =
        ValueAnimator.AnimatorUpdateListener { valueAnimator2 ->
            val valueAnimator = valueAnimator2?.animatedValue ?: throw NullPointerException()
            this.progress = (valueAnimator as Float).toFloat()
            this.invalidate()
        }

    private fun createPaintInside(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.shader = LinearGradient(
            width.toFloat(),
            0f,
            0f,
            height.toFloat(),
            colors.first,
            colors.second,
            Shader.TileMode.MIRROR
        )
        return paint
    }

    private fun createPaintOutside(): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = Color.WHITE
        paint.shader = LinearGradient(
            width.toFloat(),
            0f,
            0f,
            height.toFloat(),
            colors.first,
            colors.second,
            Shader.TileMode.CLAMP
        )
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = dpToPixels(SHADER_STROKE, context)

        return paint
    }

    private companion object {
        private const val ANIM_MAX_VALUE = 1.0f
        private const val ANIM_MIN_VALUE = 0.0f
        private const val ANIM_DURATION = 250L
        private const val SHADER_STROKE = 3f
        private const val INNER_PADDING = 6f
        private const val OUTER_PADDING = 2f
    }
}
