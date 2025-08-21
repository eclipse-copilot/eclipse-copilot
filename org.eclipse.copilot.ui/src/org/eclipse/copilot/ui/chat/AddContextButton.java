/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
  private Button btnAttach;

  /**
   * Creates a new AddContextButton.
   */
  public AddContextButton(Composite parent) {
    super(parent, SWT.NONE);
    GridLayout layout = new GridLayout(1, false);
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    setLayout(layout);

    final Image attachImage = UiUtils.buildImageFromPngPath("/icons/chat/attach_context.png");

    btnAttach = new Button(this, SWT.PUSH | SWT.FLAT);
    btnAttach.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
    btnAttach.setText(Messages.chat_addContextButton_title);
    btnAttach.setToolTipText(Messages.chat_addContextButton_tooltip);
    btnAttach.setImage(attachImage);
    btnAttach.setAlignment(SWT.LEFT);
    btnAttach.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_HAND));

    // Ensure tab traversal within this composite goes to the main button
    setTabList(new Control[] { btnAttach });

    // Provide an accessible name/description for screen readers and accessibility tools
    getAccessible().addAccessibleListener(new AccessibleAdapter() {
      @Override
      public void getName(AccessibleEvent e) {
        if (e.result == null || e.result.isEmpty()) {
          e.result = Messages.chat_filePicker_title;
        }
      }

      @Override
      public void getHelp(AccessibleEvent e) {
        if (e.result == null || e.result.isEmpty()) {
          e.result = Messages.chat_filePicker_message;
        }
      }
    });

    btnAttach.addListener(SWT.Selection, e -> {
      List<IResource> files = selectFiles();
      ReferencedFileService fileService = CopilotUi.getPlugin().getChatServiceManager().getReferencedFileService();
      fileService.addReferencedFiles(files);
    });

    this.addDisposeListener(e -> {
      if (attachImage != null && !attachImage.isDisposed()) {
        attachImage.dispose();
      }
    });
  }

  @Override
  public boolean setFocus() {
    if (btnAttach != null && !btnAttach.isDisposed()) {
      return btnAttach.setFocus();
    }
    return super.setFocus();
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