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

import static org.fest.assertions.Assertions.assertThat;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.google.common.base.Joiner;

public class NdprojWriterTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void TestCanReplaceRulesElement() throws Exception {
    Document doc = prepareDocument(1);
    Writer writer = new StringWriter();
    new NdprojWriter(doc).writeTo(writer);
    String xml = Joiner.on(System.getProperty("line.separator"))
      .join(new String[] {"<root>", "<rules/>", "</root>", ""});
    assertThat(writer.toString()).endsWith(xml);
  }

  @Test
  public void TestCantInstantiateIfNoRulesElement() throws Exception {
    Document doc = prepareDocument(0);
    Writer writer = new StringWriter();
    thrown.expect(IllegalArgumentException.class);
    new NdprojWriter(doc).writeTo(writer);
  }

  @Test
  public void TestCantInstantiateIfTooMany() throws Exception {
    Document doc = prepareDocument(2);
    Writer writer = new StringWriter();
    thrown.expect(IllegalArgumentException.class);
    new NdprojWriter(doc).writeTo(writer);
  }

  private Document prepareDocument(int howManyRulesElement) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    Document doc = factory.newDocumentBuilder().newDocument();

    Node root = doc.createElement("root");
    for (int i = 0; i < howManyRulesElement; i++) {
      Node rules = doc.createElement("rules");
      root.appendChild(rules);
      rules.appendChild(doc.createElement("rule1"));
    }

    doc.appendChild(root);
    return doc;
  }
}
