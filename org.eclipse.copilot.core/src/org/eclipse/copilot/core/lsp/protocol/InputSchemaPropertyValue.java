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
 * Property value for input schema.
 */
public class InputSchemaPropertyValue {
  private String type;
  private String description;
  private InputSchemaPropertyValue items;

  /**
   * Constructor for InputSchemaPropertyValue.
   */
  public InputSchemaPropertyValue(String type) {
    this(type, "");
  }

  /**
   * Constructor for InputSchemaPropertyValue.
   */
  public InputSchemaPropertyValue(String type, String description) {
    this.type = type;
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public InputSchemaPropertyValue getItems() {
    return items;
  }

  public void setItems(InputSchemaPropertyValue items) {
    this.items = items;
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, items, type);
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
    InputSchemaPropertyValue other = (InputSchemaPropertyValue) obj;
    return Objects.equals(description, other.description) && Objects.equals(items, other.items)
        && Objects.equals(type, other.type);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("type", type);
    builder.add("description", description);
    builder.add("items", items);
    return builder.toString();
  }
}
