package xdi2.core.impl.zephyr;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;


public class ZephyrContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 2515264347973764604L;
	private static final Logger log = LoggerFactory.getLogger(ZephyrContextNode.class);
	
	XDI3SubSegment arcXri;
	ContextNode objParentNode;
	
	
	ZephyrContextNode(Graph graph, ContextNode contextNode) {
		super(graph, contextNode);
	}

	@Override
	public XDI3SubSegment getArcXri() {
		return this.arcXri;
	}

	@Override
	public ContextNode createContextNode(XDI3SubSegment arcXri) {
		try {			
			
			//URI uri = URIUtils.createURI(PROTOCOL, URL, PORT, MailerSDKConstants.ADD_BRANDS_SERVICE_URL, null, null)
			String rootContextnode = "";

			// Check for context node in existing user graph.
//			String userGraph = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi() + "/" + ZephyrGraphFactory.rootNode  + "/*?token=" + ((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
//			JSONObject jsonGraph  = new JSONObject(userGraph);
//			Iterator<String> nodes = jsonGraph.keys();
//			while(nodes.hasNext()){
//				String key = nodes.next();
//				if(key.contains(arcXri.toString()) )
//				{
//					throw new Xdi2GraphException("Context Node already exists");
//				}
//			}
			
			if(this.getArcXri() != null)
			{
				if(!this.getArcXri().toString().equals("=root"))
				{	
				rootContextnode= this.getArcXri().toString();
				ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi() + "/" + ZephyrGraphFactory.rootNode + "/" + rootContextnode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(),arcXri.toString(), null);
				}
				else
				{
					ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi() + "/" + ZephyrGraphFactory.rootNode + "/" + arcXri.toString() + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(),arcXri.toString(), null);
				}
			}
			else
			{
				ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi() + "/" + ZephyrGraphFactory.rootNode + "/" + arcXri.toString() + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(),arcXri.toString(), null);
				this.objParentNode = this.getGraph().getRootContextNode();
				this.arcXri = XDI3SubSegment.create("=root");
			}
			
			ZephyrContextNode objZCN = new ZephyrContextNode(this.getGraph(), this);
			objZCN.arcXri = arcXri;
			objZCN.objParentNode = this;
			return objZCN;
					
		 } catch (Exception e) {
			 throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {
		try {
		String rootContextNode = this.arcXri.toString();
		String response = "";
		if(rootContextNode.equals("=root"))
		{
			response = "{}";
		}
		else{
			response = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + rootContextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
		}
		log.info(response);
		JSONObject jsonGraph  = new JSONObject(response);
		
		ZephyrContextNode contextNode = null;
		List<ContextNode> lstContextNode = new ArrayList<ContextNode>();
		
		Iterator<String> nodes = jsonGraph.keys();
		while(nodes.hasNext()){
			String key = nodes.next();
			contextNode = new ZephyrContextNode(this.getGraph(), this);
			if(!key.equals("") )
			{
			contextNode.arcXri = XDI3SubSegment.create(key);
			lstContextNode.add(contextNode);
			}
		}
		
		ReadOnlyIterator<ContextNode> itrContextNodes = new ReadOnlyIterator<ContextNode>(lstContextNode.iterator());
				
		return itrContextNodes;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
	}

	@Override
	public void deleteContextNode(XDI3SubSegment arcXri) {
		try {
			String contextNode = this.getArcXri().toString();
			// Put the context node value as null to zephyr store
			ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi() + "/" + ZephyrGraphFactory.rootNode + "/" + contextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), null);
			
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
	}

	@Override
	public void deleteContextNodes() {
		try {
			String contextNode = this.arcXri.toString();
			// Get user graph
			String response = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + contextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
						
			// Replace all relation values with null
			JSONObject jsonGraph  = new JSONObject(response);
			Iterator<String> nodes = jsonGraph.keys();
					
			while(nodes.hasNext()){
		        String key = (String)nodes.next();
		        if(!key.startsWith("+"))
		        {
		        	ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + contextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(),key, null);
		        }
		     }
			
			// Put the user graph back to zephyr store
			
						
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}

	}

	@Override
	public Relation createRelation(XDI3Segment arcXri,ContextNode targetContextNode) {
		try {
			
			String parentContextNode = this.getArcXri().toString();
			String newContextNode = targetContextNode.toString();
			
			// Check for relation in existing user graph.
			String graph = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + parentContextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
			JSONObject jsonGraph  = new JSONObject(graph);
			Iterator<String> nodes = jsonGraph.keys();
			 while(nodes.hasNext()){
				String key = nodes.next();
				if(!key.equals("") && jsonGraph.getString(key).contains(targetContextNode.toString())  )
					{
						throw new Xdi2GraphException("Relation already exists");
					}
				else
					if(key.equals(arcXri.toString()))
				 {
					newContextNode = jsonGraph.getString(key).concat("," + targetContextNode);
				 }
				}
			 
   
			 // Create relation if it is not already exists.
			ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + parentContextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), newContextNode);
			ZephyrRelation relation = new ZephyrRelation(this.getGraph(), this);
			relation.setArcXri(arcXri);
			relation.setTargetContextNodeXri(XDI3Segment.create(targetContextNode.toString()));
			return relation;
			
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public ReadOnlyIterator<Relation> getRelations() {
		try {
			String parentContextNode = this.getArcXri().toString();
			String response = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + parentContextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
			JSONObject jsonGraph  = new JSONObject(response);
			Iterator<String> nodes = jsonGraph.keys();
			ZephyrRelation relation = null;
			List<Relation> relations = new ArrayList<Relation>();
			
			while(nodes.hasNext()){
		        String key = (String)nodes.next();
		        if(key.startsWith("+"))
		        {
		        	relation = new ZephyrRelation(this.getGraph(), this);
		        	relation.setArcXri(XDI3Segment.create(key));
		        	String contextRelations[] = jsonGraph.getString(key).split(",");
		        	for(String arrRelation : contextRelations)
		        	{
		        		relation.setTargetContextNodeXri(XDI3Segment.create(arrRelation));
			        	relations.add(relation);	
		        	}
		        	
		        }
		     }
			 ReadOnlyIterator<Relation> itrReadOnlyRelations = new ReadOnlyIterator<Relation>(relations.iterator());
			return itrReadOnlyRelations;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@Override
	public void deleteRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {
		try {
		String path = this.arcXri.toString();
		// Get user graph
		String response = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + path + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
		// Replace the relation value with null
		JSONObject jsonGraph  = new JSONObject(response);
		Iterator<String> nodes = jsonGraph.keys();
				
		while(nodes.hasNext()){
	        String key = (String)nodes.next();
	        if(key.equals(arcXri.toString()))
	        {
	        	ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + path + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(),arcXri.toString(), jsonGraph.getString(key).replace(","+targetContextNodeXri.toString(), "").replace(targetContextNodeXri.toString(), ""));    	
	        }
	     }
		
		}catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}

	}

	@Override
	public void deleteRelations(XDI3Segment arcXri) {
		try {
			String contextNode = this.getArcXri().toString();
			ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + contextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(), arcXri.toString(), null);
			}catch (Exception e) {
				throw new Xdi2GraphException(e.getMessage());
			}

	}

	@Override
	public void deleteRelations() {
		try {
			String contextNode = this.getArcXri().toString();
			// Get user graph
			String response = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + contextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
						
			// Replace all relation values with null
			JSONObject jsonGraph  = new JSONObject(response);
			Iterator<String> nodes = jsonGraph.keys();
					
			while(nodes.hasNext()){
		        String key = (String)nodes.next();
		        if(key.startsWith("+"))
		        {
		        	ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()  + ZephyrGraphFactory.rootNode + "/" + contextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(), key, null);
		        	
		        }
		     }
			
			}catch (Exception e) {
				throw new Xdi2GraphException(e.getMessage());
			}

	}

	@Override
	public Literal createLiteral(String literalData) {
		try {
			String parentContextNode = this.objParentNode.getArcXri().toString();
			String contextNode = this.getArcXri().toString();
						
			String userGraph = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + parentContextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
			// Check for context node in existing user graph.
			JSONObject jsonGraph  = new JSONObject(userGraph);
			Iterator<String> nodes = jsonGraph.keys();
			 while(nodes.hasNext()){
				String key = nodes.next();
				if(jsonGraph.getString(key).contains(literalData))
					{
					throw new Xdi2GraphException("Literal already exists");
					}
			 }
			
			ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + parentContextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(), contextNode, literalData );
			
			ZephyrLiteral literal = new ZephyrLiteral(this.getGraph(), this);
			literal.setLiteralData(literalData);
			return literal;
			
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
		
	}

	@Override
	public Literal getLiteral() {
		try {
			ZephyrLiteral literal = null;
		String contextNode = this.arcXri.toString();
		String parentContextNode = this.getContextNode().getArcXri().toString();
		// Get user graph
		String response = ZephyrUtils.doGet(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + parentContextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken());
		
		// Find for the literal
		JSONObject jsonGraph  = new JSONObject(response);
		Iterator<String> nodes = jsonGraph.keys();
			while(nodes.hasNext()){
		       String key = (String)nodes.next();
		        if(key.equals(contextNode.toString()))
		        {
		        	literal = new ZephyrLiteral(this.getGraph(), this);
					literal.setLiteralData(jsonGraph.getString(key));
		        }
		     }
					
		return literal;
		} catch (Exception e) {
			throw new Xdi2GraphException(e.getMessage());
		}
	}

	@Override
	public void deleteLiteral() {
		try {
			String contextNode = this.arcXri.toString();
			String parentContextNode = this.getContextNode().getArcXri().toString();
			ZephyrUtils.doPut(((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getDataApi()+ "/"  + ZephyrGraphFactory.rootNode + "/" + parentContextNode + "?token=" +((ZephyrGraphFactory)this.getGraph().getGraphFactory()).getOauthToken(), contextNode, null );
			
			}catch (Exception e) {
				throw new Xdi2GraphException(e.getMessage());
			}

	}

	
}
