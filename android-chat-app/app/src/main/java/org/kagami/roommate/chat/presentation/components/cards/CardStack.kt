package org.kagami.roommate.chat.presentation.components.cards

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.domain.model.user.RoommateProfile
import kotlin.math.roundToInt

/**
 * A stack of cards that can be dragged.
 * If they are dragged after a [thresholdConfig] or exceed the [velocityThreshold] the card is swiped.
 *
 * @param items Cards to show in the stack.
 * @param thresholdConfig Specifies where the threshold between the predefined Anchors is. This is represented as a lambda
 * that takes two float and returns the threshold between them in the form of a [ThresholdConfig].
 * @param velocityThreshold The threshold (in dp per second) that the end velocity has to exceed
 * in order to swipe, even if the positional [thresholds] have not been reached.
 * @param enableButtons Show or not the buttons to swipe or not
 * @param onSwipeLeft Lambda that executes when the animation of swiping left is finished
 * @param onSwipeRight Lambda that executes when the animation of swiping right is finished
 * @param onEmptyRight Lambda that executes when the cards are all swiped
 */
@ExperimentalMaterialApi
@Composable
fun CardStack(
    modifier: Modifier = Modifier,
    items: List<RoommateProfile>,
    thresholdConfig: (Float, Float) -> ThresholdConfig = { _, _ -> FractionalThreshold(0.2f) },
    velocityThreshold: Dp = 125.dp,
    enableButtons: Boolean = false,
    onSwipeLeft: (item: RoommateProfile) -> Unit = {},
    onSwipeRight: (item: RoommateProfile) -> Unit = {},
    onEmptyStack: (index: Int) -> Unit = {},
    startIndex: Int = 0
) {

    var i by remember { mutableStateOf(startIndex) }
    if (i <= 1) {
        onEmptyStack(i + 1)
    }

    val cardStackController = rememberCardStackController()
    cardStackController.onSwipeLeft = {
        onSwipeLeft(items[i])
        i--
    }
    cardStackController.onSwipeRight = {
        onSwipeRight(items[i])
        i--
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        val (buttons, stack) = createRefs()

        if (enableButtons) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(buttons) {
                        bottom.linkTo(parent.bottom)
                        top.linkTo(stack.bottom)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = { if (i >= 0) cardStackController.swipeLeft() },
                    backgroundColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(5.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_thumbdown),
                        contentDescription = "", tint = Color.Red
                    )
                }
                Spacer(modifier = Modifier.width(70.dp))
                FloatingActionButton(
                    onClick = { if (i >= 0) cardStackController.swipeRight() },
                    backgroundColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(5.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_thumb_up),
                        contentDescription = "",
                        tint = Color.Green
                    )
                }
            }
        }

        Box(modifier = Modifier
            .constrainAs(stack) {
                top.linkTo(parent.top)
            }
            .draggableStack(
                controller = cardStackController,
                thresholdConfig = thresholdConfig,
                velocityThreshold = velocityThreshold
            )
            .fillMaxHeight(0.89f)
        ) {
            items.forEachIndexed { index, item ->
                println("CARDS index: $index, item: ${item.id} ${item.name}")
                Card(
                    modifier = Modifier
                        .moveTo(
                            x = if (index == i) cardStackController.offsetX.value else 0f,
                            y = if (index == i) cardStackController.offsetY.value else 0f
                        )
                        // the second condition will show the card under the top one
                        .visible(visible = index == i || index == i - 1)
                        .graphicsLayer(
                            rotationZ = if (index == i) cardStackController.rotation.value else 0f,
                            scaleX = if (index < i) cardStackController.scale.value else 1f,
                            scaleY = if (index < i) cardStackController.scale.value else 1f
                        )
                        .shadow(4.dp, RoundedCornerShape(10.dp)),
                    item = item
                )
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun Card(
    modifier: Modifier = Modifier,
    item: RoommateProfile
) {

    val context = LocalContext.current

    Box(
        modifier
    ) {
        var photoIndex by remember { mutableStateOf(0) }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            if (photoIndex > 0) photoIndex--
                        }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            if (photoIndex < item.photos.size - 1) photoIndex++
                        }
                )
            }

            // wish I knew a better way to handle this
            item.photos.forEachIndexed { index, url ->
                println("photos: $index $url")
                if (index == photoIndex) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(url)
                            .placeholder(R.drawable.ic_placeholder)
                            .size(400, 700)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.6f))
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            if (photoIndex == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (item.hasRoom) {
                        Text(
                            text = "🏠 ",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            modifier = Modifier.clickable(onClick = {}) // disable the highlight of the text when dragging
                        )
                    }
                    Text(
                        text = item.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        modifier = Modifier.clickable(onClick = {})// disable the highlight of the text when dragging
                    )
                    Text(
                        text = ", ${item.age}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        modifier = Modifier.clickable(onClick = {}) // disable the highlight of the text when dragging
                    )
                }
                Text(
                    text = "${item.budget}💲",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.clickable(onClick = {}) // disable the highlight of the text when dragging
                )
            }
            if (photoIndex == 1) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (item.userInterests.isNotEmpty()) {
                        Text(
                            text = item.userInterests.joinToString(", ") { it.interestName },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            modifier = Modifier.clickable(onClick = {})// disable the highlight of the text when dragging
                        )
                    }
                }
                Text(
                    text = "${item.city.cityName}, ${item.city.nbh}",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.clickable(onClick = {}) // disable the highlight of the text when dragging
                )
            }
            if (photoIndex == 2) {
                Text(
                    text = item.bio,
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.clickable(onClick = {}),
                    maxLines = 9// disable the highlight of the text when dragging
                )
            }
        }
    }
}

fun Modifier.moveTo(
    x: Float,
    y: Float
) = this.then(Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(x.roundToInt(), y.roundToInt())
    }
})

fun Modifier.visible(
    visible: Boolean = true
) = this.then(Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    if (visible) {
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    } else {
        layout(0, 0) {}
    }
})
