package org.kagami.roommate.chat.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.DestinationSpec
import org.kagami.roommate.chat.presentation.destinations.*

class OnBoardingNavigatorImpl(
    private val currentDestination: DestinationSpec<*>,
    private val navController: NavController
) : OnboardingNavigator {

    override fun navigateToNextScreen() {
        val nextDestination = when (currentDestination as DirectionDestination) {
            LoginScreenDestination -> RegistrationScreenDestination
            RegistrationScreenDestination -> RoomScreenDestination
            RoomScreenDestination -> CityScreenDestination
            CityScreenDestination -> BudgetScreenDestination
            BudgetScreenDestination -> AgeScreenDestination
            AgeScreenDestination -> InterestedInScreenDestination
            InterestedInScreenDestination -> InterestsScreenDestination
            InterestsScreenDestination -> BioScreenDestination
            BioScreenDestination -> PhotoScreenDestination
            PhotoScreenDestination -> HomeScreenDestination
            else -> null
        }
        if (nextDestination != null)
            if (nextDestination is HomeScreenDestination) {
                navController.navigate(nextDestination) {
                    popUpTo(LoginScreenDestination.route)
                }
            } else
                navController.navigate(nextDestination)
    }

    override fun navigateTo(destination: DirectionDestination, popBackStack: Boolean) {
        if (popBackStack) {
            navController.navigate(destination) {
                popUpTo(destination) {
                    inclusive = true
                }
            }
        } else
            navController.navigate(destination)
    }
}