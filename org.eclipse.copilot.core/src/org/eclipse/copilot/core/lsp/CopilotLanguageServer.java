/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.lsp;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageServer;

import org.eclipse.copilot.core.lsp.protocol.ChatCreateResult;
import org.eclipse.copilot.core.lsp.protocol.ChatPersistence;
import org.eclipse.copilot.core.lsp.protocol.ChatTurnResult;
import org.eclipse.copilot.core.lsp.protocol.CheckStatusParams;
import org.eclipse.copilot.core.lsp.protocol.CompletionParams;
import org.eclipse.copilot.core.lsp.protocol.CompletionResult;
import org.eclipse.copilot.core.lsp.protocol.ConversationAgent;
import org.eclipse.copilot.core.lsp.protocol.ConversationCodeCopyParams;
import org.eclipse.copilot.core.lsp.protocol.ConversationCreateParams;
import org.eclipse.copilot.core.lsp.protocol.ConversationTemplate;
import org.eclipse.copilot.core.lsp.protocol.ConversationTurnParams;
import org.eclipse.copilot.core.lsp.protocol.CopilotModel;
import org.eclipse.copilot.core.lsp.protocol.CopilotStatusResult;
import org.eclipse.copilot.core.lsp.protocol.McpServerToolsCollection;
import org.eclipse.copilot.core.lsp.protocol.NotifyAcceptedParams;
import org.eclipse.copilot.core.lsp.protocol.NotifyCodeAcceptanceParams;
import org.eclipse.copilot.core.lsp.protocol.NotifyRejectedParams;
import org.eclipse.copilot.core.lsp.protocol.NotifyShownParams;
import org.eclipse.copilot.core.lsp.protocol.NullParams;
import org.eclipse.copilot.core.lsp.protocol.RegisterToolsParams;
import org.eclipse.copilot.core.lsp.protocol.SignInConfirmParams;
import org.eclipse.copilot.core.lsp.protocol.SignInInitiateResult;
import org.eclipse.copilot.core.lsp.protocol.TelemetryExceptionParams;
import org.eclipse.copilot.core.lsp.protocol.UpdateMcpToolsStatusParams;
import org.eclipse.copilot.core.lsp.protocol.git.GenerateCommitMessageParams;
import org.eclipse.copilot.core.lsp.protocol.git.GenerateCommitMessageResult;
import org.eclipse.copilot.core.lsp.protocol.quota.CheckQuotaResult;

/**
 * Interface for Copilot Language Server.
 */
public interface CopilotLanguageServer extends LanguageServer {

  /**
   * Check the login status for current machine.
   */
  @JsonRequest
  CompletableFuture<CopilotStatusResult> checkStatus(CheckStatusParams param);

  /**
   * Check the uesr's quota status.
   */
  @JsonRequest
  CompletableFuture<CheckQuotaResult> checkQuota(NullParams param);

  /**
   * Get single completion for the given parameters.
   */
  @JsonRequest
  CompletableFuture<CompletionResult> getCompletions(CompletionParams params);

  /**
   * Initiate the sign in process.
   */
  @JsonRequest
  CompletableFuture<SignInInitiateResult> signInInitiate(NullParams param);

  /**
   * Confirm the sign in process.
   */
  @JsonRequest
  CompletableFuture<CopilotStatusResult> signInConfirm(SignInConfirmParams param);

  /**
   * Sign out the current user.
   */
  @JsonRequest
  CompletableFuture<CopilotStatusResult> signOut(NullParams params);

  /**
   * Notify the language server that the completion was shown.
   */
  @JsonRequest
  CompletableFuture<String> notifyShown(NotifyShownParams params);

  /**
   * Notify the language server that the completion was accepted.
   */
  @JsonRequest
  CompletableFuture<String> notifyAccepted(NotifyAcceptedParams params);

  /**
   * Notify the language server that the completion was rejected.
   */
  @JsonRequest
  CompletableFuture<String> notifyRejected(NotifyRejectedParams params);

  /**
   * Send exception telemetry to github sentry.
   */
  @JsonRequest("telemetry/exception")
  CompletableFuture<Object> sendExceptionTelemetry(TelemetryExceptionParams params);

  /**
   * Create a new conversation.
   */
  @JsonRequest("conversation/create")
  CompletableFuture<ChatCreateResult> create(ConversationCreateParams param);

  /**
   * Create a new conversation.
   */
  @JsonRequest("conversation/turn")
  CompletableFuture<ChatTurnResult> addTurn(ConversationTurnParams param);

  /**
   * List conversation templates.
   */
  @JsonRequest("conversation/templates")
  CompletableFuture<ConversationTemplate[]> listTemplates(NullParams param);

  /**
   * Used to track telemetry from users copying code from chat.
   */
  @JsonRequest("conversation/copyCode")
  CompletableFuture<String> copyCode(ConversationCodeCopyParams param);

  /**
   * Used to get the persistence token for the current user.
   */
  @JsonRequest("conversation/persistence")
  CompletableFuture<ChatPersistence> persistence(NullParams param);

  /**
   * Register agent tools to the language server.
   */
  @JsonRequest("conversation/registerTools")
  CompletableFuture<String> registerTools(RegisterToolsParams params);

  /**
   * List copilot models.
   */
  @JsonRequest("copilot/models")
  CompletableFuture<CopilotModel[]> listModels(NullParams param);

  /**
   * Update the status of the mcp server and tools.
   */
  @JsonRequest("mcp/updateToolsStatus")
  CompletableFuture<List<McpServerToolsCollection>> updateMcpToolsStatus(UpdateMcpToolsStatusParams param);

  /**
   * Get the conversation agents.
   */
  @JsonRequest("conversation/agents")
  CompletableFuture<ConversationAgent[]> listAgents(NullParams params);

  /**
   * Notify the code acceptance.
   */
  @JsonRequest("conversation/notifyCodeAcceptance")
  CompletableFuture<String> notifyCodeAcceptance(NotifyCodeAcceptanceParams params);
  
  /**
   * Generate commit messages.
   */
  @JsonRequest("git/commitGenerate")
  CompletableFuture<GenerateCommitMessageResult> generateCommitMessage(GenerateCommitMessageParams params);
}
