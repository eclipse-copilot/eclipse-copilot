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

/**
 * Parameter used for notifying code acceptance.
 */
public class NotifyCodeAcceptanceParams {
  private String turnId;

  /**
   * The number of files accepted by the user.
   */
  private int acceptedFileCount;

  /**
   * The initial number of files pending decision in this turn.
   */
  private int totalFileCount;

  /**
   * Constructor.
   */
  public NotifyCodeAcceptanceParams(String turnId, int acceptedFileCount, int totalFileCount) {
    this.turnId = turnId;
    this.acceptedFileCount = acceptedFileCount;
    this.totalFileCount = totalFileCount;
  }

  public String getTurnId() {
    return turnId;
  }

  public void setTurnId(String turnId) {
    this.turnId = turnId;
  }

  public int getAcceptedFileCount() {
    return acceptedFileCount;
  }

  public void setAcceptedFileCount(int acceptedFileCount) {
    this.acceptedFileCount = acceptedFileCount;
  }

  public int getTotalFileCount() {
    return totalFileCount;
  }

  public void setTotalFileCount(int totalFileCount) {
    this.totalFileCount = totalFileCount;
  }

  @Override
  public int hashCode() {
    return Objects.hash(acceptedFileCount, totalFileCount, turnId);
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
    NotifyCodeAcceptanceParams other = (NotifyCodeAcceptanceParams) obj;
    return acceptedFileCount == other.acceptedFileCount && totalFileCount == other.totalFileCount
        && Objects.equals(turnId, other.turnId);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("turnId", turnId);
    builder.add("acceptedFileCount", acceptedFileCount);
    builder.add("totalFileCount", totalFileCount);
    return builder.toString();
  }
}
