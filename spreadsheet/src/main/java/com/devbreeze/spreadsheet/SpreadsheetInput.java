package com.devbreeze.spreadsheet;

public interface SpreadsheetInput {

	boolean hasMoreTokens();

	void advanceToNextToken();

	boolean isAtSheetStart();

	boolean isAtRowStart();

	boolean isAtCellStart();

	void putInCellValue();

	Object getCellValue();

	boolean consumeCellEnd();

	boolean consumeRowEnd();

	boolean consumeSheetEnd();

}