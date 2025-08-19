/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.lsp.protocol;

import java.util.List;
import java.util.Objects;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * Capabilities for the conversation.
 */
public class ConversationCapabilities {

  public static final String CURRENT_EDITOR_SKILL = "current-editor";

  private boolean allSkills;
  private List<String> skills;

  public boolean isAllSkills() {
    return allSkills;
  }

  public void setAllSkills(boolean allSkills) {
    this.allSkills = allSkills;
  }

  public List<String> getSkills() {
    return skills;
  }

  public void setSkills(List<String> skills) {
    this.skills = skills;
  }

  @Override
  public int hashCode() {
    return Objects.hash(allSkills, skills);
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
    ConversationCapabilities other = (ConversationCapabilities) obj;
    return allSkills == other.allSkills && Objects.equals(skills, other.skills);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("allSkills", allSkills);
    builder.add("skills", skills);
    return builder.toString();
  }

}
