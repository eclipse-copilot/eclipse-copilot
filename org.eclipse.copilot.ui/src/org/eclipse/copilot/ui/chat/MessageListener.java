/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat;

/**
 * Listener for sending and canceling messages.
 */
public interface MessageListener {
  /**
   * Called when a message is sent.
   *
   * @param workDoneToken the work done token
   * @param message the message
   * @param createNewTurn whether to create a new turn or resend a message directly
   */
  public void onSend(String workDoneToken, String message, boolean createNewTurn);

  /**
   * Called when a message is cancelled.
   */
  public void onCancel();
}
