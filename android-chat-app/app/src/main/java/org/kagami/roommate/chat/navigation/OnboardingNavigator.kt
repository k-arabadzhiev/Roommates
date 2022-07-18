package org.kagami.roommate.chat.navigation

import org.kagami.roommate.chat.presentation.destinations.DirectionDestination

interface OnboardingNavigator {
    fun navigateToNextScreen()
    fun navigateTo(destination: DirectionDestination, popBackStack: Boolean = false)
}