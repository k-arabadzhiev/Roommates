package org.kagami.roommate.chat.presentation.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramcosta.composedestinations.annotation.Destination
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.domain.model.user.Gender
import org.kagami.roommate.chat.navigation.OnboardingNavGraph
import org.kagami.roommate.chat.navigation.OnboardingNavigator
import org.kagami.roommate.chat.presentation.components.ChipButton
import org.kagami.roommate.chat.presentation.components.OutlinedTextFieldValidation
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.UiEvent


@OnboardingNavGraph(start = true)
@Destination
@Composable
fun RegistrationScreen(
    scaffoldState: ScaffoldState,
    navigator: OnboardingNavigator,
    viewModel: RegisterViewModel
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    val state = viewModel.state
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(spacing.spaceSmall)
                    .width(IntrinsicSize.Min),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextFieldValidation(
                    label = { Text(text = stringResource(id = R.string.username_label)) },
                    value = state.username,
                    singleLine = true,
                    error = state.usernameError?.asString() ?: "",
                    onValueChange = {
                        viewModel.onEvent(RegistrationFormEvent.UsernameChanged(it))
                    }
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                OutlinedTextFieldValidation(
                    value = state.password,
                    onValueChange = {
                        viewModel.onEvent(RegistrationFormEvent.PasswordChanged(it))
                    },
                    label = { Text(text = stringResource(id = R.string.password_label)) },
                    singleLine = true,
                    error = state.passwordError?.asString() ?: "",
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val description =
                            if (passwordVisible) stringResource(id = R.string.hide_pass)
                            else stringResource(id = R.string.show_pass)
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            if (passwordVisible)
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_visibility),
                                    contentDescription = description
                                )
                            else Icon(
                                painter = painterResource(id = R.drawable.ic_visibility_off),
                                contentDescription = description
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                OutlinedTextFieldValidation(
                    label = { Text(text = stringResource(id = R.string.name_label)) },
                    placeholder = {
                        Text(text = stringResource(id = R.string.example_name))
                    },
                    error = state.nameError?.asString() ?: "",
                    value = state.name,
                    singleLine = true,
                    onValueChange = {
                        viewModel.onEvent(RegistrationFormEvent.NameChanged(it))
                    }
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                OutlinedTextFieldValidation(
                    label = { Text(text = stringResource(id = R.string.age_label)) },
                    placeholder = {
                        Text(text = stringResource(id = R.string.example_age))
                    },
                    value = state.age,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        viewModel.onEvent(RegistrationFormEvent.AgeChanged(it))
                    }
                )
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
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
                            viewModel.onEvent(RegistrationFormEvent.GenderChanged)
                        }
                    )
                    Spacer(modifier = Modifier.width(spacing.spaceMedium))
                    ChipButton(
                        text = stringResource(id = R.string.female),
                        isSelected = state.gender == Gender.Female,
                        color = MaterialTheme.colors.primary,
                        selectedTextColor = Color.White,
                        onClick = {
                            viewModel.onEvent(RegistrationFormEvent.GenderChanged)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.onEvent(RegistrationFormEvent.Register)
                        },
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.register_button),
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(spacing.spaceMedium))
            }
        }
    }
}
