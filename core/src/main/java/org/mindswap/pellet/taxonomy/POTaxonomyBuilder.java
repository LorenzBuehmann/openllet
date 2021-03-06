// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.taxonomy;

import aterm.ATermAppl;
import java.util.Map;
import java.util.Set;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.PartialOrderBuilder;
import org.mindswap.pellet.utils.PartialOrderComparator;
import org.mindswap.pellet.utils.progress.ProgressMonitor;

public class POTaxonomyBuilder implements TaxonomyBuilder
{

	private final PartialOrderBuilder<ATermAppl> builder;
	private KnowledgeBase kb;
	private final Taxonomy<ATermAppl> tax;

	public POTaxonomyBuilder(final KnowledgeBase kb)
	{
		this(kb, new SubsumptionComparator(kb));
	}

	public POTaxonomyBuilder(final KnowledgeBase kb, final PartialOrderComparator<ATermAppl> comparator)
	{
		this.kb = kb;
		this.tax = new Taxonomy<>(null, ATermUtils.TOP, ATermUtils.BOTTOM);
		this.builder = new PartialOrderBuilder<>(tax, comparator);
	}

	@Override
	public boolean classify()
	{
		builder.addAll(kb.getClasses());

		return true;
	}

	@Override
	public void classify(final ATermAppl c)
	{
		builder.add(c);
	}

	@Override
	public boolean realize()
	{
		throw new UnsupportedOperationException();
		/*
		 * CDOptimizedTaxonomyBuilder b = new CDOptimizedTaxonomyBuilder();
		 * b.setKB( _kb ); b.classify(); return b.realize();
		 */
	}

	@Override
	public void realize(final ATermAppl x)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setKB(final KnowledgeBase kb)
	{
		this.kb = kb;
	}

	public PartialOrderComparator<ATermAppl> getComparator()
	{
		return builder.getComparator();
	}

	public void setComparator(final PartialOrderComparator<ATermAppl> comparator)
	{
		builder.setComparator(comparator);
	}

	@Override
	public void setProgressMonitor(final ProgressMonitor monitor)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<ATermAppl, Set<ATermAppl>> getToldDisjoints()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Taxonomy<ATermAppl> getToldTaxonomy()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Taxonomy<ATermAppl> getTaxonomy()
	{
		return tax;
	}
}
