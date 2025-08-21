/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.terminal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.terminal.control.ITerminalViewControl;
import org.eclipse.terminal.connector.ITerminalControl;
import org.eclipse.terminal.view.core.internal.CoreBundleActivator;
import org.eclipse.terminal.view.core.ITerminalService;
import org.eclipse.terminal.view.core.ITerminalServiceOutputStreamMonitorListener;
import org.eclipse.terminal.view.core.ITerminalsConnectorConstants;
import org.eclipse.terminal.view.ui.internal.UIPlugin;
import org.eclipse.terminal.view.ui.IPreferenceKeys;
import org.eclipse.terminal.view.ui.IUIConstants;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.eclipse.copilot.api.IRunInTerminalTool;

/**
 * Modern terminal tool implementation for newer Eclipse versions.
 */
public class RunInTerminalTool implements IRunInTerminalTool {
  // Shared constants and static fields
  private static final Object lock = new Object();
  private static final Map<String, StringBuilder> backgroundCommandOutputs = new HashMap<>();
  private static final String BACKGROUND_TERMINAL_PREFIX = "Copilot-";

  // Non-background terminal field
  private ITerminalViewControl persistentTerminalViewControl;

  // Terminal UI-related fields
  private ITerminalControl terminalControl;
  private CTabFolder tabFolder;
  private CTabItem copilotTabItem;
  private Image terminalIcon;

  // Output and command state
  private StringBuilder sb;
  private CompletableFuture<String> resultFuture;

  /**
   * Constructor for RunInTerminalTool.
   */
  public RunInTerminalTool() {
    this.sb = new StringBuilder();
  }

  @Override
  public CompletableFuture<String> executeCommand(String command, boolean isBackground) {
    if (StringUtils.isBlank(command)) {
      return CompletableFuture.completedFuture("The command is null or empty.");
    }

    resultFuture = new CompletableFuture<>();
    
    // Retain only the last line (prompt) in the output buffer
    if (!sb.isEmpty()) {
      int lastLineStart = sb.lastIndexOf(System.lineSeparator());
      if (lastLineStart > 0) {
        sb.delete(0, lastLineStart);
      }
    }

    String executionId = UUID.randomUUID().toString();
    final String finalCommand = command + System.lineSeparator();
    
    synchronized (lock) {
      if (!isBackground && this.persistentTerminalViewControl != null) {
        bringTerminalViewAndCopilotConsoleToFront();
        this.persistentTerminalViewControl.pasteString(finalCommand);
        return CompletableFuture.completedFuture("Command executed in existing terminal");
      }

      ITerminalService service = CoreBundleActivator.getTerminalService();
      if (service == null) {
        return CompletableFuture.completedFuture("Failed to open terminal console due to terminal service is null.");
      }
      
      service.openConsole(prepareTerminalProperties(isBackground, executionId)).handle((o, e) -> {
        if (e == null) {
          ITerminalViewControl terminalViewControl = finalizeTerminalSetup(executionId, isBackground);
          if (terminalViewControl == null) {
            resultFuture.complete("Terminal view control cannot be setup for RunInTerminalTool.");
            return null;
          }

          if (!isBackground) {
            this.persistentTerminalViewControl = terminalViewControl;
            bringTerminalViewAndCopilotConsoleToFront();
          }
          terminalViewControl.pasteString(finalCommand);
        } else {
          resultFuture.complete("Failed to open terminal console: " + e.getMessage());
        }
        return null;
      });
    }

    if (isBackground) {
      return CompletableFuture.completedFuture("Command is running in terminal with ID=" + executionId);
    }
    
    return resultFuture;
  }

