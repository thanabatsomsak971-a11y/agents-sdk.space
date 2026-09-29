package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.JsonViewerCard
import com.example.ui.theme.AgentCyan
import com.example.ui.theme.CodeBlockBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DeepDarkBackground
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.FirebaseAmber
import com.example.ui.theme.FirebaseYellow
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.ConsoleTab
import com.example.ui.viewmodel.FirebaseConsoleViewModel

@Composable
fun FirebaseConsoleScreen(
    viewModel: FirebaseConsoleViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val collections by viewModel.collections.collectAsState()
    val selectedCollection by viewModel.selectedCollection.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val authUsers by viewModel.authUsers.collectAsState()
    val cloudFunctions by viewModel.cloudFunctions.collectAsState()
    val securityRules by viewModel.securityRules.collectAsState()
    val functionLogs by viewModel.functionInvocationLogs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddDocDialog by remember { mutableStateOf(false) }
    var showAddUserDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepDarkBackground)
    ) {
        // --- Firebase Header Banner ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FirebaseAmber.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "FEAR BEST CONSOLE",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SuccessGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ONLINE",
                                color = SuccessGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Project: agents-sdk-space • Region: asia-east1",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sync Active",
                    tint = SuccessGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sync",
                    color = SuccessGreen,
                    fontSize = 11.sp
                )
            }
        }

        // --- Tabs Row ---
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = DarkSurfaceElevated,
            contentColor = FirebaseAmber,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    color = FirebaseAmber
                )
            }
        ) {
            ConsoleTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { viewModel.selectTab(tab) },
                    text = {
                        Text(
                            text = when (tab) {
                                ConsoleTab.FIRESTORE -> "Firestore"
                                ConsoleTab.AUTH -> "Auth"
                                ConsoleTab.FUNCTIONS -> "Functions"
                                ConsoleTab.RULES -> "Rules"
                                ConsoleTab.PROJECT_INFO -> "Project"
                            },
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("console_tab_${tab.name}")
                )
            }
        }

        // --- Tab Contents ---
        when (selectedTab) {
            ConsoleTab.FIRESTORE -> {
                FirestoreExplorerTab(
                    collections = collections,
                    selectedCollection = selectedCollection,
                    documents = documents,
                    searchQuery = searchQuery,
                    onSelectCollection = { viewModel.selectCollection(it) },
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onDeleteDoc = { viewModel.deleteDocument(it) },
                    onAddDocClick = { showAddDocDialog = true }
                )
            }

            ConsoleTab.AUTH -> {
                val currentFirebaseUser by viewModel.currentFirebaseUser.collectAsState()
                AuthUsersTab(
                    users = authUsers,
                    currentFirebaseUser = currentFirebaseUser,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onSignInWithGoogle = {
                        (context as? Activity)?.let { act ->
                            viewModel.signInWithGoogle(
                                activity = act,
                                onSuccess = { Toast.makeText(context, "Signed in with Google successfully!", Toast.LENGTH_SHORT).show() },
                                onError = { Toast.makeText(context, "Sign in failed: $it", Toast.LENGTH_LONG).show() }
                            )
                        }
                    },
                    onSignOut = {
                        viewModel.signOut {
                            Toast.makeText(context, "Signed out of Firebase", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onToggleUser = { uid, current -> viewModel.toggleUserStatus(uid, current) },
                    onDeleteUser = { viewModel.deleteUser(it) },
                    onAddUserClick = { showAddUserDialog = true }
                )
            }

            ConsoleTab.FUNCTIONS -> {
                CloudFunctionsTab(
                    functions = cloudFunctions,
                    logs = functionLogs,
                    onTriggerTest = {
                        viewModel.triggerFunctionTest(it)
                        Toast.makeText(context, "Trigger dispatched to $it", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            ConsoleTab.RULES -> {
                SecurityRulesTab(
                    rules = securityRules,
                    onSaveRules = {
                        viewModel.updateSecurityRules(it)
                        Toast.makeText(context, "Rules compiled & published to asia-east1", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            ConsoleTab.PROJECT_INFO -> {
                ProjectOverviewTab()
            }
        }
    }

    // --- Dialog: Add Firestore Document ---
    if (showAddDocDialog) {
        var docIdInput by remember { mutableStateOf("") }
        var jsonInput by remember {
            mutableStateOf(
                """
                {
                  "name": "Custom Agent Item",
                  "enabled": true,
                  "createdAt": "${System.currentTimeMillis()}"
                }
                """.trimIndent()
            )
        }

        AlertDialog(
            onDismissRequest = { showAddDocDialog = false },
            title = {
                Text("Add Document to '$selectedCollection'", color = Color.White)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = docIdInput,
                        onValueChange = { docIdInput = it },
                        label = { Text("Document ID (Auto if blank)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = { jsonInput = it },
                        label = { Text("JSON Data Payload") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        maxLines = 8,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addDocument(selectedCollection, docIdInput, jsonInput)
                        showAddDocDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber)
                ) {
                    Text("Create Document", color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDocDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = DarkSurface
        )
    }

    // --- Dialog: Add Auth User ---
    if (showAddUserDialog) {
        var emailInput by remember { mutableStateOf("") }
        var nameInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddUserDialog = false },
            title = { Text("Add Firebase User", color = Color.White) },
            text = {
                Column {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (emailInput.isNotBlank()) {
                            viewModel.addAuthUser(emailInput, nameInput, "password")
                            showAddUserDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber)
                ) {
                    Text("Create User", color = Color.Black)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddUserDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun FirestoreExplorerTab(
    collections: List<String>,
    selectedCollection: String,
    documents: List<com.example.data.model.FirestoreDocItem>,
    searchQuery: String,
    onSelectCollection: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onDeleteDoc: (String) -> Unit,
    onAddDocClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Collection Pills
        Text(
            text = "COLLECTIONS",
            color = AgentCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(collections) { coll ->
                FilterChip(
                    selected = selectedCollection.equals(coll, ignoreCase = true),
                    onClick = { onSelectCollection(coll) },
                    label = { Text(coll) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FirebaseAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = DarkSurfaceElevated,
                        labelColor = Color.White
                    ),
                    modifier = Modifier.testTag("collection_chip_$coll")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar & Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                placeholder = { Text("Search document ID or content...", fontSize = 12.sp, color = Color.Gray) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CodeBlockBackground,
                    unfocusedContainerColor = CodeBlockBackground,
                    focusedBorderColor = AgentCyan,
                    unfocusedBorderColor = DarkSurfaceBorder
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAddDocClick,
                colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(50.dp)
                    .testTag("add_document_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Document",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Doc", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Documents List
        if (documents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No documents found in collection '$selectedCollection'",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(documents, key = { it.docId }) { doc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = FirebaseAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = doc.docId,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteDoc(doc.docId) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = ErrorRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            JsonViewerCard(
                                title = "Payload",
                                jsonString = doc.dataJson,
                                headerColor = FirebaseAmber
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthUsersTab(
    users: List<com.example.data.model.AuthUserItem>,
    currentFirebaseUser: com.google.firebase.auth.FirebaseUser?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSignInWithGoogle: () -> Unit,
    onSignOut: () -> Unit,
    onToggleUser: (String, Boolean) -> Unit,
    onDeleteUser: (String) -> Unit,
    onAddUserClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // --- Google Sign-In Banner / Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgentCyan.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (currentFirebaseUser != null) "Signed In (Google Auth)" else "Google Identity Authentication",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentFirebaseUser?.email ?: "Sign in with Google using Jetpack Credential Manager",
                        color = if (currentFirebaseUser != null) SuccessGreen else Color.LightGray,
                        fontSize = 11.sp
                    )
                }

                if (currentFirebaseUser == null) {
                    Button(
                        onClick = onSignInWithGoogle,
                        colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("google_sign_in_btn")
                    ) {
                        Text("Sign in with Google", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onSignOut,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("sign_out_btn")
                    ) {
                        Text("Sign Out", fontSize = 12.sp, color = FirebaseAmber)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                placeholder = { Text("Filter users by email/name...", fontSize = 12.sp, color = Color.Gray) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CodeBlockBackground,
                    unfocusedContainerColor = CodeBlockBackground,
                    focusedBorderColor = AgentCyan,
                    unfocusedBorderColor = DarkSurfaceBorder
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onAddUserClick,
                colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(50.dp)
                    .testTag("add_user_btn")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add User", tint = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add User", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users, key = { it.uid }) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.displayName,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (user.isEnabled) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (user.isEnabled) "ACTIVE" else "DISABLED",
                                        color = if (user.isEnabled) SuccessGreen else ErrorRed,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = user.email,
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )

                            Text(
                                text = "UID: ${user.uid} • Provider: ${user.provider}",
                                color = Color.Gray,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { onToggleUser(user.uid, user.isEnabled) },
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = if (user.isEnabled) "Disable" else "Enable",
                                    fontSize = 11.sp,
                                    color = if (user.isEnabled) FirebaseAmber else SuccessGreen
                                )
                            }

                            IconButton(
                                onClick = { onDeleteUser(user.uid) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = ErrorRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CloudFunctionsTab(
    functions: List<com.example.data.model.CloudFunctionItem>,
    logs: List<String>,
    onTriggerTest: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "DEPLOYED AGENT FUNCTIONS & TRIGGERS",
            color = AgentCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(0.55f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(functions) { fn ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = fn.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Trigger: ${fn.triggerType}",
                                color = FirebaseYellow,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${fn.invocations} calls • Latency: ${fn.avgLatencyMs}ms • ${fn.region}",
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = { onTriggerTest(fn.name) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Test",
                                tint = FirebaseAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Run", color = FirebaseAmber, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "LIVE EXECUTION LOG STREAM",
            color = SuccessGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.45f)
                .clip(RoundedCornerShape(8.dp))
                .background(CodeBlockBackground)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            LazyColumn {
                items(logs) { log ->
                    Text(
                        text = log,
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityRulesTab(
    rules: String,
    onSaveRules: (String) -> Unit
) {
    var editedRules by remember { mutableStateOf(rules) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "FIRESTORE.RULES AUDITOR",
                color = AgentCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Button(
                onClick = { onSaveRules(editedRules) },
                colors = ButtonDefaults.buttonColors(containerColor = FirebaseAmber),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text("Publish Rules", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = editedRules,
            onValueChange = { editedRules = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFFF1F5F9),
                unfocusedTextColor = Color(0xFFF1F5F9),
                focusedContainerColor = CodeBlockBackground,
                unfocusedContainerColor = CodeBlockBackground,
                focusedBorderColor = AgentCyan,
                unfocusedBorderColor = DarkSurfaceBorder
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        )
    }
}

@Composable
private fun ProjectOverviewTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Project Configuration",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProjectInfoRow(label = "Project ID", value = "gen-lang-client-0006081932")
                ProjectInfoRow(label = "Database ID", value = "ai-studio-android-agentsdk-4d2c2fe5-beef-4c2e-8d6f-8908f73bdc3f")
                ProjectInfoRow(label = "Region", value = "us-west1")
                ProjectInfoRow(label = "App Check", value = "Attested (Debug Provider OK)")
                ProjectInfoRow(label = "Auth Provider", value = "Google Sign-In (Credential Manager)")
                ProjectInfoRow(label = "Admin Owner", value = "thanabatsomsak971@gmail.com")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "GitHub & Deployment Spec",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProjectInfoRow(label = "Repository", value = "thanabartbb/agents-sdk.space")
                ProjectInfoRow(label = "Live URL", value = "https://ais-dev-e5or6cqxg5gdxqdjlfivyj-834871928505.asia-east1.run.app")
                ProjectInfoRow(label = "Live Model", value = "models/gemini-3.1-flash-live-preview")
                ProjectInfoRow(label = "Voice Engine", value = "Zephyr (PrebuiltVoiceConfig)")
            }
        }
    }
}

@Composable
private fun ProjectInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        Text(
            text = value,
            color = Color(0xFFCBD5E1),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
