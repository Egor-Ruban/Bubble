package com.example.hastalavistabubble


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.example.hastalavistabubble.databinding.ActivityMainBinding
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
            when(event.action){
                MotionEvent.ACTION_DOWN ->  {
                    val lParams = ConstraintLayout.LayoutParams(220, 220)
                    val bubble = Bubble(baseContext).apply {
                        speedX = 0.0//Random.nextDouble(-20.0, 20.0)
                        speedY = 0.0//Random.nextDouble(-20.0, 20.0)
                        speedDecreasePercentage = Random.nextInt(5, 20)
                        background = ContextCompat.getDrawable(baseContext, R.drawable.weed)
                        x = if((event.x - 110) > 0) (event.x - 110) else 1F
                        y = if((event.y - 110) > 0) (event.y - 110) else 1F
                        setOnTouchListener(BubbleTouchListener(rootView.width, rootView.height))
                    }
                    rootView.addView(bubble, lParams)
                    //todo запретить создавать элементы друг на друге
                    bubble.post {
                        bubble.move(rootView.width, rootView.height)
                    }
                }
            }
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.root.removeAllViews()
    }
}