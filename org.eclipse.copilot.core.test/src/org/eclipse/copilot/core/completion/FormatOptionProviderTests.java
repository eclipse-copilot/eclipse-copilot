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

package org.eclipse.copilot.core.completion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.lsp4j.FormattingOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.eclipse.copilot.core.format.FormatOptionProvider;
import org.eclipse.copilot.core.format.JavaFormatReader;

@ExtendWith(MockitoExtension.class)
class FormatOptionProviderTests {
  private FormatOptionProvider formatOptionProvider;
  private IFile mockFile;
  private IProject mockProject;

  private static final int PREFERENCE_DEFAULT_TAB_SIZE = 4;

  @BeforeEach
  void setUp() {
    formatOptionProvider = new FormatOptionProvider();
    mockFile = mock(IFile.class);
    mockProject = mock(IProject.class);

    when(mockFile.exists()).thenReturn(true);
    when(mockFile.isAccessible()).thenReturn(true);
    when(mockFile.getProject()).thenReturn(mockProject);
  }

  @Test
  void testGetEclipseDefaultJavaTabCharAndSize() {
    when(mockProject.getName()).thenReturn("testProject");
    when(mockFile.getFileExtension()).thenReturn("java");

    JavaFormatReader javaFormatReader = new JavaFormatReader(mockProject);
    FormattingOptions languageFormat = javaFormatReader.getFormattingOptions();
    boolean useSpace = languageFormat.isInsertSpaces();
    int tabSize = languageFormat.getTabSize();

    assertEquals(useSpace, formatOptionProvider.useSpace(mockFile));
    assertEquals(tabSize, formatOptionProvider.getTabSize(mockFile));
  }

  @Test
  void testGetCopilotDefaultTabCharAndSizeForUnknownLanguage() {
    when(mockFile.getFileExtension()).thenReturn("js");

    assertTrue(formatOptionProvider.useSpace(mockFile));
    assertEquals(PREFERENCE_DEFAULT_TAB_SIZE, formatOptionProvider.getTabSize(mockFile));
  }

  @Test
  void testGetCopilotDefaultTabCharAndSizeForNoExtensionFile() {
    when(mockFile.getFileExtension()).thenReturn(null);

    assertTrue(formatOptionProvider.useSpace(mockFile));
    assertEquals(PREFERENCE_DEFAULT_TAB_SIZE, formatOptionProvider.getTabSize(mockFile));
  }

}
