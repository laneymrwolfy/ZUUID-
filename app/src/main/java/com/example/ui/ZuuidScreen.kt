package com.example.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UuidFormat
import com.example.model.UuidHelper
import com.example.ui.components.MinecraftManifestSheet
import com.example.ui.theme.DarkBackgroundColors
import com.example.ui.theme.DarkPaletteColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZuuidScreen() {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    // State
    var rawUuid by remember { mutableStateOf(UuidHelper.generateV4()) }
    var currentFormat by remember { mutableStateOf(UuidFormat.STANDARD) }
    var isUppercase by remember { mutableStateOf(false) }
    var paletteIndex by remember { mutableIntStateOf(0) }
    var isCopiedRecently by remember { mutableStateOf(false) }
    var isColorCopiedRecently by remember { mutableStateOf(false) }
    var isMinecraftSheetVisible by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    val historyList = remember { mutableStateListOf<String>() }

    // Rotation state for big refresh FAB
    var rotationTarget by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotationTarget,
        animationSpec = tween(durationMillis = 450),
        label = "fab_rotation"
    )

    val currentPalette: DarkPaletteColor = DarkBackgroundColors[paletteIndex % DarkBackgroundColors.size]

    // Animated background color
    val animatedBgColor by animateColorAsState(
        targetValue = currentPalette.color,
        animationSpec = tween(durationMillis = 600),
        label = "bg_color_anim"
    )

    // Current formatted UUID
    val formattedUuid = remember(rawUuid, currentFormat, isUppercase) {
        UuidHelper.format(rawUuid, currentFormat, isUppercase)
    }

    // Refresh action: generates new UUID and rolls background color
    fun generateNewUuid() {
        rotationTarget += 360f
        if (!historyList.contains(rawUuid)) {
            if (historyList.size >= 20) historyList.removeAt(historyList.lastIndex)
            historyList.add(0, rawUuid)
        }
        rawUuid = UuidHelper.generateV4()

        // Choose a different random color
        var nextIndex: Int
        do {
            nextIndex = (0 until DarkBackgroundColors.size).random()
        } while (nextIndex == paletteIndex && DarkBackgroundColors.size > 1)
        paletteIndex = nextIndex
    }

    fun copyToClipboard(text: String, label: String) {
        clipboardManager.setText(AnnotatedString(text))
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { generateNewUuid() },
                containerColor = currentPalette.accentColor,
                contentColor = Color(0xFF0D1117),
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .testTag("refresh_uuid_fab")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Generate new UUID",
                    modifier = Modifier
                        .size(28.dp)
                        .rotate(animatedRotation)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Generate New UUID",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            animatedBgColor,
                            Color(0xFF07090E)
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top App Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = currentPalette.accentColor,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Z",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 22.sp,
                                        color = Color(0xFF0D1117)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ZUUID",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "v1.0.4 • Offline & No Ads",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF8B949E)
                                )
                            }
                        }

                        // Top Actions
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Minecraft quick tool button
                            FilledTonalButton(
                                onClick = { isMinecraftSheetVisible = true },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF1E3A2F),
                                    contentColor = Color(0xFF81C784)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("open_minecraft_sheet_top_button")
                            ) {
                                Text(
                                    text = "Minecraft",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // History toggle button
                            IconButton(
                                onClick = { showHistory = !showHistory },
                                modifier = Modifier.testTag("toggle_history_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "UUID History",
                                    tint = if (showHistory) currentPalette.accentColor else Color(0xFF8B949E)
                                )
                            }
                        }
                    }
                }

                // Background Color Info Bar (with Copy Option)
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF161B22).copy(alpha = 0.85f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFF30363D)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        copyToClipboard(currentPalette.hex, "Background Color ${currentPalette.hex}")
                                        isColorCopiedRecently = true
                                        scope.launch {
                                            delay(2000)
                                            isColorCopiedRecently = false
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(currentPalette.color)
                                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentPalette.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Color: ${currentPalette.hex} (tap to copy)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = currentPalette.accentColor
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        copyToClipboard(currentPalette.hex, "Background Color ${currentPalette.hex}")
                                        isColorCopiedRecently = true
                                        scope.launch {
                                            delay(2000)
                                            isColorCopiedRecently = false
                                        }
                                    },
                                    modifier = Modifier.size(34.dp).testTag("copy_color_code_button")
                                ) {
                                    Icon(
                                        imageVector = if (isColorCopiedRecently) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copy Color Code",
                                        tint = if (isColorCopiedRecently) Color(0xFF34D399) else currentPalette.accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        paletteIndex = (paletteIndex + 1) % DarkBackgroundColors.size
                                    },
                                    modifier = Modifier.size(34.dp).testTag("shuffle_color_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = "Shuffle background color",
                                        tint = Color(0xFF8B949E),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Format Tabs
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "UUID FORMAT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B949E),
                            letterSpacing = 1.sp
                        )

                        PrimaryScrollableTabRow(
                            selectedTabIndex = currentFormat.ordinal,
                            containerColor = Color(0xFF161B22).copy(alpha = 0.7f),
                            contentColor = currentPalette.accentColor,
                            edgePadding = 0.dp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF30363D), RoundedCornerShape(12.dp))
                        ) {
                            UuidFormat.values().forEach { format ->
                                Tab(
                                    selected = currentFormat == format,
                                    onClick = { currentFormat = format },
                                    text = {
                                        Text(
                                            text = format.label,
                                            fontWeight = if (currentFormat == format) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp,
                                            maxLines = 1
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // Uppercase / Lowercase Options
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LETTER CASING",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B949E),
                            letterSpacing = 1.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = !isUppercase,
                                onClick = { isUppercase = false },
                                label = { Text("lowercase (abc)") },
                                leadingIcon = if (!isUppercase) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = currentPalette.accentColor.copy(alpha = 0.2f),
                                    selectedLabelColor = currentPalette.accentColor
                                )
                            )

                            FilterChip(
                                selected = isUppercase,
                                onClick = { isUppercase = true },
                                label = { Text("UPPERCASE (ABC)") },
                                leadingIcon = if (isUppercase) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = currentPalette.accentColor.copy(alpha = 0.2f),
                                    selectedLabelColor = currentPalette.accentColor
                                )
                            )
                        }
                    }
                }

                // Large, Clear Display of Current UUID in Center
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF161B22).copy(alpha = 0.95f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            currentPalette.accentColor.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                copyToClipboard(formattedUuid, "UUID")
                                isCopiedRecently = true
                                scope.launch {
                                    delay(2000)
                                    isCopiedRecently = false
                                }
                            }
                            .testTag("uuid_display_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Card Top Badge Info
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = currentPalette.accentColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "UUID v4 (RFC 4122)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = currentPalette.accentColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "${formattedUuid.length} chars",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF8B949E),
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Center Large UUID Text
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0D1117).copy(alpha = 0.8f))
                                    .padding(vertical = 18.dp, horizontal = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = formattedUuid,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = if (formattedUuid.length > 34) 18.sp else 21.sp,
                                        lineHeight = 28.sp,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.testTag("uuid_text_display")
                                )
                            }

                            // Action Buttons (Copy & Share)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // One-Tap Copy Button
                                Button(
                                    onClick = {
                                        copyToClipboard(formattedUuid, "UUID")
                                        isCopiedRecently = true
                                        scope.launch {
                                            delay(2000)
                                            isCopiedRecently = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCopiedRecently) Color(0xFF10B981) else currentPalette.accentColor,
                                        contentColor = if (isCopiedRecently) Color.White else Color(0xFF0D1117)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("one_tap_copy_button")
                                ) {
                                    Icon(
                                        imageVector = if (isCopiedRecently) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isCopiedRecently) "Copied!" else "Copy UUID",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }

                                // Share Button
                                OutlinedButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, formattedUuid)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share UUID via"))
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30363D)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("share_uuid_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share UUID",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share", fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                // Extra Minecraft Bedrock manifest.json Helper Card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF112019)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFF2E7D32),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "MC",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 11.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Minecraft Bedrock manifest.json",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFA5D6A7)
                                        )
                                        Text(
                                            text = "Addons & Packs require unique UUIDs",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF81C784).copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            val standardHyphenUuid = remember(rawUuid) {
                                UuidHelper.format(rawUuid, UuidFormat.STANDARD, false)
                            }
                            val manifestField = "\"uuid\": \"$standardHyphenUuid\""

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0A140F))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = manifestField,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = Color(0xFFC8E6C9),
                                    maxLines = 1
                                )
                                IconButton(
                                    onClick = {
                                        copyToClipboard(manifestField, "Minecraft manifest field")
                                    },
                                    modifier = Modifier.size(28.dp).testTag("copy_minecraft_field_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy field",
                                        tint = Color(0xFF81C784),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        copyToClipboard(standardHyphenUuid, "Bedrock UUID")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2E7D32)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).testTag("copy_minecraft_uuid_button")
                                ) {
                                    Text("Copy for manifest.json", fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { isMinecraftSheetVisible = true },
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF81C784)
                                    ),
                                    modifier = Modifier.testTag("open_minecraft_pack_tool_button")
                                ) {
                                    Text("Header & Module Pair", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Recent UUIDs History Section (Animated)
                item {
                    AnimatedVisibility(visible = showHistory) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF161B22).copy(alpha = 0.85f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF30363D)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "RECENT UUIDs IN THIS SESSION",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF8B949E),
                                        letterSpacing = 1.sp
                                    )
                                    if (historyList.isNotEmpty()) {
                                        TextButton(onClick = { historyList.clear() }) {
                                            Text("Clear", color = Color(0xFFEF4444), fontSize = 12.sp)
                                        }
                                    }
                                }

                                if (historyList.isEmpty()) {
                                    Text(
                                        text = "Generated UUIDs will appear here so you never lose them.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF8B949E)
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        historyList.forEachIndexed { index, itemUuid ->
                                            val formattedItem = UuidHelper.format(itemUuid, currentFormat, isUppercase)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF0D1117))
                                                    .clickable {
                                                        copyToClipboard(formattedItem, "History UUID")
                                                    }
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = formattedItem,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFFC9D1D9),
                                                    maxLines = 1,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "Copy history item",
                                                    tint = Color(0xFF8B949E),
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
            }
        }
    }

    // Minecraft Bedrock Manifest Bottom Sheet
    if (isMinecraftSheetVisible) {
        MinecraftManifestSheet(
            initialHeaderUuid = rawUuid,
            onDismiss = { isMinecraftSheetVisible = false }
        )
    }
}
