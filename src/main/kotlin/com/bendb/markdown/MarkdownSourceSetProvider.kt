/*
 * Copyright 2017 Benjamin Bader
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bendb.markdown

import groovy.lang.Closure
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver
import org.gradle.util.ConfigureUtil
import java.lang.reflect.Constructor

internal interface MarkdownSourceSetProvider {
    fun createSourceSet(name: String): MarkdownSourceSet
}

internal interface MarkdownSourceSet {
    val markdown: SourceDirectorySet

    fun markdown(configClosure: groovy.lang.Closure<Any?>?): MarkdownSourceSet
}

internal class MarkdownSourceSetProviderImpl(
        private val fileResolver: FileResolver
) : MarkdownSourceSetProvider {
    override fun createSourceSet(name: String): MarkdownSourceSet {
        return MarkdownSourceSetImpl(name, fileResolver)
    }
}

internal class MarkdownSourceSetImpl(displayName: String, resolver: FileResolver) : MarkdownSourceSet {
    companion object {
        private val klass = DefaultSourceDirectorySet::class.java
        private val defaultConstructor = klass.constructorOrNull(String::class.java, FileResolver::class.java)

        private val useLegacyCtor by lazy {
            defaultConstructor != null && defaultConstructor.getAnnotation(java.lang.Deprecated::class.java) == null
        }

        private val directoryFileTreeFactoryClass by lazy {
            Class.forName("org.gradle.api.internal.file.collections.DirectoryFileTreeFactory")
        }

        private val alternativeConstructor by lazy {
            klass.getConstructor(String::class.java, FileResolver::class.java, directoryFileTreeFactoryClass)
        }

        private val defaultFileTreeFactoryClass by lazy {
            Class.forName("org.gradle.api.internal.file.collections.DefaultDirectoryFileTreeFactory")
        }

        private val defaultFileTreeFactory by lazy {
            defaultFileTreeFactoryClass.getConstructor().newInstance()
        }

        private fun newSourceDirectorySetInstance(displayName: String, fileResolver: FileResolver): SourceDirectorySet {
            if (useLegacyCtor) {
                return defaultConstructor!!.newInstance(displayName, fileResolver)
            } else {
                return alternativeConstructor.newInstance(displayName, fileResolver, defaultFileTreeFactory)
            }
        }
    }

    override val markdown: SourceDirectorySet = newSourceDirectorySetInstance(displayName, resolver)

    override fun markdown(configClosure: Closure<Any?>?): MarkdownSourceSet {
        ConfigureUtil.configure(configClosure, this)
        return this
    }

    init {
        markdown.filter?.include("**/*.md", "**/*.markdown")
    }
}

private fun <T> Class<T>.constructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}