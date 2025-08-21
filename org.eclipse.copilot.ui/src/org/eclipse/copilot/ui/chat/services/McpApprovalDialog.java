/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat.services;

import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.copilot.ui.chat.services.McpConfigService.McpRegistrationInfo;

/**
 * Dialog for approving third-party MCP providers.
 */
public class McpApprovalDialog extends Dialog {
  
  private Map<String, McpRegistrationInfo> mcpRegInfoMap;
  
  private ListViewer contributorListViewer;
  private Text mcpServersPreviewText;
  private Button approveButton;
  private Button denyButton;
  private Label trustStatusLabel;
  
  private String selectedContributor;

  public McpApprovalDialog(Shell parentShell, Map<String, McpRegistrationInfo> mcpRegInfoMap) {
    super(parentShell);
    this.mcpRegInfoMap = mcpRegInfoMap;
  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("MCP Provider Approval");
  }

  @Override
  protected Point getInitialSize() {
    return new Point(600, 500);
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);
    GridLayoutFactory.swtDefaults().numColumns(2).margins(10, 10).applyTo(container);

    // Title and description
    Label titleLabel = new Label(container, SWT.WRAP);
    titleLabel.setText("Third-party MCP Providers Detected");
    titleLabel.setFont(container.getFont());
    GridDataFactory.fillDefaults().span(2, 1).applyTo(titleLabel);

    Label descLabel = new Label(container, SWT.WRAP);
    descLabel.setText("The following third-party plugins want to register MCP servers with GitHub Copilot. " +
                     "Please review and approve or deny each provider. Click on a provider name to preview its MCP servers.");
    GridDataFactory.fillDefaults().span(2, 1).hint(550, SWT.DEFAULT).applyTo(descLabel);

    // Left panel - Contributors list
    Group contributorsGroup = new Group(container, SWT.NONE);
    contributorsGroup.setText("Contributors");
    GridLayoutFactory.swtDefaults().applyTo(contributorsGroup);
    GridDataFactory.fillDefaults().grab(true, true).hint(250, SWT.DEFAULT).applyTo(contributorsGroup);

    contributorListViewer = new ListViewer(contributorsGroup, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
    GridDataFactory.fillDefaults().grab(true, true).applyTo(contributorListViewer.getControl());

    contributorListViewer.setContentProvider(new IStructuredContentProvider() {
      @Override
      public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Map) {
          @SuppressWarnings("unchecked")
          Map<String, McpRegistrationInfo> map = (Map<String, McpRegistrationInfo>) inputElement;
          return map.keySet().toArray();
        }
        return new Object[0];
      }
    });

    contributorListViewer.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(Object element) {
        if (element instanceof String) {
          String contributor = (String) element;
          McpRegistrationInfo info = mcpRegInfoMap.get(contributor);
          String status = info != null && info.isApproved() ? " ✓" : " ✗";
          String trust = info != null && info.isTrusted() ? " [Signed]" : " [Unsigned]";
          return contributor + status + trust;
        }
        return super.getText(element);
      }
    });

    contributorListViewer.setInput(mcpRegInfoMap);

    contributorListViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        if (!selection.isEmpty()) {
          selectedContributor = (String) selection.getFirstElement();
          updatePreview();
        }
      }
    });

    // Right panel - Preview and approval controls
    Group previewGroup = new Group(container, SWT.NONE);
    previewGroup.setText("MCP Servers Preview");
    GridLayoutFactory.swtDefaults().applyTo(previewGroup);
    GridDataFactory.fillDefaults().grab(true, true).applyTo(previewGroup);

    trustStatusLabel = new Label(previewGroup, SWT.WRAP);
    GridDataFactory.fillDefaults().grab(true, false).applyTo(trustStatusLabel);

    mcpServersPreviewText = new Text(previewGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
    GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 200).applyTo(mcpServersPreviewText);

    // Approval buttons
    Composite buttonComposite = new Composite(previewGroup, SWT.NONE);
    GridLayoutFactory.swtDefaults().numColumns(2).applyTo(buttonComposite);
    GridDataFactory.fillDefaults().grab(true, false).applyTo(buttonComposite);

    approveButton = new Button(buttonComposite, SWT.PUSH);
    approveButton.setText("Approve");
    GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(approveButton);
    approveButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (selectedContributor != null) {
          McpRegistrationInfo info = mcpRegInfoMap.get(selectedContributor);
          if (info != null) {
            info.setApproved(true);
            contributorListViewer.refresh();
          }
        }
      }
    });

    denyButton = new Button(buttonComposite, SWT.PUSH);
    denyButton.setText("Deny");
    GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(denyButton);
    denyButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (selectedContributor != null) {
          McpRegistrationInfo info = mcpRegInfoMap.get(selectedContributor);
          if (info != null) {
            info.setApproved(false);
            contributorListViewer.refresh();
          }
        }
      }
    });

    // Initial state
    updatePreview();

    return container;
  }

  private void updatePreview() {
    if (selectedContributor == null || mcpRegInfoMap.get(selectedContributor) == null) {
      mcpServersPreviewText.setText("Select a contributor to preview MCP servers.");
      trustStatusLabel.setText("");
      approveButton.setEnabled(false);
      denyButton.setEnabled(false);
      return;
    }

    McpRegistrationInfo info = mcpRegInfoMap.get(selectedContributor);
    
    // Update trust status
    String trustText = info.isTrusted() ? 
        "This provider is from a signed bundle and is considered trusted." :
        "This provider is from an unsigned bundle. Please verify its authenticity.";
    trustStatusLabel.setText(trustText);

    // Update MCP servers preview
    String mcpServers = info.getMcpServers();
    if (mcpServers != null && !mcpServers.trim().isEmpty()) {
      mcpServersPreviewText.setText(mcpServers);
    } else {
      mcpServersPreviewText.setText("No MCP server configuration provided.");
    }

    approveButton.setEnabled(true);
    denyButton.setEnabled(true);
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, "Apply Changes", true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }
}