package xdi2.tests.core.impl;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.impl.zephyr.ZephyrGraphFactory;
import xdi2.core.impl.zephyr.ZephyrUtils;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;


public class Test {

    public static void main(String[] args) throws Exception{
    	
    	//ZephyrUtils.doPut("http://107.21.179.68:10002/(=!1111)/=markus/$!(+email)/?token=SECRET", "", "");

    	ZephyrGraphFactory graphFactory = new ZephyrGraphFactory();
     
        graphFactory.setDataApi("http://107.21.179.68:10002/");
        graphFactory.setOauthToken("?token=SECRET");

        Graph graph = graphFactory.openGraph("(=!3333)");
         
        ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));
      //ContextNode contextNode1 = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=shrey"));
      //ContextNode contextNode2 = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=animesh"));
        
        ReadOnlyIterator<ContextNode> contextNodes = contextNode.getContextNodes();
        while(contextNodes.hasNext())
        {
        System.out.println(contextNodes.next());
        }
        
                    
       // Literals Operations
       contextNode.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("shrey.jssian@gmail.com");
       contextNode.createContextNode(XDI3SubSegment.create("$!(+name)")).createLiteral("Shrey");
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

