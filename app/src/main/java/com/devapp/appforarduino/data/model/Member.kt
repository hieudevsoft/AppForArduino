package com.devapp.appforarduino.data.model

data class Member(
		val middleName: String,
		val name: String,
		val major: String,
		val majorDes: String,
		val city: String,
		val country: String = "Viet Nam",
		val phoneNumberOne: String,
		val phoneNumberOther: String,
		val email: String,
		val website: String,
		val lottieAnimationBack: Int,
		val imageFront: Int,
)
