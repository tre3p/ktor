/*
 * Copyright 2014-2023 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.server.jetty.jakarta

import io.ktor.server.config.*
import io.ktor.server.engine.*

/**
 * Jetty engine
 *
 * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.jetty.jakarta.EngineMain)
 */
public object EngineMain {
    /**
     * Main function for starting EngineMain with Jetty
     * Creates an embedded Jetty application with an environment built from command line arguments.
     *
     * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.jetty.jakarta.EngineMain.main)
     */
    @JvmStatic
    public fun main(args: Array<String>) {
        val server = createServer(args)
        server.start(true)
    }

    /**
     * Creates an instance of the embedded Jetty server without starting it.
     *
     * [Report a problem](https://ktor.io/feedback/?fqname=io.ktor.server.jetty.jakarta.EngineMain.createServer)
     *
     * @param args Command line arguments for configuring the server.
     * @return An instance of [EmbeddedServer] with the specified configuration.
     */
    public fun createServer(
        args: Array<String>
    ): EmbeddedServer<JettyApplicationEngine, JettyApplicationEngineBase.Configuration> {
        val config = CommandLineConfig(args)
        return EmbeddedServer(config.rootConfig, Jetty) {
            takeFrom(config.engineConfig)
            loadConfiguration(config.rootConfig.environment.config)
        }
    }

    private fun JettyApplicationEngineBase.Configuration.loadConfiguration(config: ApplicationConfig) {
        val deploymentConfig = config.config("ktor.deployment")
        loadCommonConfiguration(deploymentConfig)
    }
}
