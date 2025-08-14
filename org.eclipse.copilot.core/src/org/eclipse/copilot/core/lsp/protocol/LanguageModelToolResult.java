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
import java.util.List;
import java.util.Objects;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * Result Language Model Tool.
 */
public class LanguageModelToolResult {
  /**
   * A list of tool result content parts. Includes `unknown` because this list may be extended with new content types in
   * the future.
   */
  private List<LanguageModelTextPart> content;

  /**
   * Creates a new LanguageModelToolResult.
   */
  public LanguageModelToolResult() {
    this.content = new ArrayList<>();
  }

  /**
   * Creates a new LanguageModelToolResult with content.
   */
  public LanguageModelToolResult(String resultContent) {
    this.content = new ArrayList<>();
    this.content.add(new LanguageModelTextPart(resultContent));
  }

  /**
   * Creates a new LanguageModelToolResult with the specified content value.
   *
   * @param contentValue the content value to set to the LanguageModelTextPart.
   */
  public void addContent(String contentValue) {
    LanguageModelTextPart textPart = new LanguageModelTextPart(contentValue);
    this.content.add(textPart);
  }

  public List<LanguageModelTextPart> getContent() {
    return content;
  }

  public void setContent(List<LanguageModelTextPart> content) {
    this.content = content;
  }

  @Override
  public int hashCode() {
    return Objects.hash(content);
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
    LanguageModelToolResult other = (LanguageModelToolResult) obj;
    return Objects.equals(content, other.content);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("content", content);
    return builder.toString();
  }
}
