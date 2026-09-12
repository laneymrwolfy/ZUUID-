package com.example.model

import java.util.UUID

enum class UuidFormat(val label: String, val shortDesc: String) {
    STANDARD("Standard", "Standard hyphenated 8-4-4-4-12 format"),
    DIGITS_ONLY("Digits Only", "Compact 32 characters without hyphens"),
    BRACES("Braces {}", "Hyphenated enclosed in curly braces"),
    PARENTHESES("Parentheses ()", "Hyphenated enclosed in round parentheses")
}

object UuidHelper {

    fun generateV4(): String {
        return UUID.randomUUID().toString()
    }

    fun format(rawUuid: String, format: UuidFormat, isUppercase: Boolean): String {
        val clean = rawUuid.replace("-", "").lowercase()
        if (clean.length != 32) return rawUuid

        val standard = buildString {
            append(clean.substring(0, 8))
            append("-")
            append(clean.substring(8, 12))
            append("-")
            append(clean.substring(12, 16))
            append("-")
            append(clean.substring(16, 20))
            append("-")
            append(clean.substring(20, 32))
        }

        val result = when (format) {
            UuidFormat.STANDARD -> standard
            UuidFormat.DIGITS_ONLY -> clean
            UuidFormat.BRACES -> "{$standard}"
            UuidFormat.PARENTHESES -> "($standard)"
        }

        return if (isUppercase) result.uppercase() else result.lowercase()
    }

    /**
     * Formats a Bedrock manifest.json file ready for Minecraft behavior or resource packs.
     */
    fun createMinecraftManifestJson(
        headerUuid: String,
        moduleUuid: String,
        isBehaviorPack: Boolean,
        packName: String = "ZUUID Pack",
        description: String = "Created with ZUUID"
    ): String {
        val cleanHeader = format(headerUuid, UuidFormat.STANDARD, false)
        val cleanModule = format(moduleUuid, UuidFormat.STANDARD, false)
        val moduleType = if (isBehaviorPack) "data" else "resources"

        return """
{
  "format_version": 2,
  "header": {
    "name": "$packName",
    "description": "$description",
    "uuid": "$cleanHeader",
    "version": [1, 0, 0],
    "min_engine_version": [1, 20, 0]
  },
  "modules": [
    {
      "type": "$moduleType",
      "uuid": "$cleanModule",
      "version": [1, 0, 0]
    }
  ]
}
""".trimIndent()
    }

    /**
     * Header & module snippet for Minecraft Bedrock manifest.json
     */
    fun createMinecraftManifestSnippet(headerUuid: String, moduleUuid: String): String {
        val cleanHeader = format(headerUuid, UuidFormat.STANDARD, false)
        val cleanModule = format(moduleUuid, UuidFormat.STANDARD, false)
        return """
"header": {
  "uuid": "$cleanHeader"
},
"modules": [
  {
    "uuid": "$cleanModule"
  }
]
""".trimIndent()
    }
}
