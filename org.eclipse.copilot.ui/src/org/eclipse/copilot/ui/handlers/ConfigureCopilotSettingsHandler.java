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

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * Handler to open the GitHub Copilot settings configuration page.
 */
public class ConfigureCopilotSettingsHandler extends AbstractHandler {

  private static final String COPILOT_SETTINGS_URL = "https://github.com/settings/copilot";

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    try {
      UiUtils.openLink(COPILOT_SETTINGS_URL);
    } catch (Exception e) {
      CopilotCore.LOGGER.error(e);
    }

    return null;
  }
}
