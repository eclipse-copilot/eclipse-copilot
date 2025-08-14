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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.chat.services.ReferencedFileService;
import org.eclipse.copilot.ui.i18n.Messages;
import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * A widget that displays a button to attach context.
 */
public class AddContextButton extends Composite {
  private Label lblAttachIcon;
  private Label lblButtonText;

  /**
   * Creates a new AddContextButton.
   */
  public AddContextButton(Composite parent) {
    super(parent, SWT.BORDER);
    GridLayout layout = new GridLayout(2, false);
    layout.marginWidth = 4;
    layout.marginHeight = 2;
    setLayout(layout);

    lblAttachIcon = new Label(this, SWT.NONE);
    lblAttachIcon.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
    Image attachImage = UiUtils.buildImageFromPngPath("/icons/chat/attach_context.png");
    lblAttachIcon.setImage(attachImage);
    lblAttachIcon.addDisposeListener(e -> {
      if (attachImage != null && !attachImage.isDisposed()) {
        attachImage.dispose();
      }
    });
    UiUtils.useParentBackground(this.lblAttachIcon);

    lblButtonText = new Label(this, SWT.NONE);
    lblButtonText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
    lblButtonText.setText("Add Context...");
    UiUtils.useParentBackground(this.lblButtonText);

    MouseAdapter clickListener = new MouseAdapter() {
      @Override
      public void mouseDown(MouseEvent e) {
        List<IResource> files = selectFiles();
        ReferencedFileService fileService = CopilotUi.getPlugin().getChatServiceManager().getReferencedFileService();
        fileService.addReferencedFiles(files);
      }
    };
    // Add mouse listener to 'this' so that clicking margin spaces will also trigger the action
    this.addMouseListener(clickListener);
    lblAttachIcon.addMouseListener(clickListener);
    lblButtonText.addMouseListener(clickListener);
    this.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));

    UiUtils.useParentBackground(this);
  }

  /**
   * Popup a file picker dialog to select files. It's guaranteed that the selected files are unique.
   */
  @NonNull
  private List<IResource> selectFiles() {
    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IContainer container = root.getContainerForLocation(root.getLocation());
    AttachFileSelectionDialog dialog = new AttachFileSelectionDialog(shell, true, container);
    dialog.setTitle(Messages.chat_filePicker_title);
    dialog.setMessage(Messages.chat_filePicker_message);
    List<IResource> result = new ArrayList<>();
    if (dialog.open() == Window.OK) {
      Object[] selectedFiles = dialog.getResult();
      Set<String> selectedFileUris = new HashSet<>();
      for (Object selectedFile : selectedFiles) {
        if (selectedFile instanceof IFile file) {
          URI fileUri = file.getLocationURI();
          if (fileUri != null && selectedFileUris.add(fileUri.toASCIIString())) {
            result.add(file);
          }
        } else if (selectedFile instanceof IFolder folder) {
          URI folderUri = folder.getLocationURI();
          if (folderUri != null && selectedFileUris.add(folderUri.toASCIIString())) {
            result.add(folder);
          }
        }
      }
    }
    return result;
  }

}
