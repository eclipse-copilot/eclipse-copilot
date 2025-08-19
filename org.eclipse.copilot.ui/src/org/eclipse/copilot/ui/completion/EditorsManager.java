/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.completion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.copilot.core.completion.CompletionProvider;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.ui.preferences.LanguageServerSettingManager;
import org.eclipse.copilot.ui.utils.SwtUtils;

/**
 * Manages the completion managers for all available ITextEditors.
 */
public class EditorsManager {

  private CopilotLanguageServerConnection languageServer;
  private CompletionProvider completionProvider;
  private Map<ITextEditor, BaseCompletionManager> editorMap;
  private AtomicReference<ITextEditor> activeEditor;
  private LanguageServerSettingManager settingsManager;

  /**
   * Creates a new EditorManager.
   */
  public EditorsManager(CopilotLanguageServerConnection languageServer, CompletionProvider completionProvider,
      LanguageServerSettingManager settingsManager) {
    this.languageServer = languageServer;
    this.completionProvider = completionProvider;
    this.editorMap = new ConcurrentHashMap<>();
    this.activeEditor = new AtomicReference<>();
    this.settingsManager = settingsManager;
  }

  /**
   * Gets the {@link org.eclipse.copilot.ui.completion.BaseCompletionManager BaseCompletionManager} for the
   * given ITextEditor. If it does not exist, a new one will be created. Returns <code>null</code> if the editor is
   * <code>null</code>.
   */
  @Nullable
  public BaseCompletionManager getOrCreateCompletionManagerFor(ITextEditor textEditor) {
    if (textEditor == null) {
      return null;
    }

    BaseCompletionManager manager = editorMap.get(textEditor);
    if (manager != null) {
      return manager;
    }

    ITextViewer textViewer = textEditor.getAdapter(ITextViewer.class);
    if (!SwtUtils.isEditable(textViewer)) {
      return null;
    }

    manager = CompletionManagerFactory.createCompletionManager(this.languageServer, this.completionProvider, textEditor,
        this.settingsManager);
    editorMap.put(textEditor, manager);

    return manager;
  }

  /**
   * Gets the {@link org.eclipse.copilot.ui.completion.BaseCompletionManager BaseCompletionManager} for the
   * given ITextEditor. Returns <code>null</code> if there is no manager for the editor.
   */
  @Nullable
  public BaseCompletionManager getCompletionManagerFor(IEditorPart editor) {
    if (editor == null) {
      return null;
    }

    return editorMap.get(editor);
  }

  /**
   * Gets the {@link org.eclipse.copilot.ui.completion.BaseCompletionManager BaseCompletionManager} for the
   * active ITextEditor.
   */
  @Nullable
  public BaseCompletionManager getActiveCompletionManager() {
    if (this.activeEditor.get() == null) {
      return null;
    }
    return this.editorMap.get(activeEditor.get());
  }

  /**
   * Disposes the {@link org.eclipse.copilot.ui.completion.BaseCompletionManager BaseCompletionManager} for
   * the given ITextEditor.
   */
  public void disposeCompletionManagerFor(ITextEditor textEditor) {
    if (textEditor == null) {
      return;
    }
    BaseCompletionManager handler = editorMap.remove(textEditor);
    if (handler != null) {
      handler.dispose();
    }
  }

  /**
   * Sets the active editor.
   */
  public void setActiveEditor(ITextEditor textEditor) {
    this.activeEditor.set(textEditor);

  }

  @Nullable
  public ITextEditor getActiveEditor() {
    return this.activeEditor.get();
  }

  /**
   * Dispose all the managers.
   */
  public void dispose() {
    for (BaseCompletionManager handler : this.editorMap.values()) {
      handler.dispose();
    }
    this.editorMap.clear();
  }

}