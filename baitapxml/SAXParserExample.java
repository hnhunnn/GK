package baitapxml;

import org.xml.sax.Attributes;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserExample {
	public static void main(String[] args) {
		try {
		
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser= factory.newSAXParser();
			DefaultHandler handler= new DefaultHandler() {
				boolean bName=false;
				boolean bPrice=false;
		        public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
		        		if (qName.equalsIgnoreCase("NAME")) {
	                    	
	                        bName = true;
	                    }
	                    if (qName.equalsIgnoreCase("PRICE")) {
	                        bPrice = true;
	                    }
	           }
		    	
		       public void endElement(String uri, String localName, String qName) throws SAXException {}
		       public void characters(char ch[], int start, int length) throws SAXException {
                   if (bName) {
              	 
                       System.out.println("Name: " + new String(ch, start, length));
                       bName = false;
                   }
                   if (bPrice) {
                       System.out.println("Price: " + new String(ch, start, length));
                       bPrice = false;
                   }
               }
			};
			saxParser.parse("data.xml", handler);
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		
	}
}
