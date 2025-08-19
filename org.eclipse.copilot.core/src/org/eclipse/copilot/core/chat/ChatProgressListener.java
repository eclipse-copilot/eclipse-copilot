/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.chat;

import org.eclipse.copilot.core.lsp.protocol.ChatProgressValue;

/**
 * Listener for chat resolution.
 */
public interface ChatProgressListener {
  /**
   * Notifies to the listeners when the chat is resolved.
   */
  public void onChatProgress(ChatProgressValue progress);

}
