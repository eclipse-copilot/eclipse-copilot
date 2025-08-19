/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.handlers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.eclipse.copilot.core.Constants;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.lsp.protocol.ChatMode;
import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.chat.ActionBar;
import org.eclipse.copilot.ui.chat.ChatView;

/**
 * Handler for opening the chat view.
 */
public class OpenChatViewHandler extends CopilotHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    ChatView chatView = openChatView();
    if (chatView != null) {
      setUpParameters(event, chatView);
    }
    return null;
  }

  /**
   * Opens the chat view.
   */
  private ChatView openChatView() {
    IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (window != null) {
      IWorkbenchPage page = window.getActivePage();
      if (page != null) {
        try {
          ChatView view = (ChatView) page.showView(Constants.CHAT_VIEW_ID);
          if (view != null) {
            view.setFocus();
            return view;
          }
        } catch (PartInitException e) {
          CopilotCore.LOGGER.error("Failed to open chat view", e);
          return null;
        }
      }
    }

    return null;
  }

  /**
   * Sets up parameters for the chat view based on the execution event and forces the chat mode to "Ask".
   *
   * @param event the execution event containing parameters
   * @param chatView the chat view to set parameters on
   */
  private void setUpParameters(ExecutionEvent event, ChatView chatView) {
    CopilotUi.getPlugin().getChatServiceManager().getUserPreferenceService().setActiveChatMode(ChatMode.Ask.toString());

    String inputValue = event.getParameter("org.eclipse.copilot.commands.openChatView.inputValue");
    ActionBar actionBar = chatView.getActionBar();
    if (StringUtils.isNotBlank(inputValue) && actionBar != null) {
      actionBar.setInputTextViewerContent(inputValue);

      String autoSend = event.getParameter("org.eclipse.copilot.commands.openChatView.autoSend");
      if (StringUtils.isNotBlank(autoSend) && Boolean.parseBoolean(autoSend) && actionBar != null) {
        actionBar.handleSendMessage();
      }
    }
  }
}