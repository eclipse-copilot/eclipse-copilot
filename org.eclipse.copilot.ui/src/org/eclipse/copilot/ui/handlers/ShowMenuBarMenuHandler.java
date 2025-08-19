/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.handlers;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import org.eclipse.copilot.core.AuthStatusManager;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.events.CopilotEventConstants;
import org.eclipse.copilot.core.lsp.protocol.CopilotStatusResult;
import org.eclipse.copilot.core.lsp.protocol.quota.CheckQuotaResult;
import org.eclipse.copilot.core.lsp.protocol.quota.CopilotPlan;
import org.eclipse.copilot.core.utils.PlatformUtils;
import org.eclipse.copilot.ui.CopilotUi;
import org.eclipse.copilot.ui.UiConstants;
import org.eclipse.copilot.ui.i18n.Messages;
import org.eclipse.copilot.ui.preferences.LanguageServerSettingManager;
import org.eclipse.copilot.ui.utils.SwtUtils;
import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * Handler for showing GitHub Copilot menu bar menu.
 */
public class ShowMenuBarMenuHandler extends CompoundContributionItem implements IWorkbenchContribution {
  private IServiceLocator serviceLocator;
  private CommandContributionItem chatUsageItem;
  private CommandContributionItem completionsUsageItem;
  private CommandContributionItem premiumRequestsUsageItem;

  @Override
  public void initialize(IServiceLocator serviceLocator) {
    this.serviceLocator = serviceLocator;
    IEventBroker eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
    eventBroker.subscribe(CopilotEventConstants.TOPIC_AUTH_STATUS_CHANGED, event -> {
      Object data = event.getProperty(IEventBroker.DATA);
      if (data instanceof CopilotStatusResult statusResult && statusResult != null && statusResult.isNotSignedIn()) {
        if (chatUsageItem != null) {
          chatUsageItem.dispose();
          chatUsageItem = null;
        }
        if (completionsUsageItem != null) {
          completionsUsageItem.dispose();
          completionsUsageItem = null;
        }
        if (premiumRequestsUsageItem != null) {
          premiumRequestsUsageItem.dispose();
          premiumRequestsUsageItem = null;
        }
      }
    });
  }

