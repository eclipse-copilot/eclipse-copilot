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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.eclipse.copilot.core.Constants;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.ui.chat.ChatView;

/**
 * Handler for opening the chat view.
 */
public class OpenChatViewHandler extends CopilotHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    openMyView();
    return null;
  }

  /**
   * Opens the chat view.
   */
  public void openMyView() {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window != null) {
      IWorkbenchPage page = window.getActivePage();
      if (page != null) {
        try {
          ChatView view = (ChatView) page.showView(Constants.CHAT_VIEW_ID);
          if (view != null) {
            view.setFocus();
          }
        } catch (PartInitException e) {
          CopilotCore.LOGGER.error("Failed to open chat view", e);
        }
      }
    }
  }
}
