/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.completion;

import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.copilot.core.IdeCapabilities;
import org.eclipse.copilot.core.completion.CompletionProvider;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.ui.preferences.LanguageServerSettingManager;

/**
 * Factory class to create a completion manager based on IDE capabilities.
 */
public class CompletionManagerFactory {

  /**
   * Creates a completion manager based on IDE capabilities.
   *
   * @param lsConnection The Copilot language server connection
   * @param provider The completion provider
   * @param editor The text editor
   * @param settingsManager The language server settings manager
   * @return A completion manager instance - either CompletionManager or LegacyCompletionManager
   */
  public static BaseCompletionManager createCompletionManager(CopilotLanguageServerConnection lsConnection,
      CompletionProvider provider, ITextEditor editor, LanguageServerSettingManager settingsManager) {
      
    if (IdeCapabilities.canUseCodeMining()) {
      return new CompletionManager(lsConnection, provider, editor, settingsManager);
    } else {
      return new CompletionManagerLegacy(lsConnection, provider, editor, settingsManager);
    }
  }
}