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

package org.eclipse.copilot.core.lsp;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageServer;

import org.eclipse.copilot.core.lsp.protocol.ChatProgressParamsAdapter;
import org.eclipse.copilot.core.lsp.protocol.ChatReferenceTypeAdapter;

/**
 * Builder for Copilot Language Server.
 */
public class CopilotLauncherBuilder<T extends LanguageServer> extends Launcher.Builder<T> {

  /**
   * Create a new CopilotLauncherBuilder.
   */
  public CopilotLauncherBuilder() {
    this.configureGson(gsonBuilder -> gsonBuilder.registerTypeAdapterFactory(new ChatProgressParamsAdapter.Factory())
        .registerTypeAdapterFactory(new ChatReferenceTypeAdapter.Factory()));
  }

}
