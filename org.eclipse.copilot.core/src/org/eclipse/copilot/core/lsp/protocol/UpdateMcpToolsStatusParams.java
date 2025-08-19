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
 * Parameters for updating the status of the MCP tools.
 */
public class UpdateMcpToolsStatusParams {

  private List<McpServerToolsStatusCollection> servers;

  public List<McpServerToolsStatusCollection> getServers() {
    return servers;
  }

  public void setServers(List<McpServerToolsStatusCollection> servers) {
    this.servers = servers;
  }

  @Override
  public int hashCode() {
    return Objects.hash(servers);
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
    UpdateMcpToolsStatusParams other = (UpdateMcpToolsStatusParams) obj;
    return Objects.equals(servers, other.servers);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("servers", servers);
    return builder.toString();
  }
}