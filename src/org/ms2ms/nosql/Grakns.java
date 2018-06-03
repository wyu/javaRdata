package org.ms2ms.nosql;

import ai.grakn.Grakn;
import ai.grakn.client.BatchMutatorClient;
import ai.grakn.graql.InsertQuery;
//import it.cnr.icar.biograkn.uniprot.*;

import static ai.grakn.graql.Graql.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileReader;
import java.io.IOException;

public class Grakns
{
//
//  public static void disableInternalLogs()
//  {
//    org.apache.log4j.Logger logger4j = org.apache.log4j.Logger.getRootLogger();
//    logger4j.setLevel(org.apache.log4j.Level.toLevel("INFO"));
//  }
//  private static String timeConversion(long seconds) {
//
//    final int MINUTES_IN_AN_HOUR = 60;
//    final int SECONDS_IN_A_MINUTE = 60;
//
//    long minutes = seconds / SECONDS_IN_A_MINUTE;
//    seconds -= minutes * SECONDS_IN_A_MINUTE;
//
//    long hours = minutes / MINUTES_IN_AN_HOUR;
//    minutes -= hours * MINUTES_IN_AN_HOUR;
//
//    return hours + " hours " + minutes + " minutes " + seconds + " seconds";
//  }
//
//  public static void loadUniprot(String fileName) throws IOException, XMLStreamException, JAXBException
//  {
//    disableInternalLogs();
//
//    int entryCounter = 0, resCounter = 0, relCounter = 0;
//    long startTime = System.currentTimeMillis();
//
//    BatchMutatorClient loader = new BatchMutatorClient("biograkn", Grakn.DEFAULT_URI);
//
//    System.out.print("\nReading proteins entries from " + fileName + " ");
//
//    XMLInputFactory xif = XMLInputFactory.newInstance();
//    XMLStreamReader xsr = xif.createXMLStreamReader(new FileReader(fileName));
//    xsr.nextTag(); // Advance to statements element
//
//    JAXBContext jc = JAXBContext.newInstance(Entry.class);
//    Unmarshaller unmarshaller = jc.createUnmarshaller();
//
//    while (xsr.nextTag() == XMLStreamConstants.START_ELEMENT)
//    {
//      Entry entry = (Entry) unmarshaller.unmarshal(xsr);
//
//      OrganismType organism = entry.getOrganism();
//      String organismTaxonomyId = ((organism != null) && (!organism.getDbReference().isEmpty())) ? organism.getDbReference().get(0).getId() : "";
//
//      if (organismTaxonomyId.equals("9606")) {
//
//        if (entry.getAccession().isEmpty())
//          continue;
//
//        ProteinType prot = entry.getProtein();
//
//        //String accession = entry.getAccession().get(0);
//        String name = entry.getName().get(0);
//        String fullName = ((prot.getRecommendedName() != null) && (prot.getRecommendedName().getFullName() != null)) ? prot.getRecommendedName().getFullName().getValue() : "";
//        String alternativeName = ((!prot.getAlternativeName().isEmpty()) && (prot.getAlternativeName().get(0).getFullName() != null)) ? prot.getAlternativeName().get(0).getFullName().getValue() : "";
//
//        String gene = "";
//        if (!entry.getGene().isEmpty()) {
//          GeneType geneType = entry.getGene().get(0);
//          if (!geneType.getName().isEmpty()) {
//            gene = geneType.getName().get(0).getValue();
//          }
//        }
//
//        SequenceType seq = entry.getSequence();
//        String sequence = seq.getValue();
//        int sequenceLength = seq.getLength();
//        int sequenceMass = seq.getMass();
//
//        String function = "";
//        String pathway = "";
//        String subunit = "";
//        String tissue = "";
//        String ptm = "";
//        String similarity = "";
//
//        for (CommentType comment : entry.getComment()) {
//          if (comment.getText().isEmpty())
//            continue;
//
//          String s = comment.getText().get(0).getValue();
//          if (comment.getType().equals("function")) {
//            function = s;
//          } else if (comment.getType().equals("pathway")) {
//            pathway = s;
//          } else if (comment.getType().equals("subunit")) {
//            subunit = s;
//          } else if (comment.getType().equals("tissue specificity")) {
//            tissue = s;
//          } else if (comment.getType().equals("PTM")) {
//            ptm = s;
//          } else if (comment.getType().equals("similarity")) {
//            similarity = s;
//          }
//        }
//
//        InsertQuery protein = insert(
//            var("p")
//                .isa("protein")
//                .has("name", name)
//                .has("fullName", fullName)
//                .has("alternativeName", alternativeName)
//                .has("proteinGene", gene)
//                .has("function", function)
//                .has("proteinPathway", pathway)
//                .has("subunit", subunit)
//                .has("tissue", tissue)
//                .has("ptm", ptm)
//                .has("similarity", similarity)
//                .has("sequence", sequence)
//                .has("sequenceLength", sequenceLength)
//                .has("sequenceMass", sequenceMass)
//        );
//
//        entryCounter++;
//        resCounter += 13;
//
//        loader.add(protein);
//
//        if (entryCounter % 2000 == 0) {
//          System.out.print("."); System.out.flush();
//        }
//      }
//    }
//
//    loader.flush();
//    loader.waitToFinish();
//
//    long stopTime = (System.currentTimeMillis()-startTime)/1000;
//    System.out.println("\n\nCreated " + entryCounter + " entities, " + resCounter + " resources and " + relCounter + " relations in " + timeConversion(stopTime));
//  }
}
