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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.format.FormatOptionProvider;
import org.eclipse.copilot.ui.utils.CompletionUtils;

/**
 * A ghost text placed below the last line of the document. For normal block ghost text, we use code mining API to
 * display the ghost text.
 */
public class BlockGhostText extends GhostText {

  /**
   * Creates a new EolGhostText.
   */
  public BlockGhostText(String text, int modelOffset, IDocument document) {
    super(text, modelOffset, GhostTextType.BLOCK_LINE);
    IFile file = LSPEclipseUtils.getFile(document);
    if (file == null) {
      return;
    }
    FormatOptionProvider formatOptionProvider = CopilotCore.getPlugin().getFormatOptionProvider();
    if (formatOptionProvider == null) {
      return;
    }
    boolean useSpace = formatOptionProvider.useSpace(file);
    if (useSpace) {
      return;
    }
    int tabSize = formatOptionProvider.getTabSize(file);
    String replacedText = CompletionUtils.replaceTabsWithSpaces(this.text, tabSize);
    this.text = replacedText;
  }

  @Override
  public void draw(StyledText styledText, int widgetOffset, GC gc) {
    if (StringUtils.isNotBlank(this.text)) {
      Rectangle bounds = styledText.getTextBounds(widgetOffset, widgetOffset);
      int y = bounds.y + styledText.getLineHeight();
      gc.drawText(this.text, styledText.getLeftMargin(), y, true);
    }
  }
}
