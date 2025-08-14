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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.ui.UiConstants;
import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * Handler to open a URL in the default web browser. The URL is passed as a parameter in the execution event.
 */
public class OpenUrlHandler extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    String url = event.getParameter(UiConstants.OPEN_URL_PARAMETER_NAME);
    if (StringUtils.isNotBlank(url)) {
      try {
        UiUtils.openLink(url);
      } catch (Exception e) {
        CopilotCore.LOGGER.error(e);
      }
    }

    return null;
  }
}
