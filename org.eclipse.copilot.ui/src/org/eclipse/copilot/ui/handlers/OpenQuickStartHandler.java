/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.intro.IIntroPart;

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.ui.UiConstants;
import org.eclipse.copilot.ui.i18n.Messages;
import org.eclipse.copilot.ui.quickstart.FeaturePage;

/**
 * Handler for opening the quick start page.
 */
public class OpenQuickStartHandler extends AbstractHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Shell parentShell = HandlerUtil.getActiveShell(event);

    QuickStartDialog dialog = new QuickStartDialog(parentShell);
    dialog.open();

    return null;
  }

  /**
   * Custom dialog for the Quick Start window.
   */
  private class QuickStartDialog extends Dialog {
    private Color normalBackgroundColor;

    public QuickStartDialog(Shell parentShell) {
      super(parentShell);
      setShellStyle(SWT.RESIZE | SWT.APPLICATION_MODAL | SWT.NO_TRIM | SWT.ON_TOP);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
      // Create a composite to add a border around the dialog area, dialog without header doesn't have a border
      Composite mainComposite = new Composite(parent, SWT.BORDER);
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainComposite.setData(CSSSWTConstants.CSS_ID_KEY, "quick-start-container");
      GridLayout parentLayout = new GridLayout(1, false);
      parentLayout.marginWidth = 56;
      parentLayout.marginHeight = 32;
      parentLayout.verticalSpacing = 24;
      mainComposite.setLayout(parentLayout);

      new FeaturePage(mainComposite);
      this.createButtonArea(mainComposite);
      return mainComposite;
    }

    @Override
    protected Point getInitialSize() {
      return new Point(890, 544);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
      // Return null to prevent the default button bar from being created
      return null;
    }

    private void createButtonArea(Composite parent) {
      Composite buttonComposite = new Composite(parent, SWT.NONE);
      GridLayout buttonLayout = new GridLayout(1, false);
      buttonLayout.marginWidth = 0;
      buttonLayout.marginHeight = 0;
      buttonComposite.setLayout(buttonLayout);
      buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
      buttonComposite.setData(CSSSWTConstants.CSS_ID_KEY, "quick-start-container");

      Button continueButton = new Button(buttonComposite, SWT.PUSH);
      continueButton.setText(Messages.quickStart_continueButton);
      GridData buttonData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
      buttonData.widthHint = 200;
      buttonData.heightHint = 28;
      continueButton.setLayoutData(buttonData);
      continueButton.setData(CSSSWTConstants.CSS_ID_KEY, "quick-start-continue-button");

      continueButton.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseDown(MouseEvent e) {
          close();

          IWorkbench workBench = PlatformUI.getWorkbench();

          // Close the Eclipse welcome view if it's open
          IIntroPart introPart = workBench.getIntroManager().getIntro();
          if (introPart != null) {
            workBench.getIntroManager().closeIntro(introPart);
          }

          // Open the Copilot chat view
          workBench.getDisplay().asyncExec(() -> {
            IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
                .getService(IHandlerService.class);
            try {
              handlerService.executeCommand(UiConstants.OPEN_QUICK_START_COMMAND_ID, null);
            } catch (Exception ex) {
              CopilotCore.LOGGER.error("Failed to open chat view", ex);
            }
          });
        }
      });
    }

    @Override
    // Override the open method to center the dialog on the current monitor where the parent shell is located
    public int open() {
      int result = super.open();
      Shell shell = getShell();
      if (shell != null && shell.getParent() != null) {
        Shell parentShell = (Shell) shell.getParent();
        // Get the bounds of the monitor where the parent shell is located
        Monitor monitor = parentShell.getMonitor();
        Rectangle monitorBounds = monitor.getBounds();
        Rectangle shellBounds = shell.getBounds();
        // Center the dialog on the monitor
        int x = monitorBounds.x + (monitorBounds.width - shellBounds.width) / 2;
        int y = monitorBounds.y + (monitorBounds.height - shellBounds.height) / 2;
        shell.setLocation(x, y);
      }
      return result;
    }
  }
}