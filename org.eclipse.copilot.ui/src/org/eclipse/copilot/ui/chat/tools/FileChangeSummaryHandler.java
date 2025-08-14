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

package org.eclipse.copilot.ui.chat.tools;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Interface for handling file change summary actions.
 */
public interface FileChangeSummaryHandler {
  /**
   * Handles the action of keeping changes to a file.
   *
   * @param file the file to keep changes for
   */
  void onKeepChange(IFile file) throws IOException, CoreException;

  /**
   * Handles the action of keeping all changes to files.
   */
  void onKeepAllChanges(List<IFile> files) throws IOException, CoreException;

  /**
   * Handles the action of undoing changes to a file.
   *
   * @param file the file to undo changes for
   * 
   * @throws CoreException if an error occurs during the undo operation, such as a failure to delete a file
   */
  void onUndoChange(IFile file) throws CoreException;

  /**
   * Handles the action of undoing all changes to files.
   *
   * @throws CoreException if error occurs during the undo all operation, such as a failure to delete a file
   */
  void onUndoAllChanges(List<IFile> files) throws CoreException;

  /**
   * Handles the action of removing a file.
   *
   * @param file the file to remove
   *
   * @throws CoreException if an error occurs during the remove operation, such as a failure to delete a file
   */
  void onRemoveFile(IFile file) throws CoreException;

  /**
   * Handles the action of viewing the diff of a file.
   *
   * @param file the file to view the diff for
   */
  void onViewDiff(IFile file);

  /**
   * Handles the action of click done button to resolve all changes.
   */
  void onResolveAllChanges();
}
