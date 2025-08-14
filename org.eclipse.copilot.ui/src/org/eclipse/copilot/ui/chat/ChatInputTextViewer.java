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

package org.eclipse.copilot.ui.chat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.ui.chat.services.ChatCompletionService;
import org.eclipse.copilot.ui.chat.services.ChatServiceManager;
import org.eclipse.copilot.ui.chat.services.UserPreferenceService;
import org.eclipse.copilot.ui.i18n.Messages;
import org.eclipse.copilot.ui.utils.SwtUtils;
import org.eclipse.copilot.ui.utils.UiUtils;

/**
 * A custom TextViewer for the chat input area with configurable hints messages.
 */
public class ChatInputTextViewer extends TextViewer implements PaintListener {
  private static final int MAX_INPUT_ROWS = 5;

  private Composite parent;
  private Consumer<String> sendMessageHandler;
  private ChatCompletionService chatCompletionService;
  private UserPreferenceService userPreferenceService;
  private ContentAssistant contentAssistant;

  private boolean caretLineOffsetChanged = false;
  private int lastCursorLineOffset = 0;

  /**
   * Whether the color resource should be disposed. When the color is fetched from the jface registry, it should not be
   * disposed.
   */
  private boolean needDisposeColorResource;
  private Color placeholderColor;

