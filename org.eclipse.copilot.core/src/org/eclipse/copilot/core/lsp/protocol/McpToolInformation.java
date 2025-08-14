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

import com.google.gson.annotations.SerializedName;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * Information about the MCP tool. Referenced by McpServerToolsCollection.
 */
public class McpToolInformation extends LanguageModelToolInformation {

  @SerializedName("_status")
  private McpToolStatus status;

  public McpToolStatus getStatus() {
    return status;
  }

  public void setStatus(McpToolStatus status) {
    this.status = status;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(status);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    McpToolInformation other = (McpToolInformation) obj;
    return Objects.equals(status, other.status);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("status", status);
    builder.add("name", getName());
    builder.add("description", getDescription());
    builder.add("inputSchema", getInputSchema());
    builder.add("confirmationMessages", getConfirmationMessages());
    return builder.toString();
  }
}
