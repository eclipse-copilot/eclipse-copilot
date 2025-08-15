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

package org.eclipse.copilot.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.osgi.service.prefs.BackingStoreException;

import org.eclipse.copilot.core.Constants;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.i18n.Messages;

/**
 * This class is used to create the preference page for the plugin.
 */
public class CopilotPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  private Composite parent;
  private ProxyConfigLinkListener proxyConfigLinkListener;
  private Link link;
  private ControlListener controlListener;

  /**
   * Constructor.
   */
  public CopilotPreferencesPage() {
    super(GRID);
  }

  @Override
  public void createFieldEditors() {
    this.parent = getFieldEditorParent();
    parent.setLayout(new GridLayout(1, true));
    var gl = new GridLayout(1, true);
    gl.marginTop = 2;
    gl.marginLeft = 2;

    // editor group
    GridDataFactory gdf = GridDataFactory.fillDefaults().span(2, 1).align(SWT.FILL, SWT.FILL).grab(true, false);
    Group grpEditor = new Group(parent, SWT.NONE);
    grpEditor.setLayout(gl);
    gdf.applyTo(grpEditor);
    grpEditor.setText(Messages.preferences_page_editor_settings);
    // add auto show completion field
    var ctnAutoComplete = new Composite(grpEditor, SWT.NONE);
    ctnAutoComplete.setLayout(gl);
    var bfeAutoComplete = new BooleanFieldEditor(Constants.AUTO_SHOW_COMPLETION,
        Messages.preferencesPage_autoShowCompletion, ctnAutoComplete);
    addField(bfeAutoComplete);

    // proxy group
    Group grpProxy = new Group(parent, SWT.NONE);
    grpProxy.setLayout(gl);
    gdf.applyTo(grpProxy);
    grpProxy.setText(Messages.preferences_page_proxy_settings);
    // add proxy configuration link
    var linkContainer = new Composite(grpProxy, SWT.NONE);
    var glTextIndent = new GridLayout(1, false);
    glTextIndent.marginLeft = -3;
    glTextIndent.marginBottom = 1;
    linkContainer.setLayout(glTextIndent);
    this.link = new Link(linkContainer, SWT.NONE);
    link.setText(Messages.preferences_page_proxy_config_link);
    link.setToolTipText(Messages.preferences_page_proxy_config_link_tooltip);
    this.proxyConfigLinkListener = new ProxyConfigLinkListener();
    link.addSelectionListener(this.proxyConfigLinkListener);

    // add strict ssl field
    var ctnSsl = new Composite(grpProxy, SWT.NONE);
    ctnSsl.setLayout(gl);
    var bfeSsl = new BooleanFieldEditor(Constants.ENABLE_STRICT_SSL, Messages.preferences_page_enable_strict_ssl,
        ctnSsl);
    bfeSsl.getDescriptionControl(ctnSsl).setToolTipText(Messages.preferences_page_enable_strict_ssl_tooltip);
    addField(bfeSsl);

    // add Note using WrappableNoteLabel
    new WrappableNoteLabel(grpProxy, Messages.preferences_page_note_prefix, Messages.preferences_page_note_content);

    // auth group
    Group grpAuth = new Group(parent, SWT.NONE);
    grpAuth.setLayout(gl);
    gdf.applyTo(grpAuth);
    grpAuth.setText(Messages.preferences_page_auth_settings);
    // add github enterprise field
    var ctnGhe = new Composite(grpAuth, SWT.NONE);
    ctnGhe.setLayout(gl);
    ctnGhe.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    var sftGhe = new StringFieldEditor(Constants.GITHUB_ENTERPRISE, Messages.preferences_page_github_enterprise,
        ctnGhe);
    sftGhe.getLabelControl(ctnGhe).setToolTipText(Messages.preferences_page_github_enterprise_tooltip);
    addField(sftGhe);

    // chat group
    Group chatGroup = new Group(parent, SWT.NONE);
    chatGroup.setLayout(gl);
    gdf.applyTo(chatGroup);
    chatGroup.setText(Messages.preferences_page_chat_settings);
    Composite chatComposite = new Composite(chatGroup, SWT.NONE);
    chatComposite.setLayout(gl);
    chatComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    BooleanFieldEditor workspaceContextField = new BooleanFieldEditor(Constants.WORKSPACE_CONTEXT_ENABLED,
        Messages.preferences_page_watched_files, SWT.WRAP, chatComposite);
    GridData workspaceContextFieldGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    workspaceContextFieldGridData.widthHint = 400;
    workspaceContextField.getDescriptionControl(chatComposite).setLayoutData(workspaceContextFieldGridData);

    addField(workspaceContextField);

    // add chat note using WrappableNoteLabel
    new WrappableNoteLabel(chatGroup, Messages.preferences_page_note_prefix,
        Messages.preferences_page_watched_files_note_content);

    // Add control listener to handle workspace context field resizing
    controlListener = new ControlAdapter() {
      @Override
      public void controlResized(ControlEvent e) {
        // resize the workspace context field description
        var pg = CopilotPreferencesPage.this;
        int width = pg.getFieldEditorParent().getSize().x - 20;
        ((GridData) workspaceContextField.getDescriptionControl(chatComposite).getLayoutData()).widthHint = width;
        pg.getFieldEditorParent().layout();
      }
    };
    parent.addControlListener(controlListener);
    parent.addDisposeListener(e -> {
      parent.removeControlListener(controlListener);
    });
  }

  /**
   * Listener for the proxy configuration link.
   */
  public class ProxyConfigLinkListener extends SelectionAdapter {
    @Override
    public void widgetSelected(SelectionEvent e) {
      PreferencesUtil.createPreferenceDialogOn(getShell(), "org.eclipse.ui.net.NetPreferences", null, e);
    }
  }

  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(CopilotUi.getPlugin().getPreferenceStore());
  }

  @Override
  public boolean performOk() {
    boolean oldWorkspaceContextValue = getPreferenceStore().getBoolean(Constants.WORKSPACE_CONTEXT_ENABLED);
    boolean result = super.performOk();
    boolean newWorkspaceContextValue = getPreferenceStore().getBoolean(Constants.WORKSPACE_CONTEXT_ENABLED);

    if (oldWorkspaceContextValue ^ newWorkspaceContextValue) {
      boolean restart = MessageDialog.openQuestion(getShell(), Messages.preferences_page_restart_required,
          Messages.preferences_page_watched_files_restart_question);

      if (restart) {
        try {
          // Explicitly save the preferences to disk to ensure they persist across the restart
          // CopilotUi.getPlugin().savePluginPreferences() is deprecated, flush is recommended
          InstanceScope.INSTANCE.getNode("org.eclipse.copilot.ui").flush();
        } catch (BackingStoreException e) {
          CopilotCore.LOGGER.error("Failed to save preference 'Enable workspace context'", e);
        }

        // Close the preference dialog properly before restarting
        getShell().getDisplay().asyncExec(() -> {
          // Using asyncExec ensures the preference dialog completes its current operations
          PlatformUI.getWorkbench().restart();
        });
      }
    }

    return result;
  }

  @Override
  public void dispose() {
    if (link != null && !link.isDisposed() && proxyConfigLinkListener != null) {
      link.removeSelectionListener(proxyConfigLinkListener);
    }
    super.dispose();
  }
}