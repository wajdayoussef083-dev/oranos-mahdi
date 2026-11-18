package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.LanguageMode
import com.example.viewmodel.OranosViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

  @Test
  fun testLanguageModeLabels() {
    assertEquals("العربية الفصحى", LanguageMode.ARABIC.title)
    assertEquals("الدارجة المغربية", LanguageMode.DARIJA.title)
  }

  @Test
  fun testOranosViewModelInstantiatesWithoutCrash() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val vm = OranosViewModel(app)
    assertNotNull(vm)
    assertEquals(OranosViewModel.DEFAULT_ENGINEER_OUTPUT, vm.engineerOutput.value)
  }
}
