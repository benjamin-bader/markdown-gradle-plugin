package com.bendb.markdown

import org.gradle.api.tasks.GradleBuild
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class MarkdownPluginIntegrationTest {
    @get:Rule val tempFolder = TemporaryFolder()

    lateinit var buildFile: File
    lateinit var properties: File
    lateinit var markdownFolder: File

    @Before fun setup() {
        buildFile = tempFolder.newFile("build.gradle")
        properties = tempFolder.newFile("gradle.properties")
        markdownFolder = tempFolder.newFolder("src", "main", "markdown")

        buildFile.writeText("""
            plugins {
                id 'com.bendb.markdown'
            }
        """)
    }

    @Test fun `task is up-to-date if there is no input`() {
        val result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(tempFolder.root)
                .withArguments("generateMarkdownDocs", "--info", "--stacktrace")
                .build()

        assertThat(result.task(":generateMarkdownDocs").outcome, equalTo(TaskOutcome.UP_TO_DATE))
    }

    @Test fun `The task runs when there is at least one md file in the source set`() {
        // make sure there's some kind of input to process
        tempFolder.newFile("src/main/markdown/test.md").writeText("""
### HELLO THERE
        """.trim())

        val result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(tempFolder.root)
                .withArguments("generateMarkdownDocs", "--info", "--stacktrace")
                .build()

        println(result.output)

        assertThat(result.task(":generateMarkdownDocs").outcome, equalTo(TaskOutcome.SUCCESS))
        assertThat(result.output, containsString("HELLO"))
    }
}