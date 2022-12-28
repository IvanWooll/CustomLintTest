package com.ivan.lintrules

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

class LintIssueRegistry : IssueRegistry() {

    override val api: Int = CURRENT_API

    override val issues: List<Issue> = listOf(HiltAnnotationDetector.ISSUE )

    override val minApi: Int = 13
}