  @Override
  public Map<String, Object> prepareTerminalProperties(boolean runInBackground, String executionId) {
    Map<String, Object> properties = new HashMap<>();

    properties.put(ITerminalsConnectorConstants.PROP_ENCODING, "UTF-8");
    properties.put(ITerminalsConnectorConstants.PROP_TITLE_DISABLE_ANSI_TITLE, true);

    // Only set target terminal for Windows - using Platform directly instead of PlatformUtils
    if (Platform.getOS().equals(Platform.OS_WIN32)) {
      properties.put(ITerminalsConnectorConstants.PROP_PROCESS_PATH, "cmd.exe");
    } else {
      // Only set the process args if not already set by user preferences
      String args = UIPlugin.getScopedPreferences()
          .getString(IPreferenceKeys.PREF_LOCAL_TERMINAL_DEFAULT_SHELL_UNIX_ARGS);
      if (StringUtils.isBlank(args)) {
        properties.put(ITerminalsConnectorConstants.PROP_PROCESS_ARGS, "-l");
      }
    }

    properties.put(ITerminalsConnectorConstants.PROP_FORCE_NEW, true);
    properties.put(ITerminalsConnectorConstants.PROP_DELEGATE_ID,
        "org.eclipse.terminal.connector.local.launcher.local");

    if (runInBackground) {
      properties.put(ITerminalsConnectorConstants.PROP_TITLE, buildBackgroundTerminalTitle(executionId));
      properties.put(ITerminalsConnectorConstants.PROP_STDOUT_LISTENERS,
          new ITerminalServiceOutputStreamMonitorListener[] { buildOutputStreamMonitorListener(true, executionId) });
    } else {
      properties.put(ITerminalsConnectorConstants.PROP_TITLE, "Copilot");
      properties.put(ITerminalsConnectorConstants.PROP_STDOUT_LISTENERS,
          new ITerminalServiceOutputStreamMonitorListener[] { buildOutputStreamMonitorListener(false, null) });
    }

    return properties;
  }

  @Override
  public StringBuilder getBackgroundCommandOutput(String executionId) {
    StringBuilder output = backgroundCommandOutputs.get(executionId);
    return output;
  }

  private ITerminalViewControl finalizeTerminalSetup(String executionId, boolean isBackground) {
    String title = isBackground ? buildBackgroundTerminalTitle(executionId) : "Copilot";
    synchronized (lock) {
      terminalControl = getTerminalControl(title, isBackground);
      if (terminalControl != null && terminalControl instanceof ITerminalViewControl iterminalviewcontrol) {
        return iterminalviewcontrol;
      }
    }
    return null;
  }

  private ITerminalControl getTerminalControl(String terminalTitle, boolean isBackground) {
    AtomicReference<ITerminalControl> ref = new AtomicReference<>();
    
    Display.getDefault().asyncExec(() -> {
      try {
        IWorkbenchPage page = getActivePage();
        if (page != null) {
          IViewPart view = page.showView(IUIConstants.ID);
          if (view != null) {
            tabFolder = view.getAdapter(CTabFolder.class);
            if (tabFolder != null) {
              for (CTabItem item : tabFolder.getItems()) {
                if (terminalTitle.equals(item.getText())) {
                  // Create icon without UiUtils dependency
                  terminalIcon = null;
                  if (terminalIcon != null) {
                    item.setImage(terminalIcon);
                  }
                  item.addDisposeListener(
                      buildDisposeListener(terminalTitle.replace(BACKGROUND_TERMINAL_PREFIX, ""), isBackground));
                  if (!isBackground) {
                    // Foreground terminal command will reuse the tab item, so keep a reference to the tab item
                    copilotTabItem = item;
                  }
                  ref.set((ITerminalControl) item.getData());
                  break;
                }
              }
            }
          }
        }
      } catch (PartInitException e) {
        //TODO: Handle exception properly
      } catch (Exception e) {
        //TODO: Handle exception properly
      }
    });

    return ref.get();
  }

