package me.hubertus248.deployer.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.getLogger(): Logger = LoggerFactory.getLogger(T::class.java)

