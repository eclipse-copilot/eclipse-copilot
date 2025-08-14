# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 0.10.0
### Added
- Support custom instructions.
- Support MCP feature flag.
- Support GitHub MCP server OAuth.
- Support adding image to the chat context.
- Support adding folder to chat context.
- Add confirmation dialog for unhandled files when create a new conversation in agent mode.
- Add `Edit Preferences...` button into chat top banner.
- Show conversation title in chat top banner.

### Changed
- Improve the Copilot perspective with onboarding images and more shortcuts.
- Update chat view's icon.
- Merge all open url related commands into one command.

### Fixed
- Error 'Document for URI could not be found' during chat.
- Unexpected files are listed in the Search Attachments dialog.
- Correct the default index when build SignInDialog.
- Input history is not cleared after switching account.
- Preference will be cleared if username is not ready when start up.
- Delay the show hint invocation timing to avoid command not found error.
- Active model does not reset to default model when model list change.
- Welcome view does not render correctly when height is limited.
- Persist chat input when mode switches.
- Send MCP tools status notification after server started.

### Removed
- Remove CopilotAuthStatusListener from AvatarService.
- Remove CopilotAuthStatusListener from CopilotStatusManager.

## 0.9.3
### Fixed
- Update CLS to 1.348.0.

## 0.9.2
### Fixed
- Update CLS to 1.347.0.

## 0.9.1
### Fixed
- Reset history to avoid skipping the main section rendering.
- Updated bundle version to fit 2024-03.
- Fixed Linux rendering problem.
- Async open chat after closing welcome page.
- Use IPreferenceStore.getBoolean() to get the updated value.
- Perspective logo should support dark mode.

## 0.9.0
### Added
- Show MCP logs in Console View.
- Add welcome introduction page.
- Support workspace context (@workspace) in ask mode.
- Add open chat view command to perspectives' onboard command list.
- Add keyboard shortcut command for open chat view command.
- Add new Copilot perspective.
- Support generate git commit message.

### Changed
- Support Eclipse 2024-03 & 2024-06.
- Make agent mode as default chat mode.
- Improve the chat view layout.
- Improve the Copilot menu in menu bar and status bar.
- Remove the spinner when completing code.

### Fixed
- MCP tool configuration button should not be visible in ask mode.
- Use workbench job to avoid blocking shutdown action.
- Check if the project is accessible before scanning watched files.
- Fix quota rendering issue on MacOS and Linux.
- Wrong completion when IDE auto closed brackets.
- Entire settings are synced even just changing one item.
- Wrong welcome page displayed in chat view when user is not signed in.
- File with no extension cannot be attached in chat view.
- Error 'SWT Resource was not properly disposed' after sign in.

## 0.8.0
### Added
- Enable remote MCP server.
- Add up-sell link to the model picker for free plan accounts.

### Changed
- Make the chat view appear as a side bar by default.

### Fixed
- MCP tools are not visible.
- Validate duplicate keys in MCP preference page.
- Last line of the completion dialog in chat view is not visible.
- Support error status for tool invocation result.
- Fix rendering issue on Linux GTK.
- Cannot use arrow up key in the completion dialog in chat view.
- Decimal display incorrectly in usage quota.
- Invalid thread access when reuse compare editor.
- Reuse existing compare editor for create_file tool.
- Add timeout when fetching env during activation on MacOS.
- Check signin before get persisted path.

## 0.7.0
### Added
- New billing support and user interface update.
- Input history navigation.
- A button shortcut to open the MCP configuration page.

### Changed
- Update CLS to 1.327.0.
- Update Copilot status icon.

### Fixed
- Fix the memory leak issue that the document is not disconnected.
- Document for URI could not be found.
- No tools is displayed in MCP configuration page.
- NPE when resolve menu bar handler.
- Compare editor title cannot be rendered correctly.

## 0.6.1
### Fixed
- Correct the bundle version requirement to align with Eclipse 2024-09.

## 0.6.0
### Added
- Support agent mode with stdio mcp server integration in chat.

## 0.5.1
### Fixed
- Annotation model is null when triggering completion.
- Input text box shakes when sending message by hitting Enter-Key.
- SWTException when disposing completion manager.
- Timeout error shows late when fail to login.
- Improve auto scroll to bottom behavior.
- Fixed schema name copilotCapabilities.
- Wrong node runtime may be found.

## 0.5.0
### Added
- Added GitHub Copilot menu to the top menu bar.

### Changed
- Updated the LS to 1.290.0.

### Fixed
- Stop append INFO log when format preference changes.
- Should not attach bin files even it was opened in editor (behavior of VSCode).

## 0.4.0
### Added
- Support ABAP.

### Changed
- Mark org.eclipse.jdt.annotation to optional.

### Fixed
- NPE when IFile.getLocation() is null.
- Illegal state exception in Turn widget.
- SWT resources not disposed properly.
- Markdown viewer fallbacks to textviewer.
- Chat input cannot be rendered as multi line when input text in too long.
- Exception when deleting word leading with brackets in chat input box.

## 0.3.0
### Added
- Support chat feature
   - Support to create a new conversation
   - Support slash commands
   - Support to attach context files
   - Support cancel a conversation
   - Support model picker for chat

## 0.2.0
### Added
- Support C/C++ format options.

### Fixed
- Track uncaught exceptions.
- Invalid thread access when generating completion.
- NPE when authStatesManager is not ready.
- Noise error log when signin is cancelled.
- Hide the credential information in proxy log.
- Remove hard-coded plugin version in GithubPanicErrorReport.
- Move the update status icon logic to display thread.

## 0.1.0
### Added
- Support authentication from GitHub Copilot.
- Support free plan subscription.
- Support inline completion.
- Support accepting completion by word.
- Support fetching Java format options when triggering inline completion.
- Support proxy configuration.
- Support toggling auto inline completion.
- Support configuring key bindings from the status bar menu.
- Support opening feedback forum from the status bar menu.