package xdi2.tests.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.impl.zephyr.ZephyrGraphFactory;
import xdi2.core.impl.zephyr.ZephyrUtils;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;


public class Test {

    public static void main(String[] args) throws Exception{
    	
    	ZephyrUtils.doPut("http://107.21.179.68:10002/(=!1111)/=markus?token=SECRET", "$!(+email)/!", "markus.sabadello@gmail.com");

    	ZephyrGraphFactory graphFactory = new ZephyrGraphFactory();
     
        graphFactory.setDataApi("http://107.21.179.68:10002/");
        graphFactory.setOauthToken("?token=SECRET");

        Graph graph = graphFactory.getGraph("(=!2222)");
         
        ContextNode contextNode = graph.getRootContextNode().createContextNode(XDI3SubSegment.create("=markus"));

        contextNode.createContextNode(XDI3SubSegment.create("$!(+email)")).createLiteral("markus.sabadello@gmail.com");
        contextNode.createContextNode(XDI3SubSegment.create("$!(+name)")).createLiteral("Markus");

        contextNode.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=shrey"));
        contextNode.createRelation(XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
        contextNode.createRelation(XDI3Segment.create("+founder"), XDI3Segment.create("@projectdanube"));
        
        

        XDIWriterRegistry.forFormat("XDI DISPLAY", null).write(graph, System.out);

        graph.close();
    }
}

