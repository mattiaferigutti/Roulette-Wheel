package com.studio.mattiaferigutti.roulette

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wheel?.apply {
            numberOfSlices = 10
            shadowRadiusCircle = 8f

            setAnimationEnded { currentPosition ->
                Toast.makeText(this@MainActivity, "position: $currentPosition", Toast.LENGTH_SHORT).show()
            }

            setInnerCircleTouch {
                this.spinWheel()
            }
        }
    }
}