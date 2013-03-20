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
     
        graphFactory.setDataApi("http://192.168.56.101:10002");
        graphFactory.setOauthToken("SECRET");

        Graph graph = graphFactory.openGraph("(=!1111)");
        
        // ContextNode operations
               
              
        ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));
        ContextNode contextNode1 = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=shrey"));
        ContextNode contextNode2 = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=animesh"));
        
        ReadOnlyIterator<ContextNode> contextNodes = contextNode.getContextNodes();
        while(contextNodes.hasNext())
        {
        	System.out.println(contextNodes.next().getArcXri().toString());
        }
        
        // Literal Operations
        contextNode.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("shrey.jssian@gmail.com");
        contextNode1.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("markus.sabadello@gmail.com");
       // contextNode.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("shrey.jssian@gmail.com");
        contextNode.createContextNode(XDI3SubSegment.create("$!(+name)")).createLiteral("Shrey");
        
        Literal emailLiteral = contextNode.getContextNode(XDI3SubSegment.create("$!(+email)")).getLiteral();
        System.out.println(emailLiteral.getLiteralData());
        contextNode.getContextNode(XDI3SubSegment.create("$!(+email)")).deleteLiteral();
        
        // ContextNode delete operations
        contextNode.deleteContextNode(XDI3SubSegment.create("$!(+name)"));
        contextNode.deleteContextNodes();
          
        //Relations Operations
        Relation relation = contextNode.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=shrey"));
        System.out.println(relation.toString());
        contextNode.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
        contextNode.createRelation(XDI3Segment.create("+founder"), XDI3Segment.create("@projectdanube"));
        
        ReadOnlyIterator<Relation> relations = contextNode.getRelations();
        while(relations.hasNext())
        {
        	System.out.println(relations.next()); 
        }
        
        contextNode.deleteRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
        contextNode.deleteRelations(XDI3Segment.create("+founder"));
        contextNode.deleteRelations();
        
//        ContextNode markus = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));
//        ContextNode shrey = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=shrey"));
//        ContextNode animesh = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=animesh"));
//
//        markus.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("markus.sabadello@gmail.com");
//        markus.createContextNode(XDI3SubSegment.create("$!(+name)")).createLiteral("Markus");
//
//        shrey.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("shrey.jssian@gmail.com");
//        shrey.createContextNode(XDI3SubSegment.create("$!(+name)")).createLiteral("Shrey");
//
//        animesh.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("Animesh.Chowdhury@neustar.biz");
//        animesh.createContextNode(XDI3SubSegment.create("$!(+name)")).createLiteral("Animesh");

        XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(graph, System.out);

        graph.close();
    }
}

