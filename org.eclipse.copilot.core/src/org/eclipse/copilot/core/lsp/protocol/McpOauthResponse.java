/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.lsp.protocol;

import java.util.Objects;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * Represents a response to an OAuth confirmation request.
 */
public class McpOauthResponse {
  private Boolean confirm;

  /**
   * Constructor.
   */
  public McpOauthResponse(Boolean confirm) {
    this.confirm = confirm;
  }
  
  public Boolean getConfirm() {
    return confirm;
  }

  public void setConfirm(Boolean confirm) {
    this.confirm = confirm;
  }

  @Override
  public int hashCode() {
    return Objects.hash(confirm);
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
    McpOauthResponse other = (McpOauthResponse) obj;
    return Objects.equals(confirm, other.confirm);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("confirm", confirm);
    return builder.toString();
  }
  
}
