/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * See LICENSE in the project root for license information.
 */

package jetbrains.buildServer.rust.cargo;

import jetbrains.buildServer.rust.ArgumentsProvider;
import jetbrains.buildServer.rust.CargoConstants;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides arguments to cargo doc command.
 */
public class DocArgumentsProvider implements ArgumentsProvider {

    @NotNull
    @Override
    public List<String> getArguments(@NotNull final Map<String, String> parameters) {
        final List<String> arguments = new ArrayList<String>();
        arguments.add(CargoConstants.COMMAND_DOC);

        final String packageValue = parameters.get(CargoConstants.PARAM_DOC_PACKAGE);
        if (!StringUtil.isEmptyOrSpaces(packageValue)) {
            arguments.add("--package");
            arguments.add(packageValue.trim());
        }

        final String parallelJobsValue = parameters.get(CargoConstants.PARAM_DOC_PARALLEL);
        if (!StringUtil.isEmptyOrSpaces(parallelJobsValue)) {
            arguments.add("--jobs");
            arguments.add(parallelJobsValue.trim());
        }

        final String releaseValue = parameters.get(CargoConstants.PARAM_DOC_RELEASE);
        if ("true".equalsIgnoreCase(releaseValue)) {
            arguments.add("--release");
        }

        final String noDepsValue = parameters.get(CargoConstants.PARAM_DOC_NO_DEPS);
        if ("true".equalsIgnoreCase(noDepsValue)) {
            arguments.add("--no-deps");
        }

        final String featuresValue = parameters.get(CargoConstants.PARAM_DOC_FEATURES);
        if (!StringUtil.isEmptyOrSpaces(featuresValue)) {
            arguments.add("--features");
            arguments.add(featuresValue.trim());
        }

        final String noDefaultFeaturesValue = parameters.get(CargoConstants.PARAM_DOC_NO_DEFAULT_FEATURES);
        if ("true".equalsIgnoreCase(noDefaultFeaturesValue)) {
            arguments.add("--no-default-features");
        }

        final String targetValue = parameters.get(CargoConstants.PARAM_DOC_TARGET);
        if (!StringUtil.isEmptyOrSpaces(targetValue)) {
            arguments.add("--target");
            arguments.add(targetValue.trim());
        }

        final String manifestValue = parameters.get(CargoConstants.PARAM_DOC_MANIFEST);
        if (!StringUtil.isEmptyOrSpaces(manifestValue)) {
            arguments.add("--manifest-path");
            arguments.add(manifestValue.trim());
        }

        final String verbosityValue = parameters.get(CargoConstants.PARAM_VERBOSITY);
        if (!StringUtil.isEmptyOrSpaces(verbosityValue)) {
            arguments.add(verbosityValue.trim());
        }

        return arguments;
    }
}
