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

package org.eclipse.copilot.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.ui.texteditor.templates.TemplatesView;

import org.eclipse.copilot.core.Constants;

/**
 * Copilot Perspective.
 */
public class CopilotPerspectiveFactory implements IPerspectiveFactory {
    
  @Override
  public void createInitialLayout(IPageLayout layout) {
    // Get the editor area id
    String editorArea = layout.getEditorArea();

    // Right
    IFolderLayout copilotLayout = layout.createFolder("right", IPageLayout.RIGHT, 0.75f, editorArea);
    copilotLayout.addView(Constants.CHAT_VIEW_ID);

    // Left
    IFolderLayout projectExplorerLayout = layout.createFolder("left", IPageLayout.LEFT, 0.25f, editorArea);
    projectExplorerLayout.addView(IPageLayout.ID_PROJECT_EXPLORER);

    // BottomLeft
    IFolderLayout outlineLayout = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.5f, "left");
    outlineLayout.addView(IPageLayout.ID_OUTLINE);
    outlineLayout.addView("org.eclipse.mylyn.tasks.ui.views.tasks");

    // Bottom
    IFolderLayout consoleLayout = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.8f, editorArea);
    consoleLayout.addView(IPageLayout.ID_PROBLEM_VIEW);
    consoleLayout.addView(IConsoleConstants.ID_CONSOLE_VIEW);
    consoleLayout.addView("org.eclipse.tm.terminal.view.ui.TerminalsView");
    
    consoleLayout.addPlaceholder(IPageLayout.ID_BOOKMARKS);
    consoleLayout.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
    consoleLayout.addPlaceholder("org.eclipse.pde.runtime.LogView");
    consoleLayout.addPlaceholder("org.eclipse.search.ui.views.SearchView");
    
    // 'Window' > 'Show View' shortcuts
    layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
    layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
    layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
    layout.addShowViewShortcut(IProgressConstants.PROGRESS_VIEW_ID);
    layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
    layout.addShowViewShortcut(TemplatesView.ID);
    layout.addShowViewShortcut("org.eclipse.tm.terminal.view.ui.TerminalsView");
    layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");
    layout.addShowViewShortcut("org.eclipse.search.ui.views.SearchView");
    
    // 'Window' > 'Perspective' > 'Open Perspective' contributions
    layout.addPerspectiveShortcut("org.eclipse.debug.ui.DebugPerspective");
  }
}