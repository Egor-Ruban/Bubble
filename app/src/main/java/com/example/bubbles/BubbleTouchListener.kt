package com.example.bubbles

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children

class BubbleTouchListener(
        private val screenWidth: Int,
        private val screenHeight: Int,
        private val ctx : Context
) : View.OnTouchListener {
    private var dX: Float = 0f
    private var dY: Float = 0f

    private var lastX : Float = 0f
    private var lastY : Float = 0f

    private var newX: Float = 0f
    private var newY: Float = 0f

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
                if (isValidPlace(view.parent as ConstraintLayout, newX, newY, view.height, view)){
                    view as Bubble
                    view.isOnDrag = false
                    view.speedX = (newX - lastX).toDouble()
                    view.speedY = (newY - lastY).toDouble()
                    view.move( screenWidth, screenHeight)
                } else {
                    view.visibility = View.GONE //todo check ,it may won`t work
                    Toast.makeText(ctx, ctx.getString(R.string.burst), Toast.LENGTH_SHORT).show()
                    view.post {
                        (view.parent as ConstraintLayout).removeView(view)
                    }
                }
            }
        }
        return true
    }

    private fun isValidPlace(root: ConstraintLayout, x: Float, y: Float, size: Int, v : View): Boolean {
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
                x + 15,
                y + 15,
                x + size - 15,
                y + size - 15
        )

        bubble.getLocationInWindow(location)
        val rect2 = Rect(
                location[0] + 15,
                location[1] + 15,
                location[0] + bubble.width - 15,
                location[1] + bubble.height - 15
        )
        return rect1.intersect(rect2)
    }
}