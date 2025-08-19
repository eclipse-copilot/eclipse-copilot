/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.lsp4e.LanguageServerWrapper;
import org.eclipse.lsp4e.LanguageServersRegistry;
import org.eclipse.lsp4e.LanguageServiceAccessor;
import org.osgi.framework.BundleContext;

import org.eclipse.copilot.core.chat.ChatEventsManager;
import org.eclipse.copilot.core.chat.service.IChatServiceManager;
import org.eclipse.copilot.core.completion.CompletionProvider;
import org.eclipse.copilot.core.format.FormatOptionProvider;
import org.eclipse.copilot.core.logger.CopilotForEclipseLogger;
import org.eclipse.copilot.core.logger.GithubPanicErrorReport;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;

/**
 * The plug-in runtime class for the Copilot plug-in containing the core (UI-free) support, like the completion,
 * authentication, language server connection, etc.
 */
public class CopilotCore extends Plugin {

  private CopilotLanguageServerConnection copilotLanguageServer;
  private AuthStatusManager authStatusManager;
  private CompletionProvider completionProvider;
  private FormatOptionProvider formatOptionProvider;
  private GithubPanicErrorReport githubPanicErrorReport;
  private ChatEventsManager chatEventsManager;
  private IChatServiceManager chatServiceManager;
  private FeatureFlags featureFlags;

  private static CopilotCore COPILOT_CORE_PLUGIN = null;
  public static final CopilotForEclipseLogger LOGGER = new CopilotForEclipseLogger(CopilotCore.class.getName());

  /**
   * The job family for the initialization job.
   */
  public static final String INIT_JOB_FAMILY = "org.eclipse.copilot.core.initJob";

  // TODO: Remove these 2 constants after several releases since the migration will be completed in the next.
  private static final String LEGACY_PREF_NODE_NAME = "com.microsoft.copilot.eclipse.ui";
  private static final String HAS_MIGRATED_PREF_FROM_LEGACY_PLUGIN = "hasMigratedPrefFromLegacyPlugin";

  /**
   * Creates the Copilot core plugin. The plugin is created automatically by the Eclipse framework. Clients must not
   * call this constructor.
   */
  public CopilotCore() {
    super();
    COPILOT_CORE_PLUGIN = this;
  }

  public static CopilotCore getPlugin() {
    return COPILOT_CORE_PLUGIN;
  }

  @Override
  public void start(BundleContext context) throws Exception {
    migratePrefFromLegacyPlugin();
    init(context);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    if (copilotLanguageServer != null) {
      copilotLanguageServer.stop();
    }
  }

  @SuppressWarnings("restriction")
  void init(BundleContext context) {
    final Runnable initRunnable = () -> {
      addPlatformLogListener();
      LanguageServersRegistry.LanguageServerDefinition serverDef = LanguageServersRegistry.getInstance()
          .getDefinition(CopilotLanguageServerConnection.SERVER_ID);
      if (serverDef == null) {
        var ex = new IllegalStateException(
            "Language server definition not found for " + CopilotLanguageServerConnection.SERVER_ID);
        CopilotCore.LOGGER.error(ex);
        throw ex;
      }

      LanguageServerWrapper wrapper = LanguageServiceAccessor.startLanguageServer(serverDef);
      this.copilotLanguageServer = new CopilotLanguageServerConnection(wrapper);
      this.authStatusManager = new AuthStatusManager(this.copilotLanguageServer);
      this.completionProvider = new CompletionProvider(this.copilotLanguageServer, authStatusManager);
      this.githubPanicErrorReport = new GithubPanicErrorReport();
      this.featureFlags = new FeatureFlags();
      try {
        this.authStatusManager.checkStatus().get();
      } catch (InterruptedException | ExecutionException e) {
        CopilotCore.LOGGER.error("Failed to check authentication status", e);
      }
    };

    Job initJob = new Job("GitHub Copilot Initialization...") {
      protected IStatus run(IProgressMonitor monitor) {
        initRunnable.run();
        return Status.OK_STATUS;
      }

      @Override
      public boolean belongsTo(Object family) {
        return Objects.equals(INIT_JOB_FAMILY, family);
      }
    };
    initJob.setUser(false);
    initJob.schedule();
  }

