/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.handlers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.UIElement;

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
 * Handler for showing GitHub Copilot status bar menu.
 */
public class ShowStatusBarMenuHandler extends CopilotHandler implements IElementUpdater {
  private IHandlerService handlerService;
  private AuthStatusManager authStatusManager;
  private LanguageServerSettingManager languageServerSettingManager;
  private SpinnerJob spinnerJob;
  private Action completionRemainingAction;
  private Action chatRemainingAction;
  private Action premiumRequestsAction;

  /**
   * Constructor for ShowStatusBarMenuHandler.
   */
  public ShowStatusBarMenuHandler() {
    IEventBroker eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
    eventBroker.subscribe(CopilotEventConstants.TOPIC_AUTH_STATUS_CHANGED, event -> {
      Object data = event.getProperty(IEventBroker.DATA);
      if (data instanceof CopilotStatusResult statusResult && statusResult != null && statusResult.isNotSignedIn()) {
        if (completionRemainingAction != null) {
          completionRemainingAction = null;
        }
        if (chatRemainingAction != null) {
          chatRemainingAction = null;
        }
        if (premiumRequestsAction != null) {
          premiumRequestsAction = null;
        }
      }
    });
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    handlerService = HandlerUtil.getActiveWorkbenchWindow(event).getService(IHandlerService.class);
    authStatusManager = CopilotCore.getPlugin().getAuthStatusManager();
    languageServerSettingManager = CopilotUi.getPlugin().getLanguageServerSettingManager();

    MenuManager menuManager = new MenuManager();
    // Sign in/Username
    addSignInOrUsernameAction(menuManager);

    // Copilot usage section
    if (!authStatusManager.isNotSignedInOrNotAuthorized()) {
      menuManager.add(new Separator("copilotUsageGroup"));
      addCopilotUsageAction(menuManager);

      // Create a CompletableFuture to update quota information
      CopilotCore.getPlugin().getAuthStatusManager().checkQuota().thenAccept(this::updateQuotaActions);
    }

    // Open Copilot chat view section
    menuManager.add(new Separator());
    addOpenChatViewAction(menuManager);

    // Completion settings section
    menuManager.add(new Separator());
    addCompletionSettingsAction(menuManager);

    // Preferences section
    menuManager.add(new Separator());
    addEditKeyboardShortcutsAction(menuManager);
    addPreferencesAction(menuManager);

    // Provide feedback section
    menuManager.add(new Separator());
    addLinkToFeedbackForumAction(menuManager);
    addShowWhatIsNewAction(menuManager);

    // Copilot settings and Sign out section
    addAuthenticationActions(menuManager);

    Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
    Menu menu = menuManager.createContextMenu(shell);
    menu.setVisible(true);
    return null;
  }

  /**
   * Updates the quota actions with the latest quota information.
   *
   * @param quotaResult The latest quota information.
   */
  private void updateQuotaActions(CheckQuotaResult quotaResult) {
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

    if (completionRemainingAction != null) {
      completionRemainingAction.setText(calculator.getCompletionText());
    }
    if (chatRemainingAction != null) {
      chatRemainingAction.setText(calculator.getChatText());
    }
    if (premiumRequestsAction != null && quotaResult.getCopilotPlan() != CopilotPlan.free) {
      premiumRequestsAction.setText(calculator.getPremiumText());
    }
  }