  /**
   * Constructs a new ChatInputTextViewer.
   *
   * @param parent the parent composite
   * @param chatServiceManager the chat service manager to access services
   */
  public ChatInputTextViewer(Composite parent, ChatServiceManager chatServiceManager) {
    super(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
    this.parent = parent;
    this.userPreferenceService = chatServiceManager.getUserPreferenceService();
    this.chatCompletionService = chatServiceManager.getChatCompletionService();
    this.init();
  }

  public void setSendMessageHandler(Consumer<String> handler) {
    this.sendMessageHandler = handler;
  }

  public String getContent() {
    return this.getDocument().get();
  }

  /**
   * Sets the content of the text viewer.
   *
   * @param content the content to set
   */
  public void setContent(String content) {
    this.getDocument().set(content);
  }

  @Override
  public void paintControl(PaintEvent e) {
    String content = this.getContent();
    if (StringUtils.isNotEmpty(content)) {
      return;
    }
    e.gc.setForeground(placeholderColor);
    StyledText styledText = this.getTextWidget();
    e.gc.drawString(getPlaceholderText(), styledText.getLeftMargin(), styledText.getTopMargin(), true);
  }

  private void init() {
    this.setEditable(true);
    this.addTextListener(this::onTextChanged);

    StyledText tvw = this.getTextWidget();
    tvw.setBackground(tvw.getParent().getBackground());
    tvw.setLayout(new GridLayout(1, false));
    tvw.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    tvw.setAlwaysShowScrollBars(false);

    tvw.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
        SwtUtils.invokeOnDisplayThread(tvw::redraw, tvw);
      }
    });

    tvw.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        onKeyPressed(e);
      }
    });

    initializePlaceHolderColor();
    tvw.addPaintListener(this);
    SwtUtils.invokeOnDisplayThread(tvw::redraw, tvw);

    // new document after styled text redraw to avoid a redundant line being added to the document.
    this.setDocument(new Document());
  }

  private void initializePlaceHolderColor() {
    Color color = SwtUtils.getRegisteredInlineAnnotationColor(this.getTextWidget().getDisplay());
    if (color == null) {
      needDisposeColorResource = true;
      placeholderColor = SwtUtils.getDefaultGhostTextColor(this.getTextWidget().getDisplay());
    } else {
      needDisposeColorResource = false;
      placeholderColor = color;
    }
  }

  private void clearFormat(int start, int end) {
    this.getTextWidget().setStyleRange(new StyleRange(start, end - start, null, null, SWT.NORMAL));
  }

  private void onTextChanged(TextEvent event) {
    // Skip refreshing Enter-'\n' in TextEvent to avoid layout-shaking issue.
    if (isInsertLineBreakOnly(event)) {
      return;
    }

    refreshHeightLayout();
  }

  private boolean isInsertLineBreakOnly(TextEvent event) {
    String text = event.getText();
    return text != null && (text.equals("\n") || text.equals("\r\n"));
  }

  private void refreshHeightLayout() {
    StyledText tvw = ChatInputTextViewer.this.getTextWidget();
    Point size = tvw.computeSize(tvw.getSize().x, SWT.DEFAULT);
    GridData gd = (GridData) tvw.getLayoutData();
    gd.heightHint = Math.min(tvw.getLineHeight() * MAX_INPUT_ROWS, size.y);
    // TODO: An very interesting bug here, if we call layout(true, true), even no changes,
    // The width of welcome view will become shorter and shorter, may investigate it later
    ChatInputTextViewer.this.parent.getParent().layout(true, false);
  }

  private void onKeyPressed(KeyEvent e) {
    String text = this.getContent();
    // check the caret status so that we know if this is moving caret through multiple lines, or it's a switching
    this.updateCaretLineOffsetStatus();
    if (handleArrowKeyEvent(e, text)) {
      // caret status need update since arrow key event may change the position via switching input history
      this.updateCaretLineOffsetStatus();
      return;
    }
    // If current char is not line break, it means assistant pop up is visible and assistant listener handle it
    // In this case, users just want to select a command, so we should not handle it here
    if ((e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) && isLineBreakInCaret()) {
      // If Shift+Enter is pressed, or the content is empty, insert new line
      if (isShiftHolded(e) || StringUtils.isBlank(text)) {
        // KeyEvent is later than TextEvent.
        // Skipped refreshing Enter in TextEvent in onTextChanged() to avoid layout-shaking issue.
        // Refresh the layout for real new line here in KeyEvent.
        refreshHeightLayout();
        e.doit = true; // Allow the new line
      } else {
        // Users press Enter to send message, so we should remove the line break
        removeLineBreak();
        userPreferenceService.addInputToHistory(this.getContent());
        handleSendMessage();
      }
      return;
    }
    Point firstWordIndex = UiUtils.getFirstWordIndex(text);
    int begin = firstWordIndex.x;
    int end = firstWordIndex.y;
    if (!isCaretInRange(begin, end)) {
      // user is not modifying the first word, no need to highlight the command
      return;
    }
    clearFormat(0, text.length());
    String firstWord = text.substring(begin, end);
    if (e.keyCode == SWT.BS
        && chatCompletionService.isBrokenCommand(firstWord, this.getTextWidget().getCaretOffset() - begin)) {
      try {
        getDocument().replace(begin, end - begin, StringUtils.EMPTY);
      } catch (BadLocationException ex) {
        CopilotCore.LOGGER.error(ex);
      }
      return;
    }
    // we may need to highlight the command if user removed leading character before a command
    // user is typing
    if (chatCompletionService.isCommand(firstWord)) {
      this.getTextWidget().setStyleRange(new StyleRange(begin, end - begin, UiUtils.SLASH_COMMAND_FORGROUND_COLOR,
          UiUtils.SLASH_COMMAND_BACKGROUND_COLOR, SWT.BOLD));
      return;
    }
  }

  /**
   * Return true if the event is handled, false otherwise.
   */
  private boolean handleArrowKeyEvent(KeyEvent e, String text) {
    if (this.caretLineOffsetChanged || this.isProposalPopupActive()) {
      return false;
    }
    if (e.keyCode == SWT.ARROW_UP) {
      String lastInput = userPreferenceService.getPreviousInput(text);
      if (StringUtils.isNotBlank(lastInput)) {
        this.setContent(lastInput);
        this.getTextWidget().setSelection(lastInput.length());
      }
      return true;
    } else if (e.keyCode == SWT.ARROW_DOWN) {
      String nextInput = userPreferenceService.getNextInput();
      if (StringUtils.isNotBlank(nextInput)) {
        this.setContent(nextInput);
        this.getTextWidget().setSelection(nextInput.length());
        return true;
      }
    } else {
      // if it's not about navigating input history, reset the cursor, so that next time
      // when user press up arrow, it will get the latest input from history.
      userPreferenceService.resetInputHistoryCursor();
    }
    return false;
  }

  private boolean isLineBreakInCaret() {
    // for both /r/n of windows and /n of Linux, the current character is LF, so just need to check it
    String currentChar = getCurrentChar();
    return StringUtils.equals(currentChar, StringUtils.LF);
  }

  private boolean isCaretInRange(int begin, int end) {
    int caretOffset = this.getTextWidget().getCaretOffset();
    return caretOffset >= begin && caretOffset <= end;
  }

  private String getCurrentChar() {
    StyledText tvw = this.getTextWidget();
    int caretOffset = tvw.getCaretOffset() - 1;
    return caretOffset < tvw.getCharCount() && caretOffset >= 0 ? tvw.getTextRange(caretOffset, 1) : null;
  }

  private boolean isShiftHolded(KeyEvent e) {
    return (e.stateMask & SWT.SHIFT) != 0;
  }

  private void removeLineBreak() {
    StyledText tvw = this.getTextWidget();
    int caretOffset = tvw.getCaretOffset() - 1;
    String text = this.getContent();
    // Check for \r\n (Windows style)
    if (caretOffset > 0 && caretOffset < text.length() && text.charAt(caretOffset) == '\n'
        && text.charAt(caretOffset - 1) == '\r') {
      // Remove both \r and \n
      tvw.replaceTextRange(caretOffset - 1, 2, StringUtils.EMPTY);
    } else if (caretOffset >= 0 && caretOffset < text.length() && text.charAt(caretOffset) == '\n') {
      // Remove single \n (Unix/Linux style)
      tvw.replaceTextRange(caretOffset, 1, StringUtils.EMPTY);
    }
  }

  private void handleSendMessage() {
    resetCaretLineOffsetStatus();
    Optional.ofNullable(this.sendMessageHandler).ifPresent(handler -> handler.accept(this.getContent()));
  }

  private String getPlaceholderText() {
    switch (userPreferenceService.getActiveChatMode()) {
      case Agent:
        return Messages.chat_actionBar_initialContentForAgent;
      case Ask:
      default:
        return Messages.chat_actionBar_initialContent;
    }
  }

  private void updateCaretLineOffsetStatus() {
    StyledText textWidget = this.getTextWidget();
    int caretOffset = textWidget.getCaretOffset();
    int offset = textWidget.getLineAtOffset(caretOffset);
    if (lastCursorLineOffset != offset) {
      lastCursorLineOffset = offset;
      caretLineOffsetChanged = true;
    } else {
      caretLineOffsetChanged = false;
    }
  }

  private void resetCaretLineOffsetStatus() {
    lastCursorLineOffset = 0;
    caretLineOffsetChanged = false;
  }

  /**
   * Disposes the resources used by this viewer, including the placeholder color if it was created.
   */
  public void dispose() {
    if (needDisposeColorResource && placeholderColor != null && !placeholderColor.isDisposed()) {
      placeholderColor.dispose();
    }
  }

  public void setContentAssistProcessor(ContentAssistant ca) {
    contentAssistant = ca;
  }

  /**
   * Checks if the proposal popup is currently active. We use reflection instead of extends ContentAssistant to expose
   * the method. Because using the latter
   */
  private boolean isProposalPopupActive() {
    try {
      // Use reflection to call the isProposalPopupActive method from ContentAssistant
      Method method = ContentAssistant.class.getDeclaredMethod("isProposalPopupActive");
      method.setAccessible(true);
      return (boolean) method.invoke(contentAssistant);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      CopilotCore.LOGGER.error("Failed to call isProposalPopupActive via reflection", e);
      return false; // Default to false if reflection fails
    }
  }
}