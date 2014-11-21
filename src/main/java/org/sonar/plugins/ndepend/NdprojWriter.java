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
import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Takes a .ndproj XML file, and replaces the &lt;rules&gt; element
 * with the rules provided.
 *
 * Note : It will delete all rules and replace them, not merely add
 * the new rules at the end.
 */
public class NdprojWriter {

  private final Document ndproj;

  public NdprojWriter(Document ndproj) {
    this.ndproj = ndproj;
    NodeList rulesElements = getRulesElement();
    if (rulesElements.getLength() == 0) {
      throw new IllegalArgumentException("Your document has no <rules/> element");
    }
    if (rulesElements.getLength() > 1) {
      throw new IllegalArgumentException("Your document has strictly more than 1 <rules/> element");
    }
  }

  /**
   * Creates a writer from a .ndproj file.
   */
  public static NdprojWriter fromFile(File file) throws SAXException, IOException, ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    return new NdprojWriter(factory.newDocumentBuilder().parse(file));
  }

  private NodeList getRulesElement() {
    return ndproj.getElementsByTagName("rules");
  }

  /**
   * Writes the .ndproj with the replaced rules to a writer object
   * (for example a file)
   */
  public void writeTo(Writer writer) {
    // The instantiation checks guarantees there is exactly one <rules> element.
    Node rules = getRulesElement().item(0);
    rules.getParentNode().replaceChild(getRules(), rules);
    write(writer);
  }

  private void write(Writer writer) {
    Transformer transformer;
    try {
      transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      DOMSource source = new DOMSource(ndproj);
      StreamResult result = new StreamResult(writer);
      transformer.transform(source, result);
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException(e);
    } catch (TransformerFactoryConfigurationError e) {
      throw new RuntimeException(e);
    } catch (TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  private Node getRules() {
    // TODO(m.cupcic): Implement this
    return ndproj.createElement("rules");
  }
}
