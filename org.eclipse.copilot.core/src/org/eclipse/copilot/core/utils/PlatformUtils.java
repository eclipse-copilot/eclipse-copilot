/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.osgi.framework.Bundle;

import org.eclipse.copilot.core.CopilotCore;

/**
 * Utility class for platform related operations.
 */
public class PlatformUtils {

  public static final String EC_PLATFORM_BUNDLE_NAME = "org.eclipse.platform";

  private PlatformUtils() {
  }

  /**
   * Get the version of the Eclipse platform.
   */
  public static String getEclipseVersion() {
    Bundle bundle = Platform.getBundle(EC_PLATFORM_BUNDLE_NAME);
    if (bundle == null) {
      return "unknown";
    }
    return bundle.getVersion().toString();
  }

  /**
   * Get the version of the Copilot plugin.
   */
  public static String getBundleVersion() {
    Bundle bundle = CopilotCore.getPlugin().getBundle();
    return bundle == null ? "unknown" : bundle.getVersion().toString();
  }

  /**
   * Read the content of a file.
   */
  public static String readFileContent(Path path) {
    String content = "";
    try {
      if (Files.exists(path) && Files.isReadable(path)) {
        content = Files.readString(path);
      }
      return content;
    } catch (IOException e) {
      CopilotCore.LOGGER.error("File not found: " + path, e);
    }
    return "";
  }

  /**
   * Write the content to a file.
   */
  public static void writeFileContent(@NonNull Path path, String content) {
    try {
      if (Files.notExists(path)) {
        Files.createDirectories(path.getParent());
      }
      Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      CopilotCore.LOGGER.error("Failed to write to file: " + path, e);
    }
  }

  /**
   * Escapes spaces in a URL string.
   */
  public static String escapeSpaceInUrl(String urlString) {
    char[] chars = urlString.toCharArray();
    StringBuffer sb = new StringBuffer(chars.length);
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] == ' ') {
        sb.append("%20");
      } else {
        sb.append(chars[i]);
      }
    }
    return sb.toString();
  }

  public static boolean isMac() {
    return Platform.getOS().equals(Platform.OS_MACOSX);
  }

  public static boolean isLinux() {
    return Platform.getOS().equals(Platform.OS_LINUX);
  }

  public static boolean isWindows() {
    return Platform.getOS().equals(Platform.OS_WIN32);
  }

  public static boolean isIntel64() {
    return Platform.getOSArch().equals(Platform.ARCH_X86_64);
  }

  public static boolean isArm64() {
    return Platform.getOSArch().equals(Platform.ARCH_AARCH64);
  }

  /**
   * get the property value of the object with reflection.
   */
  public static Object getPropertyWithReflection(Object object, String propertyName) {
    if (object == null) {
      return null;
    }
    Field[] fields = object.getClass().getDeclaredFields();
    for (Field field : fields) {
      if (field.getName().equals(propertyName)) {
        field.setAccessible(true);
        try {
          return field.get(object);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          return null;
        }
      }
    }
    return null;
  }

  /**
   * Return the workspace root URI string.
   */
  public static String getWorkspaceRootUri() {
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    URI uri = LSPEclipseUtils.toUri((IResource) workspaceRoot);
    return uri != null ? uri.toASCIIString() : "";
  }

}
