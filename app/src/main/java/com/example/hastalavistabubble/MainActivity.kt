package com.example.hastalavistabubble


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.hastalavistabubble.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var vc = 0
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        view.setOnClickListener{
            Log.d("hey", "hey")
        }

        view.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_UP -> if (vc == 0 || vc == 1) {
                    val lParams = ConstraintLayout.LayoutParams(150, 150)
                    val btnNew = Button(baseContext)
                    btnNew.background = ContextCompat.getDrawable(this, R.drawable.weed)
                    btnNew.x = (event.x - 75)
                    btnNew.y = (event.y - 75)
                    btnNew.setOnTouchListener(CustomTouchListener(view.width, view.height))
                    view.addView(btnNew, lParams)
                    vc = 1
                    var x : Float
                    var y : Float
                    val newX: Float
                    val newY: Float
                    newX = btnNew.x + 1
                    newY = btnNew.y + 1

                    if ((newX <= 0 || newX >= view.width - btnNew.width) || (newY <= 0 || newY >= view.height - btnNew.height)) {
                        //return true
                    } else {
                        btnNew.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(1000)
                                .start()
                    }


                }
            }
            true
        }
    }
}