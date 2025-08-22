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
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import org.eclipse.copilot.ui.chat.services.McpConfigService.McpRegistrationInfo;

/**
 * Dialog for approving third-party MCP providers.
 */
public class McpApprovalDialog extends Dialog {
  
  private Map<String, McpRegistrationInfo> mcpRegInfoMap;
  
  private TableViewer contributorTableViewer;
  private Text mcpServersPreviewText;
  private Button approveButton;
  private Button denyButton;
  private Button approveAllButton;
  private Button denyAllButton;
  private Label statusLabel;
  
  private String selectedContributor;

  public McpApprovalDialog(Shell parentShell, Map<String, McpRegistrationInfo> mcpRegInfoMap) {
    super(parentShell);
    this.mcpRegInfoMap = mcpRegInfoMap;
    setShellStyle(getShellStyle() | SWT.RESIZE);
  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("MCP Provider Approval");
  }

  @Override
  protected Point getInitialSize() {
    return new Point(800, 600);
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);
    GridLayoutFactory.swtDefaults().numColumns(1).margins(10, 10).spacing(5, 10).applyTo(container);

    // Description area
    createDescriptionArea(container);
    
    // Top area with table and buttons
    Composite topArea = new Composite(container, SWT.NONE);
    GridLayoutFactory.swtDefaults().numColumns(2).spacing(10, 5).applyTo(topArea);
    GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 350).applyTo(topArea);
    
    // Left panel - Contributors table
    createContributorsArea(topArea);
    
    // Right panel - Action buttons (vertical layout like Templates page)
    createActionButtonsArea(topArea);
    
    // Bottom area - Preview and details
    createPreviewArea(container);

    // Initialize selection
    if (!mcpRegInfoMap.isEmpty()) {
      contributorTableViewer.getTable().select(0);
      updateSelection();
    }

    return container;
  }

  private void createDescriptionArea(Composite parent) {
    Label descLabel = new Label(parent, SWT.WRAP);
    descLabel.setText("Review and approve third-party plugins that want to register MCP servers with GitHub Copilot.");
    GridDataFactory.fillDefaults().hint(750, SWT.DEFAULT).applyTo(descLabel);
  }

  private void createContributorsArea(Composite parent) {
    // Contributors group
    Group contributorsGroup = new Group(parent, SWT.NONE);
    contributorsGroup.setText("&Providers:");
    GridLayoutFactory.swtDefaults().margins(5, 5).applyTo(contributorsGroup);
    GridDataFactory.fillDefaults().grab(true, true).hint(500, SWT.DEFAULT).applyTo(contributorsGroup);

    // Table viewer
    contributorTableViewer = new TableViewer(contributorsGroup, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
    Table table = contributorTableViewer.getTable();
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 250).applyTo(table);

    // Create columns
    TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
    nameColumn.setText("Provider Name");
    nameColumn.setWidth(280);

    TableColumn statusColumn = new TableColumn(table, SWT.CENTER);
    statusColumn.setText("Status");
    statusColumn.setWidth(100);

    TableColumn trustColumn = new TableColumn(table, SWT.CENTER);
    trustColumn.setText("Trust");
    trustColumn.setWidth(80);

    contributorTableViewer.setContentProvider(new IStructuredContentProvider() {
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

    contributorTableViewer.setLabelProvider(new ContributorLabelProvider());
    contributorTableViewer.setInput(mcpRegInfoMap);

    contributorTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        updateSelection();
        updateButtonStates();
      }
    });
  }

  private void createActionButtonsArea(Composite parent) {
    // Button area - vertical layout like Templates page
    Composite buttonArea = new Composite(parent, SWT.NONE);
    GridLayoutFactory.swtDefaults().numColumns(1).spacing(5, 5).applyTo(buttonArea);
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(buttonArea);

    approveButton = new Button(buttonArea, SWT.PUSH);
    approveButton.setText("A&pprove");
    GridDataFactory.swtDefaults().hint(90, SWT.DEFAULT).applyTo(approveButton);
    approveButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        approveSelected();
      }
    });

    denyButton = new Button(buttonArea, SWT.PUSH);
    denyButton.setText("&Deny");
    GridDataFactory.swtDefaults().hint(90, SWT.DEFAULT).applyTo(denyButton);
    denyButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        denySelected();
      }
    });

    // Separator
    Label separator = new Label(buttonArea, SWT.SEPARATOR | SWT.HORIZONTAL);
    GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 2).applyTo(separator);

    approveAllButton = new Button(buttonArea, SWT.PUSH);
    approveAllButton.setText("Approve &All");
    GridDataFactory.swtDefaults().hint(90, SWT.DEFAULT).applyTo(approveAllButton);
    approveAllButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        approveAll();
      }
    });

    denyAllButton = new Button(buttonArea, SWT.PUSH);
    denyAllButton.setText("D&eny All");
    GridDataFactory.swtDefaults().hint(90, SWT.DEFAULT).applyTo(denyAllButton);
    denyAllButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        denyAll();
      }
    });
  }

  private void createPreviewArea(Composite parent) {
    // Preview group at the bottom
    Group previewGroup = new Group(parent, SWT.NONE);
    previewGroup.setText("&Details:");
    GridLayoutFactory.swtDefaults().numColumns(1).margins(5, 5).spacing(5, 5).applyTo(previewGroup);
    GridDataFactory.fillDefaults().grab(true, true).applyTo(previewGroup);

    // Status label
    statusLabel = new Label(previewGroup, SWT.WRAP);
    GridDataFactory.fillDefaults().grab(true, false).hint(750, SWT.DEFAULT).applyTo(statusLabel);

    // MCP Servers
    Label serversLabel = new Label(previewGroup, SWT.NONE);
    serversLabel.setText("&MCP Server Configuration:");
    GridDataFactory.fillDefaults().applyTo(serversLabel);

    mcpServersPreviewText = new Text(previewGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
    mcpServersPreviewText.setFont(parent.getFont());
    GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 150).applyTo(mcpServersPreviewText);
  }

  private void updateButtonStates() {
    boolean hasSelection = selectedContributor != null;
    approveButton.setEnabled(hasSelection);
    denyButton.setEnabled(hasSelection);
    
    boolean hasProviders = !mcpRegInfoMap.isEmpty();
    approveAllButton.setEnabled(hasProviders);
    denyAllButton.setEnabled(hasProviders);
  }

  private void updateSelection() {
    IStructuredSelection selection = (IStructuredSelection) contributorTableViewer.getSelection();
    if (!selection.isEmpty()) {
      selectedContributor = (String) selection.getFirstElement();
      updatePreview();
    } else {
      selectedContributor = null;
      clearPreview();
    }
  }

  private void updatePreview() {
    if (selectedContributor == null || mcpRegInfoMap.get(selectedContributor) == null) {
      clearPreview();
      return;
    }

    McpRegistrationInfo info = mcpRegInfoMap.get(selectedContributor);
    
    // Update status
    String trustText = info.isTrusted() ? 
        "This provider is from a signed bundle and is considered trusted." :
        "This provider is from an unsigned bundle. Please verify its authenticity before approving.";
    statusLabel.setText(trustText);

    // Update MCP servers preview
    String mcpServers = info.getMcpServers();
    if (mcpServers != null && !mcpServers.trim().isEmpty()) {
      mcpServersPreviewText.setText(mcpServers);
    } else {
      mcpServersPreviewText.setText("No MCP server configuration provided by this provider.");
    }

    approveButton.setEnabled(true);
    denyButton.setEnabled(true);
  }

  private void clearPreview() {
    statusLabel.setText("Select a provider to view details.");
    mcpServersPreviewText.setText("");
    approveButton.setEnabled(false);
    denyButton.setEnabled(false);
  }

  private void approveSelected() {
    if (selectedContributor != null) {
      McpRegistrationInfo info = mcpRegInfoMap.get(selectedContributor);
      if (info != null) {
        info.setApproved(true);
        contributorTableViewer.refresh();
        updatePreview();
      }
    }
  }

  private void denySelected() {
    if (selectedContributor != null) {
      McpRegistrationInfo info = mcpRegInfoMap.get(selectedContributor);
      if (info != null) {
        info.setApproved(false);
        contributorTableViewer.refresh();
        updatePreview();
      }
    }
  }

  private void approveAll() {
    for (McpRegistrationInfo info : mcpRegInfoMap.values()) {
      info.setApproved(true);
    }
    contributorTableViewer.refresh();
    updatePreview();
  }

  private void denyAll() {
    for (McpRegistrationInfo info : mcpRegInfoMap.values()) {
      info.setApproved(false);
    }
    contributorTableViewer.refresh();
    updatePreview();
  }

  @Override
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, "&Apply Changes", true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * Label provider for the contributors table
   */
  private class ContributorLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
      return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
        String contributor = (String) element;
        McpRegistrationInfo info = mcpRegInfoMap.get(contributor);
        
        switch (columnIndex) {
          case 0: // Provider Name
            return contributor;
          case 1: // Status
            return info != null && info.isApproved() ? "Approved" : "Pending";
          case 2: // Trust
            return info != null && info.isTrusted() ? "Signed" : "Unsigned";
          default:
            return "";
        }
      }
      return "";
    }
  }
}