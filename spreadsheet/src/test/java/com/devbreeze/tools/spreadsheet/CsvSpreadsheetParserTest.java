package com.devbreeze.tools.spreadsheet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.io.Charsets;
import org.junit.Test;

import com.devbreeze.spreadsheet.Cell;
import com.devbreeze.spreadsheet.Row;
import com.devbreeze.spreadsheet.Sheet;
import com.devbreeze.spreadsheet.Spreadsheet;
import com.devbreeze.spreadsheet.SpreadsheetInput;
import com.devbreeze.spreadsheet.SpreadsheetParser;
import com.devbreeze.spreadsheet.SpreadsheetParserImpl;
import com.devbreeze.spreadsheet.csv.CsvSpreadsheetInput;

/**
 * @author Diego Schivo
 */
public class CsvSpreadsheetParserTest {

	@Test
	public void oneCell() throws Exception {
		InputStream stream = getClass().getResourceAsStream("one-cell.csv");
		SpreadsheetInput input = new CsvSpreadsheetInput(new InputStreamReader(stream, Charsets.toCharset("UTF-8")));
		SpreadsheetParser parser = new SpreadsheetParserImpl();
		Spreadsheet spreadsheet = parser.parse(input);

		Iterator<Sheet> sheets = spreadsheet.sheets();
		assertTrue(sheets.hasNext());
		Sheet sheet = sheets.next();

		Iterator<Row> rows = sheet.rows();
		assertTrue(rows.hasNext());
		Row row = rows.next();

		Iterator<Cell> cells = row.cells();
		assertTrue(cells.hasNext());
		Cell cell = cells.next();
		assertEquals("foo", cell.getValue());

		assertFalse(cells.hasNext());
		assertFalse(rows.hasNext());
		assertFalse(sheets.hasNext());
	}

	@Test
	public void twoCols() throws Exception {
		InputStream stream = getClass().getResourceAsStream("two-cols.csv");
		SpreadsheetInput input = new CsvSpreadsheetInput(new InputStreamReader(stream, Charsets.toCharset("UTF-8")));
		SpreadsheetParser parser = new SpreadsheetParserImpl();
		Spreadsheet spreadsheet = parser.parse(input);

		Iterator<Sheet> sheets = spreadsheet.sheets();
		assertTrue(sheets.hasNext());
		Sheet sheet = sheets.next();

		Iterator<Row> rows = sheet.rows();
		assertTrue(rows.hasNext());
		Row row = rows.next();

		Iterator<Cell> cells = row.cells();
		assertTrue(cells.hasNext());
		Cell cell = cells.next();
		assertEquals("foo", cell.getValue());

		assertTrue(cells.hasNext());
		cell = cells.next();
		assertEquals("bar", cell.getValue());

		assertFalse(cells.hasNext());
		assertFalse(rows.hasNext());
		assertFalse(sheets.hasNext());
	}

	@Test
	public void twoRows() throws Exception {
		InputStream stream = getClass().getResourceAsStream("two-rows.csv");
		SpreadsheetInput input = new CsvSpreadsheetInput(new InputStreamReader(stream, Charsets.toCharset("UTF-8")));
		SpreadsheetParser parser = new SpreadsheetParserImpl();
		Spreadsheet spreadsheet = parser.parse(input);

		Iterator<Sheet> sheets = spreadsheet.sheets();
		assertTrue(sheets.hasNext());
		Sheet sheet = sheets.next();

		Iterator<Row> rows = sheet.rows();
		assertTrue(rows.hasNext());
		Row row = rows.next();

		Iterator<Cell> cells = row.cells();
		assertTrue(cells.hasNext());
		Cell cell = cells.next();
		assertEquals("foo", cell.getValue());

		assertFalse(cells.hasNext());
		assertTrue(rows.hasNext());
		row = rows.next();

		cells = row.cells();
		assertTrue(cells.hasNext());
		cell = cells.next();
		assertEquals("bar", cell.getValue());

		assertFalse(cells.hasNext());
		assertFalse(rows.hasNext());
		assertFalse(sheets.hasNext());
	}

}
