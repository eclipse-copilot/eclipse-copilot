/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.api;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for terminal tool implementations that can execute commands in a terminal. This interface is used by the
 * SPI to allow different terminal tools to be plugged in.
 */
public interface IRunInTerminalTool {
  /**
   * Executes a command in the terminal.
   *
   * @param command The command to execute.
   * @param isBackground Whether the command should run in the background.
   * @return A CompletableFuture that resolves to the output of the command.
   */
  public CompletableFuture<String> executeCommand(String command, boolean isBackground);

  /**
   * Prepares terminal properties for the command execution.
   *
   * @param runInBackground Whether the command should run in the background.
   * @param executionId The unique identifier for the execution.
   * @return A map containing terminal properties.
   */
  public Map<String, Object> prepareTerminalProperties(boolean runInBackground, String executionId);

  /**
   * Retrieves the output of a background command execution.
   *
   * @param executionId The unique identifier for the background execution.
   * @return A StringBuilder containing the output of the command.
   */
  public StringBuilder getBackgroundCommandOutput(String executionId);
}
