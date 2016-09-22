package org.ms2ms.graph;

import org.jgrapht.DirectedGraph;
import org.jgrapht.WeightedGraph;
import org.ms2ms.utils.Tools;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Created by yuw on 9/18/16.
 */
public class Exporters
{
  /**
   * Exports a graph into a plain text file in GraphML format.
   *
   * @param writer the writer to which the graph to be exported
   * @param g the graph to be exported
   */
  public static void exportDenovoGraph(Writer writer, WeightedGraph<Double, DenovoEdge> g) throws SAXException, TransformerConfigurationException
  {
    // Prepare an XML file to receive the GraphML data
    PrintWriter out = new PrintWriter(writer);
    StreamResult streamResult = new StreamResult(out);
    SAXTransformerFactory factory =
        (SAXTransformerFactory) SAXTransformerFactory.newInstance();
    TransformerHandler handler = factory.newTransformerHandler();
    Transformer serializer = handler.getTransformer();
    serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    handler.setResult(streamResult);
    handler.startDocument();
    AttributesImpl attr = new AttributesImpl();

    // <graphml>
    handler.startPrefixMapping(
        "xsi",
        "http://www.w3.org/2001/XMLSchema-instance");

    // FIXME: Is this the proper way to add this attribute?
    attr.addAttribute(
        "",
        "",
        "xsi:schemaLocation",
        "CDATA",
        "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
    handler.startElement(
        "http://graphml.graphdrawing.org/xmlns",
        "",
        "graphml",
        attr);
    handler.endPrefixMapping("xsi");

    // <key> for vertex label attribute
    attr.clear();
    attr.addAttribute("", "", "id", "CDATA", "vertex_label");
    attr.addAttribute("", "", "for", "CDATA", "node");
    attr.addAttribute("", "", "attr.name", "CDATA", "Vertex Label");
    attr.addAttribute("", "", "attr.type", "CDATA", "string");
    handler.startElement("", "", "key", attr);
    handler.endElement("", "", "key");

    // <key> for edge label attribute
    attr.clear();
    attr.addAttribute("", "", "id", "CDATA", "label");
    attr.addAttribute("", "", "for", "CDATA", "edge");
    attr.addAttribute("", "", "attr.name", "CDATA", "Label");
    attr.addAttribute("", "", "attr.type", "CDATA", "string");
    handler.startElement("", "", "key", attr);
    handler.endElement("", "", "key");

    attr.clear();
    attr.addAttribute("", "", "id", "CDATA", "weight");
    attr.addAttribute("", "", "for", "CDATA", "edge");
    attr.addAttribute("", "", "attr.name", "CDATA", "Weight");
    attr.addAttribute("", "", "attr.type", "CDATA", "double");
    handler.startElement("", "", "key", attr);
    handler.endElement("", "", "key");

//    <key id="d1" for="edge" attr.name="weight" attr.type="double"/>

    // <graph>
    attr.clear();
    attr.addAttribute("", "", "edgedefault", "CDATA",
        (g instanceof DirectedGraph<?, ?>) ? "directed" : "undirected");
    handler.startElement("", "", "graph", attr);

    // Add all the vertices as <node> elements...
    for (Double v : g.vertexSet())
    {
      // <node>
      attr.clear();
      attr.addAttribute("", "", "id", "CDATA", Tools.d2s(v, 4));
      handler.startElement("", "", "node", attr);

      // <data>
      attr.clear();
      attr.addAttribute("", "", "key", "CDATA", "vertex_label");
      handler.startElement("", "", "data", attr);

      // Content for <data>
      String vertexLabel = Tools.d2s(v, 4);
      handler.characters(vertexLabel.toCharArray(), 0, vertexLabel.length());

      handler.endElement("", "", "data");
      handler.endElement("", "", "node");
    }

    // Add all the edges as <edge> elements...
    for (DenovoEdge e : g.edgeSet())
    {
      // <edge>
      attr.clear();
      attr.addAttribute("", "", "id", "CDATA", e.toString());
      attr.addAttribute("", "", "source", "CDATA", Tools.d2s(g.getEdgeSource(e), 4));
      attr.addAttribute("", "", "target", "CDATA", Tools.d2s(g.getEdgeTarget(e), 4));
//      attr.addAttribute("", "", "label", "CDATA", e.getLabel());
//      attr.addAttribute("", "", "weight", "CDATA", Tools.d2s(g.getEdgeWeight(e), 3));
      handler.startElement("", "", "edge", attr);

      // <data>
      attr.clear();
//      attr.addAttribute("", "", "key", "CDATA", "edge_label");
      attr.addAttribute("", "", "key", "CDATA", "label");
      handler.startElement("", "", "data", attr);
      // Content for <data>
      String edgeLabel = e.getLabel();
      handler.characters(edgeLabel.toCharArray(), 0, edgeLabel.length());
      handler.endElement("", "", "data");

      attr.clear();
      attr.addAttribute("", "", "key", "CDATA", "weight");
      handler.startElement("", "", "data", attr);

      // Content for <data>
      String weight = Tools.d2s(g.getEdgeWeight(e), 3);
      handler.characters(weight.toCharArray(), 0, weight.length());
      handler.endElement("", "", "data");

      handler.endElement("", "", "edge");
    }

    handler.endElement("", "", "graph");
    handler.endElement("", "", "graphml");
    handler.endDocument();

    out.flush();
  }
}
