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

/**
 * Gets the MCP server with its tools status. Referenced by: The request param of the endpoint mcp/updateToolsStatus.
 */
public class McpServerToolsStatusCollection {
  private String name;

  private List<McpToolsStatusCollection> tools;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<McpToolsStatusCollection> getTools() {
    return tools;
  }

  public void setTools(List<McpToolsStatusCollection> tools) {
    this.tools = tools;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, tools);
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
    McpServerToolsStatusCollection other = (McpServerToolsStatusCollection) obj;
    return Objects.equals(name, other.name) && Objects.equals(tools, other.tools);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("name", name);
    builder.add("tools", tools);
    return builder.toString();
  }
}
