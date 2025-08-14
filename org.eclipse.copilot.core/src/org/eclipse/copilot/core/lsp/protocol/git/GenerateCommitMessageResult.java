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

package org.eclipse.copilot.core.lsp.protocol.git;

import java.util.Objects;

import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;

/**
 * Result of the 'git/generateCommitMessage' request.
 */
public class GenerateCommitMessageResult {

  private String commitMessage;

  /**
   * Creates a new GenerateCommitMessageResult.
   */
  public GenerateCommitMessageResult(String commitMessage) {
    this.commitMessage = commitMessage;
  }

  public String getCommitMessage() {
    return commitMessage;
  }

  public void setCommitMessage(String commitMessage) {
    this.commitMessage = commitMessage;
  }

  @Override
  public int hashCode() {
    return Objects.hash(commitMessage);
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
    GenerateCommitMessageResult other = (GenerateCommitMessageResult) obj;
    return Objects.equals(commitMessage, other.commitMessage);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("commitMessage", commitMessage);
    return builder.toString();
  }

}
