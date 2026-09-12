package com.example

import com.example.model.UuidFormat
import com.example.model.UuidHelper
import org.junit.Assert.*
import org.junit.Test

class UuidHelperTest {

    private val sampleUuid = "c9a646d3-9c61-4cd7-bf16-44e22bc613ff"

    @Test
    fun testGenerateV4ValidFormat() {
        val uuid = UuidHelper.generateV4()
        assertNotNull(uuid)
        assertEquals(36, uuid.length)
        val regex = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
        assertTrue("UUID should be valid v4 format: $uuid", regex.matches(uuid.lowercase()))
    }

    @Test
    fun testStandardFormatLowercase() {
        val formatted = UuidHelper.format(sampleUuid, UuidFormat.STANDARD, false)
        assertEquals("c9a646d3-9c61-4cd7-bf16-44e22bc613ff", formatted)
    }

    @Test
    fun testStandardFormatUppercase() {
        val formatted = UuidHelper.format(sampleUuid, UuidFormat.STANDARD, true)
        assertEquals("C9A646D3-9C61-4CD7-BF16-44E22BC613FF", formatted)
    }

    @Test
    fun testDigitsOnlyFormatLowercase() {
        val formatted = UuidHelper.format(sampleUuid, UuidFormat.DIGITS_ONLY, false)
        assertEquals("c9a646d39c614cd7bf1644e22bc613ff", formatted)
        assertEquals(32, formatted.length)
    }

    @Test
    fun testDigitsOnlyFormatUppercase() {
        val formatted = UuidHelper.format(sampleUuid, UuidFormat.DIGITS_ONLY, true)
        assertEquals("C9A646D39C614CD7BF1644E22BC613FF", formatted)
        assertEquals(32, formatted.length)
    }

    @Test
    fun testBracesFormat() {
        val lower = UuidHelper.format(sampleUuid, UuidFormat.BRACES, false)
        assertEquals("{c9a646d3-9c61-4cd7-bf16-44e22bc613ff}", lower)

        val upper = UuidHelper.format(sampleUuid, UuidFormat.BRACES, true)
        assertEquals("{C9A646D3-9C61-4CD7-BF16-44E22BC613FF}", upper)
    }

    @Test
    fun testParenthesesFormat() {
        val lower = UuidHelper.format(sampleUuid, UuidFormat.PARENTHESES, false)
        assertEquals("(c9a646d3-9c61-4cd7-bf16-44e22bc613ff)", lower)

        val upper = UuidHelper.format(sampleUuid, UuidFormat.PARENTHESES, true)
        assertEquals("(C9A646D3-9C61-4CD7-BF16-44E22BC613FF)", upper)
    }

    @Test
    fun testMinecraftManifestGeneration() {
        val header = "11111111-2222-3333-4444-555555555555"
        val module = "66666666-7777-8888-9999-000000000000"
        val manifest = UuidHelper.createMinecraftManifestJson(header, module, isBehaviorPack = true)

        assertTrue(manifest.contains("\"uuid\": \"11111111-2222-3333-4444-555555555555\""))
        assertTrue(manifest.contains("\"uuid\": \"66666666-7777-8888-9999-000000000000\""))
        assertTrue(manifest.contains("\"type\": \"data\""))
        assertTrue(manifest.contains("\"format_version\": 2"))
    }
}
