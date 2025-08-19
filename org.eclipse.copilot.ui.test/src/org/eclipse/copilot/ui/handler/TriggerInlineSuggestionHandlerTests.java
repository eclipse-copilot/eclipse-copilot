/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.handler;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.commands.ExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.completion.CompletionManager;
import org.eclipse.copilot.ui.completion.EditorsManager;
import org.eclipse.copilot.ui.handlers.TriggerInlineSuggestionHandler;

@ExtendWith(MockitoExtension.class)
class TriggerInlineSuggestionHandlerTests {

  @Test
  void testTriggerCompletionInvocation() throws ExecutionException {
    CopilotUi mockedUi = mock(CopilotUi.class);
    EditorsManager mockedManager = mock(EditorsManager.class);
    CompletionManager mockedCompletionManager = mock(CompletionManager.class);
    when(mockedUi.getEditorsManager()).thenReturn(mockedManager);
    when(mockedManager.getActiveCompletionManager()).thenReturn(mockedCompletionManager);
    doNothing().when(mockedCompletionManager).triggerCompletion();

    TriggerInlineSuggestionHandler handler = new TriggerInlineSuggestionHandler();

    try (MockedStatic<CopilotUi> mockedStatic = mockStatic(CopilotUi.class)) {
      mockedStatic.when(CopilotUi::getPlugin).thenReturn(mockedUi);
      handler.execute(null);
      verify(mockedCompletionManager, times(1)).triggerCompletion();
    }
  }
}
