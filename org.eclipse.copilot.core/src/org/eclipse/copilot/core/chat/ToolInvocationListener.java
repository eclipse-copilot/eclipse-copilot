/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.chat;

import java.util.concurrent.CompletableFuture;

import org.eclipse.copilot.core.lsp.protocol.InvokeClientToolConfirmationParams;
import org.eclipse.copilot.core.lsp.protocol.InvokeClientToolParams;
import org.eclipse.copilot.core.lsp.protocol.LanguageModelToolConfirmationResult;
import org.eclipse.copilot.core.lsp.protocol.LanguageModelToolResult;

/**
 * Listener for tool invocation.
 */
public interface ToolInvocationListener {
  /**
   * Notifies to the listeners when a tool invocation needs to be confirmed.
   *
   * @param params The parameters for the tool confirmation.
   * @return A CompletableFuture that will be completed with the result of the tool confirmation.
   */
  public CompletableFuture<LanguageModelToolConfirmationResult> onToolConfirmation(
      InvokeClientToolConfirmationParams params);

  /**
   * Notifies to the listeners when a tool is invoked.
   *
   * @param params The parameters for invoking the tool.
   * @return A CompletableFuture that will be completed with the result of the tool invocation.
   */
  public CompletableFuture<LanguageModelToolResult[]> onToolInvocation(InvokeClientToolParams params);
}
