/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.copilot.ui.chat.services.AvatarService;
import org.eclipse.copilot.ui.chat.services.ChatServiceManager;
import org.eclipse.copilot.ui.i18n.Messages;

/**
 * A custom widget that displays a turn for the copilot.
 */
public class CopilotTurnWidget extends BaseTurnWidget {

  /**
   * Create the widget.
   */
  public CopilotTurnWidget(Composite parent, int style, ChatServiceManager serviceManager, String turnId) {
    super(parent, style, serviceManager, turnId, true);
  }

  @Override
  protected Image getAvatar(AvatarService avatarService) {
    return avatarService.getAvatarForCopilot();
  }

  @Override
  protected String getRoleName() {
    return Messages.chat_turnWidget_copilot;
  }

  @Override
  protected Label createAvatarLabel(Composite parent) {
    Label lblAvatar = new Label(parent, SWT.NONE);
    lblAvatar.setBackground(parent.getBackground());
    return lblAvatar;
  }

  @Override
  protected void createTextBlock() {
    this.currentTextBlock = new ChatMarkupViewer(this, SWT.MULTI | SWT.WRAP);
    StyledText styledText = this.currentTextBlock.getTextWidget();
    styledText.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false));
    styledText.setEditable(false);
    styledText.setBackground(this.getBackground());
  }
}
