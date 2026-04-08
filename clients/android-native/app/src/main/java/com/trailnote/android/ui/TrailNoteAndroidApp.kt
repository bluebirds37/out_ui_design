package com.trailnote.android.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import com.trailnote.android.core.navigation.TrailNoteTab
import com.trailnote.android.data.service.TrailServiceRegistry
import com.trailnote.android.designsystem.theme.Mist100
import com.trailnote.android.designsystem.theme.Pine700
import com.trailnote.android.features.auth.LoginScreen
import com.trailnote.android.features.detail.AuthorProfileScreen
import com.trailnote.android.features.detail.RouteDetailScreen
import com.trailnote.android.features.discover.DiscoverScreen
import com.trailnote.android.features.mine.MineScreen
import com.trailnote.android.features.record.RecordScreen
import com.trailnote.android.features.search.SearchScreen
import com.trailnote.android.session.TrailSessionStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailNoteAndroidApp() {
    val context = LocalContext.current
    val currentToken = TrailSessionStore.currentToken
    var selectedTab by remember { mutableStateOf(TrailNoteTab.DISCOVER) }
    var selectedRouteId by remember { mutableStateOf<Long?>(null) }
    var selectedAuthorId by remember { mutableStateOf<Long?>(null) }
    var isLoggingIn by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf("") }
    val isAuthenticated = !currentToken.isNullOrBlank()

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            selectedRouteId = null
            selectedAuthorId = null
            selectedTab = TrailNoteTab.DISCOVER
        }
    }

    if (!isAuthenticated) {
        LoginScreen(
            isSubmitting = isLoggingIn,
            errorMessage = loginError,
            onSubmit = { account, password ->
                isLoggingIn = true
                loginError = ""
                CoroutineScope(Dispatchers.Main).launch {
                    runCatching {
                        withContext(Dispatchers.IO) {
                            TrailServiceRegistry.authService.login(account, password)
                        }
                    }.onSuccess { session ->
                        TrailSessionStore.save(context.applicationContext, session.accessToken)
                    }.onFailure { error ->
                        loginError = error.message ?: "登录失败"
                    }
                    isLoggingIn = false
                }
            },
        )
        return
    }

    Scaffold(
        topBar = {
            if (selectedRouteId != null || selectedAuthorId != null) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(if (selectedAuthorId != null) "作者主页" else "路线详情")
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (selectedAuthorId != null) {
                                    selectedAuthorId = null
                                } else {
                                    selectedRouteId = null
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Mist100,
                        navigationIconContentColor = Pine700,
                        titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                    ),
                )
            }
        },
        bottomBar = {
            if (selectedRouteId == null && selectedAuthorId == null) {
                NavigationBar(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    contentColor = Pine700,
                ) {
                    TrailNoteTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = {
                                Icon(imageVector = tab.icon, contentDescription = tab.label)
                            },
                            label = { Text(tab.label) },
                            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                selectedIconColor = Mist100,
                                selectedTextColor = Pine700,
                                indicatorColor = Pine700,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
        ) {
            when {
                selectedAuthorId != null -> AuthorProfileScreen(
                    authorId = selectedAuthorId!!,
                    onOpenRoute = { routeId ->
                        selectedAuthorId = null
                        selectedRouteId = routeId
                    },
                )

                selectedRouteId != null -> RouteDetailScreen(
                    routeId = selectedRouteId!!,
                    onOpenAuthor = { authorId -> selectedAuthorId = authorId },
                )

                else -> when (selectedTab) {
                    TrailNoteTab.DISCOVER -> DiscoverScreen(onOpenRoute = { selectedRouteId = it })
                    TrailNoteTab.SEARCH -> SearchScreen(onOpenRoute = { selectedRouteId = it })
                    TrailNoteTab.RECORD -> RecordScreen()
                    TrailNoteTab.MINE -> MineScreen(
                        onOpenRoute = { selectedRouteId = it },
                        onLogout = {
                            TrailSessionStore.clear(context.applicationContext)
                            selectedRouteId = null
                            selectedAuthorId = null
                            selectedTab = TrailNoteTab.DISCOVER
                            loginError = ""
                        },
                    )
                }
            }
        }
    }
}
