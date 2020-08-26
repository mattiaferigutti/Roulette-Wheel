package com.studio.mattiaferigutti.roulette

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wheel?.numberOfSlices = 10
        wheel?.shadowRadiusCircle = 8f

        spinButton.setOnClickListener {
            wheel?.spinWheel()
        }

        wheel?.setAnimationEnded { currentPosition ->
            Toast.makeText(this, "position: $currentPosition", Toast.LENGTH_SHORT).show()
        }
    }
}