package com.bendb.markdown

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.w3c.tidy.Tidy
import java.io.StringWriter

class MarkdownPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        TODO("not implemented")
    }
}

class MarkdownTask: DefaultTask() {
    @TaskAction
    fun generateDocs() {

    }
}

fun tidyUp(html: String): String {
    val tidy = Tidy().apply {
        inputEncoding  = "UTF-8"
        outputEncoding = "UTF-8"
        xhtml          = true

        quiet          = true
        showErrors     = 0 // number of errors to emit
        showWarnings   = false
    }

    html.reader().use { reader ->
        StringWriter().use { writer ->
            tidy.parse(reader, writer)
            return writer.toString()
        }
    }
}
