package com.github.manosbatsis.lib.core.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T : Any> loggerFor(): Logger = LoggerFactory.getLogger(T::class.java)
fun Any.contextLogger(): Logger = LoggerFactory.getLogger(javaClass.enclosingClass)
