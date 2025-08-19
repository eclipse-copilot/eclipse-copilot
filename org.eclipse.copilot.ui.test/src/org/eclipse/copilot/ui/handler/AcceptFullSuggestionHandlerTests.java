/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.handler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.commands.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.completion.SuggestionUpdateManager;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.core.lsp.protocol.CompletionItem;
import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.completion.CompletionManager;
import org.eclipse.copilot.ui.completion.EditorsManager;
import org.eclipse.copilot.ui.handlers.AcceptFullSuggestionHandler;

@ExtendWith(MockitoExtension.class)
class AcceptFullSuggestionHandlerTests {

  @Test
  void testIsNotEnabledWhenNoCompletionIsAvailable() {
    CopilotUi mockedUi = mock(CopilotUi.class);
    EditorsManager mockedManager = mock(EditorsManager.class);
    CompletionManager mockedCompletionManager = mock(CompletionManager.class);
    SuggestionUpdateManager mockedSuggestionUpdateManager = mock(SuggestionUpdateManager.class);
    when(mockedCompletionManager.getSuggestionUpdateManager()).thenReturn(mockedSuggestionUpdateManager);
    when(mockedUi.getEditorsManager()).thenReturn(mockedManager);
    when(mockedManager.getActiveCompletionManager()).thenReturn(mockedCompletionManager);
    when(mockedSuggestionUpdateManager.getCurrentItem()).thenReturn(null);

    AcceptFullSuggestionHandler handler = new AcceptFullSuggestionHandler();

    try (MockedStatic<CopilotUi> mockedStatic = mockStatic(CopilotUi.class)) {
      mockedStatic.when(CopilotUi::getPlugin).thenReturn(mockedUi);
      assertFalse(handler.isEnabled());
    }
  }

  @Test
  void testAcceptionNotifiedWhenCompletionIsAccepted() throws ExecutionException {
    CopilotLanguageServerConnection mockedConnection = mock(CopilotLanguageServerConnection.class);
    when(mockedConnection.notifyAccepted(any())).thenReturn(null);
    CopilotCore mockedCore = mock(CopilotCore.class);
    when(mockedCore.getCopilotLanguageServer()).thenReturn(mockedConnection);

    CompletionItem item = new CompletionItem("uuid", "text", null, "displayText", null, 0);
    CompletionManager mockedCompletionManager = mock(CompletionManager.class);
    doNothing().when(mockedCompletionManager).acceptSuggestion(any());
    SuggestionUpdateManager mockedSuggestionUpdateManager = mock(SuggestionUpdateManager.class);
    when(mockedSuggestionUpdateManager.getCurrentItem()).thenReturn(item);
    when(mockedCompletionManager.getSuggestionUpdateManager()).thenReturn(mockedSuggestionUpdateManager);
    EditorsManager mockedManager = mock(EditorsManager.class);
    when(mockedManager.getActiveCompletionManager()).thenReturn(mockedCompletionManager);
    CopilotUi mockedUi = mock(CopilotUi.class);
    when(mockedUi.getEditorsManager()).thenReturn(mockedManager);

    AcceptFullSuggestionHandler handler = new AcceptFullSuggestionHandler();

    try (MockedStatic<CopilotUi> mockedStatic = mockStatic(CopilotUi.class);
        MockedStatic<CopilotCore> mockedStaticCore = mockStatic(CopilotCore.class)) {
      mockedStatic.when(CopilotUi::getPlugin).thenReturn(mockedUi);
      mockedStaticCore.when(CopilotCore::getPlugin).thenReturn(mockedCore);

      handler.execute(null);

      verify(mockedConnection).notifyAccepted(any());
    }
  }

}
