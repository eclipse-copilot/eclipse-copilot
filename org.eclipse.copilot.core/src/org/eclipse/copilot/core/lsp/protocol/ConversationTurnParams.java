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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * Parameters for creating a conversation.
 */
public class ConversationTurnParams {
  @NonNull
  private String workDoneToken;
  @NonNull
  private String conversationId;
  @NonNull
  private Either<String, List<ChatCompletionContentPart>> message; // String or ChatCompletionContentPart[]
  private List<ChatReference> references;
  private TextDocumentIdentifier textDocument;
  private boolean computeSuggestions;
  private String workspaceFolder;
  private List<WorkspaceFolder> workspaceFolders;
  private String[] ignoredSkills;
  private String model;
  private String chatMode;

  // TODO: remove needToolCallConfirmation when CLS fully supports it across all IDEs.
  private boolean needToolCallConfirmation;

  /**
   * Creates a new ConversationTurnParams.
   */
  public ConversationTurnParams(String workDoneToken, String conversationId,
      Either<String, List<ChatCompletionContentPart>> message) {
    this.workDoneToken = workDoneToken;
    this.conversationId = conversationId;
    this.message = message;
    this.references = new ArrayList<>();
    this.computeSuggestions = true;
  }

  public String getWorkDoneToken() {
    return workDoneToken;
  }

  public void setWorkDoneToken(String workDoneToken) {
    this.workDoneToken = workDoneToken;
  }

  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }

  public Either<String, List<ChatCompletionContentPart>> getMessage() {
    return message;
  }

  public void setMessage(Either<String, List<ChatCompletionContentPart>> message) {
    this.message = message;
  }

  public List<ChatReference> getReferences() {
    return references;
  }

  public void setReferences(List<ChatReference> references) {
    this.references = references;
  }

  public TextDocumentIdentifier getTextDocument() {
    return textDocument;
  }

  public void setTextDocument(TextDocumentIdentifier textDocument) {
    this.textDocument = textDocument;
  }

  public boolean isComputeSuggestions() {
    return computeSuggestions;
  }

  public void setComputeSuggestions(boolean computeSuggestions) {
    this.computeSuggestions = computeSuggestions;
  }

  public String getWorkspaceFolder() {
    return workspaceFolder;
  }

  public void setWorkspaceFolder(String workspaceFolder) {
    this.workspaceFolder = workspaceFolder;
  }

  public String[] getIgnoredSkills() {
    return ignoredSkills;
  }

  public void setIgnoredSkills(String[] ignoredSkills) {
    this.ignoredSkills = ignoredSkills;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getChatMode() {
    return chatMode;
  }

  public void setChatMode(String chatMode) {
    this.chatMode = chatMode;
  }

  public List<WorkspaceFolder> getWorkspaceFolders() {
    return workspaceFolders;
  }

  public void setWorkspaceFolders(List<WorkspaceFolder> workspaceFolders) {
    this.workspaceFolders = workspaceFolders;
  }

  public boolean isNeedToolCallConfirmation() {
    return needToolCallConfirmation;
  }

  public void setNeedToolCallConfirmation(boolean needToolCallConfirmation) {
    this.needToolCallConfirmation = needToolCallConfirmation;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(ignoredSkills);
    result = prime * result + Objects.hash(chatMode, computeSuggestions, conversationId, message, model,
        needToolCallConfirmation, references, textDocument, workDoneToken, workspaceFolder, workspaceFolders);
    return result;
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
    ConversationTurnParams other = (ConversationTurnParams) obj;
    return Objects.equals(chatMode, other.chatMode) && computeSuggestions == other.computeSuggestions
        && Objects.equals(conversationId, other.conversationId) && Arrays.equals(ignoredSkills, other.ignoredSkills)
        && Objects.equals(message, other.message) && Objects.equals(model, other.model)
        && needToolCallConfirmation == other.needToolCallConfirmation && Objects.equals(references, other.references)
        && Objects.equals(textDocument, other.textDocument) && Objects.equals(workDoneToken, other.workDoneToken)
        && Objects.equals(workspaceFolder, other.workspaceFolder)
        && Objects.equals(workspaceFolders, other.workspaceFolders);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("workDoneToken", workDoneToken);
    builder.add("conversationId", conversationId);
    builder.add("message", message);
    builder.add("references", references);
    builder.add("textDocument", textDocument);
    builder.add("computeSuggestions", computeSuggestions);
    builder.add("workspaceFolder", workspaceFolder);
    builder.add("workspaceFolders", workspaceFolders);
    builder.add("ignoredSkills", Arrays.toString(ignoredSkills));
    builder.add("model", model);
    builder.add("chatMode", chatMode);
    builder.add("needToolCallConfirmation", needToolCallConfirmation);
    return builder.toString();
  }
}
