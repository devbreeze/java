package com.devbreeze.commons.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;

public class CharIterator implements Iterator<Character> {

	private BufferedReader bufferedReader;
	private Character cachedCharacter;
	private boolean finished = false;

	public CharIterator(Reader reader) {
		bufferedReader = IOUtils.toBufferedReader(reader);
	}

	@Override
	public boolean hasNext() {
		if (cachedCharacter != null) {
			return true;
		} else if (finished) {
			return false;
		} else {
			try {
				int character = bufferedReader.read();
				if (character == -1) {
					finished = true;
					return false;
				} else {
					cachedCharacter = (char) character;
					return true;
				}
			} catch (IOException e) {
				finished = true;
				IOUtils.closeQuietly(bufferedReader);
				cachedCharacter = null;
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	public Character next() {
		if (!hasNext()) {
			throw new NoSuchElementException("No more characters");
		}
		Character currentCharacter = cachedCharacter;
		cachedCharacter = null;
		return currentCharacter;
	}

}
