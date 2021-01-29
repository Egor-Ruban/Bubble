package com.example.bubbles


import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.forEach
import com.example.bubbles.databinding.ActivityMainBinding
import kotlin.random.Random

@SuppressLint("ClickableViewAccessibility")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        rootView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val root = binding.field
                    if (isValidPlace(root, event.x, event.y, 220)) {
                        createBubble(binding.field, 220, binding.startField.y, event.x, event.y)
                    } else {
                        Toast.makeText(
                                baseContext,
                                getString(R.string.overlap),
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            true
        }

        binding.clear.setOnClickListener {
            binding.field.forEach { v -> (v as Bubble).cancelAnim() }
            binding.field.removeAllViews()
        }
    }

    private fun createBubble(
            root: ConstraintLayout, size: Int, top: Float, eventX: Float, eventY: Float) {
        val lParams = ConstraintLayout.LayoutParams(size, size)
        val bubble = Bubble(baseContext).apply {
            speedX = Random.nextDouble(-20.0, 20.0)
            speedY = Random.nextDouble(-20.0, 20.0)
            speedDecreasePercentage = Random.nextInt(5, 20)
            background = ContextCompat.getDrawable(baseContext, R.drawable.weed)
            x = getNewX(eventX, root.width, size)
            y = getNewY(eventY, root.height, top.toInt(), size)
            setOnTouchListener(
                    BubbleTouchListener(top.toInt(), root.width, root.height, baseContext)
            )
        }
        binding.field.addView(bubble, lParams)
        bubble.post {
            bubble.move(top.toInt(), binding.field.width, binding.field.height)
        }
    }

    private fun getNewX(eventX: Float, width: Int, size: Int): Float {
        return if ((eventX - size / 2) < 0) 1F
        else if ((eventX + size / 2) > width) (width - size).toFloat()
        else (eventX - size / 2)
    }

    private fun getNewY(eventY: Float, height: Int, startOn: Int, size: Int): Float {
        return if ((eventY - size / 2) < startOn) (startOn + 1F)
        else if ((eventY + size / 2) > height) (height - size).toFloat()
        else (eventY - size / 2)
    }

    private fun isValidPlace(
            root: ConstraintLayout, eventX: Float, eventY: Float, size: Int): Boolean {
        for (bubble in root.children) {
            if (isOverlap(bubble as Bubble, eventX.toInt(), eventY.toInt(), size)) {
                return false
            }
        }
        return true
    }

    private fun isOverlap(bubble: Bubble, eventX: Int, eventY: Int, size: Int): Boolean {
        val location = IntArray(2)

        val rect1 = Rect(
                eventX - size / 2,
                eventY - size / 2,
                eventX + size / 2,
                eventY + size / 2
        )

        bubble.getLocationInWindow(location)
        val rect2 = Rect(
                location[0] + 5,
                location[1] + 5,
                location[0] + bubble.width - 5,
                location[1] + bubble.height - 5
        )

        return rect1.intersect(rect2)
    }

    override fun onStop() {
        super.onStop()
        binding.field.forEach { v -> (v as Bubble).cancelAnim() }
        binding.field.removeAllViews()
    }
}