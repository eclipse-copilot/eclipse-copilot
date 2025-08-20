/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.completion.codemining;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipse.jface.text.source.inlined.Positions;

/**
 * A block of ghost text with multiple lines placed in new lines. We use code mining API to display the ghost text.
 */
public class BlockGhostText extends LineHeaderCodeMining {

  /**
   * Creates a new BlockGhostText.
   */
  public BlockGhostText(int beforeLineNumber, IDocument document, ICodeMiningProvider provider, String text)
      throws BadLocationException {
    super(Positions.of(beforeLineNumber, document, false), provider, null);
    this.setLabel(text);
  }

  /**
   * Creates a new BlockGhostText. (for testing purpose)
   */
  public BlockGhostText(Position position, ICodeMiningProvider provider, String text) throws BadLocationException {
    super(position, provider, null);
    this.setLabel(text);
  }

}
