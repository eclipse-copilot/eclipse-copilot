/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.api;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

/**
 * Factory for discovering and selecting terminal tool implementations using SPI.
 */
public class TerminalToolFactory {

  private static IRunInTerminalTool cachedInstance;

  // Eclipse version threshold - 4.36 and below use legacy implementation
  private static final Version LEGACY_VERSION_THRESHOLD = new Version(4, 37, 0);

  /**
   * Get the best available terminal tool implementation. Uses SPI to discover implementations and selects the one with
   * highest priority that is available in the current environment.
   *
   * @return The best available implementation, or null if none found
   */
  public static synchronized IRunInTerminalTool getInstance() {
    if (cachedInstance == null) {
      cachedInstance = discoverBestImplementation();
    }
    return cachedInstance;
  }

  /**
   * Force rediscovery of implementations (useful for testing or dynamic updates).
   */
  public static synchronized void refresh() {
    cachedInstance = null;
  }

  /**
   * Get all available implementations for debugging purposes.
   *
   * @return List of all discovered and available implementations
   */
  public static List<IRunInTerminalTool> getAllImplementations() {
    ServiceLoader<IRunInTerminalTool> loader = ServiceLoader.load(IRunInTerminalTool.class);
    List<IRunInTerminalTool> implementations = new ArrayList<>();

    for (IRunInTerminalTool impl : loader) {
      implementations.add(impl);
    }

    return implementations;
  }

  /**
   * Check if any terminal implementation is available.
   *
   * @return true if at least one implementation is available
   */
  public static boolean hasAvailableImplementation() {
    return getInstance() != null;
  }

  private static IRunInTerminalTool discoverBestImplementation() {
    ServiceLoader<IRunInTerminalTool> loader = ServiceLoader.load(IRunInTerminalTool.class);
    List<IRunInTerminalTool> availableImplementations = new ArrayList<>();

    // Collect all available implementations, testing each one
    for (IRunInTerminalTool impl : loader) {
      availableImplementations.add(impl);
    }

    if (availableImplementations.isEmpty()) {
      return null;
    }
    boolean useLegacy = shouldUseLegacyImplementation();

    IRunInTerminalTool filteredImplementation = null;
    for (IRunInTerminalTool impl : availableImplementations) {
      String className = impl.getClass().getName();
      boolean isLegacyImpl = className.contains("legacy");
      if ((useLegacy && isLegacyImpl) || (!useLegacy && !isLegacyImpl)) {
        filteredImplementation = impl;
        break;
      }
    }

    return filteredImplementation;
  }

  /**
   * Determine if we should use the legacy terminal implementation based on Eclipse version.
   *
   * @return true if Eclipse version is 4.36 or below, false otherwise
   */
  private static boolean shouldUseLegacyImplementation() {
    // Try to get the platform bundle version
    Bundle platformBundle = Platform.getBundle("org.eclipse.platform");
    if (platformBundle != null) {
      Version platformVersion = platformBundle.getVersion();
      return platformVersion.compareTo(LEGACY_VERSION_THRESHOLD) < 0;
    }
    return true;
  }
}
