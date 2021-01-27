package com.example.hastalavistabubble

import android.view.MotionEvent
import android.view.View

class CustomTouchListener(
        val screenWidth: Int,
        val screenHeight: Int
) : View.OnTouchListener {
    private var dX: Float = 0f
    private var dY: Float = 0f

    override fun onTouch(view: View, event: MotionEvent): Boolean {

        val newX: Float
        val newY: Float

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = view.x - event.rawX
                dY = view.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> {

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
        }
        return true
    }
}