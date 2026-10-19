package com.example.ui.screens

import android.util.Patterns
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.InfoDialog
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryLight
import com.example.ui.theme.RoseDue
import com.example.util.PasswordStrength
import com.example.util.SecurityUtils
import com.example.viewmodel.MyDueViewModel

enum class AuthMode {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    RESET_PASSWORD
}

@Composable
fun AuthScreen(
    viewModel: MyDueViewModel,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(AuthMode.LOGIN) }
    val focusManager = LocalFocusManager.current

    // Login Form State
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var showLoginPass by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    // Register Form State
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regUsername by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var showRegPass by remember { mutableStateOf(false) }
    var showRegConfirmPass by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }

    // Forgot / Reset Password State
    var resetIdentifier by remember { mutableStateOf("") }
    var resetVerificationCode by remember { mutableStateOf("") }
    var resetNewPassword by remember { mutableStateOf("") }
    var resetConfirmPassword by remember { mutableStateOf("") }
    var showResetNewPass by remember { mutableStateOf(false) }
    var showResetConfirmPass by remember { mutableStateOf(false) }

    // Feedback State
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Dialogs
    var codeDialogContent by remember { mutableStateOf<String?>(null) }
    var showTermsDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    fun clearFeedback() {
        errorMessage = null
        successMessage = null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("auth_screen")
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Retro Brand Header
            Image(
                painter = painterResource(id = R.drawable.app_icon_wallet_foreground),
                contentDescription = "MyDue Brand Icon",
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .testTag("auth_brand_logo")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "MyDue Ledger",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Customer Credit & Frosted Ledger",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Frosted Glass Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = com.example.ui.components.glassBorderStroke(1.2.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Mode Selector (Pill Tabs for Login vs Register)
                    if (mode == AuthMode.LOGIN || mode == AuthMode.REGISTER) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (mode == AuthMode.LOGIN) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        mode = AuthMode.LOGIN
                                        clearFeedback()
                                    }
                                    .padding(vertical = 10.dp)
                                    .testTag("tab_login"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sign In",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mode == AuthMode.LOGIN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (mode == AuthMode.REGISTER) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        mode = AuthMode.REGISTER
                                        clearFeedback()
                                    }
                                    .padding(vertical = 10.dp)
                                    .testTag("tab_register"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Register",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (mode == AuthMode.REGISTER) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Success Banner
                    AnimatedVisibility(
                        visible = successMessage != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = successMessage ?: "",
                                    color = Color(0xFF047857),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Error Banner
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = RoseDue.copy(alpha = 0.15f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = RoseDue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    color = RoseDue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Dynamic Form Content Based on Mode
                    AnimatedContent(targetState = mode, label = "AuthContent") { currentMode ->
                        when (currentMode) {
                            AuthMode.LOGIN -> {
                                LoginForm(
                                    identifier = loginIdentifier,
                                    onIdentifierChange = { loginIdentifier = it; clearFeedback() },
                                    password = loginPassword,
                                    onPasswordChange = { loginPassword = it; clearFeedback() },
                                    showPassword = showLoginPass,
                                    onToggleShowPassword = { showLoginPass = !showLoginPass },
                                    rememberMe = rememberMe,
                                    onRememberMeChange = { rememberMe = it },
                                    isLoading = isLoading,
                                    onLoginSubmit = {
                                        clearFeedback()
                                        if (loginIdentifier.isBlank()) {
                                            errorMessage = "Please enter your email or username"
                                            return@LoginForm
                                        }
                                        if (loginPassword.isBlank()) {
                                            errorMessage = "Please enter your password"
                                            return@LoginForm
                                        }
                                        isLoading = true
                                        viewModel.loginUser(
                                            identifier = loginIdentifier,
                                            pass = loginPassword,
                                            onSuccess = { isLoading = false },
                                            onError = {
                                                isLoading = false
                                                errorMessage = it
                                            }
                                        )
                                    },
                                    onForgotPassword = {
                                        resetIdentifier = loginIdentifier
                                        mode = AuthMode.FORGOT_PASSWORD
                                        clearFeedback()
                                    },
                                    onSwitchToRegister = {
                                        mode = AuthMode.REGISTER
                                        clearFeedback()
                                    }
                                )
                            }

                            AuthMode.REGISTER -> {
                                RegisterForm(
                                    name = regName,
                                    onNameChange = { regName = it; clearFeedback() },
                                    email = regEmail,
                                    onEmailChange = { regEmail = it; clearFeedback() },
                                    username = regUsername,
                                    onUsernameChange = { regUsername = it; clearFeedback() },
                                    phone = regPhone,
                                    onPhoneChange = { regPhone = it; clearFeedback() },
                                    password = regPassword,
                                    onPasswordChange = { regPassword = it; clearFeedback() },
                                    confirmPassword = regConfirmPassword,
                                    onConfirmPasswordChange = { regConfirmPassword = it; clearFeedback() },
                                    showPassword = showRegPass,
                                    onToggleShowPassword = { showRegPass = !showRegPass },
                                    showConfirmPassword = showRegConfirmPass,
                                    onToggleShowConfirmPassword = { showRegConfirmPass = !showRegConfirmPass },
                                    agreeTerms = agreeTerms,
                                    onAgreeTermsChange = { agreeTerms = it },
                                    onViewTerms = { showTermsDialog = true },
                                    isLoading = isLoading,
                                    onRegisterSubmit = {
                                        clearFeedback()
                                        if (regName.isBlank()) {
                                            errorMessage = "Please enter your full name"
                                            return@RegisterForm
                                        }
                                        if (regEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(regEmail.trim()).matches()) {
                                            errorMessage = "Please enter a valid email address"
                                            return@RegisterForm
                                        }
                                        val cleanUser = if (regUsername.isBlank()) regEmail.substringBefore("@") else regUsername.trim()
                                        if (cleanUser.length < 3) {
                                            errorMessage = "Username must be at least 3 characters"
                                            return@RegisterForm
                                        }
                                        if (regPassword.length < 4) {
                                            errorMessage = "Password must be at least 4 characters"
                                            return@RegisterForm
                                        }
                                        if (regPassword != regConfirmPassword) {
                                            errorMessage = "Passwords do not match"
                                            return@RegisterForm
                                        }
                                        if (!agreeTerms) {
                                            errorMessage = "Please agree to the Terms & Conditions"
                                            return@RegisterForm
                                        }
                                        isLoading = true
                                        viewModel.registerUser(
                                            name = regName,
                                            email = regEmail,
                                            phone = regPhone,
                                            username = cleanUser,
                                            pass = regPassword,
                                            onSuccess = { isLoading = false },
                                            onError = {
                                                isLoading = false
                                                errorMessage = it
                                            }
                                        )
                                    },
                                    onSwitchToLogin = {
                                        mode = AuthMode.LOGIN
                                        clearFeedback()
                                    }
                                )
                            }

                            AuthMode.FORGOT_PASSWORD -> {
                                ForgotPasswordForm(
                                    identifier = resetIdentifier,
                                    onIdentifierChange = { resetIdentifier = it; clearFeedback() },
                                    isLoading = isLoading,
                                    onSubmit = {
                                        clearFeedback()
                                        if (resetIdentifier.isBlank()) {
                                            errorMessage = "Please enter your registered email or username"
                                            return@ForgotPasswordForm
                                        }
                                        isLoading = true
                                        viewModel.forgotPassword(
                                            identifier = resetIdentifier,
                                            onSuccess = { code, email ->
                                                isLoading = false
                                                codeDialogContent = "Verification Code: $code\n\nA 6-digit password reset code has been generated for $email. (Code valid for 15 minutes)."
                                                resetVerificationCode = code
                                                mode = AuthMode.RESET_PASSWORD
                                                successMessage = "Code sent! Check code dialog and enter new password."
                                            },
                                            onError = {
                                                isLoading = false
                                                errorMessage = it
                                            }
                                        )
                                    },
                                    onBackToLogin = {
                                        mode = AuthMode.LOGIN
                                        clearFeedback()
                                    }
                                )
                            }

                            AuthMode.RESET_PASSWORD -> {
                                ResetPasswordForm(
                                    identifier = resetIdentifier,
                                    code = resetVerificationCode,
                                    onCodeChange = { resetVerificationCode = it; clearFeedback() },
                                    newPassword = resetNewPassword,
                                    onNewPasswordChange = { resetNewPassword = it; clearFeedback() },
                                    confirmPassword = resetConfirmPassword,
                                    onConfirmPasswordChange = { resetConfirmPassword = it; clearFeedback() },
                                    showNewPassword = showResetNewPass,
                                    onToggleShowNewPassword = { showResetNewPass = !showResetNewPass },
                                    showConfirmPassword = showResetConfirmPass,
                                    onToggleShowConfirmPassword = { showResetConfirmPass = !showResetConfirmPass },
                                    isLoading = isLoading,
                                    onSubmit = {
                                        clearFeedback()
                                        if (resetVerificationCode.trim().length != 6) {
                                            errorMessage = "Please enter the 6-digit verification code"
                                            return@ResetPasswordForm
                                        }
                                        if (resetNewPassword.length < 4) {
                                            errorMessage = "New password must be at least 4 characters"
                                            return@ResetPasswordForm
                                        }
                                        if (resetNewPassword != resetConfirmPassword) {
                                            errorMessage = "Passwords do not match"
                                            return@ResetPasswordForm
                                        }
                                        isLoading = true
                                        viewModel.resetPassword(
                                            identifier = resetIdentifier,
                                            code = resetVerificationCode,
                                            newPass = resetNewPassword,
                                            onSuccess = {
                                                isLoading = false
                                                loginIdentifier = resetIdentifier
                                                loginPassword = resetNewPassword
                                                mode = AuthMode.LOGIN
                                                successMessage = "Password reset successful! You can now log in."
                                            },
                                            onError = {
                                                isLoading = false
                                                errorMessage = it
                                            }
                                        )
                                    },
                                    onBackToLogin = {
                                        mode = AuthMode.LOGIN
                                        clearFeedback()
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }
    }

    codeDialogContent?.let { content ->
        InfoDialog(
            title = "Verification Code",
            content = content,
            onDismiss = { codeDialogContent = null }
        )
    }

    if (showTermsDialog) {
        InfoDialog(
            title = "Terms & Security Policy",
            content = "MyDue secures your credentials using cryptographic salted hashing (SHA-256 with 128-bit secure random salt). All customer ledger balances, credit entries, and account records are encrypted and stored safely in your device's private database.\n\nYou can backup, export, and restore your full records anytime via the JSON Backup manager in Settings.",
            onDismiss = { showTermsDialog = false }
        )
    }
}

@Composable
fun LoginForm(
    identifier: String,
    onIdentifierChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onToggleShowPassword: () -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    isLoading: Boolean,
    onLoginSubmit: () -> Unit,
    onForgotPassword: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Welcome Back",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Enter your credentials to access your credit ledger",
            fontSize = 12.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = identifier,
            onValueChange = onIdentifierChange,
            label = { Text("Email or Username *", fontSize = 12.5.sp) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_username_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Passcode *", fontSize = 12.5.sp) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = {
                IconButton(onClick = onToggleShowPassword) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            ),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onLoginSubmit()
            }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = onRememberMeChange,
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )
                Text("Remember session", fontSize = 12.sp)
            }

            TextButton(
                onClick = onForgotPassword,
                modifier = Modifier.testTag("forgot_password_btn")
            ) {
                Text("Forgot Passcode?", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onLoginSubmit,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("login_submit_btn"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(
                    text = "Sign In",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an account? ", fontSize = 12.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "Register",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { onSwitchToRegister() }
                    .testTag("switch_to_register_btn")
            )
        }
    }
}

@Composable
fun RegisterForm(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onToggleShowPassword: () -> Unit,
    showConfirmPassword: Boolean,
    onToggleShowConfirmPassword: () -> Unit,
    agreeTerms: Boolean,
    onAgreeTermsChange: (Boolean) -> Unit,
    onViewTerms: () -> Unit,
    isLoading: Boolean,
    onRegisterSubmit: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val passwordStrength = remember(password) { SecurityUtils.calculatePasswordStrength(password) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Create New Account",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Store customer credits securely in your offline ledger",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name *") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimaryLight) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_name_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email Address *") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BluePrimaryLight) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_email_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier
                    .weight(1f)
                    .testTag("reg_username_input")
            )

            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Phone") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = BluePrimaryLight) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier
                    .weight(1f)
                    .testTag("reg_phone_input")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password (min 4 chars) *") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimaryLight) },
            trailingIcon = {
                IconButton(onClick = onToggleShowPassword) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_password_input")
        )

        // Password Strength Indicator
        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val barColor = when (passwordStrength) {
                    PasswordStrength.TOO_SHORT -> RoseDue
                    PasswordStrength.WEAK -> Color(0xFFF59E0B)
                    PasswordStrength.MEDIUM -> BluePrimaryLight
                    PasswordStrength.STRONG -> Color(0xFF10B981)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(barColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = passwordStrength.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = barColor
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm Password *") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimaryLight) },
            trailingIcon = {
                IconButton(onClick = onToggleShowConfirmPassword) {
                    Icon(
                        imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle confirm password visibility"
                    )
                }
            },
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onRegisterSubmit()
            }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reg_confirm_password_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = agreeTerms,
                onCheckedChange = onAgreeTermsChange,
                colors = CheckboxDefaults.colors(checkedColor = BluePrimary),
                modifier = Modifier.testTag("reg_terms_checkbox")
            )
            Text("I agree to ", fontSize = 12.sp)
            Text(
                text = "Terms & Security Policy",
                fontSize = 12.sp,
                color = BluePrimaryLight,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onViewTerms() }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = onRegisterSubmit,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("register_submit_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                Text("Create Free Account", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account? ", fontSize = 13.sp)
            Text(
                text = "Login",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimaryLight,
                modifier = Modifier
                    .clickable { onSwitchToLogin() }
                    .testTag("switch_to_login_btn")
            )
        }
    }
}

