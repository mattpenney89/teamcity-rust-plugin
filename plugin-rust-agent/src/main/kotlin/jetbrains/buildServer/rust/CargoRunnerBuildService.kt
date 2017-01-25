/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * See LICENSE in the project root for license information.
 */

package jetbrains.buildServer.rust

import com.github.zafarkhaja.semver.Version
import jetbrains.buildServer.RunBuildException
import jetbrains.buildServer.agent.ToolCannotBeFoundException
import jetbrains.buildServer.agent.runner.BuildServiceAdapter
import jetbrains.buildServer.agent.runner.ProcessListener
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.rust.cargo.*
import jetbrains.buildServer.rust.logging.CargoLoggerFactory
import jetbrains.buildServer.rust.logging.CargoLoggingListener
import jetbrains.buildServer.util.StringUtil

/**
 * Cargo runner service.
 */
class CargoRunnerBuildService : BuildServiceAdapter() {
    private val osName = System.getProperty("os.name").toLowerCase()
    private val myCargoWithStdErrVersion = Version.forIntegers(0, 13)
    private val myArgumentsProviders = mapOf(
            Pair(CargoConstants.COMMAND_BENCH, BenchArgumentsProvider()),
            Pair(CargoConstants.COMMAND_BUILD, BuildArgumentsProvider()),
            Pair(CargoConstants.COMMAND_CLEAN, CleanArgumentsProvider()),
            Pair(CargoConstants.COMMAND_DOC, DocArgumentsProvider()),
            Pair(CargoConstants.COMMAND_LOGIN, LoginArgumentsProvider()),
            Pair(CargoConstants.COMMAND_PACKAGE, PackageArgumentsProvider()),
            Pair(CargoConstants.COMMAND_PUBLISH, PublishArgumentsProvider()),
            Pair(CargoConstants.COMMAND_RUN, RunArgumentsProvider()),
            Pair(CargoConstants.COMMAND_RUSTC, RustcArgumentsProvider()),
            Pair(CargoConstants.COMMAND_RUSTDOC, RustDocArgumentsProvider()),
            Pair(CargoConstants.COMMAND_TEST, TestArgumentsProvider()),
            Pair(CargoConstants.COMMAND_UPDATE, UpdateArgumentsProvider()),
            Pair(CargoConstants.COMMAND_YANK, YankArgumentsProvider()))

    override fun makeProgramCommandLine(): ProgramCommandLine {
        val parameters = runnerParameters

        val commandName = parameters[CargoConstants.PARAM_COMMAND]
        if (StringUtil.isEmpty(commandName)) {
            val buildException = RunBuildException("Cargo command name is empty")
            buildException.isLogStacktrace = false
            throw buildException
        }

        val argumentsProvider = myArgumentsProviders[commandName]
        if (argumentsProvider == null) {
            val buildException = RunBuildException("Unable to construct arguments for cargo command $commandName")
            buildException.isLogStacktrace = false
            throw buildException
        }

        val toolPath: String
        try {
            toolPath = getToolPath(CargoConstants.RUNNER_TYPE)
        } catch (e: ToolCannotBeFoundException) {
            val buildException = RunBuildException(e)
            buildException.isLogStacktrace = false
            throw buildException
        }

        val arguments = argumentsProvider.getArguments(runnerContext)
        runnerContext.configParameters[CargoConstants.CARGO_CONFIG_NAME]?.let {
            if (Version.valueOf(it).greaterThanOrEqualTo(myCargoWithStdErrVersion)) {
                if (osName.startsWith("windows")) {
                    return createProgramCommandline("cmd.exe", arrayListOf("/c", "2>&1", toolPath).apply {
                        addAll(arguments)
                    })
                } else if (osName.startsWith("freebsd") || osName.startsWith("sunos")) {
                    return createProgramCommandline("sh",
                            arrayListOf("-c", "$toolPath ${arguments.joinToString(" ")} 2>&1"))
                } else {
                    return createProgramCommandline("bash",
                            arrayListOf("-c", "$toolPath ${arguments.joinToString(" ")} 2>&1"))
                }
            }
        }

        return createProgramCommandline(toolPath, arguments)
    }

    override fun getListeners(): List<ProcessListener> {
        val loggerFactory = CargoLoggerFactory(logger)
        return listOf<ProcessListener>(CargoLoggingListener(loggerFactory))
    }
}
