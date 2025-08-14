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

package org.eclipse.copilot.ui;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.ui.PlatformUI;

import org.eclipse.copilot.core.events.CopilotEventConstants;
import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * Manager dealing with the copilot status in the Eclipse status bar.
 */
public class CopilotStatusManager {

  private IEventBroker eventBroker;

  /**
   * Constructor for the CopilotStatusManager.
   */
  public CopilotStatusManager() {
    eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
    if (eventBroker != null) {
      eventBroker.subscribe(CopilotEventConstants.TOPIC_AUTH_STATUS_CHANGED, event -> {
        onDidCopilotStatusChange();
      });
    }
  }

  private void onDidCopilotStatusChange() {
    UiUtils.refreshCopilotMenu();
  }
}