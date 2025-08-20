/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.chat.service;

/**
 * Interface for managing chat services in the Copilot chat.
 */
public interface IChatServiceManager {

  /**
   * Get the referenced file service.
   */
  IReferencedFileService getReferencedFileService();
  
  /**
   * Get the MCP config service.
   */
  IMcpConfigService getMcpConfigService();
}
