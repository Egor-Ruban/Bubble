package com.example.bubbles

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children

class BubbleTouchListener(
        private val screenWidth: Int,
        private val screenHeight: Int,
        private val ctx : Context
) : View.OnTouchListener {
    private var dX = 0f
    private var dY = 0f

    private var lastX = 0f
    private var lastY = 0f

    private var newX = 0f
    private var newY = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                (view as Bubble).isOnDrag = true
                dX = view.x - event.rawX
                dY = view.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                lastX = view.x
                lastY = view.y
                newX = event.rawX + dX
                newY = event.rawY + dY
                val isOverBorder = (newX <= 0 || newX >= screenWidth - view.width) ||
                        (newY <= 0 || newY >= screenHeight - view.height)
                if (isOverBorder) {
                    return true
                }
                view.animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(0)
                        .start()
            }
            MotionEvent.ACTION_UP -> {
                if (isValidPlace(view.parent as FrameLayout, newX, newY, view.height, view)){
                    view as Bubble
                    view.isOnDrag = false
                    view.speedX = (newX - lastX).toDouble()
                    view.speedY = (newY - lastY).toDouble()
                    view.move()
                } else {
                    view.visibility = View.GONE
                    Toast.makeText(ctx, ctx.getString(R.string.burst), Toast.LENGTH_SHORT).show()
                    view.post {
                        (view.parent as ConstraintLayout).removeView(view)
                    }
                }
            }
        }
        return true
    }

    private fun isValidPlace(root: FrameLayout, x: Float, y: Float, size: Int, v: View): Boolean {
        for (bubble in root.children){
            if (isOverlap(bubble as Bubble, x.toInt(), y.toInt(), size) && (v != bubble)){
                return false
            }
        }
        return true
    }

    private fun isOverlap(bubble : Bubble, x : Int, y: Int, size: Int) : Boolean{
        val location = IntArray(2)

        val rect1 = Rect(
                x + MARGIN_16,
                y + MARGIN_16,
                x + size - MARGIN_16,
                y + size - MARGIN_16
        )

        bubble.getLocationInWindow(location)
        val rect2 = Rect(
                location[0] + MARGIN_16,
                location[1] + MARGIN_16,
                location[0] + bubble.width - MARGIN_16,
                location[1] + bubble.height - MARGIN_16
        )
        return rect1.intersect(rect2)
    }

    companion object{
        private const val MARGIN_16 = MainActivity.MARGIN_16
    }
}