  /**
   * Add platform level log listener to catch the uncaught exceptions.
   */
  private void addPlatformLogListener() {
    Platform.addLogListener((status, plugin) -> {
      if (status.getSeverity() != IStatus.ERROR || plugin.equals(Constants.PLUGIN_ID)) {
        // only send telemetry for those errors that are not from the plugin itself
        return;
      }
      Throwable rawException = status.getException();
      if (rawException == null) {
        return;
      }
      Throwable currentException = rawException;
      do {
        StackTraceElement[] traces = currentException.getStackTrace();
        for (StackTraceElement trace : traces) {
          if (!trace.getClassName().startsWith(Constants.PLUGIN_ID)) {
            continue;
          }
          reportException(rawException);
          return;
        }
      } while ((currentException = currentException.getCause()) != null);
    });
  }

  public CopilotLanguageServerConnection getCopilotLanguageServer() {
    return copilotLanguageServer;
  }

  public AuthStatusManager getAuthStatusManager() {
    return authStatusManager;
  }

  public CompletionProvider getCompletionProvider() {
    return completionProvider;
  }

  public GithubPanicErrorReport getGithubPanicErrorReport() {
    return githubPanicErrorReport;
  }

  public FeatureFlags getFeatureFlags() {
    return featureFlags;
  }

  /**
   * Get the format option provider in lazy-load manner.
   */
  public FormatOptionProvider getFormatOptionProvider() {
    if (this.formatOptionProvider == null) {
      this.formatOptionProvider = new FormatOptionProvider();
    }
    return formatOptionProvider;
  }

  /**
   * Report the exception to the telemetry.
   *
   * @param ex the exception to report
   */
  public void reportException(Throwable ex) {
    if (this.copilotLanguageServer != null) {
      this.copilotLanguageServer.sendExceptionTelemetry(ex);
    } else {
      if (this.githubPanicErrorReport != null) {
        this.githubPanicErrorReport.report(ex);
      }
    }
  }

  /**
   * Get the chat provider.
   *
   * @return the chat provider.
   */
  public ChatEventsManager getChatEventsManager() {
    if (chatEventsManager == null) {
      chatEventsManager = new ChatEventsManager();
    }
    return chatEventsManager;
  }

  @Nullable
  public IChatServiceManager getChatServiceManager() {
    return chatServiceManager;
  }

  public void setChatServiceManager(IChatServiceManager chatServiceManager) {
    this.chatServiceManager = chatServiceManager;
  }

  /**
   * TODO: Remove this method after several releases since the migration will be completed in next release. This method
   * is called when the plugin is loaded to ensure that any preferences stored under the old bundle name are migrated to
   * the new one.
   */
  private void migratePrefFromLegacyPlugin() {
    IEclipsePreferences newPrefs = InstanceScope.INSTANCE.getNode("org.eclipse.copilot.ui");
    if (newPrefs == null || newPrefs.getBoolean(HAS_MIGRATED_PREF_FROM_LEGACY_PLUGIN, false)) {
      return;
    }

    IEclipsePreferences oldPrefs = InstanceScope.INSTANCE.getNode(LEGACY_PREF_NODE_NAME);
    if (oldPrefs == null) {
      return;
    }

    try {
      String[] keys = oldPrefs.keys();
      if (keys.length > 0) {
        for (String key : keys) {
          String value = oldPrefs.get(key, null);
          if (value != null) {
            newPrefs.put(key, value);
          }
        }

        CopilotCore.LOGGER.info("Migrated " + keys.length + " preferences from previous bundle name");
      }

      // Set migration flag
      newPrefs.putBoolean(HAS_MIGRATED_PREF_FROM_LEGACY_PLUGIN, true);
      newPrefs.flush();
    } catch (Exception e) {
      CopilotCore.LOGGER.error("Failed to migrate preferences", e);
    }
  }
}
