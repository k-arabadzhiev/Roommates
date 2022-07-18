package org.kagami.roommate.chat.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.presentation.chat.ChatViewModel
import org.kagami.roommate.chat.presentation.destinations.*
import org.kagami.roommate.chat.presentation.home.HomeViewModel

@Composable
fun TopBar(
    destination: Destination,
    navBackStackEntry: NavBackStackEntry?,
    navController: NavController
) {
    TopAppBar {
        if (destination == ChatScreenDestination) {
            IconButton(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
            if (navBackStackEntry != null) {
                val url = ChatScreenDestination.argsFrom(navBackStackEntry).profilePhoto
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .size(150, 150)
                        .transformations(CircleCropTransformation())
                        .error(R.drawable.ic_placeholder)
                        .crossfade(true)
                        .build(), contentDescription = "Profile photo"
                )
            }
        }
        if (destination == HomeScreenDestination) {
            if (navBackStackEntry != null) {
                val photo = navBackStackEntry.let {
                    hiltViewModel<HomeViewModel>(navBackStackEntry).profilePhoto
                }
                Spacer(Modifier.width(10.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photo)
                        .size(150, 150)
                        .transformations(CircleCropTransformation())
                        .error(R.drawable.ic_placeholder)
                        .crossfade(true)
                        .build(), contentDescription = "Profile photo"
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = destination.topBarTitle(navBackStackEntry),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.White
        )
    }
}

@Composable
fun Destination.topBarTitle(navBackStackEntry: NavBackStackEntry?): String {
    return when (this) {
        ChatScreenDestination -> {
            if (navBackStackEntry != null) {
                val title = ChatScreenDestination.argsFrom(navBackStackEntry).participantName
                title
            } else ""
        }
        HomeScreenDestination -> {
            if (navBackStackEntry != null) {
                val name = navBackStackEntry.let {
                    hiltViewModel<HomeViewModel>(navBackStackEntry).userName
                }
                name
            } else
                ""
        }
        AgeScreenDestination,
        BioScreenDestination,
        BudgetScreenDestination,
        CityScreenDestination,
        InterestedInScreenDestination,
        InterestsScreenDestination,
        LoginScreenDestination,
        MatchesScreenDestination -> "Matches"
        PhotoScreenDestination,
        RegistrationScreenDestination,
        RoomScreenDestination,
        SettingsScreenDestination -> "Settings"
    }
}
