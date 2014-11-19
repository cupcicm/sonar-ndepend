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

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import static org.fest.assertions.Assertions.assertThat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class NdependResultParserTest {

  private Document getTestDoc() throws SAXException, IOException, ParserConfigurationException {
    return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("src/test/resources/CodeRuleResult.xml");
  }

  @Test
  public void getGroupAndRows() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {

    final Document doc = getTestDoc();
    final NdependResultParser parser = new NdependResultParser(doc);

    assertThat(parser.getGroups().getLength()).isEqualTo(1);
    assertThat(parser.getGroupRows(parser.getGroups().item(0)).getLength()).isEqualTo(5);
  }

  @Test
  public void testParse() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
    final Document doc = getTestDoc();
    final NdependResultParser parser = new NdependResultParser(doc);
    final List<NdependIssue> issues = parser.parse();
    assertThat(issues.size()).isEqualTo(5);
    final NdependIssue sampleIssue = issues.get(0);
    assertThat(sampleIssue.getRuleKey()).isEqualTo("Method with too many parameters");
    assertThat(sampleIssue.getMessage()).isEqualTo("Code Quality \\ Method with too many parameters");
    assertThat(sampleIssue.getFile()).contains("WorkflowPlayer.cs");
    assertThat(sampleIssue.getLine()).isEqualTo(69);
  }
}