  @Override
  public void updateElement(UIElement element, Map parameters) {
    if (Job.getJobManager().find(CopilotUi.INIT_JOB_FAMILY).length > 0) {
      scheduleSpinnerJob(element);
    } else {
      // Since spinner job has 100ms delay, cancel the spinner job if it is running to avoid flickering.
      if (spinnerJob != null) {
        spinnerJob.cancel();
      }

      AuthStatusManager authStatusManager = CopilotCore.getPlugin().getAuthStatusManager();
      if (authStatusManager == null) {
        scheduleSpinnerJob(element);
        return;
      } else {
        String copilotStatus = authStatusManager.getCopilotStatus();
        String iconPath = null;

        switch (copilotStatus) {
          case CopilotStatusResult.OK:
            iconPath = "/icons/github_copilot_signed_in.png";
            break;
          case CopilotStatusResult.LOADING:
            scheduleSpinnerJob(element);
            return;
          case CopilotStatusResult.ERROR, CopilotStatusResult.WARNING:
            iconPath = "/icons/github_copilot_error.png";
            break;
          case CopilotStatusResult.NOT_AUTHORIZED:
            iconPath = "/icons/github_copilot_not_authorized.png";
            break;
          case CopilotStatusResult.NOT_SIGNED_IN:
          default:
            iconPath = "/icons/github_copilot_not_signed_in.png";
        }
        setIconOnDisplayThread(element, iconPath);
      }
    }
  }

  private void setIconOnDisplayThread(UIElement element, String iconPath) {
    if (iconPath != null) {
      SwtUtils.invokeOnDisplayThread(() -> {
        ImageDescriptor newIcon = UiUtils.buildImageDescriptorFromPngPath(iconPath);
        element.setIcon(newIcon);
      });
    }
  }

  private void addSignInOrUsernameAction(MenuManager menuManager) {
    String status = authStatusManager != null ? authStatusManager.getCopilotStatus() : CopilotStatusResult.LOADING;

    if (CopilotStatusResult.NOT_SIGNED_IN.equals(status)) {
      MenuActionFactory.createMenuAction(menuManager, Messages.menu_signToGitHub,
          UiUtils.buildImageDescriptorFromPngPath("/icons/signin.png"), handlerService,
          "org.eclipse.copilot.commands.signIn", true);
    } else if (CopilotStatusResult.OK.equals(status)) {
      MenuActionFactory.createMenuAction(menuManager, authStatusManager.getUserName(), authStatusManager.getUserName(),
          null, handlerService, "org.eclipse.copilot.commands.disabledDoNothing", false);
    }
  }

