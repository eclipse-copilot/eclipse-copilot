/*******************************************************************************
 * Copyright (c) 2025 GitHub, Inc. and others
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/

package org.eclipse.copilot.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;

import org.eclipse.copilot.core.CopilotCore;

class CopilotUiTests {

  @Test
  void testCopilotCoreWakeUp() throws Exception {
    CopilotUi ui = new CopilotUi();
    ui.start(null);

    Job.getJobManager().join(CopilotCore.INIT_JOB_FAMILY, null);

    Bundle bundle = Platform.getBundle("org.eclipse.copilot.core");

    assertNotNull(bundle);
    assertEquals(Bundle.ACTIVE, bundle.getState());
  }
}
