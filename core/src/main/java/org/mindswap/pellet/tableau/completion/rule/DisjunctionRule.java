// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.tableau.completion.rule;

import aterm.ATermAppl;
import aterm.ATermList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.mindswap.pellet.Individual;
import org.mindswap.pellet.Node;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.tableau.branch.DisjunctionBranch;
import org.mindswap.pellet.tableau.completion.CompletionStrategy;
import org.mindswap.pellet.tableau.completion.queue.NodeSelector;
import org.mindswap.pellet.utils.ATermUtils;

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
public class DisjunctionRule extends AbstractTableauRule
{
	public DisjunctionRule(final CompletionStrategy strategy)
	{
		super(strategy, NodeSelector.DISJUNCTION, BlockingType.COMPLETE);
	}

	@Override
	public void apply(final Individual node)
	{
		if (!node.canApply(Node.OR))
			return;

		final List<ATermAppl> types = node.getTypes(Node.OR);

		final int size = types.size();
		final ATermAppl[] disjunctions = new ATermAppl[size - node._applyNext[Node.OR]];
		types.subList(node._applyNext[Node.OR], size).toArray(disjunctions);
		if (PelletOptions.USE_DISJUNCTION_SORTING != PelletOptions.NO_SORTING)
			sortDisjunctions(node, disjunctions);

		for (final ATermAppl disjunction : disjunctions)
		{
			applyDisjunctionRule(node, disjunction);

			if (_strategy.getABox().isClosed() || node.isMerged())
				return;
		}
		node._applyNext[Node.OR] = size;
	}

	private static void sortDisjunctions(final Individual node, final ATermAppl[] disjunctions)
	{
		if (PelletOptions.USE_DISJUNCTION_SORTING == PelletOptions.OLDEST_FIRST)
		{
			final Comparator<ATermAppl> comparator = (d1, d2) -> node.getDepends(d1).max() - node.getDepends(d2).max();

			Arrays.sort(disjunctions, comparator);
		}
		else
			throw new InternalReasonerException("Unknown _disjunction sorting option " + PelletOptions.USE_DISJUNCTION_SORTING);
	}

	/**
	 * Apply the _disjunction rule to an specific label for an individual
	 *
	 * @param _node
	 * @param _disjunction
	 */
	protected void applyDisjunctionRule(final Individual node, final ATermAppl disjunction)
	{
		// _disjunction is now in the form not(and([not(d1), not(d2), ...]))
		final ATermAppl a = (ATermAppl) disjunction.getArgument(0);
		ATermList disjuncts = (ATermList) a.getArgument(0);
		final ATermAppl[] disj = new ATermAppl[disjuncts.getLength()];

		for (int index = 0; !disjuncts.isEmpty(); disjuncts = disjuncts.getNext(), index++)
		{
			disj[index] = ATermUtils.negate((ATermAppl) disjuncts.getFirst());
			if (node.hasType(disj[index]))
				return;
		}

		final DisjunctionBranch newBranch = new DisjunctionBranch(_strategy.getABox(), _strategy, node, disjunction, node.getDepends(disjunction), disj);
		_strategy.addBranch(newBranch);

		newBranch.tryNext();
	}

}
