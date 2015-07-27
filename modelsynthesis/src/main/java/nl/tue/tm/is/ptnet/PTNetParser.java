package nl.tue.tm.is.ptnet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PTNetParser extends DefaultHandler {

	private static final int STATE_NONE = 0;
	private static final int STATE_PLACE = 1;
	private static final int STATE_PLACE_NAME = 2;
	private static final int STATE_PLACE_NAME_VALUE = 3;
	private static final int STATE_PLACE_INITIALMARKING = 4;
	private static final int STATE_PLACE_INITIALMARKING_VALUE = 5;
	private static final int STATE_TRANSITION = 6;
	private static final int STATE_TRANSITION_NAME = 7;
	private static final int STATE_TRANSITION_NAME_VALUE = 8;
	private static final int STATE_ARC = 9;
	private static final int STATE_NTOMSPECIFIC = 10;
	private static final int STATE_ATTRIBUTE = 11;
	

	private Node currNode;
	private int currState;
	private String attributeKeyword;
	private StringBuffer charsReading;
	
	PTNet result;
	
	String suffix = "";
	
	public PTNetParser(PTNet result){
		this.result = result;
	}
	public PTNetParser(PTNet result, String suffix){
		this.result = result;
		this.suffix = suffix;
	}	
	
	public void characters(char[] arr, int start, int len) throws SAXException {
		charsReading.append (arr, start, len);
	}

	public void endElement(String namespace, String lname, String qname) throws SAXException {
		switch (currState){
		case STATE_NONE:
			break;
		case STATE_PLACE:
			if (qname.toLowerCase().equals("place")){
				currState = STATE_NONE;				
			}
			break;
		case STATE_PLACE_NAME:
			if (qname.toLowerCase().equals("name")){
				currState = STATE_PLACE;				
			}
			break;
		case STATE_PLACE_NAME_VALUE:
			if (qname.toLowerCase().equals("value") || qname.toLowerCase().equals("text")){
				currNode.setName(getCharacterString());
				currState = STATE_PLACE_NAME;				
			}
			break;
		case STATE_PLACE_INITIALMARKING:
			if (qname.toLowerCase().equals("initialmarking")){
				currState = STATE_PLACE;				
			}
			break;
		case STATE_PLACE_INITIALMARKING_VALUE:
			if (qname.toLowerCase().equals("value")){
				try {
					result.addMarking((Place) currNode, Integer.parseInt(getCharacterString()));
				} catch (NumberFormatException e) {
					throw new SAXException("Cannot parse initial marking; wrong number format.");
				}
				currState = STATE_PLACE_INITIALMARKING;				
			}
			break;
		case STATE_TRANSITION:
			if (qname.toLowerCase().equals("transition")){
				currState = STATE_NONE;				
			}
			break;
		case STATE_TRANSITION_NAME:
			if (qname.toLowerCase().equals("name")){
				currState = STATE_TRANSITION;				
			}
			break;
		case STATE_TRANSITION_NAME_VALUE:
			if (qname.toLowerCase().equals("value") || qname.toLowerCase().equals("text")){
				currNode.setName(getCharacterString());
				if (currNode.getName().length()==0){
					currNode.setName(Transition.SILENT_LABEL);
				}
				if (currNode.getName().startsWith(".\\n")){
					currNode.setName(currNode.getName().substring(3));
				}
				currState = STATE_TRANSITION_NAME;				
			}
			break;
		case STATE_ARC:
			if (qname.toLowerCase().equals("arc")){
				currState = STATE_NONE;				
			}
			break;
		case STATE_ATTRIBUTE:
			if (qname.toLowerCase().equals("attribute")){
				result.setAttribute(currNode, attributeKeyword, getCharacterString());
				currState = STATE_NTOMSPECIFIC;				
			}
			break;
		case STATE_NTOMSPECIFIC:
			if (qname.toLowerCase().equals("toolspecific")){
				currState = STATE_NONE;				
			}
			break;
		default:
			break;
		}
	}

	public void startElement(String namespace, String lname, String qname, Attributes attrs) throws SAXException {
		charsReading =  new StringBuffer ();
		if (qname.toLowerCase().equals("place")){
			currNode = new Place();
			for (int i = 0; i < attrs.getLength(); i++){
				if (attrs.getQName(i).toLowerCase().equals("id")){
					currNode.setId(attrs.getValue(i).concat(suffix));
				}
			}
			result.addPlace((Place)currNode);
			currState = STATE_PLACE;
		}else if (qname.toLowerCase().equals("transition")){
			currNode = new Transition();
			for (int i = 0; i < attrs.getLength(); i++){
				if (attrs.getQName(i).toLowerCase().equals("id")){
					currNode.setId(attrs.getValue(i).concat(suffix));
				}
			}
			result.addTransition((Transition)currNode);
			currState = STATE_TRANSITION;
		}else if (qname.toLowerCase().equals("arc")){
			Arc arc = new Arc();
			for (int i = 0; i < attrs.getLength(); i++){
				if (attrs.getQName(i).toLowerCase().equals("id")){
					arc.setId(attrs.getValue(i).concat(suffix));
				}else if (attrs.getQName(i).toLowerCase().equals("source")){
					arc.setSource(result.findNode(attrs.getValue(i).concat(suffix)));
				}else if (attrs.getQName(i).toLowerCase().equals("target")){
					arc.setTarget(result.findNode(attrs.getValue(i).concat(suffix)));
				}
			}
			result.addArc(arc);
			currState = STATE_ARC;
		}else if (qname.toLowerCase().equals("name")){
			if (currState == STATE_PLACE){
				currState = STATE_PLACE_NAME;
			}else if (currState == STATE_TRANSITION){
				currState = STATE_TRANSITION_NAME;
			}
		}else if (qname.toLowerCase().equals("initialmarking")){
			if (currState == STATE_PLACE){
				currState = STATE_PLACE_INITIALMARKING;
			}			
		}else if (qname.toLowerCase().equals("value") || qname.toLowerCase().equals("text")){
			if (currState == STATE_PLACE_NAME){
				currState = STATE_PLACE_NAME_VALUE;
			}else if (currState == STATE_PLACE_INITIALMARKING){
				currState = STATE_PLACE_INITIALMARKING_VALUE;
			}else if (currState == STATE_TRANSITION_NAME){
				currState = STATE_TRANSITION_NAME_VALUE;
			} 			
		}else if (qname.toLowerCase().equals("attribute")){
			currState = STATE_ATTRIBUTE;
			for (int i = 0; i < attrs.getLength(); i++){
				if (attrs.getQName(i).toLowerCase().equals("keyword")){
					attributeKeyword = attrs.getValue(i).trim();
				}
			}
		}
	}
	
	private String getCharacterString(){
		String result = charsReading.toString();
		result = result.replaceAll("&apos;","'");
		result = result.replaceAll("&lt;","<");
		result = result.replaceAll("&gt;",">");
		result = result.replaceAll("&amp;","&");
		result = result.replaceAll("&quot;","\"");
		return result;
	}

}
