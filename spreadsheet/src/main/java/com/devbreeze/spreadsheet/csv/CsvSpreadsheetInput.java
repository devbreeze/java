package com.devbreeze.spreadsheet.csv;

import java.io.InputStreamReader;

import org.apache.commons.collections4.iterators.PeekingIterator;

import com.devbreeze.commons.io.CharIterator;
import com.devbreeze.spreadsheet.SpreadsheetInput;

public class CsvSpreadsheetInput implements SpreadsheetInput {

	private PeekingIterator<Character> input;

	private boolean atSheetStart;

	private boolean atRowStart;

	private boolean atCellStart;

	private String cellValue;

	private StringBuilder cellValueBuilder = new StringBuilder();

	public CsvSpreadsheetInput(InputStreamReader reader) {
		input = PeekingIterator.peekingIterator(new CharIterator(reader));
		atSheetStart = atRowStart = atCellStart = true;
	}

	@Override
	public boolean hasMoreTokens() {
		return input.hasNext();
	}

	@Override
	public void advanceToNextToken() {
		input.next();
		atSheetStart = atRowStart = atCellStart = false;
	}

	@Override
	public boolean isAtSheetStart() {
		return atSheetStart;
	}

	@Override
	public boolean isAtRowStart() {
		return atRowStart;
	}

	@Override
	public boolean isAtCellStart() {
		return atCellStart;
	}

	@Override
	public void putInCellValue() {
		cellValueBuilder.append(input.peek());
		advanceToNextToken();
	}

	@Override
	public Object getCellValue() {
		return cellValue;
	}

	@Override
	public boolean consumeCellEnd() {
		boolean rowEnd = checkCharacter('\n') || checkCharacter('\r');
		if (consumeCharacter(',') || rowEnd) {
			cellValue = cellValueBuilder.toString();
			cellValueBuilder.setLength(0);
			atCellStart = !rowEnd;
			return true;
		}
		return false;
	}

	@Override
	public boolean consumeRowEnd() {
		if (consumeCharacter('\n')) {
			atRowStart = atCellStart = input.hasNext();
			return true;
		}
		if (consumeCharacter('\r')) {
			consumeCharacter('\n');
			atRowStart = atCellStart = input.hasNext();
			return true;
		}
		return false;
	}

	@Override
	public boolean consumeSheetEnd() {
		return !input.hasNext();
	}

	private boolean checkCharacter(char value) {
		Character peek = input.peek();
		if (peek == null) {
			return false;
		}
		if (peek != value) {
			return false;
		}
		return true;
	}

	private boolean consumeCharacter(char value) {
		boolean check = checkCharacter(value);
		if (check) {
			input.next();
		}
		return check;
	}

}