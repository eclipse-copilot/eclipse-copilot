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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.FeatureFlags;
import org.eclipse.copilot.core.lsp.protocol.CopilotCapabilities;
import org.eclipse.copilot.core.lsp.protocol.InitializationOptions;
import org.eclipse.copilot.core.lsp.protocol.NameAndVersion;
import org.eclipse.copilot.core.utils.PlatformUtils;

/**
 * Stream connection provider for the Copilot language server.
 */
public class LsStreamConnectionProvider extends ProcessStreamConnectionProvider {

  public static final String EDITOR_NAME = "Eclipse";
  public static final String EDITOR_PLUGIN_NAME = "copilot-eclipse";

  @Override
  public Object getInitializationOptions(@Nullable URI rootUri) {
    NameAndVersion editorInfo = new NameAndVersion(EDITOR_NAME, PlatformUtils.getEclipseVersion());
    String bundleVersion = PlatformUtils.getBundleVersion();
    NameAndVersion editorPluginInfo = new NameAndVersion(EDITOR_PLUGIN_NAME, bundleVersion);
    CopilotCapabilities capabilities = new CopilotCapabilities(false, FeatureFlags.isWorkspaceContextEnabled());
    return new InitializationOptions(editorInfo, editorPluginInfo, capabilities);
  }

  @Override
  public void start() throws IOException {
    try {
      startBinaryLspAgent();
    } catch (Exception e) {
      startJsLspAgent(e);
    }
    CopilotCore.LOGGER.info("Lsp agent started successfully.");
  }

  @Override
  protected ProcessBuilder createProcessBuilder() {
    ProcessBuilder pb = super.createProcessBuilder();
    pb.environment().putAll(getLoginShellEnvironment());
    return pb;
  }

  private void startBinaryLspAgent() throws IOException {
    CopilotCore.LOGGER.info("Starting language server with binary lsp agent.");
    this.setCommands(getBinaryLspCommands());
    super.start();
  }

  private void startJsLspAgent(Exception e) throws IOException {
    CopilotCore.LOGGER.error("Binary LSP agent start failed. Retrying with JS agent.", e);
    this.setCommands(getJavaScriptCommands());
    super.start();
  }

  private List<String> getBinaryLspCommands() throws IOException {
    Path binary = findAndValidateBinary();
    return buildCommands(binary.toString());
  }

  private Path findAndValidateBinary() throws IOException {

    Path binary = findBinary();

    if (binary == null) {
      throw new IOException("Could not find the language server binary");
    }
    // call normalize to remove any relative path components and avoid "FILE_PATH_TOO_LONG" error
    binary = binary.normalize();

    File executable = binary.toFile();
    if (!executable.canExecute() && !executable.setExecutable(true)) {
      throw new IOException("Could not make the language server binary executable");
    }

    return binary;
  }

  private List<String> getJavaScriptCommands() throws IOException {
    try {
      String nodePath = findNodeAbsolutePath();
      String jsLspPath = findJavaScriptLanguageServerPath();

      if (nodePath == null) {
        throw new IOException("Node path not found");
      }
      if (jsLspPath == null) {
        throw new IOException("JavaScript lsp path not found");
      }

      return buildCommands(nodePath, jsLspPath);
    } catch (Exception e) {
      CopilotCore.LOGGER.error("Failed to get JavaScript commands. ", e);
      throw e;
    }
    // TODO: In the future, if users have environment variables set up that impact the js server startup, we should
    // clear the related environment variables here. Reference:
    // https://github.com/microsoft/copilot-intellij/blob/df3fa9e82ddee36342c50b310be321d552238a30/core/src/main/java/com/github/copilot/lang/agent/CopilotAgentCommandLine.java#L45C4-L54C6
  }

  private List<String> buildCommands(String... commandParts) {
    List<String> commands = new ArrayList<>(Arrays.asList(commandParts));
    commands.add("--stdio");
    enforceUtf8Charset(commands);
    return commands;
  }

