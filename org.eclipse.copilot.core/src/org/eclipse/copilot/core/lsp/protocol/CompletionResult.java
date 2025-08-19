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

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * Result for getCompletions & getCompletionsCycling.
 */
public class CompletionResult {

  @NonNull
  private List<CompletionItem> completions;

  /**
   * Creates a new CompletionResult.
   */
  public CompletionResult(@NonNull List<CompletionItem> completions) {
    this.completions = completions;
  }

  public List<CompletionItem> getCompletions() {
    return completions;
  }

  public void setCompletions(List<CompletionItem> completions) {
    this.completions = completions;
  }

  @Override
  public int hashCode() {
    return Objects.hash(completions);
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
    CompletionResult other = (CompletionResult) obj;
    return Objects.equals(completions, other.completions);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("completions", completions);
    return builder.toString();
  }
}
