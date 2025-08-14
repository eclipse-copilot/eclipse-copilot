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

import org.eclipse.lsp4j.jsonrpc.util.Preconditions;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * A name and version pair.
 */
public class NameAndVersion {
  @NonNull
  private String name;

  @NonNull
  private String version;

  /**
   * Creates a new NameAndVersion.
   *
   * @param name the name.
   * @param version the version.
   */
  public NameAndVersion(@NonNull String name, @NonNull String version) {
    this.name = Preconditions.<String>checkNotNull(name, "name");
    this.version = Preconditions.<String>checkNotNull(version, "version");
  }

  @NonNull
  public String getName() {
    return name;
  }

  public void setName(@NonNull String name) {
    this.name = name;
  }

  @NonNull
  public String getVersion() {
    return version;
  }

  public void setVersion(@NonNull String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("name", name);
    b.add("version", version);
    return b.toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version);
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
    NameAndVersion other = (NameAndVersion) obj;
    return Objects.equals(name, other.name) && Objects.equals(version, other.version);
  }
}
