/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.lsp.protocol.git;

import java.util.List;
import java.util.Objects;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * Parameters for generating a commit message.
 */
public class GenerateCommitMessageParams {

  @NonNull
  private List<String> changes;

  @NonNull
  private List<String> userCommits;

  @NonNull
  private List<String> recentCommits;

  private List<String> workspaceFolder;

  private String userLanguage;

  /**
   * Creates a new GenerateCommitMessageParams.
   */
  public GenerateCommitMessageParams(List<String> changes, List<String> userCommits, List<String> recentCommits) {
    super();
    this.changes = changes;
    this.userCommits = userCommits;
    this.recentCommits = recentCommits;
  }

  public List<String> getChanges() {
    return changes;
  }

  public void setChanges(List<String> changes) {
    this.changes = changes;
  }

  public List<String> getUserCommits() {
    return userCommits;
  }

  public void setUserCommits(List<String> userCommits) {
    this.userCommits = userCommits;
  }

  public List<String> getRecentCommits() {
    return recentCommits;
  }

  public void setRecentCommits(List<String> recentCommits) {
    this.recentCommits = recentCommits;
  }

  public List<String> getWorkspaceFolder() {
    return workspaceFolder;
  }

  public void setWorkspaceFolder(List<String> workspaceFolder) {
    this.workspaceFolder = workspaceFolder;
  }

  public String getUserLanguage() {
    return userLanguage;
  }

  public void setUserLanguage(String userLanguage) {
    this.userLanguage = userLanguage;
  }

  @Override
  public int hashCode() {
    return Objects.hash(changes, recentCommits, userCommits, userLanguage, workspaceFolder);
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
    GenerateCommitMessageParams other = (GenerateCommitMessageParams) obj;
    return Objects.equals(changes, other.changes) && Objects.equals(recentCommits, other.recentCommits)
        && Objects.equals(userCommits, other.userCommits) && Objects.equals(userLanguage, other.userLanguage)
        && Objects.equals(workspaceFolder, other.workspaceFolder);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("changes", changes);
    builder.add("userCommits", userCommits);
    builder.add("recentCommits", recentCommits);
    builder.add("workspaceFolder", workspaceFolder);
    builder.add("userLanguage", userLanguage);
    return builder.toString();
  }
}
