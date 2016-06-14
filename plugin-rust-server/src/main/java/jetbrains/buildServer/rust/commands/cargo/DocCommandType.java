/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * See LICENSE in the project root for license information.
 */

package jetbrains.buildServer.rust.commands.cargo;

import jetbrains.buildServer.rust.CargoConstants;
import jetbrains.buildServer.rust.commands.CommandType;
import org.jetbrains.annotations.NotNull;

/**
 * Provides parameters for cargo doc command.
 */
public class DocCommandType implements CommandType {
    @NotNull
    @Override
    public String getName() {
        return CargoConstants.COMMAND_DOC;
    }

    @NotNull
    @Override
    public String getEditPage() {
        return "editDocParameters.jsp";
    }

    @NotNull
    @Override
    public String getViewPage() {
        return "viewDocParameters.jsp";
    }
}
