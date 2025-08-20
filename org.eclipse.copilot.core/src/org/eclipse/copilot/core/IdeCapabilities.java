/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * Class to check the IDE capabilities.
 */
public class IdeCapabilities {
  private IdeCapabilities() {
    // Prevent instantiation
  }

  /**
   * Checks if code mining capability is available based on Eclipse version.
   *
   * @return true if Eclipse version is 2024.12(4.34) or newer, false otherwise
   */
  public static boolean canUseCodeMining() {
    Bundle platformBundle = Platform.getBundle("org.eclipse.platform");
    if (platformBundle == null) {
      // In test environments, the bundle might not be available
      return false;
    }
    Version currentVersion = platformBundle.getVersion();
    Version requiredVersion = new Version(4, 34, 0);
    return currentVersion.compareTo(requiredVersion) >= 0;
  }
}
