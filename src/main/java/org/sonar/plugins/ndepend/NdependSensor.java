/*
 * SonarQube NDepend Plugin
 * Copyright (C) 2014 Criteo
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sonar.plugins.ndepend;

import java.io.File;
import java.io.IOError;
import java.util.concurrent.TimeUnit;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandException;
import org.sonar.api.utils.command.CommandExecutor;

public class NdependSensor implements Sensor {

  private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(10);
  private final Settings settings;

  public NdependSensor(Settings settings) {
    this.settings = settings;
  }

  private File getNdProjFile(Project project) {
    return new File(project.getPath(),
      settings.getString(NdependConfig.PROJECT_PATH_PROPERTY_KEY));
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return getNdProjFile(project).exists();
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    String ndependPath = settings
      .getString(NdependConfig.NDEPEND_PATH_PROPERTY_KEY);
    Command cmd = Command.create(ndependPath).addArgument("/PersistHistoricAnalysisResult").addArgument(
      getNdProjFile(project).getAbsolutePath());
    try {
      CommandExecutor.create().execute(cmd, TIMEOUT);
    } catch (CommandException e) {
      throw new IOError(e);

    }
  }
}
