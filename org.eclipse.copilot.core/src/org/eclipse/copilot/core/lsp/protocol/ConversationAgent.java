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
 * Agents are used to provide additional functionality to the conversation, e.g. resolving the whole project context.
 * Agents can be used by sending a message starting with @name
 */
public class ConversationAgent {

  private String slug;

  private String name;

  private String description;

  private String avatarUrl;

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

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

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  @Override
  public int hashCode() {
    return Objects.hash(avatarUrl, description, name, slug);
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
    ConversationAgent other = (ConversationAgent) obj;
    return Objects.equals(avatarUrl, other.avatarUrl) && Objects.equals(description, other.description)
        && Objects.equals(name, other.name) && Objects.equals(slug, other.slug);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("slug", slug);
    builder.add("name", name);
    builder.add("description", description);
    builder.add("avatarUrl", avatarUrl);
    return builder.toString();
  }
}
