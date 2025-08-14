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

package org.eclipse.copilot.ui.chat.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.databinding.swt.DisplayRealm;
import org.eclipse.swt.widgets.Display;

import org.eclipse.copilot.core.AuthStatusManager;
import org.eclipse.copilot.core.CopilotCore;
import org.eclipse.copilot.core.chat.UserPreference;
import org.eclipse.copilot.core.lsp.CopilotLanguageServerConnection;
import org.eclipse.copilot.core.lsp.protocol.ChatPersistence;
import org.eclipse.copilot.core.utils.PlatformUtils;
import org.eclipse.copilot.ui.utils.SwtUtils;

/**
 * Base class for chat services.
 */
public abstract class ChatBaseService {
  protected static final Gson gson = new Gson();
  protected static final String PREF_FILE_NAME = "pref.json";

  protected CopilotLanguageServerConnection lsConnection;
  protected AuthStatusManager authStatusManager;

  protected String persistentPath;
  private static UserPreference userPreference;

  /**
   * Constructor for the ChatBaseService.
   */
  protected ChatBaseService(CopilotLanguageServerConnection lsConnection, AuthStatusManager authStatusManager) {
    this.lsConnection = lsConnection;
    this.authStatusManager = authStatusManager;
  }

  /**
   * Get User Preference.
   */
  protected synchronized UserPreference getUserPreference() {
    if (userPreference != null) {
      return userPreference;
    }

    Path path = getPersistentFilePath();
    if (path != null) {
      try {
        String jsonContent = PlatformUtils.readFileContent(path);
        if (!jsonContent.isEmpty()) {
          userPreference = gson.fromJson(jsonContent, UserPreference.class);
          if (userPreference != null) {
            return userPreference;
          }
        }
      } catch (JsonSyntaxException e) {
        CopilotCore.LOGGER.error("Failed to get user preference, will generate a new one.", e);
      }
    }

    userPreference = new UserPreference();
    return userPreference;
  }

  /**
   * Clear User Preference.
   */
  protected void clearUserPreferenceCache() {
    userPreference = null;
  }

  /**
   * Persist User Preference.
   */
  public synchronized void persistUserPreference() {
    Path path = getPersistentFilePath();
    if (path == null) {
      return;
    }

    String jsonContent = gson.toJson(userPreference);
    PlatformUtils.writeFileContent(path, jsonContent);
  }

  /**
   * Ensures operations run in the correct Realm.
   *
   * @param runnable The code to execute in the UI Realm.
   */
  protected void ensureRealm(Runnable runnable) {
    // If we're already in the UI thread
    if (Display.getCurrent() != null) {
      Realm realm = Realm.getDefault();
      if (realm == null) {
        realm = DisplayRealm.getRealm(Display.getCurrent());
      }
      Realm.runWithDefault(realm, runnable::run);
    } else {
      SwtUtils.invokeOnDisplayThread(() -> {
        Realm realm = DisplayRealm.getRealm(Display.getDefault());
        Realm.runWithDefault(realm, runnable::run);
      });
    }
  }

  /**
   * Get the path for the persistent file.
   */
  private @Nullable Path getPersistentFilePath() {
    if (!this.authStatusManager.isSignedIn()) {
      return null;
    }
    if (this.persistentPath == null) {
      try {
        ChatPersistence chatPersistence = this.lsConnection.persistence().get();
        this.persistentPath = chatPersistence.getPath();
      } catch (InterruptedException | ExecutionException e) {
        CopilotCore.LOGGER.error("Failed to get persistent path", e);
        return null;
      }
    }

    final String user = this.authStatusManager.getUserName();
    if (StringUtils.isBlank(user)) {
      CopilotCore.LOGGER.error(new IllegalStateException("User name is empty"));
      return null;
    }

    return Paths.get(this.persistentPath, user, PREF_FILE_NAME);
  }

  /**
   * Update the value of an observable in its realm.
   *
   * @param observable The observable to update.
   * @param value The new value to set.
   */
  protected <T> void updateObservable(IObservableValue<T> observable, final T value) {
    if (observable != null) {
      observable.getRealm().asyncExec(() -> observable.setValue(value));
    }
  }
}
