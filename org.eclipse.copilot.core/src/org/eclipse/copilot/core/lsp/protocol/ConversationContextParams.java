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

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * Parameters for the conversation context (skills) request.
 */
public record ConversationContextParams(String conversationId, String turnId, String skillId) {

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("conversationId", conversationId);
    builder.add("turnId", turnId);
    builder.add("skillId", skillId);
    return builder.toString();
  }
}
