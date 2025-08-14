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
 * The result of the confirmation.
 */
public class LanguageModelToolConfirmationResult {
  String result;

  /**
   * Construct a new LanguageModelToolConfirmationResult by ToolConfirmationResult.
   */
  public LanguageModelToolConfirmationResult(ToolConfirmationResult result) {
    this.result = result.toString();
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @Override
  public int hashCode() {
    return Objects.hash(result);
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
    LanguageModelToolConfirmationResult other = (LanguageModelToolConfirmationResult) obj;
    return result == other.result;
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("result", result);
    return builder.toString();
  }

  /**
   * Specifies the result of the confirmation.
   */
  public enum ToolConfirmationResult {
    /**
     * The user accepted the tool invocation.
     */
    ACCEPT("accept"),

    /**
     * The user dismissed the tool invocation.
     */
    DISMISS("dismiss");

    private final String value;

    ToolConfirmationResult(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return value;
    }
  }
}
