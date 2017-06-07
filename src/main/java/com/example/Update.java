package com.example;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
//import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

import com.sforce.soap.enterprise.Connector;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.ws.ConnectorConfig;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Root resource (exposed at "update" path)
 */
@Path("/setMercuryNetworkStatus")
public class Update {

	// /**
	// * Method handling HTTP GET requests. The returned object will be sent
	// * to the client as "text/plain" media type.
	// *
	// * @return String that will be returned as a text/plain response.
	// */
	// @GET
	// @Produces(MediaType.TEXT_PLAIN)
	// public String getIt() {
	// return "Hello, Heroku!";
	// }
	
	static final String USERNAME = "charmer@soliantconsulting.com.vip.qa";
	static final String PASSWORD = "cjhvip2017LcCwLb5ovFvHIPmbLbpAEOHv";
	static final String AUTHENDPOINT = "https://test.salesforce.com/services/Soap/c/40.0/";

	static EnterpriseConnection connection;


	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String parseXML(@QueryParam("xmlRecords") String xmlRecords) {
		
		
		System.out.println("xmlRecords" + xmlRecords);
		String decodedValue = "";

		Map<String, String> xmlDataMap = new HashMap<String, String>();
		ConnectorConfig config = new ConnectorConfig();
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setAuthEndpoint(AUTHENDPOINT);

		try {

			connection = Connector.newConnection(config);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlRecords));
			Document doc = db.parse(is);
			
			NodeList loanNodes = doc.getElementsByTagName("LOANNUMBER");
			Element loanElm = (Element) loanNodes.item(0);
			xmlDataMap.put("LOANNUMBER", getCharacterDataFromElement(loanElm));
			
			NodeList stNodes = doc.getElementsByTagName("STATUSNAME");
			Element stElm = (Element) stNodes.item(0);
			xmlDataMap.put("STATUSNAME", getCharacterDataFromElement(stElm));

			NodeList nodes1 = doc.getElementsByTagName("TRACKINGID");
			Element line2 = (Element) nodes1.item(0);
			System.out.println("Tracking Id Is : " + getCharacterDataFromElement(line2));
			xmlDataMap.put("TRACKINGID", getCharacterDataFromElement(line2));

			NodeList statusNode = doc.getElementsByTagName("STATUSID");
			Element statusElement = (Element) statusNode.item(0);
			xmlDataMap.put("STATUSID", getCharacterDataFromElement(statusElement));

			NodeList statusTSNode = doc.getElementsByTagName("STATUSTIMESTAMP");
			Element statusTSElement = (Element) statusTSNode.item(0);
			xmlDataMap.put("STATUSTIMESTAMP", getCharacterDataFromElement(statusTSElement));
			
			NodeList nodes = doc.getElementsByTagName("STATUSCOMMENT");
		    Element line1 = (Element) nodes.item(0);
		    
			for (int i = 0; i < nodes.getLength(); i++) {
		          Element element = (Element) nodes.item(i);
		          NodeList documentNameNodes = element.getElementsByTagName("DOCUMENT");
		          for(int j = 0 ; j < documentNameNodes.getLength() ; j++){
		        	  Element docElement = (Element) documentNameNodes.item(j);
		        	  Node currentItem = documentNameNodes.item(j);
		        	    String attributeKey = currentItem.getAttributes().getNamedItem("Name").getNodeValue();
		        	    System.out.println("key is " + attributeKey);
		        	  NodeList contentNodelist = docElement.getElementsByTagName("CONTENT");
		        	  
		        	  for(int k = 0;k < contentNodelist.getLength();k++){
		        		  Element contentElement = (Element) contentNodelist.item(k);
		        		  System.out.println("Name: " + getCharacterDataFromElement(contentElement));
		        		  if(attributeKey != null && attributeKey.equals("MISMORpt.XML")){
		        			  // decodedValue = decodeEncodedValue(getCharacterDataFromElement(contentElement));
		        			   xmlDataMap.put("ENCODE", getCharacterDataFromElement(contentElement));
		        		  }
		        	  }
		          }
		          			       
		     }//end of if
			
			

			System.out.println("xmlDataMap is " + xmlDataMap);
			
			// display some current settings
	        System.out.println("Auth EndPoint: " + config.getAuthEndpoint());
	        System.out.println("Service EndPoint: "
	                + config.getServiceEndpoint());
	        System.out.println("Username: " + config.getUsername());
	        System.out.println("SessionId: " + config.getSessionId());
	     
	        System.out.println("**********************************");
	        

