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
 * Request to get the list of watched files. See:
 * https://github.com/microsoft/copilot-client/blob/main/agent/API_INTERNAL.md#copilotwatchedfiles
 */
public class GetWatchedFilesRequest {

  private String workspaceUri;

  private boolean excludeGitignoredFiles;

  private boolean excludeIdeIgnoredFiles;

  public String getWorkspaceUri() {
    return workspaceUri;
  }

  public void setWorkspaceUri(String uri) {
    this.workspaceUri = uri;
  }

  public boolean isExcludeGitignoredFiles() {
    return excludeGitignoredFiles;
  }

  public void setExcludeGitignoredFiles(boolean excludeGitignoredFiles) {
    this.excludeGitignoredFiles = excludeGitignoredFiles;
  }

  public boolean isExcludeIdeIgnoredFiles() {
    return excludeIdeIgnoredFiles;
  }

  public void setExcludeIdeIgnoredFiles(boolean excludeIdeIgnoredFiles) {
    this.excludeIdeIgnoredFiles = excludeIdeIgnoredFiles;
  }

  @Override
  public int hashCode() {
    return Objects.hash(excludeGitignoredFiles, excludeIdeIgnoredFiles, workspaceUri);
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
    GetWatchedFilesRequest other = (GetWatchedFilesRequest) obj;
    return excludeGitignoredFiles == other.excludeGitignoredFiles
        && excludeIdeIgnoredFiles == other.excludeIdeIgnoredFiles && Objects.equals(workspaceUri, other.workspaceUri);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("uri", workspaceUri);
    builder.add("excludeGitignoredFiles", excludeGitignoredFiles);
    builder.add("excludeIdeIgnoredFiles", excludeIdeIgnoredFiles);
    return builder.toString();
  }

}
