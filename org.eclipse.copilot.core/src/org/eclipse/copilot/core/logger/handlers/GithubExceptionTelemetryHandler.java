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

package org.eclipse.copilot.core.logger.handlers;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.logger.LogLevel;

/**
 * The log appender to send info and error messages to the Eclipse console.
 */
public class GithubExceptionTelemetryHandler extends Handler {
  /**
   * Constructor.
   */
  public GithubExceptionTelemetryHandler() {
  }

  @Override
  public void publish(LogRecord logRecord) {
    Object[] property = logRecord.getParameters();
    if (property == null || property.length < 2) {
      return;
    }
    if (!(property[0] instanceof LogLevel)) {
      return;
    }
    LogLevel lvl = (LogLevel) property[0];
    if (lvl != LogLevel.ERROR) {
      return;
    }
    Throwable ex = (Throwable) property[1];
    CopilotCore copilotCore = CopilotCore.getPlugin();
    if (copilotCore == null || ex == null) {
      return;
    }
    copilotCore.reportException(ex);
  }

  @Override
  public void flush() {
    // do nothing
  }

  @Override
  public void close() throws SecurityException {
    // do nothing
  }
}
