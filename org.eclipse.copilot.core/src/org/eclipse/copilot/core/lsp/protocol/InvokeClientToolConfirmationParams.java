/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.lsp.protocol;

import java.util.Objects;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * Parameters for the invoke client tool confirmation.
 */
public class InvokeClientToolConfirmationParams {
  /**
   * The name of the tool to be invoked.
   */
  private String name;

  /**
   * The title of the tool confirmation.
   */
  private String title;

  /**
   * The message of the tool confirmation.
   */
  private String message;

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
   * The unique ID for this specific tool call. Allows differentiation between multiple calls to the same tool in one
   * round.
   */
  private String toolCallId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
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
    return Objects.hash(conversationId, input, message, name, roundId, title, toolCallId, turnId);
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
    InvokeClientToolConfirmationParams other = (InvokeClientToolConfirmationParams) obj;
    return Objects.equals(conversationId, other.conversationId) && Objects.equals(input, other.input)
        && Objects.equals(message, other.message) && Objects.equals(name, other.name) && roundId == other.roundId
        && Objects.equals(title, other.title) && Objects.equals(toolCallId, other.toolCallId)
        && Objects.equals(turnId, other.turnId);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("name", name);
    builder.add("title", title);
    builder.add("message", message);
    builder.add("input", input);
    builder.add("conversationId", conversationId);
    builder.add("turnId", turnId);
    builder.add("roundId", roundId);
    builder.add("toolCallId", toolCallId);
    return builder.toString();
  }
}
