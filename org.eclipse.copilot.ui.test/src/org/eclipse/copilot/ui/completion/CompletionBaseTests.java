/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.completion;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.eclipse.copilot.ui.utils.SwtUtils;

/**
 * Base class for completion tests.
 */
public abstract class CompletionBaseTests {

  protected IProject project;

  @BeforeEach
  void setUp() throws Exception {
    project = ResourcesPlugin.getWorkspace().getRoot().getProject("TestProject");
    project.create(null);
    project.open(null);
  }

  @AfterEach
  void tearDown() throws Exception {
    project.delete(true, null);
  }

  /**
   * Opens the editor for the given file.
   *
   * @param file the file to open
   * @return the editor part
   */
  protected IEditorPart getEditorPartFor(IFile file) {
    AtomicReference<IEditorPart> ref = new AtomicReference<>();
    SwtUtils.invokeOnDisplayThread(() -> {
      IWorkbench workbench = PlatformUI.getWorkbench();
      IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
      if (window != null) {
        try {
          ref.set(window.getActivePage().openEditor(new org.eclipse.ui.part.FileEditorInput(file),
              "org.eclipse.ui.DefaultTextEditor"));
        } catch (PartInitException e) {
          // do nothing
        }
      }
    });
    return ref.get();
  }

}
