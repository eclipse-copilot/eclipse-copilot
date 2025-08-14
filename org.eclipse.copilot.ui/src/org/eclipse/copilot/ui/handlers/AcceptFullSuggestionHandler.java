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

package org.eclipse.copilot.ui.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.copilot.core.completion.AcceptSuggestionType;
import org.eclipse.copilot.core.completion.SuggestionUpdateManager;
import org.eclipse.copilot.core.lsp.protocol.CompletionItem;
import org.eclipse.copilot.core.lsp.protocol.NotifyAcceptedParams;
import org.eclipse.copilot.ui.completion.BaseCompletionManager;

/**
 * Handler for accepting the full suggestion.
 */
public class AcceptFullSuggestionHandler extends CopilotHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    BaseCompletionManager handler = getActiveCompletionManager();
    if (handler != null) {
      notifyAccepted(handler.getSuggestionUpdateManager());
      handler.acceptSuggestion(AcceptSuggestionType.FULL);
    }
    return null;
  }

  @Override
  public boolean isEnabled() {
    BaseCompletionManager manager = getActiveCompletionManager();
    if (manager == null) {
      return false;
    }
    SuggestionUpdateManager suggestionUpdateManager = manager.getSuggestionUpdateManager();
    if (suggestionUpdateManager == null) {
      return false;
    }

    CompletionItem item = suggestionUpdateManager.getCurrentItem();
    return item != null;
  }

  private void notifyAccepted(SuggestionUpdateManager manager) {
    if (manager == null) {
      return;
    }

    CompletionItem item = manager.getCurrentItem();
    if (item == null) {
      return;
    }
    String uuid = item.getUuid();
    NotifyAcceptedParams params = new NotifyAcceptedParams(uuid);
    getLanguageServerConnection().notifyAccepted(params);
  }
}
