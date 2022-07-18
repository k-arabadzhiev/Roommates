package org.kagami.roommate.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint
import org.kagami.roommate.chat.navigation.OnBoardingNavigatorImpl
import org.kagami.roommate.chat.presentation.MainViewModel
import org.kagami.roommate.chat.presentation.NavGraphs
import org.kagami.roommate.chat.presentation.components.AppScaffold
import org.kagami.roommate.chat.presentation.components.BottomBar
import org.kagami.roommate.chat.presentation.components.TopBar
import org.kagami.roommate.chat.presentation.destinations.*
import org.kagami.roommate.chat.presentation.onboarding.RegisterViewModel
import org.kagami.roommate.chat.ui.theme.RoommateChatAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        splashScreen.apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        setContent {
            RoommateChatAppTheme {
                val engine = rememberAnimatedNavHostEngine()
                val navController = engine.rememberNavController()
                val scaffoldState = rememberScaffoldState()

                val start by viewModel.startRoute
                AppScaffold(
                    navController = navController,
                    startRoute = start,
                    topBar = { dest, backStackEntry, navCon ->
                        AnimatedVisibility(dest.shouldShowTopBar()) {
                            TopBar(dest, backStackEntry, navCon)
                        }
                    },
                    bottomBar = {
                        AnimatedVisibility(it.shouldShowBottomBar()) {
                            BottomBar(navController)
                        }
                    }
                ) { paddingValues ->
                    DestinationsNavHost(
                        engine = engine,
                        navController = navController,
                        navGraph = NavGraphs.root,
                        modifier = Modifier.padding(paddingValues),
                        startRoute = start,
                        dependenciesContainerBuilder = {
                            dependency(scaffoldState)
                            dependency(OnBoardingNavigatorImpl(destination, navController))
                            dependency(NavGraphs.onboarding) {
                                val parentEntry = remember(navBackStackEntry) {
                                    navController.getBackStackEntry(NavGraphs.onboarding.route)
                                }
                                hiltViewModel<RegisterViewModel>(parentEntry)
                            }
                        }
                    )
                    // Has to be called after calling DestinationsNavHost because only
                    // then does NavController have a graph associated that we need for
                    // `appCurrentDestinationAsState` method
//                    ShowLoginWhenLoggedOut(vm, navController)
                }
            }
        }
    }
}

private fun Destination.shouldShowTopBar(): Boolean {
    val noScaffoldDestinations = listOf(
        LoginScreenDestination,
        RegistrationScreenDestination, RoomScreenDestination, CityScreenDestination,
        BudgetScreenDestination, BudgetScreenDestination, AgeScreenDestination,
        AgeScreenDestination, InterestedInScreenDestination, InterestsScreenDestination,
        BioScreenDestination, PhotoScreenDestination
    )
    return !noScaffoldDestinations.contains(this)
}

private fun Destination.shouldShowBottomBar(): Boolean {
    val noScaffoldDestinations = listOf(
        LoginScreenDestination,
        RegistrationScreenDestination, RoomScreenDestination, CityScreenDestination,
        BudgetScreenDestination, BudgetScreenDestination, AgeScreenDestination,
        AgeScreenDestination, InterestedInScreenDestination, InterestsScreenDestination,
        BioScreenDestination, PhotoScreenDestination, ChatScreenDestination
    )
    return !noScaffoldDestinations.contains(this)
}

/*
@Composable
private fun ShowLoginWhenLoggedOut(
    vm: HomeViewModel,
    navController: NavHostController
) {
    val currentDestination by navController.appCurrentDestinationAsState()
    val isLoggedIn by vm.isLoggedInFlow.collectAsState()
    val onBoardingDestinations = listOf(
        RegistrationScreenDestination, RoomScreenDestination, CityScreenDestination,
        BudgetScreenDestination, BudgetScreenDestination, AgeScreenDestination,
        AgeScreenDestination, InterestedInScreenDestination, InterestsScreenDestination,
        BioScreenDestination, PhotoScreenDestination
    )

    if (!isLoggedIn && !onBoardingDestinations.contains(currentDestination)) {
        // everytime destination changes or logged in state we check
        // if we have to show Login screen and navigate to it if so
        navController.navigate(LoginScreenDestination) {
            launchSingleTop = true
        }
    }
}*/
