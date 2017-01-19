package com.bendb.markdown

import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.w3c.tidy.Tidy
import java.io.*

/**
 * A parsed Markdown file.
 *
 * The file's name is its path, relative to the source-set root; for example,
 * if the file's absolute path is at /home/ben/project/src/main/markdown/docs/example.md,
 * then its name is "docs/example.md".
 *
 * @property name the path of the file, relative to its source-set's root.
 * @property node the parsed content, as a CommonMark node.
 */
data class MarkdownDocument(
        val name: String,
        val node: Node
) {
    val xhtml: String by lazy {
        val html = HtmlRenderer.builder()
                .build()
                .render(node)

        tidyUp(html.toByteArray(Charsets.UTF_8))
    }
}

fun parseMarkdownFile(file: File): Node {
    return file.reader(Charsets.UTF_8).use {
        Parser.builder().build().parseReader(it)
    }
}


fun tidyUp(htmlBytes: ByteArray): String {
    val tidy = Tidy().apply {
        inputEncoding  = "UTF-8"
        outputEncoding = "UTF-8"
        xhtml          = true

        quiet          = true
        showErrors     = 0 // number of errors to emit
        showWarnings   = false

        tidyMark       = false // don't add a JTidy <meta name="generator" /> tag
    }

    ByteArrayInputStream(htmlBytes).use { input ->
        ByteArrayOutputStream().use { output ->
            tidy.parse(input, output)
            return output.toString("UTF-8")
        }
    }
}
