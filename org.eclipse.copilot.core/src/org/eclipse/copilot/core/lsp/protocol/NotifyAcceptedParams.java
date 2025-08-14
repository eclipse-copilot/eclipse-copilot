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
 * Parameter used for the notify a completion acceptance.
 */
public class NotifyAcceptedParams {

  @NonNull
  private String uuid;

  private Integer acceptedLength;

  /**
   * Create a new NotifyAcceptedParams.
   */
  public NotifyAcceptedParams(String uuid) {
    super();
    this.uuid = uuid;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Integer getAcceptedLength() {
    return acceptedLength;
  }

  public void setAcceptedLength(Integer acceptedLength) {
    this.acceptedLength = acceptedLength;
  }

  @Override
  public int hashCode() {
    return Objects.hash(acceptedLength, uuid);
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
    NotifyAcceptedParams other = (NotifyAcceptedParams) obj;
    return acceptedLength == other.acceptedLength && Objects.equals(uuid, other.uuid);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("uuid", uuid);
    builder.add("acceptedLength", acceptedLength);
    return builder.toString();
  }

}
