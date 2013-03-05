package xdi2.tests.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.impl.zephyr.ZephyrGraphFactory;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;


public class Test {
	private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws Exception{
    	
    	ZephyrGraphFactory graphFactory = new ZephyrGraphFactory();
     
        graphFactory.setDataApi("http://107.21.179.68:10002/");
        graphFactory.setOauthToken("SECRET");

        Graph graph = graphFactory.openGraph("(=!3333)");
         
        ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));
        ContextNode contextNode1 = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=shrey"));
        ContextNode contextNode2 = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=animesh"));
        
        ReadOnlyIterator<ContextNode> contextNodes = contextNode.getContextNodes();
        while(contextNodes.hasNext())
        {
        	System.out.println(contextNodes.next().getArcXri().toString());
        }
        
                    
       // Literals Operations
       contextNode.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("shrey.jssian@gmail.com");
       contextNode.createContextNode(XDI3SubSegment.create("$!(+name)")).createLiteral("Shrey");
       
//      Literal emailLiteral = contextNode.getContextNode(XDI3SubSegment.create("$!(+email)")).getLiteral();
//      System.out.println(emailLiteral.getLiteralData());
//      emailLiteral.setLiteralData("markus.sabadello@xdi.org");
       
        Literal literal = contextNode.getLiteral();
        System.out.println(literal.getLiteralData());

        //Relations Operations
       Relation relation = contextNode.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=shrey"));
       System.out.println(relation.toString());
       contextNode.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
       contextNode.createRelation(XDI3Segment.create("+founder"), XDI3Segment.create("@projectdanube"));
       contextNode.getRelations();
              

        XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(graph, System.out);

        graph.close();
    }
}

