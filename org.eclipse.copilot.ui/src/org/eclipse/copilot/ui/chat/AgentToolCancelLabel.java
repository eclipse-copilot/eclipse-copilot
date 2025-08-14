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

package org.eclipse.copilot.ui.chat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * A label with icon that displays the cancel status of the agent tool.
 */
public class AgentToolCancelLabel extends Composite {
  private Image cancelIcon;

  /**
   * Create the composite.
   *
   * @param parent the parent composite
   * @param style the style
   */
  public AgentToolCancelLabel(Composite parent, int style, String cancelMessage) {
    super(parent, style);
    setLayout(new GridLayout(2, false));
    setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

    this.cancelIcon = UiUtils.buildImageFromPngPath("/icons/cancel_status.png");
    Label iconLabel = new Label(this, SWT.LEFT);
    iconLabel.setImage(this.cancelIcon);
    UiUtils.useParentBackground(iconLabel);

    Label textLabel = new Label(this, SWT.LEFT | SWT.WRAP);
    textLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    textLabel.setText(cancelMessage);
    textLabel.setBackground(this.getParent().getBackground());

    this.addDisposeListener(e -> {
      if (this.cancelIcon != null && !this.cancelIcon.isDisposed()) {
        this.cancelIcon.dispose();
      }
    });
  }
}
