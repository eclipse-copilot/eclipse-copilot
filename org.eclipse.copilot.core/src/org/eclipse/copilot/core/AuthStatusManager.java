/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import org.eclipse.copilot.core.events.CopilotEventConstants;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.core.lsp.protocol.CopilotStatusResult;
import org.eclipse.copilot.core.lsp.protocol.SignInInitiateResult;
import org.eclipse.copilot.core.lsp.protocol.quota.CheckQuotaResult;

/**
 * Manager for the authentication status.
 */
public class AuthStatusManager {

  public static final long SIGNIN_TIMEOUT_MILLIS = 180000L;

  private CopilotLanguageServerConnection connection;
  private ConcurrentLinkedQueue<CopilotAuthStatusListener> copilotAuthStatusListeners;
  private CopilotStatusResult copilotStatusResult;
  private CheckQuotaResult checkQuotaResult;
  private IEventBroker eventBroker;

  /**
   * Constructor for the AuthStatusManager.
   *
   * @param connection the connection to the language server.
   */
  public AuthStatusManager(CopilotLanguageServerConnection connection) {
    this.connection = connection;
    this.copilotAuthStatusListeners = new ConcurrentLinkedQueue<>();
    this.copilotStatusResult = new CopilotStatusResult();
    BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
    IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(bundleContext);
    eventBroker = serviceContext.get(IEventBroker.class);
    setCopilotStatus(CopilotStatusResult.LOADING);
  }

  /**
   * Initiate the sign in process.
   *
   * @throws ExecutionException if the sign in initiate process fails due to an execution error
   * @throws InterruptedException if the sign in initiate process is interrupted
   */
  public SignInInitiateResult signInInitiate() throws InterruptedException, ExecutionException {
    SignInInitiateResult result = connection.signInInitiate().get();
    if (result.isAlreadySignedIn()) {
      setCopilotStatus(CopilotStatusResult.OK);
    }

    return result;
  }

  /**
   * Confirm the sign in process.
   *
   * @throws ExecutionException if the sign in process fails due to an execution error
   * @throws InterruptedException if the sign in process is interrupted
   */
  public CopilotStatusResult signInConfirm(String userCode) throws InterruptedException, ExecutionException {
    // If a timeout occurs, NOT_SIGNED_IN will be used as the return value.
    CopilotStatusResult resultWhenTimeout = new CopilotStatusResult();
    resultWhenTimeout.setStatus(CopilotStatusResult.NOT_SIGNED_IN);

    CopilotStatusResult result = connection.signInConfirm(userCode)
        .completeOnTimeout(resultWhenTimeout, SIGNIN_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS).get();
    setCopilotUser(result.getUser());
    setCopilotStatus(result.getStatus());
    return result;
  }

  /**
   * Sign out from the GitHub Copilot.
   *
   * @throws ExecutionException if the sign out process fails due to an execution error
   * @throws InterruptedException if the sign out process is interrupted
   */
  public CopilotStatusResult signOut() throws InterruptedException, ExecutionException {
    CopilotStatusResult result = connection.signOut().get();
    setCopilotUser(result.getUser());
    setCopilotStatus(result.getStatus());
    return result;
  }

  /**
   * Set the CopilotStatusResult string to the given status and notify the listeners.
   */
  public CopilotStatusResult setCopilotStatus(String newCopilotStatusResult) {
    if (!Objects.equals(this.copilotStatusResult.getStatus(), newCopilotStatusResult)) {
      this.copilotStatusResult.setStatus(newCopilotStatusResult);
      onDidCopilotStatusChange(this.copilotStatusResult);
    }
    return this.copilotStatusResult;
  }

  /**
   * Check the authentication status for current machine.
   *
   * @return CompletableFuture that completes when the status check is done
   */
  public CompletableFuture<CopilotStatusResult> checkStatus() {
    return this.connection.checkStatus(false).handle((result, ex) -> {
      if (ex != null) {
        CopilotCore.LOGGER.error(ex);
        setCopilotStatus(CopilotStatusResult.ERROR);
      } else {
        // we will send status change event in set Status, so need to set user first
        setCopilotUser(result.getUser());
        setCopilotStatus(result.getStatus());
      }
      return this.copilotStatusResult;
    });
  }

  /**
   * Check the user's quota usage.
   *
   * @return CompletableFuture containing the check quota result
   */
  public CompletableFuture<CheckQuotaResult> checkQuota() {
    return this.connection.checkQuota().thenApply(result -> {
      setQuotaStatus(result);
      return result;
    }).exceptionally(ex -> {
      CopilotCore.LOGGER.error(ex);
      return null;
    });
  }

  /**
   * Set the user for Copilot.
   */
  public void setCopilotUser(String user) {
    this.copilotStatusResult.setUser(user);
  }

  /**
   * Get the current status of the copilot.
   */
  public String getCopilotStatus() {
    if (this.copilotStatusResult == null) {
      return CopilotStatusResult.LOADING;
    }
    return this.copilotStatusResult.getStatus();
  }

  /**
   * Get the name of the login user.
   */
  public String getUserName() {
    if (this.copilotStatusResult == null) {
      return "";
    }
    if (this.copilotStatusResult.getUser() == null) {
      return "";
    }
    return this.copilotStatusResult.getUser();
  }

  /**
   * Set the CheckQuotaResult.
   */
  public void setQuotaStatus(CheckQuotaResult checkQuotaResult) {
    this.checkQuotaResult = checkQuotaResult;
  }

  /**
   * Get the current CopilotStatusResult.
   */
  public CheckQuotaResult getQuotaStatus() {
    if (this.checkQuotaResult == null) {
      this.checkQuotaResult = new CheckQuotaResult();
    }
    return this.checkQuotaResult;
  }

  /**
   * Add a listener for the authentication status.
   */
  public void addCopilotAuthStatusListener(CopilotAuthStatusListener listener) {
    this.copilotAuthStatusListeners.add(listener);
  }

  /**
   * Remove the listener for the authentication status.
   */
  public void removeCopilotAuthStatusListener(CopilotAuthStatusListener listener) {
    this.copilotAuthStatusListeners.remove(listener);
  }

  public boolean isSignedIn() {
    return this.copilotStatusResult.isSignedIn();
  }

  public boolean isLoading() {
    return this.copilotStatusResult.isLoading();
  }

  public boolean isNotSignedInOrNotAuthorized() {
    return this.copilotStatusResult.isNotSignedIn() || this.copilotStatusResult.isNotAuthorized();
  }

  public boolean isNotAuthorized() {
    return this.copilotStatusResult.isNotAuthorized();
  }

  public boolean isNotSignedIn() {
    return this.copilotStatusResult.isNotSignedIn();
  }

  private void onDidCopilotStatusChange(CopilotStatusResult copilotStatusResult) {
    if (copilotStatusResult.isSignedIn()) {
      this.checkQuota();
    }
    if (!this.copilotAuthStatusListeners.isEmpty()) {
      for (CopilotAuthStatusListener listener : this.copilotAuthStatusListeners) {
        listener.onDidCopilotStatusChange(copilotStatusResult);
      }
    }
    if (eventBroker != null) {
      eventBroker.post(CopilotEventConstants.TOPIC_AUTH_STATUS_CHANGED, copilotStatusResult);
    }
  }
}
