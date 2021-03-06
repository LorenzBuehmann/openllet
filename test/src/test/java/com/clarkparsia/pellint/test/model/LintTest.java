package com.clarkparsia.pellint.test.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.clarkparsia.pellint.lintpattern.LintPattern;
import com.clarkparsia.pellint.model.Lint;
import com.clarkparsia.pellint.test.PellintTestCase;
import com.clarkparsia.pellint.test.lintpattern.MockLintPattern;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
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
public class LintTest extends PellintTestCase
{
	private LintPattern m_MockPattern;

	@Override
	@Before
	public void setUp() throws OWLOntologyCreationException
	{
		super.setUp();
		m_MockPattern = new MockLintPattern();
	}

	@Test
	public void testWithoutFixer() throws OWLOntologyChangeException
	{
		final Lint lint = new Lint(m_MockPattern, m_Ontology);
		assertSame(m_MockPattern, lint.getPattern());
		assertSame(m_Ontology, lint.getParticipatingOntology());
		assertFalse(lint.applyFix(m_Manager));
	}

	@Test
	public void testWithFixer() throws OWLOntologyChangeException
	{
		final Lint lint = new Lint(m_MockPattern, m_Ontology);
		final MockLintFixer fixer = new MockLintFixer();
		lint.setLintFixer(fixer);
		assertTrue(lint.applyFix(m_Manager));
		assertTrue(fixer.applyCalled);
	}
}
