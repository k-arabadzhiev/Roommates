package org.kagami.roommate.chat.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.kagami.roommate.chat.presentation.chat.ChatNavArgs
import org.kagami.roommate.chat.presentation.components.cards.CardStack
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.UiEvent

@OptIn(ExperimentalMaterialApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    scaffoldState: ScaffoldState,
    navigator: DestinationsNavigator,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val state = viewModel.state
    val context = LocalContext.current
    val spacing = LocalSpacing.current

    LaunchedEffect(key1 = context) {
        viewModel.reloadSuggestions()
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> {}
                is UiEvent.ShowMessage -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                }
                is UiEvent.NavigateUp -> {
                    navigator.popBackStack()
                }
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
                .padding(horizontal = spacing.spaceMedium),
            contentAlignment = Alignment.Center
        ) {
            if (!state.isLoading) {
                CardStack(
                     items = state.items,
                    onSwipeLeft = { profile ->
                        viewModel.onEvent(HomeEvent.LeftSwipe(profile.id))
                    },
                    onSwipeRight = { profile ->
                        viewModel.onEvent(HomeEvent.RightSwipe(profile.id, profile.name))
                    },
                    enableButtons = true,
                    onEmptyStack = {
                        viewModel.onEvent(HomeEvent.Reload(it))
                    },
                    startIndex = state.index - 1
                )
            } else {
                CircularProgressIndicator()
            }
        }
    }
}