package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.NexoraPreferences
import com.example.data.model.GameSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
    assertEquals("NEXORA Gaming", appName)
  }

  @Test
  fun `verify nexora default games and sessions seeded`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = NexoraPreferences(context)

    val games = prefs.getGames()
    assertTrue("Should have seeded demo games", games.isNotEmpty())
    assertTrue("Should contain Mobile Legends", games.any { it.title.contains("Mobile Legends") })

    val sessions = prefs.getSessions()
    assertTrue("Should have seeded demo sessions", sessions.isNotEmpty())
  }

  @Test
  fun `verify session battery drain calculations`() {
    val session = GameSession(
      id = "test_1",
      gameId = "g1",
      gameTitle = "Test Game",
      startTimestamp = 1000L,
      endTimestamp = 4600L,
      durationSeconds = 3600L, // 1 hour
      startBattery = 100,
      endBattery = 85
    )

    assertEquals(15, session.batteryDrainPercent)
    assertEquals(15.0f, session.drainPerHour, 0.01f)
  }

  @Test
  fun `verify plus demo preference persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = NexoraPreferences(context)

    var p = prefs.getUserPreferences()
    assertFalse(p.isPlusActive)

    p = p.copy(isPlusActive = true, activePlusPlan = "Formule Mensuelle")
    prefs.saveUserPreferences(p)

    val reloaded = prefs.getUserPreferences()
    assertTrue(reloaded.isPlusActive)
    assertEquals("Formule Mensuelle", reloaded.activePlusPlan)
  }
}
