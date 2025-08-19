/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.handlers;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.copilot.core.completion.SuggestionUpdateManager;
import org.eclipse.copilot.core.lsp.protocol.NotifyRejectedParams;
import org.eclipse.copilot.ui.completion.BaseCompletionManager;

/**
 * Handler for clearing the completion ghost text.
 */
public class DiscardSuggestionHandler extends CopilotHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    BaseCompletionManager handler = getActiveCompletionManager();
    if (handler != null) {
      notifyRejected(handler.getSuggestionUpdateManager());
      handler.clearGhostTexts();
    }
    return null;
  }

  private void notifyRejected(SuggestionUpdateManager manager) {
    if (manager == null) {
      return;
    }
    List<String> uuids = manager.getUuids();
    if (uuids == null || uuids.isEmpty()) {
      return;
    }

    NotifyRejectedParams params = new NotifyRejectedParams(uuids);
    getLanguageServerConnection().notifyRejected(params);
  }
}
