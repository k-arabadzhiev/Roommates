package org.kagami.roommate.chat.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.ramcosta.composedestinations.annotation.Destination
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.navigation.OnboardingNavGraph
import org.kagami.roommate.chat.navigation.OnboardingNavigator
import org.kagami.roommate.chat.presentation.components.PickImages
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.Constants.MAX_PHOTO
import org.kagami.roommate.chat.util.UiEvent

@OnboardingNavGraph
@Destination
@Composable
fun PhotoScreen(
    scaffoldState: ScaffoldState,
    navigator: OnboardingNavigator,
    viewModel: RegisterViewModel
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

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
                    .padding(vertical = spacing.spaceMedium),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.photo_screen_text, MAX_PHOTO),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h1
                )
                Spacer(modifier = Modifier.height(spacing.spaceSmall))
                PickImages(
                    modifier = Modifier.fillMaxSize(),
                    onPhotosSelect = {
                        viewModel.onEvent(RegistrationFormEvent.PhotoChange(it))
                    }
                )
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Button(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = spacing.spaceLarge),
                onClick = {
                    viewModel.onEvent(RegistrationFormEvent.ProfileUpdate)
                }, shape = RoundedCornerShape(spacing.spaceLarge)
            ) {
                Text(text = stringResource(id = R.string.next_button))
            }
        }
    }
}