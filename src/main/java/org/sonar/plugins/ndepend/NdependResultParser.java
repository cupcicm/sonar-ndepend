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

import org.w3c.dom.Element;
import com.google.common.collect.ImmutableList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;

import java.util.List;

public class NdependResultParser {
  private final Document doc;
  private final XPath xpath;
  private final ImmutableList.Builder<NdependIssue> issueBuilder;

  NdependResultParser(Document doc) {
    this.doc = doc;
    this.xpath = XPathFactory.newInstance().newXPath();
    this.issueBuilder = ImmutableList.builder();
  }

  @VisibleForTesting
  NodeList getGroups() throws XPathExpressionException {
    return (NodeList) this.xpath.compile("/RuleResult/Group/Query").evaluate(this.doc, XPathConstants.NODESET);
  }

  @VisibleForTesting
  NodeList getGroupRows(Node groupNode) throws XPathExpressionException {
    return (NodeList) this.xpath.compile("//Rows/Row").evaluate(groupNode, XPathConstants.NODESET);
  }

  public List<NdependIssue> parse() throws XPathExpressionException {
    NodeList groups = this.getGroups();
    for (int i = 0; i < groups.getLength(); i++) {
      Node group = groups.item(i);
      NodeList rows = this.getGroupRows(groups.item(i));
      String ruleKey = ((Element) group).getAttribute("Name");
      String ruleDesc = ((Element) group).getAttribute("FullName");
      for (int j = 0; j < rows.getLength(); j++) {
        Element row = (Element) rows.item(j);
        NodeList vals = row.getElementsByTagName("Val");

        String filePath = ((Element) vals.item(0)).getTextContent();
        int fileLine = Integer.parseInt(((Element) vals.item(1)).getTextContent());
        issueBuilder.add(new NdependIssue(
          ruleKey,
          ruleDesc,
          filePath,
          fileLine));
      }
    }
    return issueBuilder.build();
  }

}
