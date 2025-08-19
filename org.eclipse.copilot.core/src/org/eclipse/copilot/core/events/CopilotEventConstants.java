/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.events;

/**
 * Constants for Copilot event topics.
 */
public class CopilotEventConstants {

  /**
   * Base topic for all Copilot events.
   */
  private static final String TOPIC_BASE = "org/eclipse/copilot/";

  /**
   * Topic for chat events.
   */
  private static final String TOPIC_CHAT = TOPIC_BASE + "CHAT/";

  /**
   * Topic for auth events.
   */
  private static final String TOPIC_AUTH = TOPIC_BASE + "AUTH/";

  /**
   * Event when new conversation is started.
   */
  public static final String TOPIC_CHAT_NEW_CONVERSATION = TOPIC_CHAT + "NEW_CONVERSATION";

  /**
   * Event when a chat message is cancelled.
   */
  public static final String TOPIC_CHAT_MESSAGE_CANCELLED = TOPIC_CHAT + "MESSAGE_CANCELLED";

  /**
   * Event when auth status changed.
   */
  public static final String TOPIC_AUTH_STATUS_CHANGED = TOPIC_AUTH + "STATUS_CHANGED";

  /**
   * Event when MCP tools changed.
   */
  public static final String ON_DID_CHANGE_MCP_TOOLS = TOPIC_CHAT + "ON_DID_CHANGE_MCP_TOOLS";

  /**
   * Event when the chat message to Copilot should be sent.
   */
  public static final String TOPIC_CHAT_ON_SEND = TOPIC_CHAT + "ON_SEND";

  /**
   * Event when MCP runtime log is received.
   */
  public static final String TOPIC_CHAT_MCP_RUNTIME_LOG = TOPIC_CHAT + "MCP_RUNTIME_LOG";
  
  /**
   * Event when the chat feature flag are updated.
   */
  public static final String TOPIC_CHAT_DID_CHANGE_FEATURE_FLAGS = TOPIC_CHAT + "DID_CHANGE_FEATURE_FLAGS";
}