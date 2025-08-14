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
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * Parameters for the conversation code copy event. {@link See
 * https://github.com/microsoft/copilot-client/blob/936dc4407e15d877ea9b445f19faab32749aa7c6/agent/src/methods/conversation/conversationCodeCopy.ts#L23}
 */
public class ConversationCodeCopyParams {
  public static final String COPY_SOURCE_KEYBOARD = "keyboard";
  public static final String COPY_SOURCE_TOOLBAR = "toolbar";

  @NonNull
  private String turnId;

  @NonNull
  private int codeBlockIndex;

  @NonNull
  private String source;

  @NonNull
  private int copiedCharacters;

  @NonNull
  private int totalCharacters;

  @NonNull
  private String copiedText;

  /**
   * Constructor for the ConversationCodeCopyParams.
   */
  public ConversationCodeCopyParams(String turnId, int codeBlockIndex, String source, int copiedCharacters,
      int totalCharacters, String copiedText) {
    this.turnId = turnId;
    this.codeBlockIndex = codeBlockIndex;
    this.source = source;
    this.copiedCharacters = copiedCharacters;
    this.totalCharacters = totalCharacters;
    this.copiedText = copiedText;
  }

  public String getTurnId() {
    return turnId;
  }

  public void setTurnId(String turnId) {
    this.turnId = turnId;
  }

  public int getCodeBlockIndex() {
    return codeBlockIndex;
  }

  public void setCodeBlockIndex(int codeBlockIndex) {
    this.codeBlockIndex = codeBlockIndex;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public int getCopiedCharacters() {
    return copiedCharacters;
  }

  public void setCopiedCharacters(int copiedCharacters) {
    this.copiedCharacters = copiedCharacters;
  }

  public int getTotalCharacters() {
    return totalCharacters;
  }

  public void setTotalCharacters(int totalCharacters) {
    this.totalCharacters = totalCharacters;
  }

  public String getCopiedText() {
    return copiedText;
  }

  public void setCopiedText(String copiedText) {
    this.copiedText = copiedText;
  }

  @Override
  public int hashCode() {
    return Objects.hash(codeBlockIndex, copiedCharacters, copiedText, source, totalCharacters, turnId);
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
    ConversationCodeCopyParams other = (ConversationCodeCopyParams) obj;
    return codeBlockIndex == other.codeBlockIndex && copiedCharacters == other.copiedCharacters
        && Objects.equals(copiedText, other.copiedText) && Objects.equals(source, other.source)
        && totalCharacters == other.totalCharacters && Objects.equals(turnId, other.turnId);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("turnId", turnId);
    builder.add("codeBlockIndex", codeBlockIndex);
    builder.add("source", source);
    builder.add("copiedCharacters", copiedCharacters);
    builder.add("totalCharacters", totalCharacters);
    builder.add("copiedText", copiedText);
    return builder.toString();
  }

}
