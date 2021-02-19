package com.example.bubbles


import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.widget.FrameLayout
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
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    private val backgrounds by lazy {
        arrayOf(ContextCompat.getDrawable(baseContext, R.drawable.normal_background),
                ContextCompat.getDrawable(baseContext, R.drawable.sand),
                ContextCompat.getDrawable(baseContext, R.color.black))
    }
    private val bubbleDesigns by lazy {
        arrayOf(ContextCompat.getDrawable(baseContext, R.drawable.normal_bubble),
                ContextCompat.getDrawable(baseContext, R.drawable.weed),
                ContextCompat.getDrawable(baseContext, R.drawable.cherry))
    }
    private val texts by lazy {
        arrayOf(getString(R.string.design_normal),
                getString(R.string.design_weed),
                getString(R.string.design_cherry))
    }
    private var currentDesign = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = binding.root
        setContentView(rootView)

        binding.field.background = backgrounds[currentDesign]
        binding.design.text = texts[currentDesign]
        rootView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val root = binding.field
                    if (isValidPlace(root, event.x, event.y, BUBBLE_SIZE)) {
                        createBubble(binding.field, BUBBLE_SIZE, binding.field.y, event.x, event.y)
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

        binding.design.setOnClickListener {
            changeDesign()
        }
    }

    private fun changeDesign() {
        currentDesign = (currentDesign + 1) % 3
        binding.field.background = backgrounds[currentDesign]
        binding.design.text = texts[currentDesign]
        binding.field.forEach { v -> v.background = bubbleDesigns[currentDesign] }
    }

    private fun createBubble(
            root: FrameLayout, size: Int, top: Float, eventX: Float, eventY: Float) {
        val lParams = ConstraintLayout.LayoutParams(size, size)
        val bubble = Bubble(baseContext).apply {
            speedX = Random.nextDouble(-RANDOM_SPEED_LIMIT, RANDOM_SPEED_LIMIT)
            speedY = Random.nextDouble(-RANDOM_SPEED_LIMIT, RANDOM_SPEED_LIMIT)
            speedDecreasePercentage = Random.nextInt(5, 20)
            background = bubbleDesigns[currentDesign]
            x = getNewX(eventX, root.width, size)
            y = getNewY(eventY, root.height, top.toInt(), size)
            screenWidth = root.width
            screenHeight = root.height
            setOnTouchListener(
                    BubbleTouchListener(root.width, root.height, baseContext)
            )
        }
        binding.field.addView(bubble, lParams)
        bubble.post {
            bubble.move()
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
        else (eventY - size)
    }

    private fun isValidPlace(
            root: FrameLayout, eventX: Float, eventY: Float, size: Int): Boolean {
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
                eventX - size / 2 + 15,
                eventY - size / 2 + 15,
                eventX + size / 2 - 15,
                eventY + size - 15
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

    override fun onStop() {
        super.onStop()
        binding.field.forEach { v -> (v as Bubble).cancelAnim() }
        binding.field.removeAllViews()
    }

    companion object{
        private const val BUBBLE_SIZE = 220
        private const val RANDOM_SPEED_LIMIT = 20.0

        const val MARGIN_16 = 16
    }
}