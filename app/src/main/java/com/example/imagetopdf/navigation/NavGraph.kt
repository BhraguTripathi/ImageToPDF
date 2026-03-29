package com.example.imagetopdf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.imagetopdf.ui.screens.comingsoonfeature.ComingSoonScreen
import com.example.imagetopdf.ui.screens.conversion.AfterConversionScreen
import com.example.imagetopdf.ui.screens.conversion.BeforeConversionScreen
import com.example.imagetopdf.ui.screens.conversion.PDFViewModel
import com.example.imagetopdf.ui.screens.home.HomeScreen
import com.example.imagetopdf.ui.screens.mydoc.MyDocumentScreen
import com.example.imagetopdf.ui.screens.password.ForgetPasswordScreen
import com.example.imagetopdf.ui.screens.password.ResetPasswordScreen
import com.example.imagetopdf.ui.screens.splashscreen.SplashScreen
import kotlinx.coroutines.launch

@Composable
fun NavGraph(navController: NavHostController) {

    val sharedAuthViewModel: AuthViewModel = viewModel()
    val sharedPdfViewModel: PDFViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        /*------- Splash Screen -------*/
        composable(Screen.Splash.route) {
            val scope = rememberCoroutineScope()
            SplashScreen(
                onSplashFinished = {
                    scope.launch {
                        SupabaseClient.client.auth.awaitInitialization()
                        val isLoggedIn =
                            SupabaseClient.client.auth.currentSessionOrNull() != null
                        if (isLoggedIn) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Signup.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        /*------- Login -------*/
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
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

        /*------- Signup -------*/
        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        /*------- Forget Password -------*/
        composable(Screen.ForgetPassword.route) {
            ForgetPasswordScreen(
                onSendClick = {
                    navController.navigate(Screen.OtpScreen.route) {
                        popUpTo(Screen.ForgetPassword.route) { inclusive = true }
                    }
                },
                onBackLoginClick = {
                    navController.popBackStack()
                },
                viewModel = sharedAuthViewModel
            )
        }

        /*------- OTP Screen -------*/
        composable(Screen.OtpScreen.route) {
            OTPScreen(
                onVerifyClick = {
                    navController.navigate(Screen.ResetPassword.route) {
                        popUpTo(Screen.OtpScreen.route) { inclusive = true }
                    }
                },
                viewModel = sharedAuthViewModel
            )
        }

        /*------- Reset Password -------*/
        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onResetPasswordClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                viewModel = sharedAuthViewModel
            )
        }

        /*------- Main App Screens -------*/
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.MyDoc.route) {
            MyDocumentScreen(navController = navController)
        }

        composable(Screen.Account.route) {
            AccountScreen(navController = navController)
        }

        /*------- Conversion Screens -------*/
        composable(Screen.BeforeConversion.route) {
            BeforeConversionScreen(
                onCloseClick = {
                    navController.popBackStack()
                },
                onConvertClick = {
                    navController.navigate(Screen.AfterConversion.route) {
                        popUpTo(Screen.BeforeConversion.route) { inclusive = true }
                    }
                },
                viewModel = sharedPdfViewModel
            )
        }

        composable(Screen.AfterConversion.route) {
            AfterConversionScreen(
                navController = navController,
                viewModel = sharedPdfViewModel
            )
        }

        composable(Screen.ComingSoon.route) { backStackEntry ->
            val featureName = backStackEntry.arguments?.getString("featureName") ?: "Feature"
            ComingSoonScreen(
                featureName = featureName,
                navController = navController
            )
        }
    }
}