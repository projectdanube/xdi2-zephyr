package xdi2.tests.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.impl.zephyr.ZephyrGraphFactory;
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
        
        
        // Test Type 1
        // ContextNode operations
        ContextNode c1 = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=a"));
        ContextNode c2 = c1.createContextNode(XDI3SubSegment.create("=b"));
        ContextNode c3 = c2.createContextNode(XDI3SubSegment.create("=c"));
        ContextNode c4 = c3.createContextNode(XDI3SubSegment.create("=d"));
        
        c1.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("shrey.jssian@gmail.com");
        Literal emailLiteral = c1.getContextNode(XDI3SubSegment.create("$!(+email)")).getLiteral();
        
        c2.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("shrey.jssian@gmail.com");
        Literal emailLiteral2 = c2.getContextNode(XDI3SubSegment.create("$!(+email)")).getLiteral();
        
        c3.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("shrey.jssian@gmail.com");
        Literal emailLiteral3 = c3.getContextNode(XDI3SubSegment.create("$!(+email)")).getLiteral();
        
        c3.getContextNode(XDI3SubSegment.create("$!(+email)")).deleteLiteral();
        c3.deleteContextNode(XDI3SubSegment.create("$!(+name)"));
        c3.deleteContextNodes();
        
        //Relations Operations
        Relation relation = c3.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=shrey"));
        System.out.println(relation.toString());
        c3.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
        c3.createRelation(XDI3Segment.create("+founder"), XDI3Segment.create("@projectdanube"));
        
        ReadOnlyIterator<Relation> relations = c3.getRelations();
        while(relations.hasNext())
        {
        	System.out.println(relations.next()); 
        }
        
        c3.deleteRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
        c3.deleteRelations(XDI3Segment.create("+founder"));
        c3.deleteRelations();
        
        
        // Literal Operations
        c4.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("shrey.jssian@gmail.com");
        c4.createContextNode(XDI3SubSegment.create("$!(+name)")).createLiteral("Shrey");
        
        Literal emailLiteral4 = c4.getContextNode(XDI3SubSegment.create("$!(+email)")).getLiteral();
        System.out.println(emailLiteral4.getLiteralData());
        c4.getContextNode(XDI3SubSegment.create("$!(+email)")).deleteLiteral();
        
                 
        //Relations Operations
        Relation relation1 = c4.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=shrey"));
        System.out.println(relation1.toString());
        c4.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
        c4.createRelation(XDI3Segment.create("+founder"), XDI3Segment.create("@projectdanube"));
        
        ReadOnlyIterator<Relation> relations1 = c4.getRelations();
        while(relations.hasNext())
        {
        	System.out.println(relations1.next()); 
        }
        
        // Delete Relations
        c4.deleteRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
        c4.deleteRelations(XDI3Segment.create("+founder"));
        c4.deleteRelations();
        
        // ContextNode delete operations
        c4.deleteContextNode(XDI3SubSegment.create("$!(+name)"));
        c4.deleteContextNodes();
        
        // Test Type 2 
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
        //contextNode.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("shrey.jssian@gmail.com");
        contextNode.createContextNode(XDI3SubSegment.create("$!(+name)")).createLiteral("Shrey");
        
        Literal emailLiteral5 = contextNode.getContextNode(XDI3SubSegment.create("$!(+email)")).getLiteral();
        System.out.println(emailLiteral5.getLiteralData());
        contextNode.getContextNode(XDI3SubSegment.create("$!(+email)")).deleteLiteral();
        
        // ContextNode delete operations
        contextNode.deleteContextNode(XDI3SubSegment.create("$!(+name)"));
        contextNode.deleteContextNodes();
          
        //Relations Operations
        Relation relation2 = contextNode.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=shrey"));
        System.out.println(relation2.toString());
        contextNode.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
        contextNode.createRelation(XDI3Segment.create("+founder"), XDI3Segment.create("@projectdanube"));
        
        ReadOnlyIterator<Relation> relations2 = contextNode.getRelations();
        while(relations2.hasNext())
        {
        	System.out.println(relations2.next()); 
        }
        
        contextNode.deleteRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
        contextNode.deleteRelations(XDI3Segment.create("+founder"));
        contextNode.deleteRelations();
        
        
        // Test Type 3
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
     

        graph.close();
    }
}

