package com.devbreeze.learn.jaxp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.junit.Test;

public class StaxTest {

	@Test
	public void readUsingCursorApi() throws Exception {
		InputStream stream = getClass().getResourceAsStream("foo.xml");
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(stream, "UTF-8");
		try {
			assertEquals(XMLEvent.START_DOCUMENT, reader.getEventType());

			// <foo xmlns="http://www.example.com">
			assertEquals(XMLEvent.START_ELEMENT, reader.next());
			assertEquals("foo", reader.getLocalName());
			assertEquals("http://www.example.com", reader.getNamespaceURI());

			assertEquals(XMLEvent.CHARACTERS, reader.next());

			// <bar x="y">
			assertEquals(XMLEvent.START_ELEMENT, reader.next());
			assertEquals("bar", reader.getLocalName());
			assertEquals(1, reader.getAttributeCount());
			assertEquals("x", reader.getAttributeLocalName(0));
			assertEquals("y", reader.getAttributeValue(0));

			// Hello, world!
			assertEquals(XMLEvent.CHARACTERS, reader.next());
			assertEquals("Hello, world!", reader.getText());

			// </bar>
			assertEquals(XMLEvent.END_ELEMENT, reader.next());
			assertEquals("bar", reader.getLocalName());

			assertEquals(XMLEvent.CHARACTERS, reader.next());

			// </foo>
			assertEquals(XMLEvent.END_ELEMENT, reader.next());
			assertEquals("foo", reader.getLocalName());

			assertEquals(XMLEvent.END_DOCUMENT, reader.next());

			assertFalse(reader.hasNext());
		} finally {
			reader.close();
			stream.close();
		}
	}
}
