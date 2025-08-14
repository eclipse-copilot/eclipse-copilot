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
 * Creates a new ChatStepTitles.
 */
public class ChatStepTitles {
  // code ref
  // https://github.com/microsoft/copilot-client/blob/f6ba69740c28548515923a572a6f6908bd54e73f/lib/src/conversation/turnProcessor.ts#L52
  public static final String GENERATE_RESPONSE = "generate-response";
  public static final String COLLECT_CONTEXT = "collect-context";
}
