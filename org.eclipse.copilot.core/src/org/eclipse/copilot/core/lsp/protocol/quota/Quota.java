/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.lsp.protocol.quota;

import java.util.Objects;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * Completions quota information.
 */
public class Quota {
  private double percentRemaining;
  private boolean unlimited;
  private boolean overagePermitted;

  /**
   * Creates a new CompletionsQuota quota information with default values.
   */
  public Quota() {
    this.percentRemaining = 0.0;
    this.unlimited = false;
    this.overagePermitted = false;
  }

  /**
   * Gets the percentage of the quota remaining within the range of 0.0 to 100.0.
   */
  public double getPercentRemaining() {
    if (percentRemaining < 0.0) {
      return 0.0;
    } else if (percentRemaining > 100.0) {
      return 100.0;
    }
    return percentRemaining;
  }

  public void setPercentRemaining(double percentRemaining) {
    this.percentRemaining = percentRemaining;
  }

  public boolean isUnlimited() {
    return unlimited;
  }

  public void setUnlimited(boolean unlimited) {
    this.unlimited = unlimited;
  }

  public boolean isOveragePermitted() {
    return overagePermitted;
  }

  public void setOveragePermitted(boolean overagePermitted) {
    this.overagePermitted = overagePermitted;
  }

  @Override
  public int hashCode() {
    return Objects.hash(overagePermitted, percentRemaining, unlimited);
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
    Quota other = (Quota) obj;
    return overagePermitted == other.overagePermitted
        && Double.doubleToLongBits(percentRemaining) == Double.doubleToLongBits(other.percentRemaining)
        && unlimited == other.unlimited;
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("percentRemaining", percentRemaining);
    builder.add("unlimited", unlimited);
    builder.add("overagePermitted", overagePermitted);
    return builder.toString();
  }
}
