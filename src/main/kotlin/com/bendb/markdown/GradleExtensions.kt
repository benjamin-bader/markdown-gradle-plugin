package com.bendb.markdown

import org.gradle.api.Plugin
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.PluginManager

inline fun <reified P : Plugin<*>> PluginManager.apply() = apply(P::class.java)

inline fun <reified T : Any> Convention.getPlugin(): T = getPlugin(T::class.java)