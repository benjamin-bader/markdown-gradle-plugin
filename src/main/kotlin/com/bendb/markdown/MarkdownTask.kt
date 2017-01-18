package com.bendb.markdown

import org.gradle.api.file.FileTree
import org.gradle.api.tasks.*
import java.io.File

open class MarkdownTask : SourceTask() {

    @OutputDirectory
    open lateinit var outputDirectory: File

    @TaskAction
    open fun runTask() {

    }
}