  private void addCopilotUsageAction(MenuManager menuManager) {
    CheckQuotaResult quotaStatus = CopilotCore.getPlugin().getAuthStatusManager().getQuotaStatus();
    if (quotaStatus.getCompletionsQuota() == null || quotaStatus.getChatQuota() == null
        || StringUtils.isEmpty(quotaStatus.getResetDate())) {
      // skip quota status menu if quotas are not available
      // TODO: remove reset date null check when the CLS is ready for all IDEs.
      return;
    }

    // Calculate percentRemaining based on plan
    double percentRemaining;
    if (quotaStatus.getCopilotPlan() == CopilotPlan.free) {
      // For free plan, consider completions and chat quotas
      percentRemaining = Math.min(quotaStatus.getCompletionsQuota().getPercentRemaining(),
          quotaStatus.getChatQuota().getPercentRemaining());
    } else {
      // For paid plans, also consider premium interactions quota
      percentRemaining = Math.min(quotaStatus.getCompletionsQuota().getPercentRemaining(),
          Math.min(quotaStatus.getChatQuota().getPercentRemaining(),
              quotaStatus.getPremiumInteractionsQuota().getPercentRemaining()));
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
    MenuActionFactory.createMenuAction(menuManager, Messages.menu_quota_copilotUsage,
        Messages.menu_quota_manageCopilotTooltip, icon, handlerService, UiConstants.OPEN_URL_COMMAND_ID, parameters,
        true);

    GC gc = new GC(PlatformUI.getWorkbench().getDisplay());
    QuotaTextCalculator calculator = new QuotaTextCalculator(gc, quotaStatus);
    try {
      // Premium requests usage when rest plans are unlimited
      if (quotaStatus.getCopilotPlan() != CopilotPlan.free && quotaStatus.getCompletionsQuota().isUnlimited()
          && quotaStatus.getChatQuota().isUnlimited()) {
        String premiumRequestsText = calculator.getPremiumText();
        premiumRequestsAction = MenuActionFactory.createMenuAction(menuManager, premiumRequestsText,
            UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png"), handlerService,
            "org.eclipse.copilot.commands.enabledDoNothing", true);
      }

      // Code completions usage
      String codeCompletionsText = calculator.getCompletionText();
      completionRemainingAction = MenuActionFactory.createMenuAction(menuManager, codeCompletionsText,
          UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png"), handlerService,
          "org.eclipse.copilot.commands.enabledDoNothing", true);

      // Chat messages usage
      String chatMessagesText = calculator.getChatText();
      chatRemainingAction = MenuActionFactory.createMenuAction(menuManager, chatMessagesText,
          UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png"), handlerService,
          "org.eclipse.copilot.commands.enabledDoNothing", true);

      // Premium requests usage
      if (quotaStatus.getCopilotPlan() != CopilotPlan.free) {
        // Premium requests usage when either of the rest plans is not unlimited
        if (!quotaStatus.getCompletionsQuota().isUnlimited() || !quotaStatus.getChatQuota().isUnlimited()) {
          String premiumRequestsText = calculator.getPremiumText();
          premiumRequestsAction = MenuActionFactory.createMenuAction(menuManager, premiumRequestsText,
              UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png"), handlerService,
              "org.eclipse.copilot.commands.enabledDoNothing", true);
        }

        MenuActionFactory.createMenuAction(menuManager,
            Messages.menu_quota_additionalPremiumRequests
                + (quotaStatus.getPremiumInteractionsQuota().isOveragePermitted() ? Messages.menu_quota_enabled
                    : Messages.menu_quota_disabled),
            handlerService, "org.eclipse.copilot.commands.disabledDoNothing", false);
      }
    } finally {
      gc.dispose();
    }

    // Allowance reset date
    if (!StringUtils.isEmpty(quotaStatus.getResetDate())) {
      LocalDate resetDate = LocalDate.parse(quotaStatus.getResetDate());
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
      MenuActionFactory.createMenuAction(menuManager, Messages.menu_quota_allowanceReset + resetDate.format(formatter),
          handlerService, "org.eclipse.copilot.commands.disabledDoNothing", false);
    }

    // Upsell actions based on the user's plan
    ImageDescriptor upgradeIcon = UiUtils.buildImageDescriptorFromPngPath("/icons/quota/upgrade.png");
    if (quotaStatus.getCopilotPlan() == CopilotPlan.free) {
      // If the user is on a free plan, show a link to upgrade.
      MenuActionFactory.createMenuAction(menuManager, Messages.menu_quota_updateCopilotToPro, upgradeIcon,
          handlerService, "org.eclipse.copilot.commands.upgradeCopilotPlan", true);
    } else if (quotaStatus.getCopilotPlan() != CopilotPlan.business
        && quotaStatus.getCopilotPlan() != CopilotPlan.enterprise) {
      // If the user is not on a free plan / business plan / enterprise plan, show a link to manage subscription.
      MenuActionFactory.createMenuAction(menuManager, Messages.menu_quota_managePaidPremiumRequests, upgradeIcon,
          handlerService, UiConstants.OPEN_URL_COMMAND_ID,
          Map.of(UiConstants.OPEN_URL_PARAMETER_NAME, UiConstants.MANAGE_COPILOT_OVERAGE_URL), true);
    }
  }

  private void addOpenChatViewAction(MenuManager menuManager) {
    ImageDescriptor icon = UiUtils.buildImageDescriptorFromPngPath("/icons/github_copilot.png");
    MenuActionFactory.createMenuAction(menuManager, Messages.menu_openChatView, icon, handlerService,
        "org.eclipse.copilot.commands.openChatView", true);
  }

  private void addLinkToFeedbackForumAction(MenuManager menuManager) {
    Map<String, String> parameters = Map.of(UiConstants.OPEN_URL_PARAMETER_NAME,
        UiConstants.COPILOT_FEEDBACK_FORUM_URL);
    ImageDescriptor feedbackIcon = UiUtils.buildImageDescriptorFromPngPath("/icons/feedback_forum.png");
    MenuActionFactory.createMenuAction(menuManager, Messages.menu_giveFeedback, feedbackIcon, handlerService,
        UiConstants.OPEN_URL_COMMAND_ID, parameters, true);
  }

  private void addPreferencesAction(MenuManager menuManager) {
    ImageDescriptor editPreferencesIcon = UiUtils.buildImageDescriptorFromPngPath("/icons/edit_preferences.png");
    MenuActionFactory.createMenuAction(menuManager, Messages.menu_editPreferences, editPreferencesIcon, handlerService,
        "org.eclipse.copilot.commands.openPreferences", true);
  }

  private void addEditKeyboardShortcutsAction(MenuManager menuManager) {
    ImageDescriptor editKeyboardShortcutsIcon = UiUtils
        .buildImageDescriptorFromPngPath("/icons/edit_keyboard_shortcuts.png");
    MenuActionFactory.createMenuAction(menuManager, Messages.menu_editKeyboardShortcuts, editKeyboardShortcutsIcon,
        handlerService, "org.eclipse.copilot.commands.openEditKeyboardShortcuts", true);
  }

  private void addCompletionSettingsAction(MenuManager menuManager) {
    ImageDescriptor placeHolder = UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png");
    if (languageServerSettingManager.isAutoShowCompletionEnabled()) {
      MenuActionFactory.createMenuAction(menuManager, Messages.menu_turnOffCompletions, placeHolder, handlerService,
          "org.eclipse.copilot.commands.autoShowCompletions", true);
    } else {
      MenuActionFactory.createMenuAction(menuManager, Messages.menu_turnOnCompletions, placeHolder, handlerService,
          "org.eclipse.copilot.commands.autoShowCompletions", true);
    }
  }

  private void addShowWhatIsNewAction(MenuManager menuManager) {
    ImageDescriptor placeHolder = UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png");
    MenuActionFactory.createMenuAction(menuManager, Messages.menu_whatIsNew, placeHolder, handlerService,
        "org.eclipse.copilot.commands.showWhatIsNew", true);
  }

  private void scheduleSpinnerJob(UIElement uiElement) {
    if (spinnerJob != null) {
      spinnerJob.cancel();
    } else {
      spinnerJob = new SpinnerJob();
    }
    spinnerJob.setTargetUiElement(uiElement);
    spinnerJob.schedule();
  }

  private void addAuthenticationActions(MenuManager menuManager) {
    if (Objects.equals(authStatusManager.getCopilotStatus(), CopilotStatusResult.LOADING)
        || Objects.equals(authStatusManager.getCopilotStatus(), CopilotStatusResult.NOT_SIGNED_IN)) {
      return;
    }
    menuManager.add(new Separator());
    if (Objects.equals(authStatusManager.getCopilotStatus(), CopilotStatusResult.NOT_AUTHORIZED)) {
      MenuActionFactory.createMenuAction(menuManager, Messages.menu_configureGitHubCopilotSettings, null,
          handlerService, "org.eclipse.copilot.commands.configureCopilotSettings", true);
    }
    // Only show sign out action when the user is in OK, NOT_AUTHORIZED, WARNING, or ERROR state.
    ImageDescriptor signOutIcon = UiUtils.buildImageDescriptorFromPngPath("/icons/signout.png");
    MenuActionFactory.createMenuAction(menuManager, Messages.menu_signOutOfGitHub, signOutIcon, handlerService,
        "org.eclipse.copilot.commands.signOut", true);
  }

  private static class MenuActionFactory {

    /**
     * Creates and adds a menu action with all possible options.
     *
     * @param menuManager The MenuManager to add the action to
     * @param text The text for the action
     * @param tooltipText The tooltip text (can be null)
     * @param icon The icon descriptor (can be null)
     * @param handlerService The handler service
     * @param commandId The command ID
     * @param parameters Command parameters (can be null)
     * @param enabled Whether the action is enabled
     * @return The created Action
     */
    public static Action createMenuAction(MenuManager menuManager, String text, String tooltipText,
        ImageDescriptor icon, IHandlerService handlerService, String commandId, Map<String, String> parameters,
        boolean enabled) {

      Action action = new Action(text, icon) {
        @Override
        public void run() {
          try {
            if (commandId != null && "org.eclipse.copilot.commands.openChatView".equals(commandId)) {
              Map<String, Object> commandParameters = Map.of(
                  UiConstants.OPEN_CHAT_VIEW_INPUT_VALUE, "Hi", UiConstants.OPEN_CHAT_VIEW_AUTO_SEND, "false");
              UiUtils.executeCommandWithParameters(commandId, commandParameters);
              return;
            }
            if (parameters != null && !parameters.isEmpty()) {
              ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
              Command command = commandService.getCommand(commandId);
              ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, parameters);
              handlerService.executeCommand(parameterizedCommand, null);
            } else {
              handlerService.executeCommand(commandId, null);
            }
          } catch (Exception e) {
            CopilotCore.LOGGER.error(e);
          }
        }
      };

      action.setEnabled(enabled);
      if (tooltipText != null) {
        action.setToolTipText(tooltipText);
      }
      if (icon == null) {
        setDefaultBlankIcon(action);
      }

      menuManager.add(action);
      return action;
    }

    // Convenience method without tooltip
    public static Action createMenuAction(MenuManager menuManager, String text, ImageDescriptor icon,
        IHandlerService handlerService, String commandId, Map<String, String> parameters, boolean enabled) {
      return createMenuAction(menuManager, text, null, icon, handlerService, commandId, parameters, enabled);
    }

    // Convenience method without parameters
    public static Action createMenuAction(MenuManager menuManager, String text, ImageDescriptor icon,
        IHandlerService handlerService, String commandId, boolean enabled) {
      return createMenuAction(menuManager, text, null, icon, handlerService, commandId, null, enabled);
    }

    // Convenience method with just text and command
    public static Action createMenuAction(MenuManager menuManager, String text, IHandlerService handlerService,
        String commandId, boolean enabled) {
      return createMenuAction(menuManager, text, null, null, handlerService, commandId, null, enabled);
    }

    // Convenience method with tooltip but no icon
    public static Action createMenuAction(MenuManager menuManager, String text, String tooltipText,
        ImageDescriptor icon, IHandlerService handlerService, String commandId, boolean enabled) {
      return createMenuAction(menuManager, text, tooltipText, icon, handlerService, commandId, null, enabled);
    }

    private static void setDefaultBlankIcon(Action action) {
      ImageDescriptor blankIcon = UiUtils.buildImageDescriptorFromPngPath("/icons/blank.png");
      if (PlatformUtils.isMac()) {
        action.setImageDescriptor(blankIcon);
      }
    }
  }

