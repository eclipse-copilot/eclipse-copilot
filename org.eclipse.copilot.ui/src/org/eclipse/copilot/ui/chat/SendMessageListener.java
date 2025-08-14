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

package org.eclipse.copilot.ui.chat;

import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * Listener for sending messages.
 */
public interface SendMessageListener {
  /**
   * Called when a message is sent.
   *
   * @param workDoneToken the work done token
   * @param message the message
   */
  public void onSendMessage(String workDoneToken, String message, List<IFile> files);
}
