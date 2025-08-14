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

import org.eclipse.swt.graphics.GC;

import org.eclipse.copilot.core.lsp.protocol.quota.CheckQuotaResult;
import org.eclipse.copilot.core.lsp.protocol.quota.CopilotPlan;
import org.eclipse.copilot.core.lsp.protocol.quota.Quota;
import org.eclipse.copilot.core.utils.PlatformUtils;
import org.eclipse.copilot.ui.i18n.Messages;
import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * Helper class for quota text calculations.
 */
public class QuotaTextCalculator {
  private final GC gc;
  private final CheckQuotaResult quotaResult;
  private final int maxWidth;
  private final int spaceWidth;

  /**
   * Constructor for QuotaTextCalculator.
   *
   * @param gc the graphics context used for text measurements.
   * @param quotaResult the result containing quota information.
   */
  public QuotaTextCalculator(GC gc, CheckQuotaResult quotaResult) {
    this.gc = gc;
    this.quotaResult = quotaResult;
    this.spaceWidth = gc.textExtent(UiUtils.HAIR_SPACE).x;
    this.maxWidth = calculateMaxWidth();
  }

  private int calculateMaxWidth() {
    int max = 0;
    if (!PlatformUtils.isWindows()) {
      max = Math.max(max,
          gc.stringExtent(Messages.menu_quota_codeCompletions + getPercentUsed(quotaResult.getCompletionsQuota())).x);
      max = Math.max(max,
          gc.stringExtent(Messages.menu_quota_chatMessages + getPercentUsed(quotaResult.getChatQuota())).x);
      if (quotaResult.getCopilotPlan() != CopilotPlan.free) {
        max = Math.max(max, gc.stringExtent(
            Messages.menu_quota_premiumRequests + getPercentUsed(quotaResult.getPremiumInteractionsQuota())).x);
      }
    }
    return max;
  }

  /**
   * Returns the aligned text for code completions quota.
   */
  public String getCompletionText() {
    return getAlignedQuotaText(Messages.menu_quota_codeCompletions, quotaResult.getCompletionsQuota());
  }

  /**
   * Returns the aligned text for chat messages quota.
   */
  public String getChatText() {
    return getAlignedQuotaText(Messages.menu_quota_chatMessages, quotaResult.getChatQuota());
  }

  /**
   * Returns the aligned text for premium requests quota.
   */
  public String getPremiumText() {
    return getAlignedQuotaText(Messages.menu_quota_premiumRequests, quotaResult.getPremiumInteractionsQuota());
  }

  /**
   * Helper method to generate aligned quota text for any quota type.
   *
   * @param messagePrefix the message prefix (e.g., "Code completions")
   * @param quota the quota object to get percentage from
   * @return the aligned quota text
   */
  private String getAlignedQuotaText(String messagePrefix, Quota quota) {
    if (PlatformUtils.isWindows()) {
      // windows supports align the text via \t
      return messagePrefix.trim() + "\t" + getPercentUsed(quota);
    }
    String quotaText = getPercentUsed(quota);
    int currentWidth = gc.stringExtent(messagePrefix + quotaText).x;
    int spacesToAdd = (int) Math.round((maxWidth - currentWidth) / (double) spaceWidth) + 1;
    return UiUtils.getAlignedText(gc, messagePrefix, UiUtils.HAIR_SPACE, quotaText, spacesToAdd, maxWidth);
  }

  private String getPercentUsed(Quota quota) {
    if (quota.isUnlimited()) {
      return "Included";
    }
    double percent = Math.max(0, 100 - quota.getPercentRemaining());
    if (percent < 0.1) {
      return "0%";
    }
    return String.format("%.1f", Math.round(percent * 10) / 10.0) + "%";
  }
}
