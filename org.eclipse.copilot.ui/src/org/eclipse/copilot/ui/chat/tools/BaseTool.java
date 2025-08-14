/*******************************************************************************
 * Copyright (c) 2025 Microsoft Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Microsoft Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.copilot.ui.chat.tools;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.jdt.annotation.Nullable;

import org.eclipse.copilot.core.lsp.protocol.ConfirmationMessages;
import org.eclipse.copilot.core.lsp.protocol.LanguageModelToolInformation;
import org.eclipse.copilot.core.lsp.protocol.LanguageModelToolResult;
import org.eclipse.copilot.ui.chat.ChatView;

/**
 * Base class for tools.
 */
public abstract class BaseTool {
  protected String name;

  /**
   * Invoke the tool.
   */
  public abstract CompletableFuture<LanguageModelToolResult[]> invoke(Map<String, Object> input, ChatView chatView);

  /**
   * Get the registration information of the tool.
   */
  public LanguageModelToolInformation getToolInformation() {
    LanguageModelToolInformation toolInfo = new LanguageModelToolInformation();
    if (needConfirmation()) {
      toolInfo.setConfirmationMessages(getConfirmationMessages());
    }
    return toolInfo;
  }

  /**
   * Needs user's confirmation to continue.
   */
  public boolean needConfirmation() {
    return false;
  }

  /**
   * Get confirmed messages.
   */
  public ConfirmationMessages getConfirmationMessages() {
    return new ConfirmationMessages();
  }

  /**
   * Get the user input.
   */
  @Nullable
  public Map<String, Object> getInput() {
    return null;
  }

  /**
   * Get the name of the tool.
   */
  public String getToolName() {
    return name;
  }
}