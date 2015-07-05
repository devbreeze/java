package com.devbreeze.spreadsheet;

public class SpreadsheetParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SpreadsheetParseException(String message) {
		super(message);
	}

	public SpreadsheetParseException(String message, Throwable cause) {
		super(message, cause);
	}

}
