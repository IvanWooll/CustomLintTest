@file:Suppress("UnstableApiUsage")

package com.ivan.lintrules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import java.util.*

class HiltAnnotationDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes() = listOf<Class<out UElement>>(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = AppCompatUsageHandler(context)


    companion object {
        private const val TYPE_ACTIVITY = "android.app.Activity"
        private const val TYPE_FRAGMENT = "androidx.fragment.app.Fragment"
        private const val HILT_ENTRY_POINT_ANNOTATION = "dagger.hilt.android.AndroidEntryPoint"


        private val IMPLEMENTATION = Implementation(
            HiltAnnotationDetector::class.java, EnumSet.of(Scope.JAVA_FILE)
        )

        val ISSUE = Issue.create(
            id = "HiltAnnotationDetector",
            briefDescription = "Detects missing hilt annotations",
            explanation = "explanation",
            priority = 10,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )

    }

    class AppCompatUsageHandler(private val context: JavaContext) : UElementHandler() {

        override fun visitClass(node: UClass) {
            // is this a class that extends Activity or Fragment
            val isApplicableClassType = isApplicableClassType(node)
            if (isApplicableClassType) {
                // does this class have a variable with 'ViewModel' as part of its name
                val hasViewModel = classHasViewModel(node)
                if (hasViewModel) {
                    // check class has the '@AndroidEntryPoint' annotation
                    if (classIsMissingAnnotation(node)) {
                        reportIssue(node)
                    }
                }
            }
        }

        private fun isApplicableClassType(node: UClass): Boolean {
            val evaluator = context.evaluator
            val isActivity = evaluator.extendsClass(node, TYPE_ACTIVITY)
            val isFragment = evaluator.extendsClass(node, TYPE_FRAGMENT)
            return isActivity || isFragment
        }

        private fun classHasViewModel(node: UClass) = node
            .fields
            .any { uField -> uField.type.canonicalText.contains("ViewModel") }

        private fun classIsMissingAnnotation(node: UClass): Boolean {
            val evaluator = context.evaluator
            return evaluator
                .getAllAnnotations(node.javaPsi, false)
                .none { psiAnnotation ->
                    psiAnnotation.hasQualifiedName(HILT_ENTRY_POINT_ANNOTATION)
                }
        }


        private fun reportIssue(node: UClass) {
            context.report(
                ISSUE,
                node,
                context.getNameLocation(node),
                "Activity or Fragment which has a ViewModel should be annotated with '@AndroidEntryPoint'"
            )
        }

    }
}

