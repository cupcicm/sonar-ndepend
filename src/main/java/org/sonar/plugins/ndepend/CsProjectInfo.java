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

import java.util.HashSet;

public class CsProjectInfo {
  private String assemblyName;
  private HashSet<String> outputPaths;
  private HashSet<String> references;

  CsProjectInfo(String assemblyName, HashSet<String> outputPaths,
    HashSet<String> references) {
    this.outputPaths = outputPaths;
    this.references = references;
    this.assemblyName = assemblyName;
  }

  String getAssemblyName() {
    return assemblyName;
  }

  /**
   * @return cs project possible output paths
   */
  HashSet<String> getOutputPaths() {
    return outputPaths;
  }

  HashSet<String> getReferences() {
    return references;
  }

}