  @Override
  protected IContributionItem[] getContributionItems() {
    List<IContributionItem> items = new ArrayList<>();

    AuthStatusManager authStatusManager = CopilotCore.getPlugin().getAuthStatusManager();
    String status = authStatusManager != null ? authStatusManager.getCopilotStatus() : CopilotStatusResult.LOADING;

    // menu: username/Sign In
    if (CopilotStatusResult.NOT_SIGNED_IN.equals(status)) {
      items.add(createCommandItem("org.eclipse.copilot.commands.signIn", Messages.menu_signToGitHub,
          UiUtils.buildImageDescriptorFromPngPath("/icons/signin.png")));
    } else if (CopilotStatusResult.OK.equals(status)) {
      items.add(createCommandItem("org.eclipse.copilot.commands.disabledDoNothing",
          authStatusManager.getUserName(), authStatusManager.getUserName(), null));
    }

    // menu: copilot Usage
    addCopilotUsageItems(authStatusManager, items);

    // menu: openChatView
    items.add(new Separator());
    items.add(createCommandItem("org.eclipse.copilot.commands.openChatView", Messages.menu_openChatView,
        UiUtils.buildImageDescriptorFromPngPath("/icons/github_copilot.png")));

    // menu:(label options) Turn off Completions or Turn on Completions
    LanguageServerSettingManager languageServerSettingManager = CopilotUi.getPlugin().getLanguageServerSettingManager();
    if (languageServerSettingManager != null) {
      items.add(new Separator());
      String label = languageServerSettingManager.isAutoShowCompletionEnabled() ? Messages.menu_turnOffCompletions
          : Messages.menu_turnOnCompletions;
      items.add(createCommandItem("org.eclipse.copilot.commands.autoShowCompletions", label,
          UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png")));
    }

    // menu: editKeyboardShortcuts
    items.add(new Separator());
    items.add(createCommandItem("org.eclipse.copilot.commands.openEditKeyboardShortcuts",
        Messages.menu_editKeyboardShortcuts,
        UiUtils.buildImageDescriptorFromPngPath("/icons/edit_keyboard_shortcuts.png")));

    // menu: editPreferences
    items.add(createCommandItem("org.eclipse.copilot.commands.openPreferences", Messages.menu_editPreferences,
        UiUtils.buildImageDescriptorFromPngPath("/icons/edit_preferences.png")));

    // menu: giveFeedback
    items.add(new Separator());
    Map<String, String> parameters = Map.of(UiConstants.OPEN_URL_PARAMETER_NAME,
        UiConstants.COPILOT_FEEDBACK_FORUM_URL);
    items.add(createCommandItem(UiConstants.OPEN_URL_COMMAND_ID, Messages.menu_giveFeedback, parameters,
        UiUtils.buildImageDescriptorFromPngPath("/icons/feedback_forum.png")));

    // menu: whatIsNew
    items.add(createCommandItem("org.eclipse.copilot.commands.showWhatIsNew", Messages.menu_whatIsNew,
        UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png")));

    // menu: Copilot settings and Sign Out
    addAuthenticationActions(items, status);

    return items.toArray(new IContributionItem[0]);
  }

  private void addCopilotUsageItems(AuthStatusManager authStatusManager, List<IContributionItem> items) {
    // menu: Copilot useage
    CheckQuotaResult quotaStatus = authStatusManager.getQuotaStatus();
    if (authStatusManager.isNotSignedInOrNotAuthorized() || quotaStatus.getCompletionsQuota() == null
        || quotaStatus.getChatQuota() == null || StringUtils.isEmpty(quotaStatus.getResetDate())) {
      return;
    }
    // TODO: remove reset date null check when the CLS is ready for all IDEs.
    items.add(new Separator());
    // Calculate percentRemaining based on plan
    double percentRemaining;
    if (quotaStatus.getCopilotPlan() == CopilotPlan.free) {
      // For free plan, consider completions and chat quotas
      percentRemaining = Math.min(quotaStatus.getCompletionsQuota().getPercentRemaining(),
          quotaStatus.getChatQuota().getPercentRemaining());
    } else {
      // For paid plans, also consider premium interactions quota
      if (quotaStatus.getCompletionsQuota() == null) {
        // If completions quota is not available, set percentRemaining to 0
        percentRemaining = 0;
      } else {
        percentRemaining = Math.min(quotaStatus.getCompletionsQuota().getPercentRemaining(),
            Math.min(quotaStatus.getChatQuota().getPercentRemaining(),
                quotaStatus.getPremiumInteractionsQuota().getPercentRemaining()));
      }
    }

    ImageDescriptor icon;
    // Set icon based on the lowest percentRemaining
    if (percentRemaining <= 10) {
      icon = UiUtils.buildImageDescriptorFromPngPath("/icons/quota/usage_red.png");
    } else if (percentRemaining > 10 && percentRemaining <= 25) {
      icon = UiUtils.buildImageDescriptorFromPngPath("/icons/quota/usage_yellow.png");
    } else {
      icon = UiUtils.buildImageDescriptorFromPngPath("/icons/quota/usage_blue.png");
    }

    Map<String, String> parameters = Map.of(UiConstants.OPEN_URL_PARAMETER_NAME, UiConstants.MANAGE_COPILOT_URL);
    items.add(createCommandItem(UiConstants.OPEN_URL_COMMAND_ID, Messages.menu_quota_copilotUsage, parameters,
        Messages.menu_quota_manageCopilotTooltip, icon));

    GC gc = new GC(PlatformUI.getWorkbench().getDisplay());
    QuotaTextCalculator calculator = new QuotaTextCalculator(gc, quotaStatus);
    try {
      // Premium requests usage when rest plans are unlimited
      if (quotaStatus.getCopilotPlan() != CopilotPlan.free && quotaStatus.getCompletionsQuota().isUnlimited()
          && quotaStatus.getChatQuota().isUnlimited()) {
        String premiumRequestsText = calculator.getPremiumText();
        this.premiumRequestsUsageItem = createCommandItem("org.eclipse.copilot.commands.enabledDoNothing",
            premiumRequestsText, UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png"));
        items.add(this.premiumRequestsUsageItem);
      }

      // Code completions useage
      String codeCompletionsText = calculator.getCompletionText();
      this.completionsUsageItem = createCommandItem("org.eclipse.copilot.commands.enabledDoNothing",
          codeCompletionsText, UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png"));
      items.add(this.completionsUsageItem);

      // Chat messages usage
      String chatMessagesText = calculator.getChatText();
      this.chatUsageItem = createCommandItem("org.eclipse.copilot.commands.enabledDoNothing",
          chatMessagesText, UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png"));
      items.add(this.chatUsageItem);

      // Premium requests usage
      if (quotaStatus.getCopilotPlan() != CopilotPlan.free) {
        // Premium requests usage when either of the rest plans is not unlimited
        if (!quotaStatus.getCompletionsQuota().isUnlimited() || !quotaStatus.getChatQuota().isUnlimited()) {
          String premiumRequestsText = calculator.getPremiumText();
          this.premiumRequestsUsageItem = createCommandItem("org.eclipse.copilot.commands.enabledDoNothing",
              premiumRequestsText, UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png"));
          items.add(this.premiumRequestsUsageItem);
        }

        CommandContributionItem additionalPremiumRequestsDesc = createCommandItem(
            "org.eclipse.copilot.commands.disabledDoNothing",
            Messages.menu_quota_additionalPremiumRequests
                + (quotaStatus.getPremiumInteractionsQuota().isOveragePermitted() ? Messages.menu_quota_enabled
                    : Messages.menu_quota_disabled),
            null);
        items.add(additionalPremiumRequestsDesc);
      }
    } finally {
      gc.dispose();
    }

    // Allowance reset date
    if (!StringUtils.isEmpty(quotaStatus.getResetDate())) {
      LocalDate resetDate = LocalDate.parse(quotaStatus.getResetDate());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
      items.add(createCommandItem("org.eclipse.copilot.commands.disabledDoNothing",
          Messages.menu_quota_allowanceReset + resetDate.format(formatter), null));
    }

    // Upsell actions based on the user's plan
    ImageDescriptor upgradeIcon = UiUtils.buildImageDescriptorFromPngPath("/icons/quota/upgrade.png");
    if (quotaStatus.getCopilotPlan() == CopilotPlan.free) {
      // If the user is on a free plan, show a link to upgrade.
      items.add(createCommandItem("org.eclipse.copilot.commands.upgradeCopilotPlan",
          Messages.menu_quota_updateCopilotToPro, Messages.menu_quota_updateCopilotToProPlus, upgradeIcon));
    } else if (quotaStatus.getCopilotPlan() != CopilotPlan.business
        && quotaStatus.getCopilotPlan() != CopilotPlan.enterprise) {
      // If the user is not on a free plan / business plan / enterprise plan, show a link to manage subscription.
      items.add(createCommandItem(UiConstants.OPEN_URL_COMMAND_ID, Messages.menu_quota_managePaidPremiumRequests,
          Map.of(UiConstants.OPEN_URL_PARAMETER_NAME, UiConstants.MANAGE_COPILOT_OVERAGE_URL), upgradeIcon));
    }
    // Create a CompletableFuture to update quota information
    CopilotCore.getPlugin().getAuthStatusManager().checkQuota().thenAccept(this::updateQuotaItems);
  }

  private void addAuthenticationActions(List<IContributionItem> items, String status) {
    if (CopilotStatusResult.LOADING.equals(status) || CopilotStatusResult.NOT_SIGNED_IN.equals(status)) {
      return;
    }
    items.add(new Separator());
    if (CopilotStatusResult.NOT_AUTHORIZED.equals(status)) {
      items.add(createCommandItem("org.eclipse.copilot.commands.configureCopilotSettings",
          Messages.menu_configureGitHubCopilotSettings, null));
    }
    items.add(createCommandItem("org.eclipse.copilot.commands.signOut", Messages.menu_signOutOfGitHub,
        UiUtils.buildImageDescriptorFromPngPath("/icons/signout.png")));
  }

  /**
   * Updates the quota items with the latest quota information.
   *
   * @param quotaResult The latest quota information.
   */
  private void updateQuotaItems(CheckQuotaResult quotaResult) {
    if (quotaResult == null) {
      return;
    }

    SwtUtils.invokeOnDisplayThread(() -> {
      GC gc = new GC(PlatformUI.getWorkbench().getDisplay());
      try {
        updateQuotaActionTexts(quotaResult, gc);
      } finally {
        gc.dispose();
      }
    });
  }

  private void updateQuotaActionTexts(CheckQuotaResult quotaResult, GC gc) {
    QuotaTextCalculator calculator = new QuotaTextCalculator(gc, quotaResult);

    if (this.chatUsageItem != null && quotaResult.getChatQuota() != null) {
      String chatMessagesText = calculator.getChatText();
      updateCommandItemLabel(this.chatUsageItem, chatMessagesText);
    }

    if (this.completionsUsageItem != null && quotaResult.getCompletionsQuota() != null) {
      String codeCompletionsText = calculator.getCompletionText();
      updateCommandItemLabel(this.completionsUsageItem, codeCompletionsText);
    }

    if (this.premiumRequestsUsageItem != null && quotaResult.getPremiumInteractionsQuota() != null) {
      String premiumRequestsText = calculator.getPremiumText();
      updateCommandItemLabel(this.premiumRequestsUsageItem, premiumRequestsText);
    }

    if (this.chatUsageItem != null) {
      this.chatUsageItem.update();
    }
    if (this.completionsUsageItem != null) {
      this.completionsUsageItem.update();
    }
    if (this.premiumRequestsUsageItem != null) {
      this.premiumRequestsUsageItem.update();
    }
  }

  /**
   * Updates the label of a CommandContributionItem.
   *
   * @param item The CommandContributionItem to update
   * @param newLabel The new label to set
   */
  private void updateCommandItemLabel(CommandContributionItem item, String newLabel) {
    try {
      Field labelField = CommandContributionItem.class.getDeclaredField("label");
      labelField.setAccessible(true);
      labelField.set(item, newLabel);
    } catch (Exception e) {
      // Skip updating the label if reflection fails
    }
  }

  private CommandContributionItem createCommandItem(String commandId, String label, Map<String, String> parameters,
      ImageDescriptor icon) {
    return createCommandItem(commandId, label, parameters, null, icon);
  }

  private CommandContributionItem createCommandItem(String commandId, String label, ImageDescriptor icon) {
    return createCommandItem(commandId, label, null, null, icon);
  }

  private CommandContributionItem createCommandItem(String commandId, String label, String tooltip,
      ImageDescriptor icon) {
    return createCommandItem(commandId, label, null, tooltip, icon);
  }

  private CommandContributionItem createCommandItem(String commandId, String label, Map<String, String> parameters,
      String tooltip, ImageDescriptor icon) {
    CommandContributionItemParameter parameter = createCommandContributionItemParameter(commandId, label, parameters,
        tooltip, icon);

    return new CommandContributionItem(parameter);
  }

  private CommandContributionItemParameter createCommandContributionItemParameter(String commandId, String label,
      Map<String, String> parameters, String tooltip, ImageDescriptor icon) {
    CommandContributionItemParameter parameter = new CommandContributionItemParameter(serviceLocator, null, commandId,
        CommandContributionItem.STYLE_PUSH);
    if (icon != null) {
      parameter.icon = icon;
    } else {
      setDefaultBlankIcon(parameter);
    }

    if (label != null) {
      parameter.label = label;
    }

    if (tooltip != null) {
      parameter.tooltip = tooltip;
    }

    if (parameters != null && !parameters.isEmpty()) {
      parameter.parameters = parameters;
    }

    return parameter;
  }

  private void setDefaultBlankIcon(CommandContributionItemParameter parameter) {
    ImageDescriptor icon = UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png");
    if (PlatformUtils.isMac()) {
      parameter.icon = icon;
    }
  }
}
