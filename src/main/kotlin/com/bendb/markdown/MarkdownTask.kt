package com.bendb.markdown

import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

open class MarkdownTask : SourceTask() {

    open lateinit var sourceRoot: File

    @OutputDirectory
    open lateinit var outputDirectory: File

    @TaskAction
    open fun runTask() {
        val rootPath = Paths.get(sourceRoot.canonicalPath)
        getSource().files.map {
            val filePath = Paths.get(it.canonicalPath)
            val relativePath = rootPath.relativize(filePath)
            val parsedNode = parseMarkdownFile(it)
            MarkdownDocument("$relativePath", parsedNode)
        }.forEach {
            println(it.xhtml)
        }
    }
}