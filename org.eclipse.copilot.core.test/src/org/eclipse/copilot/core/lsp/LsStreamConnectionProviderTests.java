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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import org.eclipse.copilot.core.lsp.protocol.InitializationOptions;

class LsStreamConnectionProviderTests {

  @Test
  void testInitializationOptions() {
    LsStreamConnectionProvider provider = new LsStreamConnectionProvider();

    InitializationOptions options = (InitializationOptions) provider.getInitializationOptions(null);

    assertEquals(LsStreamConnectionProvider.EDITOR_NAME, options.getEditorInfo().getName());
    assertEquals(LsStreamConnectionProvider.EDITOR_PLUGIN_NAME, options.getEditorPluginInfo().getName());
  }

  @Test
  void testStartLanguageServer() throws IOException {
    LsStreamConnectionProvider provider = new LsStreamConnectionProvider();
    try {
      provider.start();
    } finally {
      provider.stop();
    }
  }
}
