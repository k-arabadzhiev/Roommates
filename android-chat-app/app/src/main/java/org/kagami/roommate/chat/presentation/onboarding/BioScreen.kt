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
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.navigation.OnboardingNavGraph
import org.kagami.roommate.chat.navigation.OnboardingNavigator
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.Constants.MAX_BIO
import org.kagami.roommate.chat.util.UiEvent

@OnboardingNavGraph
@Destination
@Composable
fun BioScreen(
    scaffoldState: ScaffoldState,
    navigator: OnboardingNavigator,
    viewModel: RegisterViewModel
) {
    val spacing = LocalSpacing.current
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
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.bio_screen_text),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h1
                )
                Spacer(modifier = Modifier.height(spacing.spaceSmall))
                BioTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = viewModel.state.bio,
                    onTextChange = {
                        viewModel.onEvent(RegistrationFormEvent.BioChanged(it))
                    },
                    characters = viewModel.state.bioCharacters
                )
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

@Composable
fun BioTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    characters: Int,
    maxLines: Int = 8
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = modifier.height(200.dp),
            value = text,
            onValueChange = onTextChange,
            maxLines = maxLines,
            singleLine = false
        )
        Box(
            modifier = modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(text = "$characters / $MAX_BIO")
        }
    }
}