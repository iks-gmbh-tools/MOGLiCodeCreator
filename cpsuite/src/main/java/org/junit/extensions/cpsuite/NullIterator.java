<<<<<<< HEAD
<<<<<<< HEAD
=======
<<<<<<< HEAD
/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package org.junit.extensions.cpsuite;

import java.util.*;

public class NullIterator<T> implements Iterable<T>, Iterator<T> {

	public Iterator<T> iterator() {
		return this;
	}

	public boolean hasNext() {
		return false;
	}

	public T next() {
		throw new NoSuchElementException();
	}

	public void remove() {
	}

}
=======
=======
>>>>>>> 9fc3cbd437cf194e6ad0560123dd6958fe55cdfc
>>>>>>> 656c84c58ad794ed34c58c30ecc9bf656c921412
/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package org.junit.extensions.cpsuite;

import java.util.*;

public class NullIterator<T> implements Iterable<T>, Iterator<T> {

	public Iterator<T> iterator() {
		return this;
	}

	public boolean hasNext() {
		return false;
	}

	public T next() {
		throw new NoSuchElementException();
	}

	public void remove() {
	}

}
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
>>>>>>> 656c84c58ad794ed34c58c30ecc9bf656c921412
=======
/*
 * @author Johannes Link (business@johanneslink.net)
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package org.junit.extensions.cpsuite;

import java.util.*;

public class NullIterator<T> implements Iterable<T>, Iterator<T> {

	public Iterator<T> iterator() {
		return this;
	}

	public boolean hasNext() {
		return false;
	}

	public T next() {
		throw new NoSuchElementException();
	}

	public void remove() {
	}

}
<<<<<<< HEAD
=======
>>>>>>> 9fc3cbd437cf194e6ad0560123dd6958fe55cdfc
>>>>>>> 656c84c58ad794ed34c58c30ecc9bf656c921412
>>>>>>> originReikOberrath/master
