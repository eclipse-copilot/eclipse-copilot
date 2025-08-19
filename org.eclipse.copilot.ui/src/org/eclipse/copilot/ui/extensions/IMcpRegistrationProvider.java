/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.extensions;

/**
 * Interface for providing MCP (Model Context Protocol) server configurations.
 * Implementations of this interface can be contributed via the mcpRegistration extension point
 * to dynamically provide MCP server configurations to the GitHub Copilot plugin.
 */
public interface IMcpRegistrationProvider {

  /**
   * Provides MCP server configurations in JSON format.
   *
   * @return JSON string containing MCP server configurations in the format:
   *         {"servers":{"serverName1":{...config...},"serverName2":{...config...}}}
   *         Returns null or empty string if no configurations are available.
   */
  String getMcpServerConfigurations();

  /**
   * Gets the unique identifier for this provider.
   * This can be used for debugging and logging purposes.
   *
   * @return A unique identifier for this provider
   */
  default String getProviderId() {
    return getClass().getName();
  }
}
