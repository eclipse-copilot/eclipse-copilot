/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * Message class for the i18n.
 */
public final class Messages extends NLS {
  private static final String BUNDLE_NAME = "org.eclipse.copilot.ui.i18n.messages"; //$NON-NLS-1$
  public static String menu_copilotStatus;
  public static String menu_copilotStatus_ready;
  public static String menu_copilotStatus_loading;
  public static String menu_copilotStatus_completionInProgress;
  public static String menu_copilotStatus_notSignedInToGitHub;
  public static String menu_copilotStatus_unknownError;
  public static String menu_copilotStatus_notAuthorized;
  public static String menu_copilotStatus_agentWarning;
  public static String menu_signToGitHub;
  public static String menu_signOutOfGitHub;
  public static String menu_configureGitHubCopilotSettings;
  public static String menu_giveFeedback;
  public static String menu_whatIsNew;
  public static String menu_editPreferences;
  public static String menu_editKeyboardShortcuts;
  public static String menu_turnOnCompletions;
  public static String menu_turnOffCompletions;
  public static String menu_openChatView;
  public static String menu_quota_copilotUsage;
  public static String menu_quota_codeCompletions;
  public static String menu_quota_chatMessages;
  public static String menu_quota_premiumRequests;
  public static String menu_quota_additionalPremiumRequests;
  public static String menu_quota_enabled;
  public static String menu_quota_disabled;
  public static String menu_quota_allowanceReset;
  public static String menu_quota_manageCopilotTooltip;
  public static String menu_quota_updateCopilotToPro;
  public static String menu_quota_updateCopilotToProPlus;
  public static String menu_quota_managePaidPremiumRequests;
  public static String signInDialog_title;
  public static String signInDialog_button_cancel;
  public static String signInDialog_button_copyOpen;
  public static String signInDialog_info_instructions;
  public static String signInDialog_info_deviceCodePrefix;
  public static String signInDialog_info_githubWebSitePrefix;
  public static String signInConfirmDialog_progress;
  public static String signInConfirmDialog_progressTimeout;
  public static String signInConfirmDialog_progressCanceled;
  public static String signInConfirmDialog_deviceCodeFormatString;
  public static String signInConfirmDialog_authResult_notSignedIn;
  public static String signInConfirmDialog_authResult_notAuthed;
  public static String signInHandler_msgDialog_githubCopilot;
  public static String signInHandler_msgDialog_title;
  public static String signInHandler_msgDialog_alreadySignedIn;
  public static String signInHandler_msgDialog_signInSuccess;
  public static String signInHandler_msgDialog_signInFailed;
  public static String signInHandler_msgDialog_signInFailedTryAgain;
  public static String signInHandler_msgDialog_signInFailedFailure;
  public static String signOutHandler_msgDialog_githubCopilot;
  public static String signOutHandler_msgDialog_signOutSuccess;
  public static String signOutHandler_msgDialog_signOutFailed;
  public static String signOutHandler_msgDialog_signOutFailedFailure;
  public static String preferences_page_restart_required;
  public static String preferencesPage_autoShowCompletion;
  public static String preferences_page_enable_strict_ssl;
  public static String preferences_page_proxy_kerberos_sp;
  public static String preferences_page_github_enterprise;
  public static String preferences_page_watched_files;
  public static String preferences_page_watched_files_note_content;
  public static String preferences_page_watched_files_restart_question;
  public static String preferences_page_mcp;
  public static String preferences_page_proxy_config_link;
  public static String preferences_page_proxy_settings;
  public static String preferences_page_editor_settings;
  public static String preferences_page_chat_settings;
  public static String preferences_page_auth_settings;
  public static String preferences_page_mcp_settings;
  public static String preferences_page_enable_strict_ssl_tooltip;
  public static String preferences_page_proxy_config_link_tooltip;
  public static String preferences_page_github_enterprise_tooltip;
  public static String preferences_page_mcp_tooltip;
  public static String preferences_page_mcp_note_content;
  public static String preferences_page_mcp_disabled_tip;
  public static String preferences_page_mcp_tools_settings;
  public static String preferences_page_mcp_server_init_error;
  public static String preferences_page_custom_instructions_copilot_instructions;
  public static String preferences_page_custom_instructions_copilot_instructions_desc;
  public static String preferences_page_custom_instructions_copilot_instructions_note;
  public static String preferences_page_custom_instructions_workspace;
  public static String preferences_page_custom_instructions_workspace_enable;
  public static String preferences_page_custom_instructions_workspace_tooltip;
  public static String preferences_page_custom_instructions_project;
  public static String preferences_page_custom_instructions_project_enable;
  public static String preferences_page_custom_instructions_project_tooltip;
  public static String preferences_page_custom_instructions_project_intro;
  public static String preferences_page_custom_instructions_project_table_projectName;
  public static String preferences_page_custom_instructions_project_table_fileLocation;
  public static String preferences_page_custom_instructions_project_table_deleteButton;
  public static String preferences_page_custom_instructions_project_table_editButton;
  public static String preferences_page_custom_instructions_project_table_note;
  public static String preferences_page_custom_instructions_project_editDialog_title;
  public static String preferences_page_custom_instructions_project_editDialog_message;
  public static String preferences_page_custom_instructions_project_editDialog_button_close;
  public static String preferences_page_custom_instructions_project_editDialog_button_stay;
  public static String preferences_page_custom_instructions_project_file_save_reminder_title;
  public static String preferences_page_custom_instructions_project_file_save_reminder_desc;
  public static String preferences_page_note_prefix;
  public static String preferences_page_note_content;
  public static String preferences_page_mcpOAuth_confirmTitle;
  public static String preferences_page_mcpOAuth_confirmMessage;
  public static String chat_topBanner_newConversationButton_Tooltip;
  public static String chat_topBanner_defaultChatTitle;
  public static String chat_actionBar_initialContent;
  public static String chat_actionBar_initialContentForAgent;
  public static String chat_actionBar_attachContextButton_Tooltip;
  public static String chat_actionBar_sendButton_Tooltip;
  public static String chat_actionBar_cancelButton_Tooltip;
  public static String chat_actionBar_toolButton_toolTip;
  public static String chat_actionBar_toolButton_disabled_toolTip;
  public static String chat_welcomeView_title;
  public static String chat_welcomeView_description;
  public static String chat_welcomeView_agentSuffix;
  public static String chat_welcomeView_mcpSuffix;
  public static String chat_welcomeView_completionSuffix;
  public static String chat_welcomeView_chatSuffix;
  public static String chat_welcomeView_freeCopilotLink;
  public static String chat_welcomeView_freeCopilotIntroPrefix;
  public static String chat_welcomeView_freeCopilotIntroSuffix;
  public static String chat_welcomeView_signInButton;
  public static String chat_welcomeView_signInButton_Tooltip;
  public static String chat_welcomeView_termsPrefix;
  public static String chat_welcomeView_termsLink;
  public static String chat_welcomeView_termsSuffix;
  public static String chat_welcomeView_privacyPolicyPrefix;
  public static String chat_welcomeView_privacyPolicyLink;
  public static String chat_welcomeView_privacyPolicySuffix;
  public static String chat_welcomeView_footerPublicCodePrefix;
  public static String chat_welcomeView_footerPublicCodeLink;
  public static String chat_welcomeView_footerPublicCodeSuffix;
  public static String chat_welcomeView_footerSettingsPrefix;
  public static String chat_welcomeView_footerSettingsLink;
  public static String chat_welcomeView_footerSettingsSuffix;
  public static String chat_aiWarn_description;
  public static String chat_initialChatView_title;
  public static String chat_initialChatView_attachContextSuffix;
  public static String chat_initialChatView_useCommandsIntro;
  public static String chat_agentModeView_title;
  public static String chat_agentModeView_agentModeIntro;
  public static String chat_agentModeView_configureMcpSuffix;
  public static String chat_agentModeView_attachContextSuffix;
  public static String chat_loadingView_title;
  public static String chat_loadingView_description;
  public static String chat_noAuthView_title;
  public static String chat_noAuthView_description;
  public static String chat_noAuthView_checkSubButton;
  public static String chat_noAuthView_checkSubButton_Tooltip;
  public static String chat_noAuthView_checkSubLink;
  public static String chat_filePicker_title;
  public static String chat_filePicker_message;
  public static String chat_addContextButton_title;
  public static String chat_addContextButton_tooltip;
  public static String chat_noQuotaView_fallbackModel;
  public static String chat_noQuotaView_updatePlanButton;
  public static String chat_noQuotaView_updatePlanButton_Tooltip;
  public static String chat_noQuotaView_updatePlanLink;
  public static String chat_noQuotaView_enablePremiumRequestsButton;
  public static String chat_noQuotaView_enablePremiumRequestsButton_tooltip;
  public static String chat_noQuotaView_enablePremiumRequestsLink;
  public static String chat_noQuotaView_proProplusWarnMsg;
  public static String chat_noQuotaView_cbCeWarnMsg;
  public static String chat_currentReferencedFile_description;
  public static String chat_chatContentView_errorTemplate;
  public static String chat_turnWidget_copilot;
  public static String chat_turnWidget_user;
  public static String chat_model_unsupported_message;
  public static String chat_copilotModels;
  public static String chat_standardModels;
  public static String chat_premiumModels;
  public static String chat_addPremiumModels;
  public static String chat_referencedFile_noVision_tooltip;
  public static String agent_tool_terminal_copilotTerminalTitle;
  public static String agent_tool_compareEditor_titlePrefix;
  public static String agent_tool_compareEditor_proposedChangesTitle;
  public static String agent_tool_cancelConfirmationDialog_defaultTitle;
  public static String quickStart_title;
  public static String quickStart_description;
  public static String quickStart_closeButton_tooltip;
  public static String quickStart_continueButton;
  public static String quickStart_agent_title;
  public static String quickStart_agent_description;
  public static String quickStart_ask_title;
  public static String quickStart_ask_description;
  public static String quickStart_completion_title;
  public static String quickStart_completion_description;
  public static String generateCommitMessage_requiredBundlesMissing_title;
  public static String generateCommitMessage_requiredBundlesMissing_message;
  public static String generateCommitMessage_noRepo_title;
  public static String generateCommitMessage_noRepo_message;
  public static String generateCommitMessage_noStagedFiles_title;
  public static String generateCommitMessage_noStagedFiles_message;
  public static String newChat_confirmationTitle;
  public static String newChat_confirmationMessage;
  public static String newChat_keepChangesButton;
  public static String newChat_undoChangesButton;
  public static String newChat_cancelButton;
  public static String addToReference_addFile_title;
  public static String addToReference_addFolder_title;

  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}