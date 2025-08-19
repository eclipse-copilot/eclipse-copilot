/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.lsp.protocol;

import java.util.Objects;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.util.ToStringBuilder;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/**
 * An item of a completion result list.
 */
public class CompletionItem {

  @NonNull
  private String uuid;

  @NonNull
  private String text;

  @NonNull
  private Range range;

  @NonNull
  private String displayText;

  @NonNull
  Position position;

  @NonNull
  private int docVersion;

  /**
   * Creates a new CompletionItem.
   */
  public CompletionItem(@NonNull String uuid, @NonNull String text, @NonNull Range range, @NonNull String displayText,
      @NonNull Position position, @NonNull int docVersion) {
    this.uuid = uuid;
    this.text = text;
    this.range = range;
    this.displayText = displayText;
    this.position = position;
    this.docVersion = docVersion;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Range getRange() {
    return range;
  }

  public void setRange(Range range) {
    this.range = range;
  }

  public String getDisplayText() {
    return displayText;
  }

  public void setDisplayText(String displayText) {
    this.displayText = displayText;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public int getDocVersion() {
    return docVersion;
  }

  public void setDocVersion(int docVersion) {
    this.docVersion = docVersion;
  }

  @Override
  public int hashCode() {
    return Objects.hash(displayText, docVersion, position, range, text, uuid);
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
    CompletionItem other = (CompletionItem) obj;
    return Objects.equals(displayText, other.displayText) && docVersion == other.docVersion
        && Objects.equals(position, other.position) && Objects.equals(range, other.range)
        && Objects.equals(text, other.text) && Objects.equals(uuid, other.uuid);
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this);
    builder.add("uuid", uuid);
    builder.add("text", text);
    builder.add("range", range);
    builder.add("displayText", displayText);
    builder.add("position", position);
    builder.add("docVersion", docVersion);
    return builder.toString();
  }
}
