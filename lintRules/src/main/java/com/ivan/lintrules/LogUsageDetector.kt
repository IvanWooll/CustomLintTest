package com.ivan.lintrules

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

class LogUsageDetector : Detector(), SourceCodeScanner {

    override fun getApplicableMethodNames(): List<String> =
        listOf("tag", "format", "v", "d", "i", "w", "e", "wtf")


    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        super.visitMethodCall(context, node, method)
        val evaluator = context.evaluator
        if (evaluator.isMemberInClass(method, "android.util.Log")) {
            reportUsage(context, node)
        }
    }


    private fun reportUsage(context: JavaContext, node: UCallExpression) {
        context.report(
            issue = ISSUE,
            scope = node,
            location = context.getCallLocation(
                call = node,
                includeReceiver = true,
                includeArguments = true
            ),
            message = "android.util.Log usage is forbidden."
        )
    }


    companion object {
        private val IMPLEMENTATION = Implementation(
            LogUsageDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )
        val ISSUE = Issue.create(
            id = "LogUsageDetector",
            briefDescription = "Detects illegal log class usage",
            explanation = "",
            category = Category.CUSTOM_LINT_CHECKS,
            priority = 10,
            severity = Severity.ERROR,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )
    }
}