// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tbox.impl;

import aterm.ATermAppl;
import com.clarkparsia.pellet.utils.CollectionUtils;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.iterator.IteratorUtils;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Evren Sirin
 */
public class UnaryTBox
{
	public static final Logger log = Logger.getLogger(UnaryTBox.class.getName());

	private Map<ATermAppl, List<Unfolding>> _unfoldings = CollectionUtils.makeIdentityMap();

	public UnaryTBox()
	{
		_unfoldings = CollectionUtils.makeIdentityMap();
	}

	public void add(final ATermAppl sub, ATermAppl sup, final Set<ATermAppl> explanation)
	{
		List<Unfolding> list = _unfoldings.get(sub);
		if (list == null)
		{
			list = CollectionUtils.makeList();
			_unfoldings.put(sub, list);
		}

		sup = ATermUtils.normalize(sup);

		if (log.isLoggable(Level.FINE))
			log.fine("Add sub: " + ATermUtils.toString(sub) + " < " + ATermUtils.toString(sup));

		list.add(Unfolding.create(sup, explanation));
	}

	public boolean remove(@SuppressWarnings("unused") final ATermAppl axiom)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Iterator<Unfolding> unfold(final ATermAppl concept)
	{
		final List<Unfolding> unfoldingList = _unfoldings.get(concept);
		return unfoldingList == null ? IteratorUtils.<Unfolding> emptyIterator() : unfoldingList.iterator();
	}

	public void print(final Appendable out) throws IOException
	{
		for (final Entry<ATermAppl, List<Unfolding>> e : _unfoldings.entrySet())
		{
			out.append(ATermUtils.toString(e.getKey()));
			out.append(" < ");
			out.append(e.getValue().toString());
			out.append("\n");
		}
	}
}
