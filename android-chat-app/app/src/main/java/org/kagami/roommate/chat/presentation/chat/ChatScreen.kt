package org.kagami.roommate.chat.presentation.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.ramcosta.composedestinations.annotation.Destination
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.data.remote.ws.models.messages.ChatMessage
import org.kagami.roommate.chat.data.remote.ws.models.messages.PhotoMessage
import org.kagami.roommate.chat.data.remote.ws.models.messages.TextMessage
import org.kagami.roommate.chat.navigation.MatchNavGraph
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.UiEvent
import org.kagami.roommate.chat.util.format
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@MatchNavGraph
@Destination(navArgsDelegate = ChatNavArgs::class)
@Composable
fun ChatScreen(
    navArgs: ChatNavArgs,
    scaffoldState: ScaffoldState,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val state = viewModel.state

    LaunchedEffect(key1 = context) {
        viewModel.loadMessages()
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> {}
                is UiEvent.ShowMessage -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message.asString(context)
                    )
                }
                else -> Unit
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.observeMessages()
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceMedium),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            item {
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
            }
            items(state.messages) { message ->
                val isIncoming = navArgs.participantId == message.from
                ChatMessageItem(message, isIncoming, navArgs.profilePhoto)
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = viewModel.messageText.value,
                onValueChange = viewModel::onMessageChange,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = viewModel::sendMessage) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isIncoming: Boolean,
    photo: String
) {

    val isHidden = remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                isHidden.value = !isHidden.value
            },
//            .border(2.dp, Color.Red),
        contentAlignment = if (isIncoming) {
            Alignment.CenterStart
        } else Alignment.CenterEnd,
    ) {
        Column(
            modifier = Modifier
                .width(290.dp)
                .padding(horizontal = 10.dp),
//                .border(2.dp, Color.Blue),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = if (isIncoming) {
                Alignment.Start
            } else Alignment.End
        ) {

            when (message) {
                is TextMessage -> {
                    if (isIncoming) {
                        Row(
//                            modifier = Modifier.border(2.dp, Color.Green),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(photo)
                                    .size(130, 130)
                                    .transformations(CircleCropTransformation())
                                    .error(R.drawable.ic_placeholder)
                                    .crossfade(true)
                                    .build(), contentDescription = "Profile Image"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = message.content, fontSize = 16.sp)
                        }
                    } else
                        Text(text = message.content, fontSize = 16.sp)
                }
                is PhotoMessage -> {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(message.url)
                            .size(450, 850)
                            .error(R.drawable.ic_placeholder)
                            .crossfade(true)
                            .build(), contentDescription = "Image"
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                contentAlignment = if (isIncoming)
                    Alignment.BottomStart
                else Alignment.BottomEnd,
            ) {
                if (!isHidden.value) {
                    val time = ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(message.timestamp),
                        ZoneId.systemDefault()
                    )
                    Text(
                        text = time.format(),
//                        modifier = Modifier.align(alignment = Alignment.BottomEnd)
                    )
                }
            }
        }
    }
}