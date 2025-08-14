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

package org.eclipse.copilot.ui.chat.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.core.databinding.observable.sideeffect.ISideEffect;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.EventHandler;

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.chat.service.IMcpConfigService;
import org.eclipse.copilot.core.events.CopilotEventConstants;
import org.eclipse.copilot.core.lsp.protocol.DidChangeFeatureFlagsParams;
import org.eclipse.copilot.core.lsp.protocol.McpOauthRequest;
import org.eclipse.copilot.core.lsp.protocol.McpServerToolsCollection;
import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.i18n.Messages;
import org.eclipse.copilot.ui.preferences.McpPreferencePage;

/**
 * This class is responsible for handling the MCP config service.
 */
public class McpConfigService extends ChatBaseService implements IMcpConfigService {
  // MCP tools
  private IObservableValue<List<McpServerToolsCollection>> mcpToolsObservableValue;
  private ISideEffect mcpToolsSideEffect;
  private EventHandler mcpToolNotifiedEventHandler;
  private boolean mcpToolsInitialized = false;

  // MCP feature flag
  private EventHandler featureFlagNotifiedEventHandler;
  private IObservableValue<Boolean> mcpEnabledObservableValue;
  private ISideEffect mcpPreferenceSideEffect;
  private ISideEffect mcpToolButtonSideEffect;

  private IEventBroker eventBroker;

  /**
   * Constructor for the McpConfigService.
   */
  public McpConfigService() {
    super(null, null);

    eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
    if (eventBroker == null) {
      CopilotCore.LOGGER.error(new IllegalStateException("Event broker is null"));
      return;
    }

    initializeMcpToolUpdateEvent();
    initializeMcpFeatureFlagUpdateEvent();
  }

  private void initializeMcpToolUpdateEvent() {
    // User may open the mcp preference page before the event comes.
    // Initialization of the ObservableValue is needed, or "bind" will fail by null pointer.
    ensureRealm(() -> mcpToolsObservableValue = new WritableValue<>(new ArrayList<>(), List.class));

    mcpToolNotifiedEventHandler = event -> {
      // On IDE startup: Initialize MCP tools status after MCP servers start.
      // This event is always received because the event broker is set up before the setting manager syncs MCP servers
      // to language server.
      if (!mcpToolsInitialized) {
        CopilotUi.getPlugin().getLanguageServerSettingManager().initializeMcpToolsStatus();
        mcpToolsInitialized = true;
      }

      Object params = event.getProperty(IEventBroker.DATA);
      if (params instanceof List mcpServerTools) {
        ensureRealm(() -> mcpToolsObservableValue.setValue(mcpServerTools));
      }
    };

    eventBroker.subscribe(CopilotEventConstants.ON_DID_CHANGE_MCP_TOOLS, mcpToolNotifiedEventHandler);
  }

  private void initializeMcpFeatureFlagUpdateEvent() {
    ensureRealm(
        () -> mcpEnabledObservableValue = new WritableValue<>(CopilotCore.getPlugin().getFeatureFlags().isMcpEnabled(),
            Boolean.class));

    featureFlagNotifiedEventHandler = event -> {
      Object params = event.getProperty(IEventBroker.DATA);
      if (params instanceof DidChangeFeatureFlagsParams featureFlagsParams) {
        ensureRealm(() -> mcpEnabledObservableValue.setValue(featureFlagsParams.isMcpEnabled()));
      }
    };

    eventBroker.subscribe(CopilotEventConstants.TOPIC_CHAT_DID_CHANGE_FEATURE_FLAGS, featureFlagNotifiedEventHandler);
  }

  /**
   * Bind the observable with UI in McpPreferencePage.
   */
  public void bindWithMcpPreferencePage(McpPreferencePage page) {
    ensureRealm(() -> {
      unbindWithMcpPreferencePage();
      mcpToolsSideEffect = ISideEffect.create(mcpToolsObservableValue::getValue, page::displayServerToolsInfo);
      mcpPreferenceSideEffect = ISideEffect.create(mcpEnabledObservableValue::getValue, page::updateMcpPreferencePage);
    });
  }

  private void unbindWithMcpPreferencePage() {
    if (mcpToolsSideEffect != null) {
      mcpToolsSideEffect.dispose();
      mcpToolsSideEffect = null;
    }

    if (mcpPreferenceSideEffect != null) {
      mcpPreferenceSideEffect.dispose();
      mcpPreferenceSideEffect = null;
    }
  }

  /**
   * Bind the observable with mcpToolButton in ActionBar.
   */
  public void bindWithMcpToolButton(Button mcpToolButton, Image mcpToolImage, Image mcpToolDisabledImage) {
    unbindWithMcpToolButton();
    ensureRealm(
        () -> mcpToolButtonSideEffect = ISideEffect.create(mcpEnabledObservableValue::getValue, (Boolean isEnabled) -> {
          if (mcpToolButton != null && !mcpToolButton.isDisposed()) {
            if (Boolean.TRUE.equals(isEnabled)) {
              mcpToolButton.setImage(mcpToolImage);
              mcpToolButton.setToolTipText(Messages.chat_actionBar_toolButton_toolTip);
            } else {
              mcpToolButton.setImage(mcpToolDisabledImage);
              mcpToolButton.setToolTipText(Messages.chat_actionBar_toolButton_disabled_toolTip);
            }
          }
        }));
  }

  /**
   * Unbind the observable with mcpToolButton in ActionBar.
   */
  public void unbindWithMcpToolButton() {
    if (mcpToolButtonSideEffect != null) {
      mcpToolButtonSideEffect.dispose();
      mcpToolButtonSideEffect = null;
    }
  }

  /**
   * Handles the OAuth confirmation request.
   *
   * @return true if the OAuth confirmation is successful, false otherwise.
   */
  public boolean mcpOauth(McpOauthRequest request) {
    String title = Messages.preferences_page_mcpOAuth_confirmTitle;
    String message = String.format(Messages.preferences_page_mcpOAuth_confirmMessage, request.getMcpServer(),
        request.getAuthLabel());

    CompletableFuture<Boolean> result = new CompletableFuture<>();
    ensureRealm(() -> {
      var shell = new Shell(PlatformUI.getWorkbench().getDisplay());
      boolean confirmed = MessageDialog.openConfirm(shell, title, message);
      result.complete(confirmed);
    });

    try {
      return result.get();
    } catch (ExecutionException | InterruptedException e) {
      CopilotCore.LOGGER.error("Error during MCP OAuth confirmation", e);
      return false;
    }
  }

  /**
   * Dispose the service.
   */
  public void dispose() {
    if (eventBroker != null) {
      if (mcpToolNotifiedEventHandler != null) {
        eventBroker.unsubscribe(mcpToolNotifiedEventHandler);
        mcpToolNotifiedEventHandler = null;
      }

      if (featureFlagNotifiedEventHandler != null) {
        eventBroker.unsubscribe(featureFlagNotifiedEventHandler);
        featureFlagNotifiedEventHandler = null;
      }
    }
  }
}
