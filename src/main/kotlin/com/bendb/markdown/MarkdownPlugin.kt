package com.bendb.markdown

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.TaskAction
import org.w3c.tidy.Tidy
import java.io.File
import java.io.StringWriter
import javax.inject.Inject

class MarkdownPlugin @Inject constructor(
        fileResolver: FileResolver
): Plugin<Project> {
    internal val sourceSetProvider: MarkdownSourceSetProvider

    init {
        sourceSetProvider = MarkdownSourceSetProviderImpl(fileResolver)
    }

    override fun apply(project: Project) {
        // We need the Java Plugin to get source sets.
        // Folks are probably using it anyways.
        project.pluginManager.apply<JavaPlugin>()

        project.convention.getPlugin<JavaPluginConvention>().sourceSets.forEach { sourceSet ->
            val markdownSourceSet = sourceSetProvider.createSourceSet((sourceSet as DefaultSourceSet).displayName)
            val srcDir = "src/${sourceSet.name}/markdown"
            markdownSourceSet.markdown.srcDir(srcDir)
            sourceSet.allSource.source(markdownSourceSet.markdown)

            val taskName = sourceSet.getTaskName("generate", "MarkdownDocs")
            project.tasks.create(taskName, MarkdownTask::class.java).apply {
                setSource(markdownSourceSet.markdown)
                outputDirectory = File(project.buildDir, "outputs/markdown")
                description = "Generates ${sourceSet.name} Markdown documentation"
                group = "reporting"
            }
        }
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
