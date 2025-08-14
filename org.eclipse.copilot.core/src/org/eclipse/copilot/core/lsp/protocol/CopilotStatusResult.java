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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * Result for the Authentication status.
 */
public class CopilotStatusResult {

  public static final String OK = "OK";
  public static final String ERROR = "Error";
  public static final String LOADING = "Loading";
  public static final String WARNING = "Warning";
  public static final String NOT_SIGNED_IN = "NotSignedIn";
  public static final String NOT_AUTHORIZED = "NotAuthorized";

  @NonNull
  private String status;

  private String user;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public boolean isSignedIn() {
    return OK.equals(this.status) && StringUtils.isNotEmpty(this.user);
  }

  public boolean isNotSignedIn() {
    return NOT_SIGNED_IN.equals(this.status);
  }

  public boolean isWarning() {
    return WARNING.equals(this.status);
  }

  public boolean isError() {
    return ERROR.equals(this.status);
  }

  public boolean isNotAuthorized() {
    return NOT_AUTHORIZED.equals(this.status);
  }

  public boolean isLoading() {
    return LOADING.equals(this.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, user);
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
    CopilotStatusResult other = (CopilotStatusResult) obj;
    return Objects.equals(status, other.status) && Objects.equals(user, other.user);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("status", status);
    builder.add("user", user);
    return builder.toString();
  }

}
