package org.kagami.roommate.chat.presentation.matches

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.kagami.roommate.chat.navigation.MatchNavGraph
import org.kagami.roommate.chat.presentation.chat.ChatNavArgs
import org.kagami.roommate.chat.presentation.components.MatchListItem
import org.kagami.roommate.chat.presentation.destinations.ChatScreenDestination
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.UiEvent

@MatchNavGraph(start = true)
@Destination
@Composable
fun MatchesScreen(
    scaffoldState: ScaffoldState,
    navigator: DestinationsNavigator,
    viewModel: MatchesViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        viewModel.loadList()
        viewModel.uiEvent.collect { event ->
            when (event) {
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
    ) { paddingValues: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(spacing.spaceMedium),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = viewModel.state.matches,
                    key = { it.id }
                ) { match ->
                    MatchListItem(
                        match = match,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { participant ->
                            navigator.navigate(
                                ChatScreenDestination(
                                    navArgs = ChatNavArgs(
                                        matchId = match.id,
                                        participantId = participant.id,
                                        participantName = participant.name,
                                        profilePhoto = participant.photos[0]
                                    )
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}