	        com.sforce.soap.SpearAppraisalAPI.SoapConnection soap =    com.sforce.soap.SpearAppraisalAPI.Connector.newConnection("","");
	        soap.setSessionHeader(config.getSessionId());
	        
	        XStream xStream = new XStream(new DomDriver());
	        xStream.alias("map", java.util.Map.class);
	        String xmlDataString = xStream.toXML(xmlDataMap);
	        
	        System.out.println("xmlDataString" + xmlDataString);
	        
	        System.out.println("decodedValue is ::::" + decodedValue);
	        
	        String xmlString = createXML(xmlDataMap,decodedValue);
	        
	        //com.sforce.soap.SpearAppraisalAPI.OrderResponse response = soap.setMercuryNetworkStatus(xmlDataString);///caspers's code
	        com.sforce.soap.SpearAppraisalAPI.OrderResponse response = soap.setMercuryNetworkStatus(xmlString);
	        
	                     
	        return response.getErrorDescription();
	        
		} // end of try
		catch (Exception e) {
			e.printStackTrace();
			//return e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			return sw.toString();
		}

		
		//return xmlDataMap.toString();
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}
	
	//public static String decodeEncodedValue(String encodedValue){/*
		 // byte[] decodedValue = Base64.getDecoder().decode(encodedValue); 
		  
		 // byte[] decodedValue =  DatatypeConverter.parseBase64Binary(encodedValue);
		  
		  // Basic Base64 decoding
	     // String decodesString = new String(decodedValue);
	      //System.out.println(decodesString);
	      //return decodesString;
		
	//*///}
	
	public String createXML(Map<String, String> xmlDataMap,String decodedString) throws Exception{
		String retString = "";
		try{
			
			 // byte[] encoded = Base64.encodeBase64(decodedString.getBytes()); 
			  
			 //String encoded =  Base64.getEncoder().encodeToString(decodedString.getBytes(StandardCharsets.UTF_8));
		      
			 //byte[] decodedValue =  DatatypeConverter.parseBase64Binary(encodedValue);
			 
		        //System.out.println("Base64 Encoded String : " + new String(encoded));


			
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			 DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		     Document doc = docBuilder.newDocument();
		     
			 Element rootElement = doc.createElement("STATUS");
			 doc.appendChild(rootElement);
			 
			 Element loanNumber = doc.createElement("LOANNUMBER");
			 loanNumber.appendChild(doc.createTextNode(xmlDataMap.get("LOANNUMBER")));
		     rootElement.appendChild(loanNumber);
		     
		     Element statusName = doc.createElement("STATUSNAME");
		     statusName.appendChild(doc.createTextNode(xmlDataMap.get("STATUSNAME")));
		     rootElement.appendChild(statusName);
		     
		     Element trackingId = doc.createElement("TRACKINGID");
		     trackingId.appendChild(doc.createTextNode(xmlDataMap.get("TRACKINGID")));
		     rootElement.appendChild(trackingId);
		     
		     Element statusId = doc.createElement("STATUSID");
		     statusId.appendChild(doc.createTextNode(xmlDataMap.get("STATUSID")));
		     rootElement.appendChild(statusId);
		     
		     Element statusTime = doc.createElement("STATUSTIMESTAMP");
		     statusTime.appendChild(doc.createTextNode(xmlDataMap.get("STATUSTIMESTAMP")));
		     rootElement.appendChild(statusTime);
		     
		     Element statusCmnt = doc.createElement("STATUSCOMMENT");
		     
		     Element documentXml = doc.createElement("DOCUMENT");
		     Attr attr = doc.createAttribute("Name");
				attr.setValue("MISMORpt.XML");
				documentXml.setAttributeNode(attr);
				
				Attr attr2 = doc.createAttribute("Type");
				attr2.setValue("MISMO 2.6 GSE");
				documentXml.setAttributeNode(attr2);
				
				Element contentElement = doc.createElement("CONTENT");
				contentElement.appendChild(doc.createTextNode(xmlDataMap.get("ENCODE")));
				documentXml.appendChild(contentElement);
		     
		     statusCmnt.appendChild(documentXml);
		     rootElement.appendChild(statusCmnt);
		     retString = printXmlDocument(doc);
		}catch(Exception ex){
			ex.printStackTrace();
		}

		return retString;
		
	} 
	
	public static String printXmlDocument(Document document) {
		String xml = "";
		try{
			   DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation();
			    LSSerializer lsSerializer = 
			        domImplementationLS.createLSSerializer();
			     xml = lsSerializer.writeToString(document);
			    System.out.println("1111111111111111111111111111" + xml);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	 
	    return xml;
	}
	
}
