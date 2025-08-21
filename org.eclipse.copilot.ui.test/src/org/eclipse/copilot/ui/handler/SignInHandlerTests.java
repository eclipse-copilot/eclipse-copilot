/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.eclipse.copilot.core.AuthStatusManager;
import org.eclipse.copilot.core.Constants;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.ui.UiConstants;
import org.eclipse.copilot.ui.dialogs.SignInConfirmDialog;
import org.eclipse.copilot.ui.handlers.SignInHandler;
import org.eclipse.copilot.ui.i18n.Messages;
import org.eclipse.copilot.ui.utils.SwtUtils;
import org.eclipse.copilot.ui.utils.UiUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.prefs.BackingStoreException;

@ExtendWith(MockitoExtension.class)
class SignInHandlerTests {

  @Test
  void testHandleSignInConfirmation_showsQuickStartThenSuccess() throws Exception {
    // Ensure config pref starts from 0 for the test
    IEclipsePreferences configPrefs = ConfigurationScope.INSTANCE.getNode(Constants.PLUGIN_ID);
    configPrefs.putInt(Constants.COPILOT_QUICK_START_VERSION, 0);
    try {
      configPrefs.flush();
    } catch (BackingStoreException e) {
      fail("Failed to reset configuration preferences for test: " + e.getMessage());
    }

    CopilotCore mockedCore = mock(CopilotCore.class);
    AuthStatusManager mockedAuthStatusManager = mock(AuthStatusManager.class);
    when(mockedCore.getAuthStatusManager()).thenReturn(mockedAuthStatusManager);

    SignInConfirmDialog mockedConfirmDialog = mock(SignInConfirmDialog.class);
    when(mockedConfirmDialog.getStatus()).thenReturn(Status.OK_STATUS);

    try (MockedStatic<CopilotCore> mockedCopilotCore = mockStatic(CopilotCore.class);
        MockedStatic<SwtUtils> mockedSwt = mockStatic(SwtUtils.class);
        MockedStatic<UiUtils> mockedUiUtils = mockStatic(UiUtils.class);
        MockedStatic<MessageDialog> mockedMsgDialog = mockStatic(MessageDialog.class)) {

      mockedCopilotCore.when(CopilotCore::getPlugin).thenReturn(mockedCore);
      mockedSwt.when(() -> SwtUtils.invokeOnDisplayThreadAsync(any())).thenAnswer(invocation -> {
        Runnable r = invocation.getArgument(0);
        r.run();
        return null;
      });

      // Track quick start command invocation
      mockedUiUtils
          .when(() -> UiUtils.executeCommandWithParameters(UiConstants.OPEN_QUICK_START_COMMAND_ID, null))
          .thenAnswer(invocation -> null);

      // Track success message invocations
      mockedMsgDialog.when(() -> MessageDialog.openInformation(any(Shell.class), any(String.class), any(String.class)))
          .thenAnswer(invocation -> null);
      SignInHandler handler = new SignInHandler();

      // Create SignInJob instance via reflection without running it
      Class<?>[] classes = SignInHandler.class.getDeclaredClasses();
      Class<?> signInJobClass = null;
      for (Class<?> c : classes) {
        if ("SignInJob".equals(c.getSimpleName())) {
          signInJobClass = c;
          break;
        }
      }
      if (signInJobClass == null) {
        fail("SignInJob inner class not found");
      }

      Constructor<?> ctor = signInJobClass.getDeclaredConstructor(SignInHandler.class, ExecutionEvent.class);
      ctor.setAccessible(true);
      Object job = ctor.newInstance(handler, null);

      Method method = signInJobClass.getDeclaredMethod("handleSignInConfirmation", Shell.class,
          SignInConfirmDialog.class);
      method.setAccessible(true);

      // 1st call: should trigger quick start (command executed) and update config pref
      method.invoke(job, (Shell) null, mockedConfirmDialog);

      mockedUiUtils.verify(
          () -> UiUtils.executeCommandWithParameters(UiConstants.OPEN_QUICK_START_COMMAND_ID, null), times(1));
      mockedMsgDialog.verify(() -> MessageDialog.openInformation(null, Messages.signInHandler_msgDialog_githubCopilot,
              Messages.signInHandler_msgDialog_signInSuccess), times(0));
      // No success message assertion on first call; will assert exactly once after second call

      // Pref should be updated to current version
      IEclipsePreferences configPrefsAfter = ConfigurationScope.INSTANCE.getNode(Constants.PLUGIN_ID);
      int stored = configPrefsAfter.getInt(Constants.COPILOT_QUICK_START_VERSION, 0);
      assertEquals(Constants.CURRENT_COPILOT_QUICK_START_VERSION, stored,
          "Quick Start version should be persisted to configuration scope");

      // 2nd call: should show success message and not trigger quick start again
      method.invoke(job, (Shell) null, mockedConfirmDialog);

      mockedUiUtils.verify(
          () -> UiUtils.executeCommandWithParameters(UiConstants.OPEN_QUICK_START_COMMAND_ID, null), times(1));
      mockedMsgDialog.verify(() -> MessageDialog.openInformation(null, Messages.signInHandler_msgDialog_githubCopilot,
          Messages.signInHandler_msgDialog_signInSuccess), times(1));
    }
  }
}