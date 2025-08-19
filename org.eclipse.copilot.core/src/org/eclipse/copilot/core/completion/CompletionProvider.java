/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.core.completion;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.lsp4j.Position;

import org.eclipse.copilot.core.AuthStatusManager;
import org.eclipse.copilot.core.Constants;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.format.FormatOptionProvider;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.core.lsp.protocol.CompletionDocument;
import org.eclipse.copilot.core.lsp.protocol.CompletionItem;
import org.eclipse.copilot.core.lsp.protocol.CompletionParams;
import org.eclipse.copilot.core.lsp.protocol.CompletionResult;
import org.eclipse.copilot.core.lsp.protocol.CopilotStatusResult;
import org.eclipse.copilot.core.utils.FileUtils;

/**
 * Provider for inline completion.
 */
public class CompletionProvider {

  /**
   * The job family for completion jobs, can be used to find out this completion job.
   */
  public static final String COMPLETION_JOB_FAMILY = "org.eclipse.copilot.completionJobFamily";

  private CompletionJob completionJob;
  private Set<CompletionListener> completionListeners;
  private FormatOptionProvider formatOptionProvider;
  private AuthStatusManager statusManager;

  /**
   * Creates a new completion provider.
   */
  public CompletionProvider(CopilotLanguageServerConnection lsConnection, AuthStatusManager statusManager) {
    this.statusManager = statusManager;
    this.completionJob = new CompletionJob(lsConnection);
    this.completionListeners = new LinkedHashSet<>();
    this.formatOptionProvider = CopilotCore.getPlugin().getFormatOptionProvider();
  }

  /**
   * Trigger an inline completion.
   *
   * @param position the position of the cursor.
   * @param documentVersion the version of the document.
   */
  public void triggerCompletion(IFile file, Position position, int documentVersion) {
    if (statusManager.isNotSignedInOrNotAuthorized()) {
      return;
    }
    this.completionJob.cancel();
    String uriString = FileUtils.getResourceUri(file);
    CompletionDocument completionDoc = new CompletionDocument(uriString, position);
    completionDoc.setVersion(documentVersion);

    boolean insertSpaces = this.formatOptionProvider.useSpace(file);
    int tabSize = this.formatOptionProvider.getTabSize(file);

    completionDoc.setInsertSpaces(insertSpaces);
    completionDoc.setTabSize(tabSize);
    CompletionParams params = new CompletionParams(completionDoc);

    this.completionJob.setCompletionParams(params);
    this.completionJob.setFile(file);
    this.completionJob.schedule();
  }

  /**
   * Add a completion listener.
   */
  public void addCompletionListener(CompletionListener listener) {
    this.completionListeners.add(listener);
  }

  /**
   * Remove a completion listener.
   */
  public void removeCompletionListener(CompletionListener listener) {
    this.completionListeners.remove(listener);
  }

  /**
   * TODO: public for testing.
   */
  public class CompletionJob extends Job {

    private static final int COMPLETION_TIMEOUT_MILLIS = 5000;

    private CopilotLanguageServerConnection lsConnection;
    private CompletionParams params;
    private IResource file;
    private List<CompletionItem> completions;

    /**
     * Creates a new completion job.
     */
    public CompletionJob(CopilotLanguageServerConnection lsConnection) {
      super("Generating completion...");
      this.lsConnection = lsConnection;
      this.setSystem(true);
      this.setPriority(Job.INTERACTIVE);
    }

    public void setCompletionParams(CompletionParams params) {
      this.params = params;
    }

    public void setFile(IResource file) {
      this.file = file;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
      this.completions = null;
      IStatus status = runCompletion(monitor);
      if (status.isOK() && this.completions != null) {
        notifyCompletionResolved();
      }
      return status;
    }

    private IStatus runCompletion(IProgressMonitor monitor) {
      if (params == null) {
        CopilotCore.LOGGER.error(new IllegalStateException("Invalid completion parameters"));
        return new Status(IStatus.ERROR, Constants.PLUGIN_ID, "Invalid completion parameters");
      }
      if (monitor.isCanceled()) {
        return Status.CANCEL_STATUS;
      }
      // the resource may be moved or renamed (closed at CLS side), so do more check before sending the request.
      if (this.file == null) {
        this.file = LSPEclipseUtils.findResourceFor(this.params.getDoc().getUri());
      }
      if (this.file == null || !this.file.exists()) {
        return Status.CANCEL_STATUS;
      }

      try {
        CompletionResult result = this.lsConnection.getCompletions(params).get(COMPLETION_TIMEOUT_MILLIS,
            TimeUnit.MILLISECONDS);
        if (result == null || result.getCompletions() == null || result.getCompletions().isEmpty()) {
          return Status.OK_STATUS;
        }

        this.completions = result.getCompletions();
      } catch (InterruptedException e) {
        return Status.CANCEL_STATUS;
      } catch (ExecutionException e) {
        statusManager.setCopilotStatus(CopilotStatusResult.ERROR);
        CopilotCore.LOGGER.error(e);
        return Status.OK_STATUS;
      } catch (TimeoutException e) {
        CopilotCore.LOGGER.info("Completion request timed out after " + COMPLETION_TIMEOUT_MILLIS + " milliseconds");
        return Status.CANCEL_STATUS;
      }
      if (monitor.isCanceled()) {
        return Status.CANCEL_STATUS;
      }
      return Status.OK_STATUS;
    }

    @Override
    public boolean belongsTo(Object family) {
      return Objects.equals(family, COMPLETION_JOB_FAMILY);
    }

    private void notifyCompletionResolved() {
      for (CompletionListener listener : CompletionProvider.this.completionListeners) {
        // TODO: notify the listener according to the listen uri?
        listener.onCompletionResolved(this.params.getDoc().getUri(), this.completions);
      }
      // If the completion can be resolved, it means the Copilot is working. Set the status to OK to resolve the
      // potential invalid status.
      statusManager.setCopilotStatus(CopilotStatusResult.OK);
    }
  }
}
