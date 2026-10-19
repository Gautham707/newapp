package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CreditLedgerScreen
import com.example.ui.screens.CustomerDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryLight
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MyDueViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: MyDueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = userProfile.isDarkMode) {
                MyDueApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MyDueApp(viewModel: MyDueViewModel) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Intercept back gestures/buttons across secondary screens
    BackHandler(enabled = currentScreen !is Screen.Home && currentScreen !is Screen.Auth) {
        viewModel.navigateBack()
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.dismissToast()
        }
    }

    val showBottomNav = currentScreen !is Screen.Auth && currentScreen !is Screen.Detail

    com.example.ui.components.GlassMorphicBackground(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                AnimatedVisibility(
                    visible = showBottomNav,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    FloatingBottomNav(
                        currentScreen = currentScreen,
                        onNavigate = { screen -> viewModel.navigateTo(screen) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.98f, animationSpec = tween(220)))
                            .togetherWith(fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.98f, animationSpec = tween(180)))
                    },
                    label = "screen_transition"
                ) { targetScreen ->
                    when (targetScreen) {
                        is Screen.Auth -> AuthScreen(viewModel = viewModel)
                        is Screen.Home -> HomeScreen(viewModel = viewModel)
                        is Screen.Credit -> CreditLedgerScreen(viewModel = viewModel)
                        is Screen.Profile -> ProfileScreen(viewModel = viewModel)
                        is Screen.Detail -> CustomerDetailScreen(
                            customerId = targetScreen.customerId,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = com.example.ui.theme.isAppInDarkTheme()
    val glassBorder = com.example.ui.components.glassBorderStroke(1.2.dp)
    val navShape = RoundedCornerShape(32.dp)
    val shadowSpot = if (isDark) Color(0xFF6366F1).copy(alpha = 0.28f) else Color(0xFF6366F1).copy(alpha = 0.12f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 10.dp,
                    shape = navShape,
                    spotColor = shadowSpot,
                    ambientColor = Color.Black.copy(alpha = 0.04f)
                )
                .clip(navShape)
                .background(com.example.ui.components.glassSurfaceBrush())
                .border(glassBorder.width, glassBorder.brush, navShape)
                .testTag("floating_nav_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTabItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = currentScreen is Screen.Home,
                    testTag = "nav_home",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.Home) }
                )

                NavTabItem(
                    icon = Icons.Default.AccountBalanceWallet,
                    label = "Credit",
                    isSelected = currentScreen is Screen.Credit,
                    testTag = "nav_credit",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.Credit) }
                )

                NavTabItem(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    isSelected = currentScreen is Screen.Profile,
                    testTag = "nav_profile",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(Screen.Profile) }
                )
            }
        }
    }
}

@Composable
fun NavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = com.example.ui.theme.isAppInDarkTheme()
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant
    val activeBg = if (isSelected) {
        if (isDark) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else Color.Transparent

    val activeBorder = if (isSelected) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    } else null

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .then(
                if (activeBorder != null) Modifier.border(activeBorder.width, activeBorder.brush, RoundedCornerShape(22.dp))
                else Modifier
            )
            .background(activeBg)
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else inactiveColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) activeColor else inactiveColor
            )
        }
    }
}
