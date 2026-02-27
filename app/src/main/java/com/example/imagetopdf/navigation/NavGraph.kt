package com.example.imagetopdf.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.imagetopdf.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import com.example.imagetopdf.ui.screens.account.AccountScreen
import com.example.imagetopdf.ui.screens.authentication.AuthViewModel
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


    val sharedAuthViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ){

        /*-------Splash Screen------*/
        composable(Screen.Splash.route){
            SplashScreen (
                onSplashFinished = {

                    val isLoggedIn = SupabaseClient.client.auth.currentSessionOrNull() != null

                    if (isLoggedIn) {
                        navController.navigate(Screen.Home.route){
                            popUpTo(0)
                        }
                    } else {
                        navController.navigate(Screen.Login.route){
                            popUpTo(0)
                        }
                    }
                }
            )
        }

        /*-------Authentication-------*/
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0)
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
                        popUpTo(0)
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
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
                },
                viewModel = sharedAuthViewModel
            )
        }

        composable(Screen.OtpScreen.route) {
            OTPScreen(
                onVerifyClick = {
                    navController.navigate(Screen.ResetPassword.route){
                        popUpTo (Screen.OtpScreen.route) { inclusive = true }
                    }
                },
                viewModel = sharedAuthViewModel
            )
        }

        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onResetPasswordClick = {
                    navController.navigate(Screen.Login.route){
                        popUpTo(0)
                    }
                },
                viewModel = sharedAuthViewModel
            )
        }

        /*-------Main App------*/
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