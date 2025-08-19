/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat.services;

import org.eclipse.copilot.core.AuthStatusManager;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.chat.service.IChatServiceManager;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.ui.chat.tools.FileToolService;

/**
 * Manager for chat services.
 */
public class ChatServiceManager implements IChatServiceManager {

  private CopilotLanguageServerConnection lsConnection;
  private AuthStatusManager authStatusManager;

  private ChatCompletionService chatCompletionService;
  private UserPreferenceService userPreferenceService;
  private AvatarService avatarService;
  private AgentToolService agentToolService;
  private FileToolService fileToolService;
  private ReferencedFileService referencedFileService;
  private McpConfigService mcpConfigService;
  private McpRuntimeLogger mcpRuntimeLogger;

  /**
   * Constructor for the ChatServiceManager.
   */
  public ChatServiceManager() {
    this.lsConnection = CopilotCore.getPlugin().getCopilotLanguageServer();
    this.authStatusManager = CopilotCore.getPlugin().getAuthStatusManager();
    chatCompletionService = new ChatCompletionService(this.lsConnection, this.authStatusManager);
    userPreferenceService = new UserPreferenceService(this.lsConnection, this.authStatusManager);
    avatarService = new AvatarService(this.authStatusManager);
    agentToolService = new AgentToolService(this.lsConnection);
    fileToolService = new FileToolService(this.lsConnection);
    referencedFileService = new ReferencedFileService();
    mcpConfigService = new McpConfigService();
    mcpRuntimeLogger = new McpRuntimeLogger();
  }

  /**
   * Get the authentication status manager.
   */
  public AuthStatusManager getAuthStatusManager() {
    return authStatusManager;
  }

  /**
   * Get the chat command service of Ask Mode.
   *
   * @return the chat command service of Ask Mode
   */
  public ChatCompletionService getChatCompletionService() {
    return chatCompletionService;
  }

  public CopilotLanguageServerConnection getLanguageServerConnection() {
    return lsConnection;
  }

  /**
   * Get the avatar service.
   *
   * @return the avatar service
   */
  public AvatarService getAvatarService() {
    return avatarService;
  }

  /**
   * Get the copilot model service.
   *
   * @return the copilot model service
   */
  public UserPreferenceService getUserPreferenceService() {
    return userPreferenceService;
  }

  /**
   * Get the agent tool service.
   *
   * @return the agent tool service
   */
  public AgentToolService getAgentToolService() {
    return agentToolService;
  }

  /**
   * Get the edit file tool service.
   *
   * @return the edit file tool service
   */
  public FileToolService getFileToolService() {
    return fileToolService;
  }

  @Override
  public McpConfigService getMcpConfigService() {
    return mcpConfigService;
  }

  @Override
  public ReferencedFileService getReferencedFileService() {
    return referencedFileService;
  }

  /**
   * Dispose of the chat services.
   */
  public void dispose() {
    this.avatarService.dispose();
    this.chatCompletionService.dispose();
    this.userPreferenceService.dispose();
    this.agentToolService.dispose();
    this.referencedFileService.dispose();
    this.mcpConfigService.dispose();
  }

}
