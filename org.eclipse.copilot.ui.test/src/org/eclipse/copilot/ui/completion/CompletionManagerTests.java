/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.completion;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.eclipse.copilot.core.completion.AcceptSuggestionType;
import org.eclipse.copilot.core.completion.CompletionProvider;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.core.lsp.protocol.CompletionItem;
import org.eclipse.copilot.core.lsp.protocol.CopilotLanguageServerSettings;
import org.eclipse.copilot.ui.preferences.LanguageServerSettingManager;

@ExtendWith(MockitoExtension.class)
class CompletionManagerTests extends CompletionBaseTests {

  @Mock
  private CopilotLanguageServerConnection mockLsConnection;

  @Test
  void testReplaceCompletion1() throws Exception {
    IFile file = project.getFile("Test.java");
    String content = """
          public class App {

          public void hi() {
            System.out.println("");
          }

        }
        """;
    file.create(content.getBytes(), IResource.FORCE, null);
    int documentVersion = 1;

    IEditorPart editorPart = getEditorPartFor(file);
    assertTrue(editorPart instanceof ITextEditor);

    ITextEditor textEditor = (ITextEditor) editorPart;

    when(mockLsConnection.getDocumentVersion(any())).thenReturn(documentVersion);
    CopilotLanguageServerSettings settings = new CopilotLanguageServerSettings();
    LanguageServerSettingManager languageServerSettingManager = mock(LanguageServerSettingManager.class);
    when(languageServerSettingManager.getSettings()).thenReturn(settings);
    CompletionManager manager = new CompletionManager(mockLsConnection, mock(CompletionProvider.class), textEditor,
        languageServerSettingManager);

    List<CompletionItem> completions = List.of(new CompletionItem("uuid", "    System.out.println(\"hi\");",
        new Range(new Position(3, 0), new Position(3, 27)), "hi\");", new Position(3, 24), documentVersion));

    manager.onCompletionResolved(LSPEclipseUtils.toUri(file.getLocation().toFile()).toASCIIString(), completions);
    manager.acceptSuggestion(AcceptSuggestionType.FULL);

    IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
    assertTrue(document.get().contains("  System.out.println(\"hi\");\n"));
  }

  @Test
  void testEnableCompletionContext() throws Exception {
    IFile file = project.getFile("Test2.java");
    String content = """
        public class Test2 {
          public void hi() {
            System.out.println("");
          }
        }
        """;
    file.create(content.getBytes(), IResource.FORCE, null);
    int documentVersion = 1;

    IEditorPart editorPart = getEditorPartFor(file);
    assertTrue(editorPart instanceof ITextEditor);

    ITextEditor textEditor = (ITextEditor) editorPart;

    when(mockLsConnection.getDocumentVersion(any())).thenReturn(documentVersion);
    CopilotLanguageServerSettings settings = new CopilotLanguageServerSettings();
    LanguageServerSettingManager languageServerSettingManager = mock(LanguageServerSettingManager.class);
    when(languageServerSettingManager.getSettings()).thenReturn(settings);
    CompletionManager manager = new CompletionManager(mockLsConnection, mock(CompletionProvider.class), textEditor,
        languageServerSettingManager);

    List<CompletionItem> completions = List.of(new CompletionItem("uuid", "    System.out.println(\"hi\");",
        new Range(new Position(2, 0), new Position(2, 27)), "hi\");", new Position(3, 24), documentVersion));

    manager.onCompletionResolved(LSPEclipseUtils.toUri(file.getLocation().toFile()).toASCIIString(), completions);

    IContextService contextService = PlatformUI.getWorkbench().getService(IContextService.class);
    Set<Object> activeContexts = new HashSet<>(contextService.getActiveContextIds());
    assertTrue(activeContexts.contains("org.eclipse.copilot.completionAvailableContext"));
  }

}
