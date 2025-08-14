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

import java.util.Objects;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * Parameters for invoking a client tool.
 */
public class InvokeClientToolParams {
  /**
   * The name of the tool to be invoked.
   */
  private String name;

  /**
   * The input to the tool.
   */
  private Object input;

  /**
   * The ID of the conversation this tool invocation belongs to.
   */
  private String conversationId;

  /**
   * The ID of the turn this tool invocation belongs to.
   */
  private String turnId;

  /**
   * The ID of the round this tool invocation belongs to.
   */
  private int roundId;

  /**
   * The unique ID for this specific tool call.
   */
  private String toolCallId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getInput() {
    return input;
  }

  public void setInput(Object input) {
    this.input = input;
  }

  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }

  public String getTurnId() {
    return turnId;
  }

  public void setTurnId(String turnId) {
    this.turnId = turnId;
  }

  public int getRoundId() {
    return roundId;
  }

  public void setRoundId(int roundId) {
    this.roundId = roundId;
  }

  public String getToolCallId() {
    return toolCallId;
  }

  public void setToolCallId(String toolCallId) {
    this.toolCallId = toolCallId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(conversationId, input, name, roundId, toolCallId, turnId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    InvokeClientToolParams other = (InvokeClientToolParams) obj;
    return Objects.equals(conversationId, other.conversationId) && Objects.equals(input, other.input)
        && Objects.equals(name, other.name) && roundId == other.roundId && Objects.equals(toolCallId, other.toolCallId)
        && Objects.equals(turnId, other.turnId);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("name", name);
    builder.add("input", input);
    builder.add("conversationId", conversationId);
    builder.add("turnId", turnId);
    builder.add("roundId", roundId);
    builder.add("toolCallId", toolCallId);
    return builder.toString();
  }
}
