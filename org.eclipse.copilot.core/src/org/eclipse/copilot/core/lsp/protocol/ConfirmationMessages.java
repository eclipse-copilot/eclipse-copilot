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
 * Confirmation message for the tool invocation.
 */
public class ConfirmationMessages {
  private String title;
  private String message;

  /**
   * Default constructor.
   */
  public ConfirmationMessages() {
  }

  /**
   * Construct a new ConfirmationMessages.
   *
   * @param title The title of the confirmation.
   * @param message The message of the confirmation.
   */
  public ConfirmationMessages(String title, String message) {
    this.title = title;
    this.message = message;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, title);
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
    ConfirmationMessages other = (ConfirmationMessages) obj;
    return Objects.equals(message, other.message) && Objects.equals(title, other.title);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("title", title);
    builder.add("message", message);
    return builder.toString();
  }

}
