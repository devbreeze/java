package com.devbreeze.spreadsheet;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Diego Schivo
 */
public class SpreadsheetParserImpl implements SpreadsheetParser {

	@Override
	public Spreadsheet parse(SpreadsheetInput input) {
		Iterator<Sheet> sheets = sheets(input);
		Spreadsheet spreadsheet = new Spreadsheet() {

			@Override
			public Iterator<Sheet> sheets() {
				return sheets;
			}
		};
		return spreadsheet;
	}

	protected Iterator<Sheet> sheets(SpreadsheetInput input) {
		Iterator<Sheet> sheets = new Iterator<Sheet>() {

			private boolean exhausted;

			@Override
			public boolean hasNext() {
				if (exhausted) {
					return false;
				}
				while (!(exhausted = !input.hasMoreTokens()) && !input.isAtSheetStart()) {
					input.advanceToNextToken();
				}
				return !exhausted;
			}

			@Override
			public Sheet next() {
				if (!hasNext()) {
					throw new NoSuchElementException("No more sheets");
				}
				Iterator<Row> rows = rows(input);
				Sheet sheet = new Sheet() {

					@Override
					public Iterator<Row> rows() {
						return rows;
					}
				};
				return sheet;
			}

		};
		return sheets;
	}

	protected Iterator<Row> rows(SpreadsheetInput input) {
		Iterator<Row> rows = new Iterator<Row>() {

			private boolean exhausted;

			@Override
			public boolean hasNext() {
				if (exhausted) {
					return false;
				}
				while (!input.isAtRowStart() && !(exhausted = input.consumeSheetEnd())) {
					input.advanceToNextToken();
				}
				return !exhausted;
			}

			@Override
			public Row next() {
				if (!hasNext()) {
					throw new NoSuchElementException("No more rows");
				}
				Iterator<Cell> cells = cells(input);
				Row row = new Row() {

					@Override
					public Iterator<Cell> cells() {
						return cells;
					}
				};
				return row;
			}

		};
		return rows;
	}

	protected Iterator<Cell> cells(SpreadsheetInput input) {
		Iterator<Cell> cells = new Iterator<Cell>() {

			private boolean exhausted;

			@Override
			public boolean hasNext() {
				if (exhausted) {
					return false;
				}
				while (!input.isAtCellStart() && !(exhausted = input.consumeRowEnd())) {
					input.advanceToNextToken();
				}
				return !exhausted;
			}

			@Override
			public Cell next() {
				if (!hasNext()) {
					throw new NoSuchElementException("No more cells");
				}
				while (input.hasMoreTokens() && !input.consumeCellEnd()) {
					input.putInCellValue();
				}
				Object value = input.getCellValue();
				Cell cell = new Cell() {

					@Override
					public Object getValue() {
						return value;
					}
				};
				return cell;
			}

		};
		return cells;
	}

}
