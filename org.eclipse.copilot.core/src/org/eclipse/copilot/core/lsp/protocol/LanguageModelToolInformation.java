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
 * This class is used to provide information about a tool that can be used by a language model. It includes the name of
 * the tool, a description of the tool, and a JSON schema for the input that the tool accepts.
 */
public class LanguageModelToolInformation {
  /**
   * The name of the tool.
   */
  private String name;

  /**
   * A description of this tool that may be used by a language model to select it.
   */
  private String description;

  /**
   * A JSON schema for the input this tool accepts. The input must be an object at the top level. A particular language
   * model may not support all JSON schema features. See the documentation for the language model family you are using
   * for more information.
   */
  private InputSchema inputSchema;

  /**
   * A message that should be shown to the user when the tool is invoked.
   */
  private ConfirmationMessages confirmationMessages;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public InputSchema getInputSchema() {
    return inputSchema;
  }

  public void setInputSchema(InputSchema inputSchema) {
    this.inputSchema = inputSchema;
  }

  public ConfirmationMessages getConfirmationMessages() {
    return confirmationMessages;
  }

  public void setConfirmationMessages(ConfirmationMessages confirmationMessages) {
    this.confirmationMessages = confirmationMessages;
  }

  @Override
  public int hashCode() {
    return Objects.hash(confirmationMessages, description, inputSchema, name);
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
    LanguageModelToolInformation other = (LanguageModelToolInformation) obj;
    return Objects.equals(confirmationMessages, other.confirmationMessages)
        && Objects.equals(description, other.description) && Objects.equals(inputSchema, other.inputSchema)
        && Objects.equals(name, other.name);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("name", name);
    builder.add("description", description);
    builder.add("inputSchema", inputSchema);
    builder.add("confirmationMessages", confirmationMessages);
    return builder.toString();
  }
}
