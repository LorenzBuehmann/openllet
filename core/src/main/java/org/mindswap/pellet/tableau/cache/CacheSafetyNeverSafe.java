// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.cache;

import aterm.ATermAppl;
import com.clarkparsia.pellet.expressivity.Expressivity;
import org.mindswap.pellet.Individual;

/**
 * A singleton implementation of CacheSafety that says it is never safe to reuse cached results.
 *
 * @author Evren Sirin
 */
public class CacheSafetyNeverSafe implements CacheSafety
{
	private static CacheSafetyNeverSafe INSTANCE = new CacheSafetyNeverSafe();

	public static CacheSafetyNeverSafe getInstance()
	{
		return INSTANCE;
	}

	private CacheSafetyNeverSafe()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSafe(final ATermAppl c, final Individual ind)
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canSupport(final Expressivity expressivity)
	{
		return true;
	}
}
