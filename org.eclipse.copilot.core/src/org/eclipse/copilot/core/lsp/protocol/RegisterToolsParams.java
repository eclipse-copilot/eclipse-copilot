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
 * Register tools parameters.
 */
public class RegisterToolsParams {
  private List<LanguageModelToolInformation> tools;

  /**
   * Creates a new register tools parameters.
   */
  public RegisterToolsParams() {
    this.tools = new ArrayList<>();
  }

  public List<LanguageModelToolInformation> getTools() {
    return tools;
  }

  public void setTools(List<LanguageModelToolInformation> tools) {
    this.tools = tools;
  }

  /**
   * Adds a toolInfo to the list of LanguageModelToolInformation.
   *
   * @param toolInfo the LanguageModelToolInformation to add
   */
  public void addTool(LanguageModelToolInformation toolInfo) {
    this.tools.add(toolInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tools);
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
    RegisterToolsParams other = (RegisterToolsParams) obj;
    return Objects.equals(tools, other.tools);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("tools", tools);
    return builder.toString();
  }
}
