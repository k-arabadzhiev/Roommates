package org.kagami.roommate.chat.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
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
import org.kagami.roommate.chat.domain.model.user.Interest
import org.kagami.roommate.chat.navigation.OnboardingNavGraph
import org.kagami.roommate.chat.navigation.OnboardingNavigator
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.Constants.MAX_INTERESTS
import org.kagami.roommate.chat.util.UiEvent

@OnboardingNavGraph
@Destination
@Composable
fun InterestsScreen(
    scaffoldState: ScaffoldState,
    navigator: OnboardingNavigator,
    viewModel: RegisterViewModel
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        // maybe move that to previous screen to act like prefetch
        viewModel.getInterestsList()
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
                    .padding(bottom = spacing.spaceExtraLarge),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.5f)
                        .padding(vertical = spacing.spaceMedium),
                    text = stringResource(id = R.string.interests_text, MAX_INTERESTS),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h1
                )
                InterestsList(
                    list = viewModel.state.interests,
                    onCheckedInterest = { id, checked ->
                        viewModel.onEvent(RegistrationFormEvent.InterestChecked(id, checked))
                    },
                    modifier = Modifier
                        .weight(8f)
                        .padding(bottom = spacing.spaceLarge)
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
fun InterestsList(
    list: List<Interest>,
    onCheckedInterest: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = list,
            key = { it.id }
        ) { item ->
            InterestListItem(
                interestName = item.name,
                checked = item.checked,
                onCheckedChange = { checked ->
                    onCheckedInterest(item.id, checked)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InterestListItem(
    interestName: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        text = { Text(interestName) },
        trailing = {
            Checkbox(
                checked = checked,
                onCheckedChange = null
            )
        },
        modifier = modifier.toggleable(
            value = checked,
            onValueChange = onCheckedChange
        )
    )
}