  private ITerminalServiceOutputStreamMonitorListener buildOutputStreamMonitorListener(boolean isBackground,
      String executionId) {
    StringBuilder output = isBackground ? new StringBuilder() : sb;
    if (isBackground) {
      backgroundCommandOutputs.put(executionId, output);
    }

    return (byteBuffer, bytesRead) -> {
      String content = new String(byteBuffer, 0, bytesRead);
      // Remove ANSI escape sequences
      // Sometimes it also removes the linebreaks. But we need the last prompt line to be a separate line later. So we
      // add line separator back to the content.
      content = content.replaceAll("\u001B\\[(\\?)?[\\d;]*[a-zA-Z]", System.lineSeparator());
      
      // Handle Windows terminal title sequences - using Platform instead of PlatformUtils
      if (Platform.getOS().equals(Platform.OS_WIN32)) {
        // Remove terminal title sequences in Windows
        // It sometimes appears at the last line, which will also destroy the validation of the last prompt line.
        content = content.replaceAll("\u001B\\][0-9];.*?(\u0007|\u001B\\\\)", "");
      }
      
      output.append(content);
      String terminalOutput = output.toString().trim();
      int lastNewLineIndex = terminalOutput.lastIndexOf(System.lineSeparator());
      if (lastNewLineIndex > 0) {
        String lastLine = terminalOutput.substring(lastNewLineIndex).trim();

        // Check if last line is a prompt line
        // Mac always has single '%' as last line, that's not what we want.
        if (StringUtils.isNotBlank(lastLine) && lastLine.length() != 1) {
          char lastChar = lastLine.charAt(lastLine.length() - 1);
          boolean isPromptChar = lastChar == '>' || lastChar == '#' || lastChar == '$' || lastChar == '%';

          if (isPromptChar) {
            // Extract result text between prompts
            String contentWithoutLastPrompt = terminalOutput.substring(0, lastNewLineIndex);
            int promptStartIndex = contentWithoutLastPrompt.indexOf(lastLine);
            // If the prompt line is not found, set start index to 0. Sometimes it starts with the commandResult.
            if (promptStartIndex == -1) {
              promptStartIndex = 0;
            } else {
              promptStartIndex += lastLine.length();
            }

            if (!contentWithoutLastPrompt.isBlank()) {
              String commandResult = contentWithoutLastPrompt.substring(promptStartIndex).trim();
              if (resultFuture != null && !resultFuture.isDone()) {
                resultFuture.complete(commandResult);
              }
            }
          }
        }
      }
    };
  }

  private DisposeListener buildDisposeListener(String executionId, boolean isBackground) {
    return e -> {
      if (isBackground) {
        backgroundCommandOutputs.remove(executionId);
      } else {
        persistentTerminalViewControl = null;
      }

      if (backgroundCommandOutputs.isEmpty() && persistentTerminalViewControl == null) {
        terminalControl = null;
        if (terminalIcon != null && !terminalIcon.isDisposed()) {
          terminalIcon.dispose();
          terminalIcon = null;
        }
      }
    };
  }

  private void bringTerminalViewAndCopilotConsoleToFront() {
    if (tabFolder != null && copilotTabItem != null) {
      Display.getDefault().asyncExec(() -> {
        try {
          IWorkbenchPage page = getActivePage();
          if (page != null) {
            IViewPart view = page.showView(IUIConstants.ID);
            if (tabFolder.isDisposed() && view != null) {
              tabFolder = view.getAdapter(CTabFolder.class);
            }
          }
          if (tabFolder != null && !tabFolder.isDisposed()) {
            tabFolder.setSelection(copilotTabItem);
          }
        } catch (PartInitException e) {
          // Skip exception
        }
      });
    }
  }

  private String buildBackgroundTerminalTitle(String executionId) {
    return BACKGROUND_TERMINAL_PREFIX + executionId;
  }

  /**
   * Get active workbench page without UiUtils dependency.
   */
  private IWorkbenchPage getActivePage() {
    try {
      if (PlatformUI.isWorkbenchRunning()) {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      }
    } catch (Exception e) {
      // Fallback - try to get any available page
      try {
        return PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage();
      } catch (Exception ex) {
        //Todo: Handle exception properly
      }
    }
    return null;
  }
}