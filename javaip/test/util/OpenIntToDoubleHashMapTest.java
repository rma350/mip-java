package util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.apache.commons.math3.util.OpenIntToDoubleHashMap;
import org.apache.commons.math3.util.OpenIntToDoubleHashMap.Iterator;
import org.junit.Test;

import com.google.common.collect.Maps;

public class OpenIntToDoubleHashMapTest {

	@Test
	public void testIteratorEmpty() {
		OpenIntToDoubleHashMap map = new OpenIntToDoubleHashMap();
		Iterator it = map.iterator();
		assertFalse(it.hasNext());
	}

	@Test
	public void testIteratorNonEmpty() {
		OpenIntToDoubleHashMap map = new OpenIntToDoubleHashMap();
		map.put(2, 20.0);
		map.put(5, 50.0);
		Map<Integer, Double> actualResults = Maps.newHashMap();
		for (Iterator it = map.iterator(); it.hasNext();) {
			it.advance();
			Integer key = it.key();
			Double val = it.value();
			assertFalse(actualResults.containsKey(key));
			actualResults.put(key, val);
		}
		Map<Integer, Double> expectedResults = Maps.newHashMap();
		expectedResults.put(2, 20.0);
		expectedResults.put(5, 50.0);
		assertEquals(expectedResults, actualResults);

	}

}
