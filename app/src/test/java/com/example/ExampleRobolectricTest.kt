package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Agent SDK Space", appName)
  }

  @Test
  fun `verify agent config defaults`() {
    val config = com.example.data.model.AgentSdkConfig()
    assertEquals("Zephyr", config.voiceName)
    assertEquals("gemini-3.5-flash", config.modelName)
    assertEquals("104857", config.triggerTokens)
    assertEquals("52428", config.slidingWindowTokens)
    assertTrue(config.isGoogleSearchEnabled)
  }
}
