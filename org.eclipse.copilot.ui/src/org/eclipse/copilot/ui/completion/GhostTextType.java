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

package org.eclipse.copilot.ui.completion;

/**
 * Type of ghost text.
 */
public enum GhostTextType {
  /**
   * Single line of ghost text placed in the line (not at the end).
   */
  IN_LINE,

  /**
   * Single line of ghost text placed at the end of the line.
   */
  END_OF_LINE,

  /**
   * Block of ghost text with multiple lines placed below the first line of the ghost text.
   */
  BLOCK_LINE
}
