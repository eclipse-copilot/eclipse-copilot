/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.eclipse.copilot.core.AuthStatusManager;
import org.eclipse.copilot.core.lsp.protocol.ChatMode;
import org.eclipse.copilot.ui.chat.services.ChatServiceManager;
import org.eclipse.copilot.ui.chat.services.McpConfigService;
import org.eclipse.copilot.ui.chat.services.ReferencedFileService;
import org.eclipse.copilot.ui.chat.services.UserPreferenceService;
import org.eclipse.copilot.ui.utils.SwtUtils;

@ExtendWith(MockitoExtension.class)
class ChatViewTest {

  private Shell shell;
  private Composite parent;
  private ChatView chatView;

  @Mock
  private ChatServiceManager mockChatServiceManager;
  @Mock
  private UserPreferenceService mockUserPreferenceService;
  @Mock
  private AuthStatusManager mockAuthStatusManager;
  @Mock
  private ReferencedFileService mockReferencedFileService;
  @Mock
  private McpConfigService mockMcpConfigService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    SwtUtils.invokeOnDisplayThread(() -> {
      setupSwtComponents();
      setupChatView();
    });
    setupMocks();
  }

  private void setupSwtComponents() {
    shell = new Shell(Display.getDefault());
    parent = new Composite(shell, SWT.NONE);
  }

  private void setupChatView() {
    chatView = new ChatView();

    setFieldValue(chatView, "parent", parent);
    setFieldValue(chatView, "chatServiceManager", mockChatServiceManager);
  }

  private void setupMocks() {
    when(mockChatServiceManager.getAuthStatusManager()).thenReturn(mockAuthStatusManager);
    when(mockChatServiceManager.getReferencedFileService()).thenReturn(mockReferencedFileService);
    when(mockChatServiceManager.getUserPreferenceService()).thenReturn(mockUserPreferenceService);
  }

  private void setFieldValue(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set field " + fieldName, e);
    }
  }

  @Test
  void testSwitchChatModeWithCachedInputContent() {
    String cachedInputContent = "Cached input content";

    // Build the view for Agent mode and set cached content to the input viewer
    when(mockUserPreferenceService.getActiveChatMode()).thenReturn(ChatMode.Agent);
    when(mockChatServiceManager.getMcpConfigService()).thenReturn(mockMcpConfigService);
    SwtUtils.invokeOnDisplayThread(() -> {
      chatView.buildViewFor(ChatMode.Agent);
    });
    ActionBar agentActionBar = (ActionBar) chatView.getActionBar();
    agentActionBar.setInputTextViewerContent(cachedInputContent);

    // Switch to Ask mode and verify the cached content is retained
    when(mockUserPreferenceService.getActiveChatMode()).thenReturn(ChatMode.Ask);
    SwtUtils.invokeOnDisplayThread(() -> {
      chatView.buildViewFor(ChatMode.Ask);
    });
    ActionBar askActionBar = (ActionBar) chatView.getActionBar();
    assertEquals(cachedInputContent, askActionBar.getInputTextViewerContent());
  }

  @AfterEach
  void tearDown() {
    SwtUtils.invokeOnDisplayThread(() -> {
      if (shell != null && !shell.isDisposed()) {
        shell.dispose();
      }
    });
  }
}