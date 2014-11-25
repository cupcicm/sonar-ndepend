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
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Settings;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandException;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.plugins.ndepend.ndproj.CsProjectParseError;
import org.sonar.plugins.ndepend.ndproj.NdprojCreator;

public class NdependSensor implements Sensor {

  private static final Logger LOG = LoggerFactory
      .getLogger(NdependSensor.class);
  private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(10);
  private final Settings settings;

  public NdependSensor(Settings settings) {
    this.settings = settings;
  }

  private File getNdProjFile(FileSystem filesystem) {
    return new File(filesystem.baseDir(), "sonar.ndproj");
  }

  @Override
  public void execute(SensorContext context) {
    LOG.debug("Executing NDepend sensor...");

    File ndprojFile = getNdProjFile(context.fileSystem());
    NdprojCreator creator = new NdprojCreator(settings);
    try {
      creator.create(ndprojFile);
    } catch (IOException e) {
      throw new IOError(e);
    } catch (CsProjectParseError e) {
      throw new RuntimeException(e);
    }

    String ndependPath = settings
        .getString(NdependConfig.NDEPEND_PATH_PROPERTY_KEY);
    Command cmd = Command.create(ndependPath)
        .addArgument("/PersistHistoricAnalysisResult")
        .addArgument(ndprojFile.getAbsolutePath());
    try {
      CommandExecutor.create().execute(cmd, TIMEOUT);
    } catch (CommandException e) {
      throw new IOError(e);
    }
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    LOG.debug("Describing NDepend sensor...");

    descriptor.createIssuesForRuleRepositories(NdependConfig.REPOSITORY_KEY)
        .workOnFileTypes(InputFile.Type.MAIN, InputFile.Type.TEST)
        .workOnLanguages(NdependConfig.LANGUAGE_KEY)
        .name("NDepend");
  }
}
