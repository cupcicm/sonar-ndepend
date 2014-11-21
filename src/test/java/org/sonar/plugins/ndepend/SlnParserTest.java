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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class SlnParserTest {

  private SlnParser getTestParser() {
    return new SlnParser();
  }

  @Test
  public void registerProjectWithRelativePath() throws IOException {
    SlnParser parser = getTestParser();
    assertThat(parser.getProjectFile(new File(".").getCanonicalFile(), new File("bar.csproj"))
      .getAbsolutePath()).isEqualTo(new File("bar.csproj").getCanonicalPath());
  }

  @Test
  public void registerProjectWithAbsolutePath() throws IOException {
    SlnParser parser = getTestParser();
    assertThat(parser.getProjectFile(new File(".").getCanonicalFile(), new File("bar/bazz.csproj").getAbsoluteFile())
      .getAbsolutePath()).isEqualTo(new File("bar/bazz.csproj").getCanonicalPath());
  }

  @Test
  public void parse() {
    SlnParser parser = getTestParser();
    List<File> projectFiles = new ArrayList<File>(parser.parse(new File("src/test/resources/sample.sln")));
    assertThat(projectFiles.size()).isEqualTo(2);
    List<String> paths = Lists.transform(projectFiles, new Function<File, String>() {
      @Override
      public String apply(File f) {
        return f.getName();
      }
    });
    assertThat(paths).contains("Project.csproj", "Project.UTest.csproj");

  }
}
