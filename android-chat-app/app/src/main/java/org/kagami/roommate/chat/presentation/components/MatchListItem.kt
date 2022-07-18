package org.kagami.roommate.chat.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.data.remote.ws.models.messages.PhotoMessage
import org.kagami.roommate.chat.data.remote.ws.models.messages.TextMessage
import org.kagami.roommate.chat.domain.model.user.Match
import org.kagami.roommate.chat.domain.model.user.RoommateProfile
import org.kagami.roommate.chat.ui.theme.LocalSpacing

@Composable
fun MatchListItem(match: Match, modifier: Modifier = Modifier, onClick: (RoommateProfile) -> Unit) {

    val context = LocalContext.current
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier
            .clickable {
                onClick(match.participant)
            }
            .padding(vertical = spacing.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(match.participant.photos[0])
                .size(300, 300)
                .transformations(CircleCropTransformation())
                .error(R.drawable.ic_placeholder)
                .crossfade(true)
                .build(),
            contentDescription = "Profile Photo",
        )
        Column(
            modifier = modifier
                .padding(horizontal = spacing.spaceMedium, vertical = spacing.spaceSmall)
        ) {
            Text(text = match.participant.name, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            when (val message = match.messages) {
                is TextMessage -> {
                    Text(text = message.content, fontStyle = FontStyle.Italic, fontSize = 20.sp)
                }
                is PhotoMessage -> {
                    Text(text = stringResource(id = R.string.photo_message))
                }
            }
        }
    }

}