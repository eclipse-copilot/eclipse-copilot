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

package org.eclipse.copilot.core.completion;

import java.util.List;

import org.eclipse.copilot.core.lsp.protocol.CompletionItem;

/**
 * Listener for completion resolution.
 */
public interface CompletionListener {

  /**
   * Notifies to the listeners when the completion is resolved.
   */
  void onCompletionResolved(String uriString, List<CompletionItem> completions);

}