  private class SpinnerJob extends Job {
    private static final int INITIAL_ICON_INDEX = 1;
    private static final int TOTAL_SPINNER_ICONS = 8;
    private static final long COMPLETION_IN_PROGRESS_SPINNER_ROTATE_RATE_MILLIS = 100L;

    private int currentIconIndex = INITIAL_ICON_INDEX;
    private UIElement uiElement;

    public SpinnerJob() {
      super("Spinner Job");
      this.setSystem(true);
    }

    public void setTargetUiElement(UIElement uiElement) {
      this.uiElement = uiElement;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
      try {
        if (this.uiElement == null) {
          throw new IllegalStateException("UI element is not set. Spinner cannot be set.");
        }
        setIconOnDisplayThread(this.uiElement, String.format("/icons/spinner/%d.png", currentIconIndex));
        currentIconIndex = (currentIconIndex % TOTAL_SPINNER_ICONS) + 1;
        if (CopilotCore.getPlugin().getAuthStatusManager() != null
            && CopilotCore.getPlugin().getAuthStatusManager().isLoading()) {
          schedule(COMPLETION_IN_PROGRESS_SPINNER_ROTATE_RATE_MILLIS);
        } else {
          cancel();
        }
      } catch (Exception e) {
        CopilotCore.LOGGER.error(e);
        return Status.CANCEL_STATUS;
      }
      return Status.OK_STATUS;
    }
  }
}
