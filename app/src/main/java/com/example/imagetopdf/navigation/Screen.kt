package com.example.imagetopdf.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Home : Screen("home")
    object MyDoc : Screen("my_doc")
    object Profile : Screen("profile")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object BeforeConversion: Screen("before_conversion")
    object AfterConversion: Screen("after_conversion")
    object ForgetPassword: Screen("forget_password")
    object ResetPassword: Screen("reset_password")
    object OtpScreen: Screen("otp_screen")
}