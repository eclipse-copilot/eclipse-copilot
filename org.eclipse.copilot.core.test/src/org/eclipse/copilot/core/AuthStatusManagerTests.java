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

package org.eclipse.copilot.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.core.lsp.protocol.CopilotStatusResult;
import org.eclipse.copilot.core.lsp.protocol.SignInInitiateResult;
import org.eclipse.copilot.core.lsp.protocol.quota.CheckQuotaResult;

@ExtendWith(MockitoExtension.class)
class AuthStatusManagerTests {

  @Mock
  CopilotLanguageServerConnection mockConnection;
  AuthStatusManager authStatusManager;

  @BeforeEach
  void setUp() {
    authStatusManager = new AuthStatusManager(mockConnection);
  }

  @Test
  void testSignInInitiate() throws InterruptedException, ExecutionException {
    SignInInitiateResult mockResult = mock(SignInInitiateResult.class);
    when(mockResult.isAlreadySignedIn()).thenReturn(true);
    when(mockConnection.signInInitiate()).thenReturn(CompletableFuture.completedFuture(mockResult));

    SignInInitiateResult result = authStatusManager.signInInitiate();

    assertEquals(CopilotStatusResult.OK, authStatusManager.getCopilotStatus());
    assertEquals(mockResult, result);
  }

  @Test
  void testSignInConfirm() throws InterruptedException, ExecutionException {
    String userCode = "testUserCode";
    String mockedUser = "mockedUser";
    CopilotStatusResult mockResult = mock(CopilotStatusResult.class);
    when(mockResult.getUser()).thenReturn(mockedUser);
    when(mockResult.getStatus()).thenReturn(CopilotStatusResult.OK);
    when(mockConnection.signInConfirm(userCode)).thenReturn(CompletableFuture.completedFuture(mockResult));
    when(mockConnection.checkQuota()).thenReturn(CompletableFuture.completedFuture(new CheckQuotaResult()));

    CopilotStatusResult result = authStatusManager.signInConfirm(userCode);

    assertEquals(mockedUser, authStatusManager.getUserName());
    assertEquals(CopilotStatusResult.OK, authStatusManager.getCopilotStatus());
    assertEquals(mockResult, result);
  }

  @Test
  void testSignOut() throws InterruptedException, ExecutionException {
    CopilotStatusResult mockResult = mock(CopilotStatusResult.class);
    when(mockResult.getStatus()).thenReturn(CopilotStatusResult.NOT_SIGNED_IN);
    when(mockConnection.signOut()).thenReturn(CompletableFuture.completedFuture(mockResult));

    CopilotStatusResult result = authStatusManager.signOut();

    assertTrue(authStatusManager.getUserName().isEmpty());
    assertEquals(CopilotStatusResult.NOT_SIGNED_IN, authStatusManager.getCopilotStatus());
    assertEquals(mockResult, result);
  }

  @Test
  void testCopilotStatusResultOnSuccess() {
    CopilotStatusResult expectedResult = new CopilotStatusResult();
    expectedResult.setStatus(CopilotStatusResult.OK);
    when(mockConnection.checkStatus(false)).thenReturn(CompletableFuture.completedFuture(expectedResult));

    authStatusManager.checkStatus();

    assertEquals(CopilotStatusResult.OK, authStatusManager.getCopilotStatus());
  }

  @Test
  void testCheckStatusOk() throws InterruptedException {
    String mockedUser = "mockedUser";
    // Arrange
    CopilotStatusResult expectedResult = new CopilotStatusResult();
    expectedResult.setStatus(CopilotStatusResult.OK);
    expectedResult.setUser(mockedUser);
    CompletableFuture<CopilotStatusResult> future = new CompletableFuture<>();
    when(mockConnection.checkStatus(false)).thenReturn(future);
    future.complete(expectedResult);

    authStatusManager.checkStatus();

    // Assert final status is OK
    assertEquals(CopilotStatusResult.OK, authStatusManager.getCopilotStatus());
    assertEquals(mockedUser, authStatusManager.getUserName());
  }

  @Test
  void testCheckStatusError() {
    CompletableFuture<CopilotStatusResult> future = new CompletableFuture<>();
    future.completeExceptionally(new CompletionException(new Exception("Some other error")));

    when(mockConnection.checkStatus(false)).thenReturn(future);

    authStatusManager.checkStatus();

    assertEquals(CopilotStatusResult.ERROR, authStatusManager.getCopilotStatus());
  }

}
