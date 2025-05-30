/*
 * Copyright 2014-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import ktorbuild.KtorBuildExtension
import ktorbuild.internal.*
import ktorbuild.internal.gradle.maybeNamed
import ktorbuild.targets.*
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("ktorbuild.base")
    kotlin("multiplatform")
    id("org.jetbrains.kotlinx.atomicfu")
    id("ktorbuild.codestyle")
}

kotlin {
    jvmToolchain(KtorBuildExtension.DEFAULT_JDK)
    explicitApi()

    compilerOptions {
        progressiveMode = ktorBuild.kotlinLanguageVersion.map { it >= KotlinVersion.DEFAULT }
        apiVersion = ktorBuild.kotlinApiVersion
        languageVersion = ktorBuild.kotlinLanguageVersion
        freeCompilerArgs.addAll("-Xexpect-actual-classes")
    }

    applyHierarchyTemplate(KtorTargets.hierarchyTemplate)
    addTargets(ktorBuild.targets)
}

val targets = ktorBuild.targets

configureCommon()
if (targets.hasJvm) configureJvm()
if (targets.hasJs) configureJs()
if (targets.hasWasmJs) configureWasmJs()

if (targets.hasJsOrWasmJs) {
    tasks.configureEach {
        if (name == "compileJsAndWasmSharedMainKotlinMetadata") enabled = false
    }
}

// Run native tests only on matching host.
// There is no need to configure `onlyIf` for Darwin targets as they're configured by KGP.
@Suppress("UnstableApiUsage")
if (targets.hasNative) {
    tasks.maybeNamed("linkDebugTestLinuxX64") {
        val os = ktorBuild.os.get()
        onlyIf("run only on Linux") { os.isLinux }
    }
    tasks.maybeNamed("linkDebugTestLinuxArm64") {
        val os = ktorBuild.os.get()
        onlyIf("run only on Linux") { os.isLinux }
    }
    tasks.maybeNamed("linkDebugTestMingwX64") {
        val os = ktorBuild.os.get()
        onlyIf("run only on Windows") { os.isWindows }
    }
}

setupTrain()
if (ktorBuild.isCI.get()) configureTestTasksOnCi()
