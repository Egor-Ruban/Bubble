package com.example.hastalavistabubble

import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class BubbleTouchListener(
        private val screenWidth: Int,
        private val screenHeight: Int
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
                dX = view.x - event.rawX
                dY = view.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                lastX = view.x
                lastY = view.y
                newX = event.rawX + dX
                newY = event.rawY + dY

                if ((newX <= 0 || newX >= screenWidth - view.width) || (newY <= 0 || newY >= screenHeight - view.height)) {
                    return true
                }
                view.animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(0)
                        .start()
            }
            MotionEvent.ACTION_UP -> {
                view as Bubble

                view.speedX = (newX - lastX).toDouble()
                view.speedY = (newY - lastY).toDouble()
                view.move(screenWidth, screenHeight)
            }
        }
        return true
    }
}