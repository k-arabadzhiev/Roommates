package org.kagami.roommate.chat.presentation.account

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.domain.model.user.Gender
import org.kagami.roommate.chat.presentation.components.ChipButton
import org.kagami.roommate.chat.presentation.components.OutlinedTextFieldValidation
import org.kagami.roommate.chat.presentation.components.PickImages
import org.kagami.roommate.chat.presentation.components.SliderWithLabel
import org.kagami.roommate.chat.presentation.onboarding.BioTextField
import org.kagami.roommate.chat.presentation.onboarding.RegistrationFormEvent
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.Constants
import org.kagami.roommate.chat.util.UiEvent

@RootNavGraph
@Destination
@Composable
fun SettingsScreen(
    scaffoldState: ScaffoldState,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
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

    val scrollState = rememberScrollState()
    BoxWithConstraints {
        val pageSize = this.maxHeight
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.spaceMedium)
                .verticalScroll(state = scrollState),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // this should not be like that, idk whats wrong
                    .height(pageSize + 600.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextFieldValidation(
                    label = { Text(text = stringResource(id = R.string.name_label)) },
                    placeholder = {
                        Text(text = state.name)
                    },
                    value = state.name,
                    singleLine = true,
                    onValueChange = {
                        viewModel.onEvent(SettingsEvent.NameChanged(it))
                    }
                )
                Divider(modifier = Modifier.fillMaxWidth())
                OutlinedTextFieldValidation(
                    label = { Text(text = stringResource(id = R.string.age_label)) },
                    placeholder = {
                        Text(text = stringResource(id = R.string.example_age))
                    },
                    value = state.age,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        viewModel.onEvent(SettingsEvent.AgeChanged(it))
                    }
                )
                Divider(modifier = Modifier.fillMaxWidth())
                SliderWithLabel(
                    values = state.ageFilterMin.rangeTo(state.ageFilterMax),
                    onValueChange = {
                        val min = it.start
                        val max = it.endInclusive
                        viewModel.onEvent(SettingsEvent.AgeRange(min, max))
                    },
                    valueRange = Constants.MIN_AGE..Constants.MAX_AGE,
                    steps = 0
                )
                Divider(modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    ChipButton(
                        text = stringResource(id = R.string.male),
                        isSelected = state.gender == Gender.Male,
                        color = MaterialTheme.colors.primary,
                        selectedTextColor = Color.White,
                        onClick = {
                            viewModel.onEvent(SettingsEvent.GenderChanged)
                        }
                    )
                    Spacer(modifier = Modifier.width(spacing.spaceMedium))
                    ChipButton(
                        text = stringResource(id = R.string.female),
                        isSelected = state.gender == Gender.Female,
                        color = MaterialTheme.colors.primary,
                        selectedTextColor = Color.White,
                        onClick = {
                            viewModel.onEvent(SettingsEvent.GenderChanged)
                        }
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !state.hasRoom,
                        onClick = { viewModel.onEvent(SettingsEvent.HasRoomChanged) },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary)
                    )
                    Text(
                        text = stringResource(id = R.string.false_hasRoom)
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth())
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.hasRoom,
                        onClick = { viewModel.onEvent(SettingsEvent.HasRoomChanged) },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary)
                    )
                    Text(
                        text = stringResource(id = R.string.true_hasRoom)
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth())
                SliderWithLabel(
                    values = state.minBudgetRange.rangeTo(state.maxBudgetRange),
                    onValueChange = {
                        val min = it.start
                        val max = it.endInclusive
                        viewModel.onEvent(SettingsEvent.BudgetChanged(min, max))
                    },
                    valueRange = Constants.MIN_BUDGET..Constants.MAX_BUDGET,
                    steps = 0
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.budget_on_profile_explanation,
                            state.budget.toInt()
                        ),
                        style = TextStyle(
                            fontStyle = FontStyle.Italic,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Start
                        )
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth())

                val (selectedOption, onOptionSelected) = remember {
                    mutableStateOf(0)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Constants.GENDER_CHOICES.forEachIndexed { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(
                                selected = selectedOption == index,
                                onClick = {
                                    onOptionSelected(index)
                                    viewModel.onEvent(SettingsEvent.InterestedInChanged(index))
                                },
                                enabled = true,
                                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary)
                            )
                            val annotatedString = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(fontWeight = FontWeight.Bold)
                                ) { append(item) }
                            }
                            ClickableText(
                                text = annotatedString,
                                onClick = {
                                    viewModel.onEvent(
                                        SettingsEvent.InterestedInChanged(
                                            index
                                        )
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(spacing.spaceMedium))
                        }
                    }
                }
                Divider(modifier = Modifier.fillMaxWidth())
                OutlinedTextFieldValidation(
                    value = state.city.cityName,
                    label = { Text(text = stringResource(id = R.string.city_label)) },
                    singleLine = true,
                    onValueChange = {
                        val city = state.city.copy(cityName = it)
                        viewModel.onEvent(SettingsEvent.CityChanged(city))
                    }
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                OutlinedTextFieldValidation(
                    value = state.city.nbh,
                    label = { Text(text = stringResource(id = R.string.nbh_label)) },
                    singleLine = true,
                    onValueChange = {
                        val city = state.city.copy(nbh = it)
                        viewModel.onEvent(SettingsEvent.CityChanged(city))
                    }
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Divider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                BioTextField(
                    text = viewModel.state.bio,
                    onTextChange = {
                        viewModel.onEvent(SettingsEvent.BioChanged(it))
                    },
                    characters = viewModel.state.bioCharacters
                )
                Divider(modifier = Modifier.fillMaxWidth())
                if (state.photoList.isEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 96.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        userScrollEnabled = false
                    ) {
                        items(state.photos) { url ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(url)
                                    .build(),
                                contentDescription = "Image to upload"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Divider(modifier = Modifier.fillMaxWidth())
                PickImages(
                    modifier = Modifier.fillMaxWidth(),
                    onPhotosSelect = {
                        viewModel.onEvent(SettingsEvent.PhotoChange(it))
                    }
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Divider(modifier = Modifier.fillMaxWidth())
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd)
                    /*.border(2.dp, Color.Magenta)*/,
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(onClick = {
                    viewModel.onEvent(SettingsEvent.Update)
                }) {
                    Text(text = stringResource(id = R.string.update))
                }
            }
            Spacer(
                modifier = Modifier
                    .padding(top = pageSize)
                    .fillMaxWidth()
                    .height(92.dp)
            )
        }
    }
}
