/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.logger;

/**
 * The event type enum.
 */
public enum LogLevel {
  INFO("INFO"), WARNING("WARNING"), ERROR("ERROR");

  private String value;

  LogLevel(String string) {
    this.value = string;
  }

  public String getValue() {
    return this.value;
  }
}
