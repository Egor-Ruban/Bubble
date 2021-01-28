package com.example.hastalavistabubble

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import java.lang.Math.abs
import kotlin.random.Random

class Bubble @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    var speedX: Double = 1.0
    var speedY: Double = 1.0
    var speedDecreasePercentage : Int = 10

    private var decelerationX : Double = 1.0
    private var decelerationY : Double = 1.0
    private var lastX : Float = 1F
    private var lastY : Float = 1F

    private lateinit var animation : ViewPropertyAnimator

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    fun move(screenWidth: Int, screenHeight: Int){
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight
        move()
    }

    private fun isStopped() = speedX == 0.0 && speedY == 0.0

    private fun move() {
        //stop moving if speed drops so low
        if (abs(speedX) < 1.0 && abs(speedY) < 1.0){
            speedX = 0.0
            speedY = 0.0
            return
        }

        decelerationX = abs((speedX * speedDecreasePercentage / 100))
        decelerationY = abs((speedY * speedDecreasePercentage / 100))
        val newX = this.x + speedX
        val newY = this.y + speedY
        if ((newX <= 0) || (newX >= screenWidth - this.width)) {
            onHitVerticalBorders()
            move()
        } else if ((newY <= 0) || (newY >= screenHeight - this.height)) {
            onHitHorizontalBorders()
            move()
        } else {
            doAnimation(newX, newY)
        }
    }

    private fun doAnimation(newX : Double, newY: Double){
        animation = animate()
        animation.x(newX.toFloat())
                .y(newY.toFloat())
                .setUpdateListener {
                    for (bubble in (parent as ConstraintLayout).children){
                        if (bubble != this){
                            if (isOverlap(bubble)){
                                onHitBubble(bubble as Bubble)
                            }
                        }
                    }
                    lastX = x
                    lastY = y
                }
                .setDuration(10)
                .withEndAction {
                    move()
                }
                .start()
    }

    private fun restartMove(){
        x = lastX
        y = lastY
        animation.cancel()
        post{
            move()
        }
    }

    // onHitBubble knows how to deal with hits
    private fun onHitBubble(bubble: Bubble){
        if (bubble.isStopped()){
            bubble.speedY = 0.5 * speedY
            bubble.speedX = 0.5 * speedX
            speedX = -speedX * 0.5
            speedY = -speedY * 0.5
            bubble.post{
                bubble.move()
            }
            restartMove()
        } else {
            var a = speedX
            speedX = bubble.speedX
            bubble.speedX = a
            a = speedY
            speedY = bubble.speedY
            bubble.speedY = a
            bubble.restartMove()
            restartMove()
        }
    }

    // randomSpeedChange returns random deviation of speed on hit
    private fun randomSpeedChange(speed : Double) : Double{
        if (abs(speed) < 1) return 0.0
        return if (speed < 0) Random.nextDouble((speed * 0.20), -(speed * 0.20))
        else Random.nextDouble(-(speed * 0.20), (speed * 0.20))
    }

    // onHitVerticalBorders updates speed of Bubble when hit occurs
    private fun onHitVerticalBorders(){
        speedX = -speedX
        if (speedX < 0) {
            speedX += decelerationX
        } else {
            speedX -= decelerationX
        }
        speedX += randomSpeedChange(speedX)
        speedY += randomSpeedChange(speedY)
    }

    // onHitHorizontalBorders updates speed of Bubble when hit occurs
    private fun onHitHorizontalBorders(){
        speedY = -speedY
        if (speedY < 0) {
            speedY += decelerationY
        } else {
            speedY -= decelerationY
        }
        speedX += randomSpeedChange(speedX)
        speedY += randomSpeedChange(speedY)
    }

    // isOverlap returns true if this Bubble intersects with another bubble
    private fun isOverlap(bubble: View) : Boolean{
        val location = IntArray(2)

        getLocationInWindow(location)
        val rect1 = Rect(
                location[0] + 10,
                location[1] + 15,
                location[0] + width - 10,
                location[1] + height - 15
        )

        bubble.getLocationInWindow(location)
        val rect2 = Rect(
                location[0] + 10,
                location[1] + 15,
                location[0] + bubble.width - 10,
                location[1] + bubble.height - 15
        )

        return rect1.intersect(rect2)
    }

    private var isInFocus = true
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (isInFocus){
            animation.cancel()
        } else {
            move()
        }
        isInFocus = !isInFocus
    }
}


