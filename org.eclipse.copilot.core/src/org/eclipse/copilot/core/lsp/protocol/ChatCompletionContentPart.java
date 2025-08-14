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

/**
 * Parameters for create Turn message with texts and images.
 */
public interface ChatCompletionContentPart {

  /**
   * Get the type of the content.
   *
   * @return the content as a String
   */
  String getType();

  /**
   * Enum representing the type of content that can be sent in a chat completion message.
   */
  public enum ContentType {
    TEXT("text"), IMAGE_URL("image_url");

    private final String value;

    ContentType(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }
}
