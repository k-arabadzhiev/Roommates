package org.kagami.roommate.chat.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import org.kagami.roommate.chat.R
import org.kagami.roommate.chat.navigation.OnboardingNavGraph
import org.kagami.roommate.chat.presentation.components.OutlinedTextFieldValidation
import org.kagami.roommate.chat.presentation.destinations.HomeScreenDestination
import org.kagami.roommate.chat.presentation.destinations.LoginScreenDestination
import org.kagami.roommate.chat.presentation.destinations.RegistrationScreenDestination
import org.kagami.roommate.chat.ui.theme.LocalSpacing
import org.kagami.roommate.chat.util.UiEvent


@RootNavGraph
@OnboardingNavGraph
@Destination
@Composable
fun LoginScreen(
    scaffoldState: ScaffoldState,
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    navigator.navigate(HomeScreenDestination) {
                        popUpTo(HomeScreenDestination) {
                            inclusive = false
                        }
                    }
                }
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
        ) {
            ClickableText(
                text = AnnotatedString(stringResource(id = R.string.sign_up_here)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(spacing.spaceLarge),
                onClick = {
                    navigator.navigate(RegistrationScreenDestination) {
                        popUpTo(LoginScreenDestination) {
                            inclusive = true
                        }
                    }
                },
                style = TextStyle(
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.primaryVariant,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.login_logo_text),
                style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextFieldValidation(
                label = { Text(text = stringResource(id = R.string.username_label)) },
                value = state.username,
                singleLine = true,
                error = state.usernameError?.asString() ?: "",
                onValueChange = {
                    viewModel.onEvent(LoginEvent.UsernameChanged(it))
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextFieldValidation(
                value = state.password,
                onValueChange = {
                    viewModel.onEvent(LoginEvent.PasswordChanged(it))
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
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = { viewModel.onEvent(LoginEvent.Login) },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.login),
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}