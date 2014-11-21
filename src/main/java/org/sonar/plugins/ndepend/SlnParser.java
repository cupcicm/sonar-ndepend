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

import com.google.common.annotations.VisibleForTesting;
import org.sonar.plugins.visualstudio.VisualStudioSolution;
import org.sonar.plugins.visualstudio.VisualStudioSolutionParser;
import org.sonar.plugins.visualstudio.VisualStudioSolutionProject;

import java.io.File;
import java.util.HashSet;

public class SlnParser {

  /**
   * @param slnFile path to a solution file
   * @return a set containing sub-project absolute paths.
   */
  public HashSet<File> parse(File slnFile) {
    HashSet<File> subProjects = new HashSet<File>();

    VisualStudioSolutionParser slnParser = new VisualStudioSolutionParser();
    VisualStudioSolution sln = slnParser.parse(slnFile);

    for (VisualStudioSolutionProject project : sln.projects()) {
      subProjects.add(getProjectFile(slnFile.getAbsoluteFile().getParentFile(), new File(project.path())));
    }
    return subProjects;
  }

  @VisibleForTesting
  File getProjectFile(File slnPath, File projectPath) {
    return projectPath.isAbsolute() ? projectPath : new File(slnPath, projectPath.getPath());
  }

}
