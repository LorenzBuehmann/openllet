// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.test.rbox;

import junit.framework.JUnit4TestAdapter;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Evren Sirin
 */
@RunWith(Suite.class)
@SuiteClasses({ DisjointPropertyTests.class, PropertyChainTests.class, PropertyCharacteristicsTests.class, TestTopBottom.class, RBoxUpdateTests.class })
public class RBoxTestSuite
{
	public static junit.framework.Test suite()
	{
		return new JUnit4TestAdapter(RBoxTestSuite.class);
	}
}
