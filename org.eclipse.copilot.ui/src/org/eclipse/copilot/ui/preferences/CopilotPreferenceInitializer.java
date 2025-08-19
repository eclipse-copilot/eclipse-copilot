/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.copilot.core.Constants;
import org.eclipse.copilot.ui.CopilotUi;

/**
 * A class to initialize the default preferences for the plugin.
 */
public class CopilotPreferenceInitializer extends AbstractPreferenceInitializer {

  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore pref = CopilotUi.getPlugin().getPreferenceStore();
    pref.setDefault(Constants.AUTO_SHOW_COMPLETION, true);
    pref.setDefault(Constants.ENABLE_STRICT_SSL, true);
    pref.setDefault(Constants.PROXY_KERBEROS_SP, "");
    pref.setDefault(Constants.GITHUB_ENTERPRISE, "");
    pref.setDefault(Constants.WORKSPACE_CONTEXT_ENABLED, false);
    pref.setDefault(Constants.CUSTOM_INSTRUCTIONS_WORKSPACE_ENABLED, false);
    pref.setDefault(Constants.CUSTOM_INSTRUCTIONS_WORKSPACE, "");
    pref.setDefault(Constants.MCP, """
        {
          "servers": {
            // example 1: local mcp server
            // "local-mcp-server": {
            //   "type": "stdio",
            //   "command": "my-command",
            //   "args": [],
            //   "env": { "<KEY>": "<VALUE>" }
            // }
            // example 2: remote mcp server
            // "remote-mcp-server": {
            //   "url": "<URL>",
            //   "requestInit": {
            //     "headers": {
            //       "Authorization": "Bearer <TOKEN>"
            //     }
            //   }
            // }
          }
        }
         """);
    pref.setDefault(Constants.MCP_TOOLS_STATUS, "{}");
    pref.setDefault(Constants.QUICK_START_VERSION, 0);
  }
}
