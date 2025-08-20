/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.chat;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.EventHandler;

import org.eclipse.copilot.core.events.CopilotEventConstants;
import org.eclipse.copilot.ui.swt.CssConstants;
import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * A label with icon that displays the running status of the agent.
 */
public class AgentStatusLabel extends Composite {
  private static final int TOTAL_FRAMES = 8; // Adjust based on actual number of spinner images

  private Image runningIcon;
  private Image completedIcon;
  private Image cancelledIcon;
  private Image errorIcon;
  private Label iconLabel;
  private ChatMarkupViewer textLabel;
  private int currentFrame = 1;
  private Runnable animationRunnable;
  private Status status;
  private EventHandler cancelStatusHandler;
  private IEventBroker eventBroker;

  /**
   * Create the composite.
   *
   * @param parent the parent composite
   * @param style the style
   */
  public AgentStatusLabel(Composite parent, int style) {
    super(parent, style);
    setLayout(new GridLayout(2, false));
    setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    this.addDisposeListener(e -> {
      stopAnimation();
      if (this.runningIcon != null && !this.runningIcon.isDisposed()) {
        this.runningIcon.dispose();
      }
      if (this.completedIcon != null && !this.completedIcon.isDisposed()) {
        this.completedIcon.dispose();
      }
      if (this.cancelledIcon != null && !this.cancelledIcon.isDisposed()) {
        this.cancelledIcon.dispose();
      }
      if (this.errorIcon != null && !this.errorIcon.isDisposed()) {
        this.errorIcon.dispose();
      }
      if (this.eventBroker != null) {
        this.eventBroker.unsubscribe(cancelStatusHandler);
      }
    });
    iconLabel = new Label(this, SWT.LEFT);

    this.status = Status.RUNNING;
    this.cancelStatusHandler = new EventHandler() {
      @Override
      public void handleEvent(org.osgi.service.event.Event event) {
        setCancelledStatus();
      }
    };
    this.eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
    this.eventBroker.subscribe(CopilotEventConstants.TOPIC_CHAT_MESSAGE_CANCELLED, cancelStatusHandler);
  }

  /**
   * Set the status as completed for the agent with a status message.
   *
   * @param statusText the text to display when the agent is completed
   */
  public void setCompletedStatus(String statusText) {
    stopAnimation();

    if (this.completedIcon == null) {
      this.completedIcon = UiUtils.buildImageFromPngPath("/icons/complete_status.png");
    }
    iconLabel.setImage(completedIcon);

    setText(statusText);
    this.status = Status.COMPLETED;
  }

  /**
   * Set the status as running for the agent with a rotating spinner and a status message.
   *
   * @param statusText the text to display when the agent is running
   */
  public void setRunningStatus(String statusText) {
    // Stop any existing animation
    stopAnimation();

    // Start new animation
    startAnimation();

    setText(statusText);
    this.status = Status.RUNNING;
  }

  /**
   * Set the error status for the agent with a status message.
   */
  public void setErrorStatus() {
    if (this.status == Status.RUNNING) {
      stopAnimation();
    }
    if (this.errorIcon == null) {
      this.errorIcon = UiUtils.buildImageFromPngPath("/icons/message_error.png");
    }
    iconLabel.setImage(errorIcon);
    this.status = Status.ERROR;
  }

  /**
   * Cancel the current running status of the agent status label.
   */
  public void setCancelledStatus() {
    if (this.status == Status.RUNNING) {
      stopAnimation();

      if (this.cancelledIcon == null) {
        this.cancelledIcon = UiUtils.buildImageFromPngPath("/icons/cancel_status.png");
      }
      iconLabel.setImage(cancelledIcon);

      this.status = Status.CANCELLED;
    }
  }

  private void startAnimation() {
    final Display display = getDisplay();

    animationRunnable = new Runnable() {
      @Override
      public void run() {
        if (isDisposed() || iconLabel.isDisposed()) {
          return;
        }

        // Dispose previous image
        if (runningIcon != null && !runningIcon.isDisposed()) {
          runningIcon.dispose();
        }

        // Load the next frame
        String imagePath = String.format("/icons/spinner/%d.png", currentFrame);
        runningIcon = UiUtils.buildImageFromPngPath(imagePath);
        iconLabel.setImage(runningIcon);
        // request layout to update the icon, otherwise the scale of the spinner will be wrong
        iconLabel.requestLayout();

        // Update frame counter
        currentFrame = (currentFrame % TOTAL_FRAMES) + 1;

        // Schedule next frame
        display.timerExec(100, this);
      }
    };

    // Start the animation
    display.timerExec(0, animationRunnable);
  }

  private void stopAnimation() {
    if (animationRunnable != null) {
      getDisplay().timerExec(-1, animationRunnable);
      animationRunnable = null;
    }
  }

  private void setText(String text) {
    if (this.textLabel == null) {
      textLabel = new ChatMarkupViewer(this, SWT.LEFT | SWT.WRAP);
      StyledText styledText = textLabel.getTextWidget();
      styledText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
      styledText.setEditable(false);
      styledText.setData(CssConstants.CSS_CLASS_NAME_KEY, "text-secondary");
    }
    textLabel.setMarkup(text);
  }

  private enum Status {
    RUNNING, COMPLETED, ERROR, CANCELLED
  }
}
