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

package org.eclipse.copilot.core.lsp.protocol;

/**
 * Scope constants for Copilot.
 * https://github.com/microsoft/copilot-client/blob/main/lib/src/conversation/promptTemplates.ts#L15
 */
public class CopilotScope {
  public static final String CHAT_PANEL = "chat-panel";
  // Scope targeting the editor, such as right click context.
  public static final String EDITOR = "editor";
  // Scope targeting the inline chat.
  public static final String INLINE = "inline";
  // Scope targeting code completions.
  public static final String COMPLETION = "completion";
  // Scope targeting the agent mode.
  public static final String AGENT_PANEL = "agent-panel";
}
