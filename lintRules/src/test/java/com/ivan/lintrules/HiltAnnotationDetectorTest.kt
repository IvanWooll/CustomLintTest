@file:Suppress("UnstableApiUsage") // We know that Lint APIs aren't final.
package com.ivan.lintrules

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.java
import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kt
import org.junit.Test
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint


class HiltAnnotationDetectorTest {
    private val activityStub = java(
        """
package androidx.appcompat.app;
public class MainActivity extends AppCompatActivity {
}
    """,
    ).indented()

    @Test
    fun incorrectClassName() {
        lint()
            .allowCompilationErrors()
            .files(
                activityStub,
                kt(
                    """package foo

                        import androidx.appcompat.app.AppCompatActivity

                        class MainActivity : AppCompatActivity() {
                        private lateinit var viewModel: MainViewModel
}""".trimMargin()
                )
            )
            .issues(HiltAnnotationDetector.ISSUE)
            .run()
            .expect(
                """
        """.trimMargin()
            )
    }
}
