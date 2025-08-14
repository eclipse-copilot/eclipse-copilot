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
 * Initialization options for the Copilot language server.
 */
public class InitializationOptions {
  @NonNull
  private NameAndVersion editorInfo;

  @NonNull
  private NameAndVersion editorPluginInfo;

  private CopilotCapabilities copilotCapabilities;

  /**
   * Creates a new InitializationOptions.
   */
  public InitializationOptions(NameAndVersion editorInfo, NameAndVersion editorPluginInfo) {
    this.editorInfo = editorInfo;
    this.editorPluginInfo = editorPluginInfo;
  }

  /**
   * Creates a new InitializationOptions.
   */
  public InitializationOptions(NameAndVersion editorInfo, NameAndVersion editorPluginInfo,
      CopilotCapabilities copilotCapabilities) {
    this.editorInfo = editorInfo;
    this.editorPluginInfo = editorPluginInfo;
    this.copilotCapabilities = copilotCapabilities;
  }

  @NonNull
  public NameAndVersion getEditorInfo() {
    return editorInfo;
  }

  public void setEditorInfo(@NonNull NameAndVersion editorInfo) {
    this.editorInfo = editorInfo;
  }

  @NonNull
  public NameAndVersion getEditorPluginInfo() {
    return editorPluginInfo;
  }

  public void setEditorPluginInfo(@NonNull NameAndVersion editorPluginInfo) {
    this.editorPluginInfo = editorPluginInfo;
  }

  public CopilotCapabilities getCopilotCapabilities() {
    return copilotCapabilities;
  }

  public void setCopilotCapabilities(CopilotCapabilities copilotCapabilities) {
    this.copilotCapabilities = copilotCapabilities;
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("editorInfo", editorInfo);
    builder.add("editorPluginInfo", editorPluginInfo);
    builder.add("copilotCapabilities", copilotCapabilities);
    return builder.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(copilotCapabilities, editorInfo, editorPluginInfo);
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
    InitializationOptions other = (InitializationOptions) obj;
    return Objects.equals(copilotCapabilities, other.copilotCapabilities)
        && Objects.equals(editorInfo, other.editorInfo) && Objects.equals(editorPluginInfo, other.editorPluginInfo);
  }
}
