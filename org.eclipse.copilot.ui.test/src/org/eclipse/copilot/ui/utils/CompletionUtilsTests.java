/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CompletionUtilsTests {
  @Test
  void testReplaceTabsWithSpaces() {
    String input = "\tfoo\n\t\tbar";
    String expected = "  foo\n    bar";
    assertEquals(expected, CompletionUtils.replaceTabsWithSpaces(input, 2));
  }

}
