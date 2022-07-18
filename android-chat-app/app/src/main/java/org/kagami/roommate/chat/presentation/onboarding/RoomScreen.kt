package org.kagami.roommate.chat.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.navigation.OnboardingNavGraph
import org.kagami.roommate.chat.navigation.OnboardingNavigator
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.UiEvent

@OnboardingNavGraph
@Destination
@Composable
fun RoomScreen(
    scaffoldState: ScaffoldState,
    navigator: OnboardingNavigator,
    viewModel: RegisterViewModel
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val state = viewModel.state

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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = spacing.spaceMedium),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.room_preference_screen_text),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h1
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !state.hasRoom,
                        onClick = { viewModel.onEvent(RegistrationFormEvent.HasRoomChanged) },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary)
                    )
                    Text(
                        text = stringResource(id = R.string.false_hasRoom)
                    )
                }
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.hasRoom,
                        onClick = { viewModel.onEvent(RegistrationFormEvent.HasRoomChanged) },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary)
                    )
                    Text(
                        text = stringResource(id = R.string.true_hasRoom)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.spaceMedium),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(id = R.string.true_explanation),
                        style = TextStyle(
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start
                        )
                    )
                }
            }
            Button(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = spacing.spaceLarge),
                onClick = {
                    viewModel.onEvent(RegistrationFormEvent.Next)
                }, shape = RoundedCornerShape(spacing.spaceLarge)
            ) {
                Text(text = stringResource(id = R.string.next_button))
            }
        }
    }
}