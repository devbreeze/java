package com.devbreeze.spreadsheet.xlsx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.SAXException;

import com.devbreeze.spreadsheet.SpreadsheetInput;
import com.devbreeze.spreadsheet.SpreadsheetParseException;

public class XlsxSpreadsheetInput implements SpreadsheetInput {

	private XSSFReader xssfReader;

	private ReadOnlySharedStringsTable strings;

	private Iterator<InputStream> sheetStreams;

	private boolean exhausted;

	private boolean sheetStart;

	private XMLStreamReader reader;

	private String cellType;

	private boolean isCellValue;

	private String cellValue;

	public XlsxSpreadsheetInput(OPCPackage opcPackage) {
		try {
			try {
				xssfReader = new XSSFReader(opcPackage);
			} catch (OpenXML4JException e) {
				throw new SpreadsheetParseException(e.getMessage(), e);
			}
			try {
				strings = new ReadOnlySharedStringsTable(opcPackage);
			} catch (SAXException e) {
				throw new SpreadsheetParseException(e.getMessage(), e);
			}
			try {
				sheetStreams = xssfReader.getSheetsData();
			} catch (InvalidFormatException e) {
				throw new SpreadsheetParseException(e.getMessage(), e);
			}
		} catch (IOException e) {
			throw new SpreadsheetParseException(e.getMessage(), e);
		}
	}

	@Override
	public boolean hasMoreTokens() {
		if (exhausted) {
			return false;
		}
		try {
			if (reader != null) {
				if (reader.hasNext()) {
					return true;
				} else {
					reader = null;
				}
			}
		} catch (XMLStreamException e) {
			throw new SpreadsheetParseException(e.getMessage(), e);
		}
		while (reader == null && sheetStreams.hasNext()) {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			try {
				reader = factory.createXMLStreamReader(sheetStreams.next());
				if (reader.hasNext()) {
					sheetStart = true;
					break;
				} else {
					reader = null;
				}
			} catch (XMLStreamException e) {
				throw new SpreadsheetParseException(e.getMessage(), e);
			}
		}
		exhausted = reader == null;
		return !exhausted;
	}

	@Override
	public void advanceToNextToken() {
		if (!hasMoreTokens()) {
			throw new SpreadsheetParseException("");
		}
		try {
			reader.next();
		} catch (XMLStreamException e) {
			throw new SpreadsheetParseException(e.getMessage(), e);
		}
		sheetStart = false;
	}

	@Override
	public boolean isAtSheetStart() {
		return hasMoreTokens() && sheetStart;
	}

	@Override
	public boolean isAtRowStart() {
		return reader.isStartElement() && StringUtils.equals(reader.getLocalName(), "row");
	}

	@Override
	public boolean isAtCellStart() {
		if (reader.isStartElement() && StringUtils.equals(reader.getLocalName(), "c")) {
			cellType = reader.getAttributeValue(null, "t");
			return true;
		}
		return false;
	}

	@Override
	public void putInCellValue() {
		advanceToNextToken();
		if ((reader.isStartElement() || reader.isEndElement())
				&& (StringUtils.equals(reader.getLocalName(), "v") || StringUtils.equals(reader.getLocalName(),
						"inlineStr"))) {
			isCellValue = reader.isStartElement();
		} else if (isCellValue && reader.isCharacters()) {
			String text = reader.getText();
			if (StringUtils.equals(cellType, "b")) {
				cellValue = StringUtils.startsWith(text, "0") ? "FALSE" : "TRUE";
			} else if (StringUtils.equals(cellType, "e")) {
				cellValue = "ERROR: " + text;
			} else if (StringUtils.equals(cellType, "inlineStr")) {
				cellValue = new XSSFRichTextString(text).toString();
			} else if (StringUtils.equals(cellType, "s")) {
				int idx = Integer.parseInt(text);
				XSSFRichTextString rtss = new XSSFRichTextString(strings.getEntryAt(idx));
				cellValue = rtss.toString();
			} else if (StringUtils.equals(cellType, "str")) {
				cellValue = text;
			}
		}
	}

	@Override
	public Object getCellValue() {
		return cellValue;
	}

	@Override
	public boolean consumeCellEnd() {
		if (reader.isEndElement() && StringUtils.equals(reader.getLocalName(), "c")) {
			advanceToNextToken();
			return true;
		}
		return false;
	}

	@Override
	public boolean consumeRowEnd() {
		if (reader.isEndElement() && StringUtils.equals(reader.getLocalName(), "row")) {
			advanceToNextToken();
			return true;
		}
		return false;
	}

	@Override
	public boolean consumeSheetEnd() {
		try {
			if (reader == null || reader.hasNext()) {
				return false;
			}
		} catch (XMLStreamException e) {
			throw new SpreadsheetParseException(e.getMessage(), e);
		}
		reader = null;
		return true;
	}

}