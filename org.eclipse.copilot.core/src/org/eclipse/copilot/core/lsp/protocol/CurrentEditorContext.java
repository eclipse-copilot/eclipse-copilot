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

import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * Result of the current editor context request.
 */
public class CurrentEditorContext {
  @NonNull
  String uri;
  Range visibleRange;
  Range selection;

  /**
   * Creates a new ConversationContextResult.
   */
  public CurrentEditorContext(String uri) {
    this.uri = uri;
  }

  public String getUri() {
    return uri;
  }

  public Range getVisibleRange() {
    return visibleRange;
  }

  public Range getSelection() {
    return selection;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public void setVisibleRange(Range visibleRange) {
    this.visibleRange = visibleRange;
  }

  public void setSelection(Range selection) {
    this.selection = selection;
  }

  @Override
  public int hashCode() {
    return Objects.hash(selection, uri, visibleRange);
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
    CurrentEditorContext other = (CurrentEditorContext) obj;
    return Objects.equals(selection, other.selection) && Objects.equals(uri, other.uri)
        && Objects.equals(visibleRange, other.visibleRange);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("uri", uri);
    builder.add("visibleRange", visibleRange);
    builder.add("selection", selection);
    return builder.toString();
  }
}
