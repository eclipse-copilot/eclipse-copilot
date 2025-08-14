/*******************************************************************************
 * Copyright (c) 2025 Microsoft Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Microsoft Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.copilot.ui.handlers;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.copilot.core.AuthStatusManager;
import org.eclipse.copilot.core.Constants;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.lsp.protocol.CopilotStatusResult;
import org.eclipse.copilot.core.lsp.protocol.SignInInitiateResult;
import org.eclipse.copilot.ui.dialogs.SignInConfirmDialog;
import org.eclipse.copilot.ui.dialogs.SignInDialog;
import org.eclipse.copilot.ui.i18n.Messages;
import org.eclipse.copilot.ui.utils.SwtUtils;
import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * Handler for signing to GitHub Copilot.
 */
public class SignInHandler extends AbstractHandler {

  private AuthStatusManager authStatusManager;

  /**
   * Initialize the Copilot Language Server for the SignInHandler.
   */
  public SignInHandler() {
    this.authStatusManager = CopilotCore.getPlugin().getAuthStatusManager();
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    SignInJob signInJob = new SignInJob(event);
    signInJob.schedule();

    return null;
  }

  private class SignInJob extends Job {

    private final ExecutionEvent event;

    /**
     * Creates a new completion job.
     */
    public SignInJob(ExecutionEvent event) {
      super("Initializing GitHub Copilot sign-in process...");
      this.event = event;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
      try {
        Shell shell = SwtUtils.getShellFromEvent(event);
        IStatus status = runInitiateSignIn(shell);
        return status;
      } catch (Exception e) {
        String msg = Messages.signInHandler_msgDialog_signInFailed;
        if (StringUtils.isNotBlank(e.getMessage())) {
          msg += " " + e.getMessage();
          CopilotCore.LOGGER.error(msg, e);
        }

        String errorMsg = "Sign in failed: " + e.getMessage();
        return new Status(IStatus.ERROR, Constants.PLUGIN_ID, errorMsg);
      }
    }

    private IStatus runInitiateSignIn(Shell shell)
        throws InterruptedException, java.util.concurrent.ExecutionException {
      SignInInitiateResult result = initiateSignIn();
      if (result.isAlreadySignedIn()) {
        showAlreadySignedInMessage(shell);
        return Status.OK_STATUS;
      } else {
        handleSignIn(shell, result);
        return Status.OK_STATUS;
      }
    }

    private SignInInitiateResult initiateSignIn() throws InterruptedException, java.util.concurrent.ExecutionException {
      return authStatusManager.signInInitiate();
    }

    private void showAlreadySignedInMessage(Shell shell) {
      SwtUtils.invokeOnDisplayThread(() -> {
        MessageDialog.openInformation(shell, Messages.signInHandler_msgDialog_title,
            Messages.signInHandler_msgDialog_alreadySignedIn);
      }, shell);
    }

    private void handleSignIn(Shell shell, SignInInitiateResult result) {
      AtomicReference<SignInInitiateResult> signInInitiateResultHolder = new AtomicReference<>(result);
      SwtUtils.invokeOnDisplayThread(() -> {
        SignInDialog signInDialog = new SignInDialog(shell, signInInitiateResultHolder.get());
        int openResult = signInDialog.open();
        if (openResult > 0) {
          UiUtils.openLink(signInInitiateResultHolder.get().getVerificationUri());
          SignInConfirmDialog signInConfirmDialog = new SignInConfirmDialog(shell,
              signInInitiateResultHolder.get().getUserCode());
          signInConfirmDialog.run();
          handleSignInConfirmation(shell, signInConfirmDialog);
        }
      }, shell);
    }

    private void handleSignInConfirmation(Shell shell, SignInConfirmDialog signInConfirmDialog) {
      IStatus status = signInConfirmDialog.getStatus();
      if (status == null) {
        CopilotCore.LOGGER.error(new IllegalStateException("Sign in confirmation failed: status is null"));
        return;
      }
      if (status.isOK()) {
        showSignInSuccessMessage(shell);
      } else if (Objects.equals(CopilotStatusResult.ERROR, SignInHandler.this.authStatusManager.getCopilotStatus())) {
        // the CLS will show a dialog for not authorized case
        showSignInFailMessage(shell, status);
      }
    }

    private void showSignInSuccessMessage(Shell shell) {
      MessageDialog.openInformation(shell, Messages.signInHandler_msgDialog_githubCopilot,
          Messages.signInHandler_msgDialog_signInSuccess);
    }

    private void showSignInFailMessage(Shell shell, IStatus status) {
      String msg = Messages.signInHandler_msgDialog_signInFailed;
      if (status != null && StringUtils.isNotBlank(status.getMessage())) {
        msg += ": " + status.getMessage();
      }
      MessageDialog.openInformation(shell, Messages.signInHandler_msgDialog_githubCopilot,
          msg + Messages.signInHandler_msgDialog_signInFailedTryAgain);
    }
  }
}
