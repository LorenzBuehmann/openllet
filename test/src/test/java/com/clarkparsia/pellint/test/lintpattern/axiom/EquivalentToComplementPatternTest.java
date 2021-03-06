package com.clarkparsia.pellint.test.lintpattern.axiom;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.owlapi.OWL;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToComplementPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.model.LintFixer;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.util.CollectionUtil;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 *
 * @author Harris Lin
 */
public class EquivalentToComplementPatternTest extends PellintTestCase
{

	private EquivalentToComplementPattern m_Pattern;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();
		m_Pattern = new EquivalentToComplementPattern();
	}

	@Test
	public void testNone() throws OWLException
	{
		assertTrue(m_Pattern.isFixable());

		final OWLClassExpression comp = OWL.not(m_Cls[0]);
		OWLAxiom axiom = OWL.subClassOf(m_Cls[0], comp);
		assertNull(m_Pattern.match(m_Ontology, axiom));

		axiom = OWL.equivalentClasses(m_P0AllC0, comp);
		assertNull(m_Pattern.match(m_Ontology, axiom));

		axiom = OWL.equivalentClasses(CollectionUtil.asSet(m_Cls[0], m_Cls[1], comp));
		assertNull(m_Pattern.match(m_Ontology, axiom));

		axiom = OWL.equivalentClasses(OWL.Nothing, OWL.Thing);
		assertNull(m_Pattern.match(m_Ontology, axiom));
	}

	@Test
	public void testComplementOfItself() throws OWLException
	{
		final OWLClassExpression comp = OWL.not(m_Cls[0]);
		final OWLAxiom axiom = OWL.equivalentClasses(m_Cls[0], comp);

		final Lint lint = m_Pattern.match(m_Ontology, axiom);
		assertNotNull(lint);
		assertTrue(lint.getParticipatingClasses().contains(m_Cls[0]));

		final LintFixer fixer = lint.getLintFixer();
		assertTrue(fixer.getAxiomsToRemove().contains(axiom));
		final OWLAxiom expectedAxiom = OWL.subClassOf(m_Cls[0], comp);
		assertTrue(fixer.getAxiomsToAdd().contains(expectedAxiom));

		assertNull(lint.getSeverity());
		assertSame(m_Ontology, lint.getParticipatingOntology());
	}

	@Test
	public void testComplementOfOthers() throws OWLException
	{
		final OWLClassExpression comp = OWL.not(OWL.or(m_Cls[1], m_Cls[2]));
		final OWLAxiom axiom = OWL.equivalentClasses(m_Cls[0], comp);
		final Lint lint = m_Pattern.match(m_Ontology, axiom);
		assertNotNull(lint);
		assertTrue(lint.getParticipatingClasses().contains(m_Cls[0]));

		final LintFixer fixer = lint.getLintFixer();
		assertTrue(fixer.getAxiomsToRemove().contains(axiom));
		final OWLAxiom expectedAxiom = OWL.subClassOf(m_Cls[0], comp);
		assertTrue(fixer.getAxiomsToAdd().contains(expectedAxiom));
	}
}
