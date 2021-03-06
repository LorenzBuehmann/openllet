package com.clarkparsia.pellint.test.lintpattern;

import com.clarkparsia.pellint.format.LintFormat;
import com.clarkparsia.pellint.lintpattern.LintPattern;

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
public class MockLintPattern implements LintPattern
{
	private final boolean m_IsFixable;

	public MockLintPattern()
	{
		this(false);
	}

	public MockLintPattern(final boolean isFixable)
	{
		m_IsFixable = isFixable;
	}

	@Override
	public String getName()
	{
		return this.toString();
	}

	@Override
	public String getDescription()
	{
		return this.toString();
	}

	@Override
	public boolean isFixable()
	{
		return m_IsFixable;
	}

	@Override
	public LintFormat getDefaultLintFormat()
	{
		return null;
	}

}
