package com.example.bubbles

import android.content.Context
import android.graphics.Rect
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import java.lang.Math.abs
import kotlin.random.Random

class Bubble @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    var speedX = 1.0
    var speedY = 1.0
    var speedDecreasePercentage = 10
    var isOnDrag = false

    private var decelerationX = 1.0
    private var decelerationY = 1.0
    private var lastX = 1F
    private var lastY = 1F

    private var animation : ViewPropertyAnimator? = null

    var screenWidth = 0
    var screenHeight = 0

    fun cancelAnim(){
        if (!isStopped() && animation != null){
            animation!!.cancel()
        }
        animation = null
    }

    private fun isStopped() = speedX == 0.0 && speedY == 0.0

    fun move() {
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
        if (animation != null) cancelAnim()
        animation = animate()
        animation!!.x(newX.toFloat())
                .y(newY.toFloat())
                .setUpdateListener {
                    for (bubble in (parent as FrameLayout).children){
                        bubble as Bubble
                        if (bubble != this && !bubble.isOnDrag && !isOnDrag){
                            if (isOverlap(bubble)){
                                onHitBubble(bubble)
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
        cancelAnim()
        post{
            move()
        }
    }

    // onHitBubble knows how to deal with hits
    private fun onHitBubble(bubble: Bubble){
        if (bubble.isStopped()){
            bubble.speedY = speedY * SPEED_PARTITION_COEF
            bubble.speedX = speedX * SPEED_PARTITION_COEF
            speedX = -speedX * SPEED_PARTITION_COEF
            speedY = -speedY * SPEED_PARTITION_COEF
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
        if (abs(speed) < 1) return Random.nextDouble((-5.0), 5.0)
        return if (speed < 0)
            Random.nextDouble((speed * RANDOM_SPEED_COEF), -(speed * RANDOM_SPEED_COEF))
        else Random.nextDouble(-(speed * RANDOM_SPEED_COEF), (speed * RANDOM_SPEED_COEF))
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
                location[0] + MARGIN_16,
                location[1] + MARGIN_16,
                location[0] + width - MARGIN_16,
                location[1] + height - MARGIN_16
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

    private var isInFocus = true
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (isInFocus){
            cancelAnim()
        } else {
            move()
        }
        isInFocus = !isInFocus
    }

    companion object{
        private const val SPEED_PARTITION_COEF = 0.5
        private const val RANDOM_SPEED_COEF = 0.20
        private const val MARGIN_16 = MainActivity.MARGIN_16
    }
}


