/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat.viewers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import org.eclipse.copilot.ui.i18n.Messages;
import org.eclipse.copilot.ui.swt.WrapLabel;
import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * A widget that displays a loading view.
 */
public class LoadingViewer extends BaseViewer {
  private Image mainIcon;
  private Font mainLabelFont;

  /**
   * Create the composite.
   *
   * @param parent the parent composite
   * @param style the style
   */
  public LoadingViewer(Composite parent, int style) {
    super(parent, style);

    GridLayout layout = new GridLayout(1, true);
    layout.verticalSpacing = ALIGNED_MARGIN_TOP * 2;
    setLayout(layout);
    buildMainIconAndLabel();
  }

  private void buildMainIconAndLabel() {
    Composite iconLabelComposite = new Composite(this, SWT.NONE);
    GridLayout iconLabelGridlayout = new GridLayout(1, true);
    iconLabelGridlayout.verticalSpacing = ALIGNED_MARGIN_TOP;
    iconLabelComposite.setLayout(iconLabelGridlayout);
    iconLabelComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

    this.mainIcon = UiUtils.buildImageFromPngPath("/icons/chat/chatview_icon_loading.png");
    Label icon = new Label(iconLabelComposite, SWT.CENTER);
    icon.setImage(mainIcon);
    icon.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

    WrapLabel label = new WrapLabel(iconLabelComposite, SWT.CENTER);
    label.setText(Messages.chat_loadingView_title);
    label.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
    FontData fontData = new FontData();
    fontData.setHeight(ALIGNED_TITLE_HEIGHT);
    fontData.setStyle(SWT.BOLD);
    if (this.mainLabelFont != null && !this.mainLabelFont.isDisposed()) {
      this.mainLabelFont.dispose();
    }
    this.mainLabelFont = new Font(Display.getCurrent(), fontData);
    label.setFont(mainLabelFont);

    WrapLabel subLabel = new WrapLabel(iconLabelComposite, SWT.CENTER);
    subLabel.setText(Messages.chat_loadingView_description);
    subLabel.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
  }

  @Override
  public void dispose() {
    if (this.mainIcon != null) {
      this.mainIcon.dispose();
    }
    if (this.mainLabelFont != null) {
      this.mainLabelFont.dispose();
    }
    super.dispose();
  }
}