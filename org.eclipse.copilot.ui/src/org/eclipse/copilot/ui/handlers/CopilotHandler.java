/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jdt.annotation.Nullable;

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.completion.BaseCompletionManager;
import org.eclipse.copilot.ui.completion.EditorsManager;

/**
 * Base class for Copilot handlers.
 */
public abstract class CopilotHandler extends AbstractHandler {
  /**
   * Gets the active {@link BaseCompletionManager} for the current editor.
   */
  @Nullable
  public BaseCompletionManager getActiveCompletionManager() {
    CopilotUi copilotUi = CopilotUi.getPlugin();
    if (copilotUi == null) {
      return null;
    }
    EditorsManager manager = copilotUi.getEditorsManager();
    if (manager == null) {
      return null;
    }
    return manager.getActiveCompletionManager();
  }

  public CopilotLanguageServerConnection getLanguageServerConnection() {
    return CopilotCore.getPlugin().getCopilotLanguageServer();
  }

}
