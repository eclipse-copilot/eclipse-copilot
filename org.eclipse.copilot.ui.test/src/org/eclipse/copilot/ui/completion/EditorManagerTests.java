/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.completion;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.eclipse.copilot.core.completion.CompletionProvider;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.ui.preferences.LanguageServerSettingManager;

@ExtendWith(MockitoExtension.class)
class EditorManagerTests {

  @Mock
  private CopilotLanguageServerConnection mockServer;

  @Mock
  private CompletionProvider mockProvider;

  @Mock
  private LanguageServerSettingManager mockSettingManager;

  @Test
  void testCreateManagerForNull() {
    EditorsManager manager = new EditorsManager(mockServer, mockProvider, mockSettingManager);

    assertNull(manager.getOrCreateCompletionManagerFor(null));
  }

  @Test
  void testGetOrCreateCompletionManagerWhenNotPresent() {
    ITextEditor mockEditor = mock(ITextEditor.class);
    ITextViewer mockViewer = mock(ITextViewer.class);

    when(mockEditor.getAdapter(any())).thenReturn(mockViewer);
    when(mockViewer.isEditable()).thenReturn(true);
    EditorsManager manager = new EditorsManager(mockServer, mockProvider, mockSettingManager);
    BaseCompletionManager completionManager = manager.getOrCreateCompletionManagerFor(mockEditor);

    assertNotNull(completionManager);
  }

  @Test
  void testGetActiveManagerWhenNoActiveEditor() {
    EditorsManager manager = new EditorsManager(mockServer, mockProvider, mockSettingManager);

    assertNull(manager.getActiveCompletionManager());
  }

  @Test
  void testGetActiveHandlerWhenActiveEditor() {
    ITextEditor mockEditor = mock(ITextEditor.class);
    ITextViewer mockViewer = mock(ITextViewer.class);

    when(mockEditor.getAdapter(any())).thenReturn(mockViewer);
    when(mockViewer.isEditable()).thenReturn(true);
    EditorsManager manager = new EditorsManager(mockServer, mockProvider, mockSettingManager);
    manager.getOrCreateCompletionManagerFor(mockEditor);
    manager.setActiveEditor(mockEditor);

    assertNotNull(manager.getActiveCompletionManager());
  }
}
