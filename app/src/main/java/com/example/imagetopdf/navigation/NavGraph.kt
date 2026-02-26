package com.example.imagetopdf.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.imagetopdf.ui.screens.account.AccountScreen
import com.example.imagetopdf.ui.screens.authentication.LoginScreen
import com.example.imagetopdf.ui.screens.authentication.OTPScreen
import com.example.imagetopdf.ui.screens.authentication.SignupScreen
import com.example.imagetopdf.ui.screens.conversion.AfterConversionScreen
import com.example.imagetopdf.ui.screens.conversion.BeforeConversionScreen
import com.example.imagetopdf.ui.screens.home.HomeScreen
import com.example.imagetopdf.ui.screens.mydoc.MyDocumentScreen
import com.example.imagetopdf.ui.screens.password.ForgetPasswordScreen
import com.example.imagetopdf.ui.screens.password.ResetPasswordScreen
import com.example.imagetopdf.ui.screens.splashscreen.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ){

        /*-------Splash Screen------*/
        composable(Screen.Splash.route){
            SplashScreen (
                onSplashFinished = {
                    navController.navigate(Screen.Login.route){
                        popUpTo(Screen.Splash.route){ inclusive = true }
                    }
                }
            )
        }

        /*-------Authentication-------*/
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignupClick = {
                    navController.navigate(Screen.Signup.route)
                },
                onForgetPasswordClick = {
                    navController.navigate(Screen.ForgetPassword.route)
                }
            )
        }

        composable(Screen.Signup.route){
            SignupScreen(
                onSignupClick ={
                    navController.navigate(Screen.Home.route){
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack() // Goes back to the previous screen (Login)
                }
            )
        }

        /*------Password-------*/
        composable(Screen.ForgetPassword.route) {
            ForgetPasswordScreen(
                onSendClick ={
                    navController.navigate(Screen.OtpScreen.route)
                },
                onBackLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.OtpScreen.route) {
            OTPScreen(
                onVerifyClick = {
                    navController.navigate(Screen.ResetPassword.route){
                        popUpTo (Screen.OtpScreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onResetPasswordClick = {
                    navController.navigate(Screen.Login.route){
                        popUpTo(Screen.ResetPassword.route) { inclusive = true }
                    }
                }
            )
        }

        /*-------Main App (These need NavController for the BottomBar!) ------*/
        // Notice we pass the navController directly here because the BottomBar needs to drive!
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.MyDoc.route) {
            MyDocumentScreen(navController = navController)
        }

        composable(Screen.Account.route) {
            AccountScreen(navController = navController)
        }

        /*-------Conversion Screens------*/
        composable(Screen.BeforeConversion.route) {
            BeforeConversionScreen(
                onCloseClick = { navController.popBackStack() },
                onConvertClick = { navController.navigate(Screen.AfterConversion.route) }
            )
        }

        composable(Screen.AfterConversion.route) {
            AfterConversionScreen(navController = navController)
        }
    }
}