  /**
   * Enforce UTF-8 charset for the LSP agent commands.
   */
  private void enforceUtf8Charset(List<String> commands) {
    commands.replaceAll(command -> new String(command.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
  }

  private @Nullable String findNodeAbsolutePath() throws IOException {
    try {
      // The 'wildwebdeveloper' bundle is optional for Eclipse. Ensure it is available before attempting to use it.
      Class.forName("org.eclipse.wildwebdeveloper.embedder.node.NodeJSManager");
    } catch (ClassNotFoundException | NoClassDefFoundError e) {
      CopilotCore.LOGGER
          .info("Get JavaScript commands aborted. org.eclipse.wildwebdeveloper.embedder.node.NodeJSManager not found.");
      return null;
    }
    File nodeJsLocation = NodeJsManager.getNodeJsLocation();
    if (nodeJsLocation == null) {
      throw new IOException("Failed to find Node.js path");
    }
    return nodeJsLocation.getAbsolutePath();
  }

  private @Nullable String findJavaScriptLanguageServerPath() throws IOException {
    Path distPath = findAgentDistDirectoryPath();

    if (distPath == null) {
      throw new IOException("Unable to locate dist dir for js language server");
    }

    Path jsFilePath = distPath.resolve("language-server.js");
    if (!Files.exists(jsFilePath)) {
      throw new IOException("Unable to locate language-server.js file");
    }

    return jsFilePath.toString();
  }

  private @Nullable Path findAgentDistDirectoryPath() {
    URL url = CopilotCore.getPlugin().getBundle().getEntry("copilot-agent/dist");
    if (url == null) {
      return null;
    }

    try {
      return URIUtil.toFile(URIUtil.toURI(FileLocator.toFileURL(url))).toPath();
    } catch (URISyntaxException | IOException e) {
      CopilotCore.LOGGER.error(e);
      return null;
    }
  }

  private @Nullable Path findBinary() throws IOException {
    // the binary is only supported on macOS and Intel x86_64 with Linux and Windows
    if (!PlatformUtils.isMac() && !PlatformUtils.isIntel64()) {
      CopilotCore.LOGGER.error(
          new IllegalStateException("Unsupport platform for CLS: " + Platform.getOS() + ", " + Platform.getOSArch()));
      return null;
    }

    Path binDir = findAgentBinaryDirectoryPath();
    if (binDir == null) {
      return null;
    }

    Path executable = null;
    if (PlatformUtils.isLinux()) {
      executable = binDir.resolve("linux-x64/copilot-language-server");
    } else if (PlatformUtils.isWindows()) {
      executable = binDir.resolve("win32-x64/copilot-language-server.exe");
    } else if (PlatformUtils.isMac()) {
      if (PlatformUtils.isArm64()) {
        executable = binDir.resolve("darwin-arm64/copilot-language-server");
      } else {
        executable = binDir.resolve("darwin-x64/copilot-language-server");
      }
    }

    return executable != null && Files.exists(executable) ? executable : null;
  }

  private @Nullable Path findAgentBinaryDirectoryPath() throws IOException {
    URL url = CopilotCore.getPlugin().getBundle().getEntry("copilot-agent/native");
    if (url == null) {
      return null;
    }

    try {
      return URIUtil.toFile(URIUtil.toURI(FileLocator.toFileURL(url))).toPath();
    } catch (URISyntaxException | IOException e) {
      CopilotCore.LOGGER.error(e);
      return null;
    }
  }

  /**
   * Get the login shell environment variables on MacOS. Otherwise, some of the mcp server cannot be started due to
   * missing environment variables.
   */
  private Map<String, String> getLoginShellEnvironment() {
    Map<String, String> env = new HashMap<>();
    if (!PlatformUtils.isMac()) {
      return env;
    }

    Process process = null;
    try {
      // Execute login shell and get environment variables
      ProcessBuilder pb = new ProcessBuilder("/bin/zsh", "-i", "-l", "-c", "env");
      process = pb.start();

      env = getEnvironmentVariables(process);

      // Check if process completed successfully
      if (process.waitFor(1, TimeUnit.SECONDS)) {
        int exitCode = process.exitValue();
        if (exitCode != 0) {
          CopilotCore.LOGGER.error(
              new IllegalStateException("Failed to get login shell environment variables. Exit code: " + exitCode));
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      CopilotCore.LOGGER.error("Interrupted while getting login shell environment variables", e);
    } catch (IOException e) {
      CopilotCore.LOGGER.error("IOException while getting login shell environment variables", e);
    } finally {
      if (process != null && process.isAlive()) {
        process.destroy();
      }
    }

    return env;
  }

  private Map<String, String> getEnvironmentVariables(Process process) throws InterruptedException {
    // Create a separate thread to read the process output with a timeout, this avoids blocking the original thread
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Map<String, String>> future = executor.submit(() -> {
      Map<String, String> result = new HashMap<>();
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
        String line;
        while ((line = reader.readLine()) != null) {
          int separator = line.indexOf('=');
          if (separator > 0) {
            String key = line.substring(0, separator);
            String value = line.substring(separator + 1);
            result.put(key, value);
          }
        }
      }
      return result;
    });

    try {
      return future.get(5, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      CopilotCore.LOGGER.error("Timed out waiting for login shell environment variables", e);
      future.cancel(true);
    } catch (ExecutionException e) {
      CopilotCore.LOGGER.error("Error reading login shell environment variables", e);
    } finally {
      executor.shutdownNow();
    }
    return new HashMap<>();
  }
}
