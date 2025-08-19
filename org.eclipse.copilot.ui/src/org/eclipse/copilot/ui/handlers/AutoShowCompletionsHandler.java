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
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.preferences.LanguageServerSettingManager;

/**
 * Handler for enabling and disabling auto show completions.
 */
public class AutoShowCompletionsHandler extends AbstractHandler implements IHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    LanguageServerSettingManager settingManager = CopilotUi.getPlugin().getLanguageServerSettingManager();
    boolean autoShowCompletions = settingManager.isAutoShowCompletionEnabled();
    settingManager.setAutoShowCompletion(!autoShowCompletions);

    return null;
  }

}
