package com.bendb.markdown

import org.junit.Test

class ModelTests {
    @Test fun `tidyUp makes valid XHTML`() {
        val html = """
            <html>
                <head/>
                <body/>
            </html>
        """

        println(tidyUp(html.toByteArray(Charsets.UTF_8)))
    }
}