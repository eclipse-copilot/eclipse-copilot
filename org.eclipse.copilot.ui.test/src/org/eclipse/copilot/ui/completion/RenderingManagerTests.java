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

package org.eclipse.copilot.ui.completion;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IEditorPart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RenderingManagerTests extends CompletionBaseTests {

  @Test
  void testShouldNotDisposeJfaceColor() throws Exception {
    IFile file = project.getFile("Test.java");
    file.create("".getBytes(), IResource.FORCE, null);

    IEditorPart editorPart = getEditorPartFor(file);
    ITextViewer textViewer = (ITextViewer) editorPart.getAdapter(ITextViewer.class);

    RenderingManager manager = new RenderingManager(textViewer);
    manager.dispose();

    ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
    Color color = colorRegistry.get("org.eclipse.ui.editors.inlineAnnotationColor");
    assertFalse(color.isDisposed());
  }
}
