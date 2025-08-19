/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.preferences;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.copilot.core.Constants;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.FeatureFlags;
import org.eclipse.copilot.core.lsp.protocol.CopilotLanguageServerSettings;
import org.eclipse.copilot.core.lsp.protocol.LanguageModelToolInformation;
import org.eclipse.copilot.core.lsp.protocol.McpServerToolsCollection;
import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.i18n.Messages;

/**
 * Preference page for GitHub Copilot MCP settings.
 */
public class McpPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

  private static final int GROUP_HEIGHT_HINT = 300;
  private static final Gson GSON = new Gson();

  private Group toolsGroup;
  private Group mcpGroup;
  private Tree toolsTree;
  private boolean hasFailedMcpServer;
  private StringFieldEditor mcpField;

  /**
   * Constructor.
   */
  public McpPreferencePage() {
    super(GRID);
  }

  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(CopilotUi.getPlugin().getPreferenceStore());
    Job job = new Job("Binding to MCP service...") {
      @Override
      protected IStatus run(IProgressMonitor monitor) {
        try {
          Job.getJobManager().join(CopilotUi.INIT_JOB_FAMILY, null);
        } catch (OperationCanceledException | InterruptedException e) {
          CopilotCore.LOGGER.error(e);
        }
        CopilotUi.getPlugin().getChatServiceManager().getMcpConfigService()
            .bindWithMcpPreferencePage(McpPreferencePage.this);
        return Status.OK_STATUS;
      }
    };
    job.setUser(true);
    job.schedule();
  }

  @Override
  protected Control createContents(Composite parent) {
    // Create a simple note for the feature disabled case
    FeatureFlags flags = CopilotCore.getPlugin().getFeatureFlags();
    if (flags != null && !flags.isMcpEnabled()) {
      return new WrappableIconLink(parent, "/icons/message_warning.png", Messages.preferences_page_mcp_disabled_tip);
    }

    // Call the default implementation for enabled case
    return super.createContents(parent);
  }

  @Override
  protected void createFieldEditors() {
    FeatureFlags flags = CopilotCore.getPlugin().getFeatureFlags();
    if (flags != null && !flags.isMcpEnabled()) {
      // Don't create field editors when MCP is disabled - handled in createContents
      return;
    }

    Composite parent = getFieldEditorParent();
    parent.setLayout(new GridLayout(1, true));
    var gl = new GridLayout(1, true);
    gl.marginTop = 2;
    gl.marginLeft = 2;

    GridDataFactory gdf = GridDataFactory.fillDefaults().span(2, 1).align(SWT.FILL, SWT.FILL).grab(true, true);
    mcpGroup = new Group(parent, SWT.NONE);
    mcpGroup.setLayout(gl);
    gdf.applyTo(mcpGroup);
    mcpGroup.setText(Messages.preferences_page_mcp_settings);
    // add mcp field
    var mcpFieldContainer = new Composite(mcpGroup, SWT.NONE);
    mcpFieldContainer.setLayout(gl);
    mcpFieldContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    mcpField = new StringFieldEditor(Constants.MCP, Messages.preferences_page_mcp, StringFieldEditor.UNLIMITED, 20,
        StringFieldEditor.VALIDATE_ON_KEY_STROKE, mcpFieldContainer) {
      @Override
      protected boolean doCheckState() {
        return validateMcpField(this);
      }

      @Override
      protected void doFillIntoGrid(Composite parent, int numColumns) {
        super.doFillIntoGrid(parent, numColumns);
        getTextControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      }
    };

    mcpField.getLabelControl(mcpFieldContainer).setToolTipText(Messages.preferences_page_mcp_tooltip);
    // @formatter:off
    mcpField.getLabelControl(mcpFieldContainer).setLayoutData(new GridData(
        SWT.LEFT, 
        SWT.TOP, 
        false, 
        false, 
        2, // The label-control will take up 2 column cells itself, so the text-control will be underneath it.
        1));
    // @formatter:on
    addField(mcpField);

    // add note to mcp field using WrappableNoteLabel
    new WrappableNoteLabel(mcpGroup, Messages.preferences_page_note_prefix, Messages.preferences_page_mcp_note_content);

    toolsGroup = new Group(parent, SWT.WRAP);
    toolsGroup.setLayout(gl);
    GridDataFactory toolsGdf = GridDataFactory.fillDefaults().span(2, 1).align(SWT.FILL, SWT.FILL).grab(true, true);
    toolsGdf.applyTo(toolsGroup);
    toolsGroup.setText(Messages.preferences_page_mcp_tools_settings);

    // Set equal height constraint for both groups
    ((GridData) mcpGroup.getLayoutData()).heightHint = GROUP_HEIGHT_HINT;
    ((GridData) toolsGroup.getLayoutData()).heightHint = GROUP_HEIGHT_HINT;
  }

  private boolean validateMcpField(StringFieldEditor mcpField) {
    String stringValue = mcpField.getStringValue();
    if (StringUtils.isBlank(stringValue)) {
      return true;
    }

    try {
      // First check for basic JSON syntax using GSON parser
      GSON.fromJson(stringValue, Object.class);

      // Second check for duplicate keys in the JSON
      try (JsonReader reader = new JsonReader(new StringReader(stringValue))) {
        // Configure the reader to be lenient using version-appropriate method
        configureLenientJsonReader(reader);
        return validateDuplicateKeys(mcpField, reader);
      }
    } catch (Exception e) {
      String errorMsg = e.getMessage();
      if (errorMsg != null) {
        int exceptionIndex = errorMsg.indexOf("Exception: ");
        if (exceptionIndex >= 0) {
          errorMsg = errorMsg.substring(exceptionIndex + "Exception: ".length());
        }

        int seeHttpsIndex = errorMsg.indexOf("See https:");
        if (seeHttpsIndex >= 0) {
          errorMsg = errorMsg.substring(0, seeHttpsIndex).trim();
        }
      }
      mcpField.setErrorMessage("SyntaxError: " + errorMsg);
      return false;
    }
  }

  /**
   * Recursively checks for duplicate keys in a JSON structure.
   */
  private boolean validateDuplicateKeys(StringFieldEditor mcpField, JsonReader reader) throws IOException {
    JsonToken token = reader.peek();

    switch (token) {
      case BEGIN_OBJECT:
        reader.beginObject();
        Set<String> objectKeys = new HashSet<>();

        while (reader.hasNext()) {
          String key = reader.nextName();
          if (!objectKeys.add(key)) {
            mcpField.setErrorMessage("Error: Duplicate key '" + key + "' found in JSON object");
            return false;
          }

          if (!validateDuplicateKeys(mcpField, reader)) {
            return false;
          }
        }

        reader.endObject();
        break;

      case BEGIN_ARRAY:
        reader.beginArray();

        while (reader.hasNext()) {
          if (!validateDuplicateKeys(mcpField, reader)) {
            return false;
          }
        }

        reader.endArray();
        break;

      case STRING:
        reader.nextString();
        break;

      case NUMBER:
        reader.nextDouble();
        break;

      case BOOLEAN:
        reader.nextBoolean();
        break;

      case NULL:
        reader.nextNull();
        break;

      default:
        reader.skipValue();
    }

    return true;
  }

  private String getServerRunningStatusHint(McpServerToolsCollection server) {
    switch (server.getStatus()) {
      case running:
      case stopped:
        return StringUtils.EMPTY;
      case error:
        return " " + Messages.preferences_page_mcp_server_init_error;
      default:
        return StringUtils.EMPTY;
    }
  }

  /**
   * Updates the UI based on the MCP enabled setting.
   *
   * @param mcpEnabled true if MCP is enabled, false otherwise
   */
  public void updateMcpPreferencePage(Boolean mcpEnabled) {
    if (mcpEnabled) {
      return;
    }

    if (mcpGroup != null && !mcpGroup.isDisposed()) {
      mcpGroup.dispose();
      mcpGroup = null;
    }

    if (toolsGroup != null && !toolsGroup.isDisposed()) {
      toolsGroup.dispose();
      toolsGroup = null;
    }
  }

  /**
   * Displays the server names and tool names in the tools group using a tree view.
   */
  public void displayServerToolsInfo(List<McpServerToolsCollection> servers) {
    if (toolsGroup == null || toolsGroup.isDisposed()) {
      return;
    }

    // Clear existing children
    for (var child : toolsGroup.getChildren()) {
      if (child != null && !child.isDisposed()) {
        child.dispose();
      }
    }

    // Create a new Tree widget with checkboxes
    toolsTree = new Tree(toolsGroup, SWT.SINGLE | SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL);
    GridData treeGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    toolsTree.setLayoutData(treeGridData);

    Map<String, Map<String, Boolean>> savedServerToolStatusMap = loadToolStatusFromPreferences();

    // Add servers and tools to the tree
    for (McpServerToolsCollection server : servers) {
      if (server == null) {
        continue;
      }

      TreeItem serverNode = new TreeItem(toolsTree, SWT.NONE);
      serverNode.setText(server.getName() + getServerRunningStatusHint(server));

      for (LanguageModelToolInformation tool : server.getTools()) {
        if (tool == null) {
          continue;
        }

        boolean isEnabled = savedServerToolStatusMap.getOrDefault(server.getName(), Map.of())
            .getOrDefault(tool.getName(), true);

        TreeItem toolNode = new TreeItem(serverNode, SWT.NONE);
        toolNode.setText(tool.getName());
        toolNode.setChecked(isEnabled);
      }

      serverNode.setExpanded(true);
      updateServerCheckStatus(serverNode);
    }

    // Add selection listener to update status changes
    toolsTree.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        if (e.detail == SWT.CHECK) {
          TreeItem item = (TreeItem) e.item;
          TreeItem parent = item.getParentItem();

          if (parent == null) {
            // Handle server node action
            updateToolsCheckStatus(item);
          } else {
            // Handle tool node action
            updateServerCheckStatus(parent);
          }
        }
      }
    });

    toolsGroup.requestLayout();
  }

  private void updateServerCheckStatus(TreeItem serverNode) {
    if (serverNode == null) {
      return;
    }

    TreeItem[] toolNodes = serverNode.getItems();
    boolean allChecked = true;
    boolean allUnchecked = true;

    for (TreeItem toolNode : toolNodes) {
      allChecked &= toolNode.getChecked();
      allUnchecked &= !toolNode.getChecked();
    }

    // corner case: server fails to init
    if (toolNodes == null || toolNodes.length == 0) {
      hasFailedMcpServer = true;
      allChecked = false;
    }

    if (allChecked) {
      serverNode.setGrayed(false);
      serverNode.setChecked(true);
    } else if (allUnchecked) {
      serverNode.setGrayed(false);
      serverNode.setChecked(false);
    } else {
      serverNode.setGrayed(true);
      serverNode.setChecked(true);
    }
  }

  private void updateToolsCheckStatus(TreeItem serverNode) {
    if (serverNode == null) {
      return;
    }

    TreeItem[] toolNodes = serverNode.getItems();
    for (TreeItem toolNode : toolNodes) {
      toolNode.setChecked(serverNode.getChecked());
    }

    serverNode.setGrayed(false);
  }

  private Map<String, Map<String, Boolean>> loadToolStatusFromPreferences() {
    Map<String, Map<String, Boolean>> result = new HashMap<>();

    IPreferenceStore preferenceStore = getPreferenceStore();
    String jsonStatus = preferenceStore.getString(Constants.MCP_TOOLS_STATUS);

    if (StringUtils.isNotBlank(jsonStatus)) {
      try {
        result = GSON.fromJson(jsonStatus, new com.google.gson.reflect.TypeToken<Map<String, Map<String, Boolean>>>() {
        }.getType());
      } catch (Exception e) {
        CopilotCore.LOGGER.error("Failed to parse MCP tools status JSON", e);
      }
    }

    return result;
  }

  private void saveToolStatusToPreferences() {
    if (toolsTree == null || toolsTree.isDisposed()) {
      return;
    }

    Map<String, Map<String, Boolean>> serverToolStatus = new HashMap<>();
    for (TreeItem serverNode : toolsTree.getItems()) {
      String serverName = serverNode.getText();
      Map<String, Boolean> toolStatus = new HashMap<>();
      for (TreeItem toolNode : serverNode.getItems()) {
        toolStatus.put(toolNode.getText(), toolNode.getChecked());
      }
      serverToolStatus.put(serverName, toolStatus);
    }

    String jsonResult = GSON.toJson(serverToolStatus);
    IPreferenceStore preferenceStore = getPreferenceStore();
    preferenceStore.setValue(Constants.MCP_TOOLS_STATUS, jsonResult);
  }

  /**
   * Resynchronizes MCP servers when there are failed server instances.
   *
   * <p>
   * This method is specifically designed to handle cases where the MCP field value remains unchanged but server
   * synchronization is still required. When the field value doesn't change, the normal property change event mechanism
   * is not triggered, so this method provides an alternative way to force server resynchronization.
   * </p>
   */
  private void resyncMcpServers() {
    if (!hasFailedMcpServer) {
      return;
    }
    hasFailedMcpServer = false;

    IPreferenceStore preferenceStore = getPreferenceStore();
    String storedMcp = preferenceStore.getString(Constants.MCP);
    String currentMcp = mcpField.getStringValue();
    if (StringUtils.equals(currentMcp, storedMcp)) {
      CopilotLanguageServerSettings settings = new CopilotLanguageServerSettings();
      settings.setMcpServers(mcpField.getStringValue());
      LanguageServerSettingManager mgr = CopilotUi.getPlugin().getLanguageServerSettingManager();
      mgr.syncSingleConfiguration(new CopilotLanguageServerSettings(null, null, null, settings.getGithubSettings()));
    }
  }

  @Override
  public boolean performOk() {
    saveToolStatusToPreferences();
    resyncMcpServers();
    return super.performOk();
  }

  /**
   * Configures a JsonReader to be lenient using reflection to handle different Gson versions. Tries to use
   * setStrictness(Strictness.LENIENT) for newer Gson versions, falls back to setLenient(true) for older versions.
   */
  private void configureLenientJsonReader(JsonReader reader) {
    try {
      // Load Strictness enum class dynamically
      Class<?> strictnessClass = Class.forName("com.google.gson.Strictness");
      Object lenientValue = null;

      // Get the LENIENT enum value
      for (Object enumConstant : strictnessClass.getEnumConstants()) {
        if ("LENIENT".equals(enumConstant.toString())) {
          lenientValue = enumConstant;
          break;
        }
      }

      // Get setStrictness method and invoke it
      if (lenientValue != null) {
        Method setStrictnessMethod = JsonReader.class.getMethod("setStrictness", strictnessClass);
        setStrictnessMethod.invoke(reader, lenientValue);
      }
    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      reader.setLenient(true); // Fallback to older API
    }
  }
}