/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.chat.service;

import org.eclipse.copilot.core.lsp.protocol.McpOauthRequest;

/**
 * Interface for the MCP config service.
 * This service handles the OAuth confirmation process.
 */
public interface IMcpConfigService {
  /**
   * Handles the OAuth confirmation request.
   */
  boolean mcpOauth(McpOauthRequest request);
}
