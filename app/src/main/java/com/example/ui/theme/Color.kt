package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val CyanPrimary = Color(0xFF38BDF8)
val CyanOnPrimary = Color(0xFF003547)
val CyanPrimaryContainer = Color(0xFF004D65)
val CyanOnPrimaryContainer = Color(0xFFC2E8FF)

val PurpleSecondary = Color(0xFFA78BFA)
val PurpleOnSecondary = Color(0xFF2E1260)
val PurpleSecondaryContainer = Color(0xFF452B77)
val PurpleOnSecondaryContainer = Color(0xFFEADBFF)

val EmeraldTertiary = Color(0xFF34D399)
val EmeraldOnTertiary = Color(0xFF003823)

val DarkBackgroundDefault = Color(0xFF0D1117)
val DarkSurfaceDefault = Color(0xFF161B22)
val DarkSurfaceVariant = Color(0xFF21262D)
val DarkOutline = Color(0xFF30363D)
val DarkOutlineVariant = Color(0xFF21262D)

data class DarkPaletteColor(
    val name: String,
    val hex: String,
    val color: Color,
    val accentColor: Color
)

val DarkBackgroundColors = listOf(
    DarkPaletteColor("Obsidian Abyss", "#0D1117", Color(0xFF0D1117), Color(0xFF38BDF8)),
    DarkPaletteColor("Midnight Slate", "#0F172A", Color(0xFF0F172A), Color(0xFF60A5FA)),
    DarkPaletteColor("Deep Violet", "#170D29", Color(0xFF170D29), Color(0xFFA78BFA)),
    DarkPaletteColor("Eclipse Navy", "#0A192F", Color(0xFF0A192F), Color(0xFF38BDF8)),
    DarkPaletteColor("Dark Emerald", "#071F17", Color(0xFF071F17), Color(0xFF34D399)),
    DarkPaletteColor("Cyber Teal", "#072026", Color(0xFF072026), Color(0xFF2DD4BF)),
    DarkPaletteColor("Dark Wine", "#230E1F", Color(0xFF230E1F), Color(0xFFF472B6)),
    DarkPaletteColor("Royal Abyss", "#13142F", Color(0xFF13142F), Color(0xFF818CF8)),
    DarkPaletteColor("Dark Carbon", "#14171A", Color(0xFF14171A), Color(0xFF94A3B8)),
    DarkPaletteColor("Deep Cocoa", "#1F1412", Color(0xFF1F1412), Color(0xFFFB923C)),
    DarkPaletteColor("Plum Void", "#200E26", Color(0xFF200E26), Color(0xFFE879F9)),
    DarkPaletteColor("Deep Pine", "#0B1D19", Color(0xFF0B1D19), Color(0xFF4ADE80))
)
