/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import org.eclipse.copilot.api.IRunInTerminalTool;
import org.eclipse.copilot.api.TerminalToolFactory;
import org.eclipse.copilot.core.lsp.protocol.ConfirmationMessages;
import org.eclipse.copilot.core.lsp.protocol.InputSchema;
import org.eclipse.copilot.core.lsp.protocol.InputSchemaPropertyValue;
import org.eclipse.copilot.core.lsp.protocol.LanguageModelToolInformation;
import org.eclipse.copilot.core.lsp.protocol.LanguageModelToolResult;
import org.eclipse.copilot.ui.chat.ChatView;

/**
 * Adapter that bridges the UI tool interface with SPI-based terminal implementations.
 */
public class RunInTerminalToolAdapter extends BaseTool {
  private static final String TOOL_NAME = "run_in_terminal";
  private static final IRunInTerminalTool impl = TerminalToolFactory.getInstance();

  /**
   * Constructor for the RunInTerminalToolAdapter.
   */
  public RunInTerminalToolAdapter() {
    this.name = TOOL_NAME;
  }

  @Override
  public boolean needConfirmation() {
    return true;
  }
  
  @Override
  public ConfirmationMessages getConfirmationMessages() {
    return new ConfirmationMessages("Run command in terminal",
        "The tool is about to run the following command in the terminal.");
  }

  @Override
  public LanguageModelToolInformation getToolInformation() {
    // Create a new instance of LanguageModelToolInformation
    LanguageModelToolInformation toolInfo = new LanguageModelToolInformation();

    // Set the name and description of the tool
    toolInfo.setName(TOOL_NAME);
    toolInfo.setDescription("""
        Run a shell command in a terminal. State is persistent across tool calls.
        - Use this tool instead of printing a shell codeblock and asking the user to run it.
        - If the command is a long-running background process, you MUST pass isBackground=true.
        Background terminals will return a terminal ID which you can use to check the output
        of a background process with copilot_getTerminalOutput.
        - If a command may use a pager, you must something to disable it.
        For example, you can use `git --no-pager`.
        Otherwise you should add something like ` | cat`. Examples: git, less, man, etc.
        """);

    // Define the input schema for the tool
    InputSchema inputSchema = new InputSchema();
    inputSchema.setType("object");

    // Define the properties of the input schema
    Map<String, InputSchemaPropertyValue> properties = new HashMap<>();
    properties.put("command", new InputSchemaPropertyValue("string", "The command to run in the terminal"));
    properties.put("explanation", new InputSchemaPropertyValue("string", """
        A one-sentence description of what the command does.
        This will be shown to the user before the command is run."""));
    properties.put("isBackground", new InputSchemaPropertyValue("boolean", """
        Whether the command starts a background process.
        If true, the command will run in the background and you will not see the output.
        If false, the tool call will block on the command finishing, and then you will get the output.
        Examples of background processes: building in watch mode, starting a server.
        You can check the output of a background process later on by using copilot_getTerminalOutput.
        """));

    // Set the properties and required fields for the input schema
    inputSchema.setProperties(properties);
    inputSchema.setRequired(List.of("command", "explanation", "isBackground"));

    // Attach the input schema to the tool information
    toolInfo.setInputSchema(inputSchema);

    if (needConfirmation()) {
      toolInfo.setConfirmationMessages(getConfirmationMessages());
    }

    return toolInfo;
  }

  @Override
  public CompletableFuture<LanguageModelToolResult[]> invoke(Map<String, Object> input, ChatView chatView) {
    if (impl == null) {
      LanguageModelToolResult errorResult = new LanguageModelToolResult();
      errorResult.addContent("No terminal implementation available. Available implementations: "
          + TerminalToolFactory.getAllImplementations().size());
      return CompletableFuture.completedFuture(new LanguageModelToolResult[] { errorResult });
    }

    String command = (String) input.get("command");
    if (StringUtils.isBlank(command)) {
      LanguageModelToolResult errorResult = new LanguageModelToolResult();
      errorResult.addContent("The tool cannot be invoked due to the command is null or empty.");
      return CompletableFuture.completedFuture(new LanguageModelToolResult[] { errorResult });
    }

    boolean isBackground = false;
    Object isBackgroundObj = input.get("isBackground");
    if (isBackgroundObj instanceof Boolean) {
      isBackground = (Boolean) isBackgroundObj;
    } else if (isBackgroundObj instanceof String) {
      isBackground = Boolean.parseBoolean((String) isBackgroundObj);
    }

    return impl.executeCommand(command, isBackground)
        .thenApply(result -> new LanguageModelToolResult[] { new LanguageModelToolResult(result) })
        .exceptionally(throwable -> new LanguageModelToolResult[] {
            new LanguageModelToolResult("Terminal execution failed: " + throwable.getMessage()) });
  }
  
  /**
   * Tool to retrieve the output of a terminal command that was previously started with run_in_terminal.
   */
  public static class GetTerminalOutputTool extends BaseTool {
    private static final String TOOL_NAME = "get_terminal_output";

    /**
     * Constructor for GetTerminalOutputTool.
     */
    public GetTerminalOutputTool() {
      this.name = TOOL_NAME;
    }

    @Override
    public LanguageModelToolInformation getToolInformation() {
      LanguageModelToolInformation toolInfo = super.getToolInformation();

      // Set the name and description of the tool
      toolInfo.setName(TOOL_NAME);
      toolInfo.setDescription("Get the output of a terminal command previous started with run_in_terminal.");

      // Define the input schema for the tool
      InputSchema inputSchema = new InputSchema();
      inputSchema.setType("object");

      // Define the properties of the input schema
      Map<String, InputSchemaPropertyValue> properties = new HashMap<>();
      properties.put("id", new InputSchemaPropertyValue("string", "The ID of the terminal command output to check."));

      // Set the properties and required fields for the input schema
      inputSchema.setProperties(properties);
      inputSchema.setRequired(List.of("id"));

      // Attach the input schema to the tool information
      toolInfo.setInputSchema(inputSchema);

      return toolInfo;
    }

    @Override
    public CompletableFuture<LanguageModelToolResult[]> invoke(Map<String, Object> input, ChatView chatView) {
      String id = (String) input.get("id");
      LanguageModelToolResult toolResult = new LanguageModelToolResult();
      CompletableFuture<LanguageModelToolResult[]> resultFuture = new CompletableFuture<>();
      if (StringUtils.isBlank(id)) {
        toolResult.addContent("The tool cannot be invoked due to the ID is null or empty.");
      } else {
        StringBuilder output = impl.getBackgroundCommandOutput(id);
        if (output == null) {
          toolResult.addContent("Invalid terminal ID " + id);
        } else {
          toolResult.addContent(output.toString());
        }
      }
      resultFuture.complete(new LanguageModelToolResult[] { toolResult });
      return resultFuture;
    }

  }

}