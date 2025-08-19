/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.lsp.protocol;

/**
 * Creates a new ChatStepStatus.
 */
public class ChatStepStatus {
  public static final String RUNNING = "running";
  public static final String COMPLETED = "completed";
  public static final String FAILED = "failed";
  public static final String CANCELLED = "cancelled";
}
