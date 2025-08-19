/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.lsp.protocol;

import java.util.List;
import java.util.Objects;

import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * A turn in a conversation.
 */
public class Turn {
  @NonNull
  Either<String, List<ChatCompletionContentPart>> request;
  String response;
  String agentSlug;

  /**
   * Creates a new Turn.
   */
  public Turn(@NonNull Either<String, List<ChatCompletionContentPart>> request, String response, String agentSlug) {
    this.request = request;
    this.response = response;
    this.agentSlug = agentSlug;
  }

  public Either<String, List<ChatCompletionContentPart>> getRequest() {
    return request;
  }

  public String getResponse() {
    return response;
  }

  public String getAgentSlug() {
    return agentSlug;
  }

  public void setRequest(Either<String, List<ChatCompletionContentPart>> request) {
    this.request = request;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public void setAgentSlug(String agentSlug) {
    this.agentSlug = agentSlug;
  }

  @Override
  public int hashCode() {
    return Objects.hash(request, response, agentSlug);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Turn turn = (Turn) o;
    return Objects.equals(request, turn.request) && Objects.equals(response, turn.response)
        && Objects.equals(agentSlug, turn.agentSlug);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("request", request);
    builder.add("response", response);
    builder.add("agentSlug", agentSlug);
    return builder.toString();
  }
}
