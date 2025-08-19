/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat.services;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jdt.annotation.Nullable;

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.chat.ChatEventsManager;
import org.eclipse.copilot.core.chat.ToolInvocationListener;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.core.lsp.protocol.InvokeClientToolConfirmationParams;
import org.eclipse.copilot.core.lsp.protocol.InvokeClientToolParams;
import org.eclipse.copilot.core.lsp.protocol.LanguageModelToolConfirmationResult;
import org.eclipse.copilot.core.lsp.protocol.LanguageModelToolResult;
import org.eclipse.copilot.core.lsp.protocol.RegisterToolsParams;
import org.eclipse.copilot.ui.chat.BaseTurnWidget;
import org.eclipse.copilot.ui.chat.ChatContentViewer;
import org.eclipse.copilot.ui.chat.ChatView;
import org.eclipse.copilot.ui.chat.tools.BaseTool;
import org.eclipse.copilot.ui.chat.tools.CreateFileTool;
import org.eclipse.copilot.ui.chat.tools.EditFileTool;
import org.eclipse.copilot.ui.chat.tools.GetErrorsTool;
import org.eclipse.copilot.ui.chat.tools.RunInTerminalToolAdapter;
import org.eclipse.copilot.ui.chat.tools.RunInTerminalToolAdapter.GetTerminalOutputTool;
import org.eclipse.copilot.ui.utils.SwtUtils;

/**
 * Service to manage and access tools.
 */
public class AgentToolService implements ToolInvocationListener {
  private ConcurrentHashMap<String, BaseTool> tools;
  private ChatView boundChatView;

  protected CopilotLanguageServerConnection lsConnection;

  /**
   * Constructor for AgentToolService.
   */
  public AgentToolService(CopilotLanguageServerConnection lsConnection) {
    this.tools = new ConcurrentHashMap<>();
    this.lsConnection = lsConnection;
    registerTerminalTools();
    registerDefaultTools();
  }

  /**
   * Register default tools.
   */
  private void registerDefaultTools() {
    // File operations
    registerTool(new CreateFileTool());
    registerTool(new EditFileTool());

    // Diagnostic tools
    registerTool(new GetErrorsTool());

    // Register the tools to the language server
    registerToolWithServer();

    ChatEventsManager chatEventsManager = CopilotCore.getPlugin().getChatEventsManager();
    if (chatEventsManager != null) {
      chatEventsManager.registerAgentToolListener(this);
    }
  }

  /**
   * Register tools to the language server.
   *
   * @param registerToolsParams The parameters for tool registration
   * @return true if registration was successful, false otherwise
   */
  private void registerToolWithServer() {
    RegisterToolsParams registerToolsParams = new RegisterToolsParams();
    for (BaseTool tool : getAllTools()) {
      registerToolsParams.addTool(tool.getToolInformation());
    }

    lsConnection.registerTools(registerToolsParams).thenAccept(registrationResult -> {
      if (!Objects.equals("OK", registrationResult)) {
        CopilotCore.LOGGER
            .error(new IllegalStateException("Tool registration failed with result: " + registrationResult));
      }
    }).exceptionally(e -> {
      CopilotCore.LOGGER.error("Error registering tools with the server", e);
      return null;
    });
  }

  
  private void registerTerminalTools() {
    registerTool(new RunInTerminalToolAdapter());
    registerTool(new GetTerminalOutputTool());
  }

  /**
   * Register a tool.
   *
   * @param tool The tool to register
   */
  public void registerTool(BaseTool tool) {
    tools.put(tool.getToolName(), tool);
  }

  /**
   * Unregister a tool.
   *
   * @param toolName The name of the tool to unregister
   * @return The removed tool, or null if not found
   */
  public BaseTool unregisterTool(String toolName) {
    return tools.remove(toolName);
  }

  /**
   * Get a tool by name.
   *
   * @param toolName The name of the tool
   * @return The tool, or null if not found
   */
  public BaseTool getTool(String toolName) {
    return tools.get(toolName);
  }

  /**
   * Get all registered tools.
   *
   * @return An unmodifiable collection of all tools
   */
  public Collection<BaseTool> getAllTools() {
    return Collections.unmodifiableCollection(tools.values());
  }

  /**
   * Bind the chat view to the auth status.
   */
  public void bindChatView(ChatView chatView) {
    if (chatView == null) {
      return;
    }

    // Unbind any previously bound chat view
    unbindChatView();
    this.boundChatView = chatView;
  }

  /**
   * Unbind the currently bound chat view if any.
   */
  public void unbindChatView() {
    boundChatView = null;
  }

  /**
   * Invoke a tool by its name.
   *
   * @param toolName The name of the tool to invoke
   * @return The result of the tool invocation, or null if the tool was not found
   */
  public CompletableFuture<LanguageModelToolResult[]> invokeTool(String toolName, @Nullable Map<String, Object> input,
      @Nullable ChatView chatView) {
    BaseTool tool = getTool(toolName);
    LanguageModelToolResult result = new LanguageModelToolResult();
    if (tool == null) {
      result.addContent("Tool invocation failed due to the tool not found: " + toolName);
      return CompletableFuture.completedFuture(new LanguageModelToolResult[] { result });
    }

    return tool.invoke(input, chatView);
  }

  @Override
  public CompletableFuture<LanguageModelToolResult[]> onToolInvocation(InvokeClientToolParams params) {
    if (!validToolConfirmInvokeParams(params.getConversationId(), params.getTurnId())) {
      return null;
    }
    return invokeTool(params.getName(), (Map<String, Object>) params.getInput(), boundChatView);
  }

  @Override
  public CompletableFuture<LanguageModelToolConfirmationResult> onToolConfirmation(
      InvokeClientToolConfirmationParams params) {
    if (!validToolConfirmInvokeParams(params.getConversationId(), params.getTurnId())) {
      return null;
    }

    BaseTurnWidget turnWidget = boundChatView.getChatContentViewer().getTurnWidget(params.getTurnId());
    if (turnWidget == null) {
      return null;
    }
    AtomicReference<CompletableFuture<LanguageModelToolConfirmationResult>> ref = new AtomicReference<>();
    SwtUtils.invokeOnDisplayThread(() -> {
      ref.set(turnWidget.requestToolExecutionConfirmation(params.getTitle(), params.getMessage(), params.getInput()));
      boundChatView.getChatContentViewer().refreshScrollerLayout();
    });

    return ref.get();
  }

  private boolean validToolConfirmInvokeParams(String conversationId, String turnId) {
    if (boundChatView == null || !Objects.equals(conversationId, boundChatView.getConversationId())) {
      return false;
    }

    ChatContentViewer chatContentViewer = boundChatView.getChatContentViewer();
    if (chatContentViewer == null || chatContentViewer.getTurnWidget(turnId) == null) {
      return false;
    }
    return true;
  }

  /**
   * Dispose the service.
   */
  public void dispose() {
    this.tools.clear();
    unbindChatView();
  }
}