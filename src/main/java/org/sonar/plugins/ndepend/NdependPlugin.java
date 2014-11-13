/*
 * SonarQube NDepend Plugin
 * Copyright (C) 2014 Criteo
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
// Copyright 2014 Criteo
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.sonar.plugins.ndepend;

import java.util.List;

import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import com.google.common.collect.ImmutableList;

public class NdependPlugin extends SonarPlugin {

  public static final String PROJECT_PATH_PROPERTY_KEY = "sonar.ndepend.projectPath";
  public static final String NDEPEND_PATH_PROPERTY_KEY = "sonar.ndepend.ndependPath";

  private static final String CATEGORY = "Ndepend";

  /**
   * {@inheritDoc}
   */
  @Override
  public List getExtensions() {
    ImmutableList.Builder builder = ImmutableList.builder();
    builder.addAll(NdependProvider.extensions());
    builder.addAll(pluginProperties());
    return builder.build();
  }

  private static ImmutableList<PropertyDefinition> pluginProperties() {
    return ImmutableList.of(
      PropertyDefinition.builder(PROJECT_PATH_PROPERTY_KEY)
        .name("Path to the .ndproj file")
        .description("Example : myproject.ndproj")
        .category(CATEGORY)
        .onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
        .build(),

        PropertyDefinition.builder(NDEPEND_PATH_PROPERTY_KEY)
          .name("Path to NDepend.Console.exe")
          .description("Example: C:/ndepend/NDepend.Console.exe")
          .defaultValue("C:/tools/ndepend/NDepend.Console.exe")
          .category(CATEGORY)
          .onQualifiers(Qualifiers.PROJECT)
          .build()
    );
  }
}
