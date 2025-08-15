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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

import org.eclipse.copilot.ui.utils.SwtUtils;

/**
 * Handler for opening the preferences dialog.
 */
public class OpenPreferencesHandler extends AbstractHandler {

  public static final String copilotPreferencesPage = 
      "org.eclipse.copilot.ui.preferences.CopilotPreferencesPage";

  public static final String mcpPreferencePage = "org.eclipse.copilot.ui.preferences.McpPreferencePage";
  public static final String customInstructionsPreferencePage = 
      "org.eclipse.copilot.ui.preferences.CustomInstructionPreferencePage";

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Shell shell = SwtUtils.getShellFromEvent(event);
    PreferenceDialog dialog;

    String activePageId = event.getParameter("org.eclipse.copilot.commands.openPreferences.activePageId");
    if (!StringUtils.isBlank(activePageId)) {
      String[] pageIds = event.getParameter("org.eclipse.copilot.commands.openPreferences.pageIds")
          .split(",");
      dialog = PreferencesUtil.createPreferenceDialogOn(shell, activePageId, pageIds, null);
    } else {
      dialog = PreferencesUtil.createPreferenceDialogOn(shell, copilotPreferencesPage,
          new String[] { customInstructionsPreferencePage, mcpPreferencePage }, null);
    }

    dialog.open();
    return null;
  }

}
