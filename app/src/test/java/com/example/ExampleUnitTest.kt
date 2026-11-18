package com.example

import com.example.data.local.AlternativeBillingManager
import com.example.data.network.GeminiApiClient
import com.example.domain.model.AgentType
import com.example.domain.model.EngineProfile
import com.example.domain.model.LanguageMode
import com.example.domain.model.NavigationTab
import com.example.domain.model.ProfessionalMode
import com.example.domain.model.TaskType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun verify_multi_agent_architecture_completeness() {
    val agents = AgentType.entries
    assertEquals(12, agents.size)
    assertNotNull(agents.find { it == AgentType.COMMANDER })
    assertNotNull(agents.find { it == AgentType.RESEARCH })
    assertNotNull(agents.find { it == AgentType.BUSINESS })
    assertNotNull(agents.find { it == AgentType.DIGITAL_INCOME })
    assertNotNull(agents.find { it == AgentType.CONTENT_STUDIO })
    assertNotNull(agents.find { it == AgentType.MARKETING })
    assertNotNull(agents.find { it == AgentType.PRODUCT_INTEL })
    assertNotNull(agents.find { it == AgentType.TRANSLATION })
    assertNotNull(agents.find { it == AgentType.DEVELOPER })
    assertNotNull(agents.find { it == AgentType.FINANCIAL_ANALYST })
    assertNotNull(agents.find { it == AgentType.MEMORY })
    assertNotNull(agents.find { it == AgentType.QUALITY_CONTROL })
  }

  @Test
  fun verify_professional_modes_and_auto_detection() {
    val modes = ProfessionalMode.entries
    assertEquals(6, modes.size)
    assertEquals(ProfessionalMode.DEVELOPER, GeminiApiClient.detectModeFromPrompt("Fix this Kotlin API bug"))
    assertEquals(ProfessionalMode.CREATIVE, GeminiApiClient.detectModeFromPrompt("عطيني ستوري بورد لفيديو ريلز"))
    assertEquals(ProfessionalMode.DIGITAL_INCOME, GeminiApiClient.detectModeFromPrompt("كيفاش نربح من الفريلانس بالدولار؟"))
    assertEquals(ProfessionalMode.BUSINESS, GeminiApiClient.detectModeFromPrompt("حساب هامش الربح وخطة عمل للمشروع"))
    assertEquals(ProfessionalMode.RESEARCH, GeminiApiClient.detectModeFromPrompt("تحليل السوق والمنافسين"))
  }

  @Test
  fun verify_auto_agent_detection_and_alternative_billing_guards() {
    assertEquals(AgentType.CONTENT_STUDIO, GeminiApiClient.detectAgentFromPrompt("اكتب لي سيناريو فيديو ريلز احترافي"))
    assertEquals(AgentType.DIGITAL_INCOME, GeminiApiClient.detectAgentFromPrompt("أبحث عن فرص عمل بالدولار على Upwork"))
    assertEquals(AgentType.DEVELOPER, GeminiApiClient.detectAgentFromPrompt("أحتاج كود Kotlin للتكامل مع API"))
    assertEquals(AgentType.TRANSLATION, GeminiApiClient.detectAgentFromPrompt("ترجم هذه الرسالة إلى الفرنسية"))
    assertEquals(AgentType.COMMANDER, GeminiApiClient.detectAgentFromPrompt("مرحباً أورانوس، ما هي خطتنا لليوم؟"))

    // Placeholder checkout URL & Payoneer target must never report configured
    assertFalse(AlternativeBillingManager.isValidHttpUrl("YOUR_GLOBAL_CHECKOUT_URL"))
    assertFalse(AlternativeBillingManager.isValidHttpUrl("javascript:alert(1)"))
    assertTrue(AlternativeBillingManager.isValidHttpUrl("https://checkout.example.com/oranos-pro"))
    assertFalse(AlternativeBillingManager.isConfiguredPayoneerTarget("YOUR_PAYONEER_TARGET"))
    assertTrue(AlternativeBillingManager.isConfiguredPayoneerTarget("payments@oranos.example"))
  }

  @Test
  fun verify_multi_engine_routing_and_security_sanitization() {
    assertEquals(
      EngineProfile.PRECISION_CODING,
      GeminiApiClient.selectEngineProfile(ProfessionalMode.DEVELOPER, AgentType.DEVELOPER)
    )
    assertEquals(
      EngineProfile.DEEP_RESEARCH,
      GeminiApiClient.selectEngineProfile(ProfessionalMode.RESEARCH, AgentType.RESEARCH)
    )
    assertEquals(
      EngineProfile.CREATIVE_STUDIO,
      GeminiApiClient.selectEngineProfile(ProfessionalMode.CREATIVE, AgentType.CONTENT_STUDIO)
    )
    assertEquals(
      EngineProfile.STRATEGIC_PLANNER,
      GeminiApiClient.selectEngineProfile(ProfessionalMode.BUSINESS, AgentType.COMMANDER)
    )

    val leaky = "Error connecting with AIzaSyTestFakeKey1234567890 and Bearer secret_token_xyz"
    val clean = GeminiApiClient.sanitizeSensitiveText(leaky)
    assertFalse(clean.contains("AIzaSyTestFakeKey1234567890"))
    assertFalse(clean.contains("secret_token_xyz"))
    assertTrue(clean.contains("[REDACTED_API_KEY]"))
    assertTrue(clean.contains("Bearer [REDACTED_TOKEN]"))
  }

  @Test
  fun verify_tasks_suite_completeness() {
    val tasks = TaskType.entries
    assertEquals(11, tasks.size)
    assertNotNull(tasks.find { it == TaskType.DOLLAR_JOBS })
    assertNotNull(tasks.find { it == TaskType.VIDEO_CONTENT })
    assertNotNull(tasks.find { it == TaskType.AFFILIATE })
    assertNotNull(tasks.find { it == TaskType.WINNING_PRODUCTS })
    assertNotNull(tasks.find { it == TaskType.MARKET_ANALYSIS })
    assertNotNull(tasks.find { it == TaskType.TRANSLATION })
    assertNotNull(tasks.find { it == TaskType.CAMPAIGNS })
    assertNotNull(tasks.find { it == TaskType.PROFIT_TRACKING })
    assertNotNull(tasks.find { it == TaskType.DEV_LAB })
    assertNotNull(tasks.find { it == TaskType.PROBLEM_SOLVER })
    assertNotNull(tasks.find { it == TaskType.IDEA_MATRIX })
  }

  @Test
  fun verify_navigation_tabs() {
    val tabs = NavigationTab.entries
    assertEquals(4, tabs.size)
    assertEquals(NavigationTab.HOME, tabs[0])
    assertEquals(NavigationTab.TASKS, tabs[1])
    assertEquals(NavigationTab.FAVORITES, tabs[2])
    assertEquals(NavigationTab.CHATS, tabs[3])
  }

  @Test
  fun verify_language_modes() {
    val modes = LanguageMode.entries
    assertTrue(modes.any { it == LanguageMode.ARABIC })
    assertTrue(modes.any { it == LanguageMode.DARIJA })
    assertTrue(modes.any { it == LanguageMode.HYBRID })
    assertTrue(modes.any { it == LanguageMode.ENGLISH })
    assertTrue(modes.any { it == LanguageMode.FRENCH })
    assertTrue(modes.any { it == LanguageMode.AUTO })
  }
}
