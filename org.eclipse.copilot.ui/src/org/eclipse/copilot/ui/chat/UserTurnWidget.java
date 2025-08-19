/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat;

import java.util.Optional;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.copilot.ui.chat.services.AvatarService;
import org.eclipse.copilot.ui.chat.services.ChatServiceManager;
import org.eclipse.copilot.ui.i18n.Messages;

/**
 * A custom widget that displays a turn for the user.
 */
public class UserTurnWidget extends BaseTurnWidget {

  /**
   * Create the widget.
   */
  public UserTurnWidget(Composite parent, int style, ChatServiceManager serviceManager, String turnId) {
    super(parent, style, serviceManager, turnId, false);
  }

  @Override
  protected Image getAvatar(AvatarService avatarService) {
    return avatarService.getAvatarForCurrentUser(getDisplay());
  }

  @Override
  protected String getRoleName() {
    return Optional.ofNullable(serviceManager.getAuthStatusManager().getUserName()).filter(s -> !s.isEmpty())
        .orElse(Messages.chat_turnWidget_user);
  }

  @Override
  protected Label createAvatarLabel(Composite parent) {
    Label lblAvatar = new Label(parent, SWT.DOUBLE_BUFFERED);
    lblAvatar.setBackground(parent.getBackground());

    // Set size based on icon dimensions
    int size = icon.getBounds().width;
    GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
    gridData.widthHint = size;
    gridData.heightHint = size;
    lblAvatar.setLayoutData(gridData);
    lblAvatar.addPaintListener(new PaintListener() {
      @Override
      public void paintControl(PaintEvent e) {
        Image avatar = lblAvatar.getImage();
        if (avatar == null || avatar.isDisposed() || lblAvatar.isDisposed()) {
          return;
        }

        GC gc = e.gc;
        gc.setAdvanced(true);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);

        Rectangle bounds = e.gc.getClipping();

        // Clear previous content with background color
        gc.setBackground(lblAvatar.getBackground());
        gc.fillRectangle(bounds);

        // Create circular clipping path for the image
        Rectangle imgBounds = avatar.getBounds();
        int diameter = Math.min(imgBounds.width, imgBounds.height);
        Path path = new Path(getDisplay());
        path.addArc(0, 0, diameter, diameter, 0, 360);

        Color borderColor = getDisplay().getSystemColor(SWT.COLOR_GRAY);
        int borderWidth = 1;

        // Draw the image first
        gc.setClipping(path);
        gc.drawImage(avatar, 0, 0, imgBounds.width, imgBounds.height, borderWidth, borderWidth,
            diameter - (2 * borderWidth), diameter - (2 * borderWidth));

        // Reset clipping to draw the border
        gc.setClipping(bounds);

        // Draw border
        gc.setForeground(borderColor);
        gc.setLineWidth(borderWidth);
        gc.drawOval(0, 0, diameter - 1, diameter - 1);

        path.dispose();
      }
    });

    return lblAvatar;
  }

  @Override
  protected void createTextBlock() {
    this.currentTextBlock = new SourceViewer(this, null, SWT.MULTI | SWT.WRAP);
    StyledText styledText = this.currentTextBlock.getTextWidget();
    styledText.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false));
    styledText.setEditable(false);
    styledText.setBackground(this.getBackground());
  }
}