@Composable
fun ForgotPasswordForm(
    identifier: String,
    onIdentifierChange: (String) -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackToLogin) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login")
            }
            Text(
                text = "Forgot Password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "Enter your registered email address or username. A 6-digit verification code will be generated to reset your password.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 16.dp)
        )

        OutlinedTextField(
            value = identifier,
            onValueChange = onIdentifierChange,
            label = { Text("Registered Email or Username *") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BluePrimaryLight) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onSubmit()
            }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("forgot_email_input")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onSubmit,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("forgot_submit_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                Text("Generate Reset Code", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Sign In", color = BluePrimaryLight, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ResetPasswordForm(
    identifier: String,
    code: String,
    onCodeChange: (String) -> Unit,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    showNewPassword: Boolean,
    onToggleShowNewPassword: () -> Unit,
    showConfirmPassword: Boolean,
    onToggleShowConfirmPassword: () -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackToLogin) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Reset Password",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "Enter the 6-digit code for account '$identifier' and create your new secure password.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 14.dp)
        )

        OutlinedTextField(
            value = code,
            onValueChange = { if (it.length <= 6) onCodeChange(it) },
            label = { Text("6-Digit Verification Code *") },
            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = BluePrimaryLight) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reset_code_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = { Text("New Password (min 4 chars) *") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimaryLight) },
            trailingIcon = {
                IconButton(onClick = onToggleShowNewPassword) {
                    Icon(
                        imageVector = if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reset_password_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirm New Password *") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimaryLight) },
            trailingIcon = {
                IconButton(onClick = onToggleShowConfirmPassword) {
                    Icon(
                        imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle confirm password visibility"
                    )
                }
            },
            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onSubmit()
            }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reset_confirm_password_input")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onSubmit,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("reset_submit_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                Text("Update Password & Login", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel and Return to Login", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
