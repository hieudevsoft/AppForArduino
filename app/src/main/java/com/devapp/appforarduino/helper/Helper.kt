package com.devapp.appforarduino.helper

import android.view.View

object Helper {
		fun scaleViewPress(v: View){
				v.scaleX = 0.5F
				v.scaleY = 0.5F
		}
		fun scaleViewUp(v: View){
				v.animate().scaleX(1F).setDuration(500).start()
				v.animate().scaleY(1F).setDuration(500).start()
		}
}