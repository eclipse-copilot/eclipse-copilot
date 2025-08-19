/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.eclipse.copilot.core.AuthStatusManager;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;

class ChatBaseServiceTests {

  @Mock
  private CopilotLanguageServerConnection mockLsConnection;

  @Mock
  private AuthStatusManager mockAuthStatusManager;

  @Mock
  private CopilotCore mockCopilotCore;

  private TestChatBaseService chatBaseService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    chatBaseService = new TestChatBaseService(mockLsConnection, mockAuthStatusManager);
  }

  @Test
  void persistUserPreference_WhenNotSignedIn_ShouldSkipLspInteraction() {
    // Arrange
    when(mockAuthStatusManager.isSignedIn()).thenReturn(false);

    // Act
    chatBaseService.persistUserPreference();

    // Assert
    verify(mockAuthStatusManager).isSignedIn();
    verifyNoInteractions(mockLsConnection); // LSP connection should not be used
  }

  /**
   * Test implementation of ChatBaseService for testing purposes
   */
  private static class TestChatBaseService extends ChatBaseService {
    public TestChatBaseService(CopilotLanguageServerConnection lsConnection, AuthStatusManager authStatusManager) {
      super(lsConnection, authStatusManager);
    }

    public void persistUserPreference() {
      super.persistUserPreference();
    }
  }
}