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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import org.eclipse.copilot.ui.utils.ResourceUtils;
import org.eclipse.copilot.ui.utils.UiUtils;


/**
 * Handler to show context menu items for adding files or folders to references in package explorer/project explorer.
 */
public class ShowContextMenuHandler extends CompoundContributionItem {

  @Override
  protected IContributionItem[] getContributionItems() {
    IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    var sel = win != null ? win.getSelectionService().getSelection() : null;


    if (!(sel instanceof IStructuredSelection s) || s.isEmpty()) {
      return new IContributionItem[0];
    }
    
    IStructuredSelection selection = (IStructuredSelection) sel;

    ResourceUtils.SelectionStats stats = ResourceUtils.analyzeSelection(selection);

    if (!stats.hasOnlyValidResources()) {
      return new IContributionItem[0];
    }

    List<IContributionItem> items = new ArrayList<>();

    items.add(new Separator("org.eclipse.copilot.ui.contextMenu.start"));

    ImageDescriptor menuIcon = UiUtils.buildImageDescriptorFromPngPath("/icons/github_copilot.png");

    MenuManager submenu = new MenuManager("Copilot", menuIcon, "org.eclipse.copilot.ui.contextMenu");
    
    // Add "Add to References" command item
    CommandContributionItemParameter p = new CommandContributionItemParameter(
        win, null, "com.microsoft.copilot.eclipse.commands.addToReferences", CommandContributionItem.STYLE_PUSH);
    submenu.add(new CommandContributionItem(p));
    items.add(submenu);

    items.add(new GroupMarker("org.eclipse.copilot.ui.contextMenu.end"));

    return items.toArray(IContributionItem[]::new);
  }
}
