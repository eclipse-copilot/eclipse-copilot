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
 * Enum representing the chat mode.
 */
// TODO: now there is some issue with the enum type adapter, so we need to call .toString() or .valueOf() for conversion
public enum ChatMode {

  /**
   * Normal chat mode.
   */
  Ask {
    @Override
    public String displayName() {
      return "Ask";
    }
  },

  /**
   * Agent mode.
   */
  Agent {
    @Override
    public String displayName() {
      return "Agent";
    }
  };

  /**
   * Returns a human-readable display name for the chat mode.
   *
   * @return The display name for this chat mode.
   */
  public abstract String displayName();
}
