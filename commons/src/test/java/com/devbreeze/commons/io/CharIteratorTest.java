package com.devbreeze.commons.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.NoSuchElementException;

import org.junit.Test;

public class CharIteratorTest {

	@Test
	public void empty() {
		CharIterator iterator = new CharIterator(new StringReader(""));

		assertFalse(iterator.hasNext());
		try {
			iterator.next();
			fail();
		} catch (NoSuchElementException e) {
		}
	}

	@Test
	public void oneChar() {
		CharIterator iterator = new CharIterator(new StringReader("a"));

		assertTrue(iterator.hasNext());
		assertEquals('a', (char) iterator.next());

		assertFalse(iterator.hasNext());
		try {
			iterator.next();
			fail();
		} catch (NoSuchElementException e) {
		}
	}

	@Test
	public void twoChars() {
		CharIterator iterator = new CharIterator(new StringReader("ab"));

		assertTrue(iterator.hasNext());
		assertEquals('a', (char) iterator.next());

		assertTrue(iterator.hasNext());
		assertEquals('b', (char) iterator.next());

		assertFalse(iterator.hasNext());
		try {
			iterator.next();
			fail();
		} catch (NoSuchElementException e) {
		}
	}
}
