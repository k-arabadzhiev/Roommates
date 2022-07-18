package org.kagami.roommate.chat.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.navigation.OnboardingNavGraph
import org.kagami.roommate.chat.navigation.OnboardingNavigator
import org.kagami.roommate.chat.presentation.components.OutlinedTextFieldValidation
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.UiEvent

@OnboardingNavGraph
@Destination
@Composable
fun CityScreen(
    scaffoldState: ScaffoldState,
    navigator: OnboardingNavigator,
    viewModel: RegisterViewModel
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> navigator.navigateToNextScreen()
                is UiEvent.ShowMessage -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                }
                else -> Unit
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(
                    horizontal = spacing.spaceMedium
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(spacing.spaceSmall)
                    .width(IntrinsicSize.Min),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextFieldValidation(
                    value = state.city.cityName,
                    label = { Text(text = stringResource(id = R.string.city_label)) },
                    singleLine = true,
                    onValueChange = {
                        val city = state.city.copy(cityName = it)
                        viewModel.onEvent(RegistrationFormEvent.CityChanged(city))
                    }
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                OutlinedTextFieldValidation(
                    value = state.city.nbh,
                    label = { Text(text = stringResource(id = R.string.nbh_label)) },
                    singleLine = true,
                    onValueChange = {
                        val city = state.city.copy(nbh = it)
                        viewModel.onEvent(RegistrationFormEvent.CityChanged(city))
                    }
                )
//            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            }
            Button(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = spacing.spaceLarge),
                onClick = {
                    viewModel.onEvent(RegistrationFormEvent.CityNext)
                }, shape = RoundedCornerShape(spacing.spaceLarge)
            ) {
                Text(text = stringResource(id = R.string.next_button))
            }
        }
    }
}