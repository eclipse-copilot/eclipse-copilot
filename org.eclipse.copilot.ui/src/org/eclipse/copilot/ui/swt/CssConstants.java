/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui.swt;

/**
 * Constants for CSS styling in the Eclipse UI.
 */
public class CssConstants {

  private CssConstants() {
    // Prevent instantiation
  }

  /**
   * Key value for setting and getting the CSS class name of a widget.
   *
   * @see org.eclipse.swt.widgets.Widget#getData(String)
   * @see org.eclipse.swt.widgets.Widget#setData(String, Object)
   */
  public static final String CSS_CLASS_NAME_KEY = "org.eclipse.e4.ui.css.CssClassName";

  /**
   * Key value for setting and getting the CSS ID of a widget.
   *
   * @see org.eclipse.swt.widgets.Widget#getData(String)
   * @see org.eclipse.swt.widgets.Widget#setData(String, Object)
   */
  public static final String CSS_ID_KEY = "org.eclipse.e4.ui.css.id";

}
