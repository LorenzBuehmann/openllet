// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.rules.rete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mindswap.pellet.DependencySet;
import org.mindswap.pellet.exceptions.InternalReasonerException;

/**
 * <p>
 * Title: Tuple Implementation
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Ron Alford
 */
public class Tuple<T>
{
	private final DependencySet ds;
	private final List<T> elements;

	public Tuple(final DependencySet ds, final T... elementArgs)
	{
		if (ds == null)
			throw new InternalReasonerException("Null dependencyset argument to rete tuple");
		this.ds = ds;
		this.elements = Collections.unmodifiableList(Arrays.asList(elementArgs));
	}

	public Tuple(final DependencySet ds, final List<T> elements)
	{
		this.ds = ds;
		this.elements = Collections.unmodifiableList(new ArrayList<>(elements));
	}

	public DependencySet getDependencySet()
	{
		return ds;
	}

	public List<T> getElements()
	{
		return elements;
	}

	@Override
	public int hashCode()
	{
		return elements.hashCode();
	}

	@Override
	public String toString()
	{
		return elements.toString();
	}
}
