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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.copilot.core.AuthStatusManager;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.lsp.protocol.CopilotStatusResult;
import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.i18n.Messages;
import org.eclipse.copilot.ui.utils.SwtUtils;

/**
 * Handler for signing out from GitHub Copilot.
 */
public class SignOutHandler extends AbstractHandler {

  private AuthStatusManager authStatusManager;

  /**
   * Initialize the Copilot Language Server and Auth Status Manager for the SignOutHandler.
   */
  public SignOutHandler() {
    this.authStatusManager = CopilotCore.getPlugin().getAuthStatusManager();
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Shell shell = SwtUtils.getShellFromEvent(event);
    try {
      // Persist user preferences before signing out due to once the user signs out, the preferences file path will be
      // cleared.
      CopilotUi.getPlugin().getChatServiceManager().getUserPreferenceService().persistUserPreference();
      CopilotStatusResult result = authStatusManager.signOut();
      if (!result.isSignedIn()) {
        showSignOutMessage(shell);
        authStatusManager.checkStatus();
      }
    } catch (Exception e) {
      handleSignOutException(shell, e);
    }

    return null;
  }

  private void handleSignOutException(Shell shell, Exception e) {
    String msg = Messages.signOutHandler_msgDialog_signOutFailed;
    if (StringUtils.isNotBlank(e.getMessage())) {
      msg += " " + e.getMessage();
      CopilotCore.LOGGER.error(e);
    }
    MessageDialog.openError(shell, Messages.signOutHandler_msgDialog_signOutFailedFailure, msg);
  }

  private void showSignOutMessage(Shell shell) {
    MessageDialog.openInformation(shell, Messages.signOutHandler_msgDialog_githubCopilot,
        Messages.signOutHandler_msgDialog_signOutSuccess);
  }

}
