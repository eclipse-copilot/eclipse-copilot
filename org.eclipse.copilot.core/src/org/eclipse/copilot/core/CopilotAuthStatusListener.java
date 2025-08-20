/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core;

import org.eclipse.copilot.core.lsp.protocol.CopilotStatusResult;

/**
 * Listener for the authentication status.
 */
public interface CopilotAuthStatusListener {
  
  /**
   * Notifies to the listeners when the authentication status is changed.
   */
  void onDidCopilotStatusChange(CopilotStatusResult copilotStatusResult);
}
