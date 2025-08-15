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

package org.eclipse.copilot.core;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * Class to manage feature flags for the Copilot plugin.
 * This class allows enabling or disabling features.
 */
public class FeatureFlags {
  private boolean agentModeEnabled = true;

  private boolean mcpEnabled = true;

  public boolean isAgentModeEnabled() {
    return agentModeEnabled;
  }

  public void setAgentModeEnabled(boolean agentModeEnabled) {
    this.agentModeEnabled = agentModeEnabled;
  }

  public boolean isMcpEnabled() {
    return mcpEnabled;
  }
  
  public void setMcpEnabled(boolean mcpEnabled) {
    this.mcpEnabled = mcpEnabled;
  }
  
  /**
   * Checks if the workspace context is enabled.
   *
   * @return true if the workspace context is enabled, false otherwise.
   */
  public static boolean isWorkspaceContextEnabled() {
    // Directly access the instance scope of Eclipse preferences, which are preferences that are specific to the
    // current workspace. So the code won't need to involve any component from the UI plugin.
    // The file name for the preferences is "org.eclipse.copilot.ui.prefs"
    IEclipsePreferences uiPrefs = InstanceScope.INSTANCE.getNode("org.eclipse.copilot.ui");
    if (uiPrefs != null) {
      return uiPrefs.getBoolean(Constants.WORKSPACE_CONTEXT_ENABLED, false);
    }

    return false;
  }
}
