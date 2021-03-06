// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellint.lintpattern;

import com.clarkparsia.pellint.lintpattern.axiom.AxiomLintPattern;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToAllValuePattern;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToComplementPattern;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToMaxCardinalityPattern;
import com.clarkparsia.pellint.lintpattern.axiom.EquivalentToTopPattern;
import com.clarkparsia.pellint.lintpattern.axiom.GCIPattern;
import com.clarkparsia.pellint.lintpattern.axiom.LargeCardinalityPattern;
import com.clarkparsia.pellint.lintpattern.axiom.LargeDisjunctionPattern;
import com.clarkparsia.pellint.lintpattern.ontology.EquivalentAndSubclassAxiomPattern;
import com.clarkparsia.pellint.lintpattern.ontology.ExistentialExplosionPattern;
import com.clarkparsia.pellint.lintpattern.ontology.OntologyLintPattern;
import com.clarkparsia.pellint.lintpattern.ontology.TooManyDifferentIndividualsPattern;
import com.clarkparsia.pellint.util.CollectionUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Lint Pattern Loader
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
public class LintPatternLoader
{
	private static final Logger LOGGER = Logger.getLogger(LintPatternLoader.class.getName());

	public static final List<AxiomLintPattern> DEFAULT_AXIOM_LINT_PATTERNS = Arrays.asList(new EquivalentToAllValuePattern(), new EquivalentToMaxCardinalityPattern(), new EquivalentToComplementPattern(), new EquivalentToTopPattern(), new GCIPattern(), new LargeCardinalityPattern(), new LargeDisjunctionPattern());

	public static final List<OntologyLintPattern> DEFAULT_ONTOLOGY_LINT_PATTERNS = Arrays.asList(new EquivalentAndSubclassAxiomPattern(), new ExistentialExplosionPattern(), new TooManyDifferentIndividualsPattern());

	private List<AxiomLintPattern> m_AxiomLintPatterns;
	private List<OntologyLintPattern> m_OntologyLintPatterns;

	public LintPatternLoader()
	{
		m_AxiomLintPatterns = DEFAULT_AXIOM_LINT_PATTERNS;
		m_OntologyLintPatterns = DEFAULT_ONTOLOGY_LINT_PATTERNS;
	}

	public LintPatternLoader(final Properties properties)
	{
		final Collection<LintPattern> patterns = loadPatterns(formatProperties(properties));
		if (patterns.isEmpty())
		{
			m_AxiomLintPatterns = DEFAULT_AXIOM_LINT_PATTERNS;
			m_OntologyLintPatterns = DEFAULT_ONTOLOGY_LINT_PATTERNS;
		}
		else
		{
			m_AxiomLintPatterns = CollectionUtil.makeList();
			m_OntologyLintPatterns = CollectionUtil.makeList();
			for (final LintPattern pattern : patterns)
				if (pattern instanceof AxiomLintPattern)
					m_AxiomLintPatterns.add((AxiomLintPattern) pattern);
				else
					if (pattern instanceof OntologyLintPattern)
						m_OntologyLintPatterns.add((OntologyLintPattern) pattern);
		}
	}

	public List<AxiomLintPattern> getAxiomLintPatterns()
	{
		return m_AxiomLintPatterns;
	}

	public List<OntologyLintPattern> getOntologyLintPatterns()
	{
		return m_OntologyLintPatterns;
	}

	private static Map<String, String> formatProperties(final Properties properties)
	{
		final Map<String, String> formattedProperties = new HashMap<>();
		for (final Entry<Object, Object> entry : properties.entrySet())
		{
			final Object key = entry.getKey();
			final Object value = entry.getValue();
			final String keyStr = (key == null) ? "" : key.toString().trim();
			final String valueStr = (value == null) ? "" : value.toString().trim();
			formattedProperties.put(keyStr, valueStr);
		}
		return formattedProperties;
	}

	private static Collection<LintPattern> loadPatterns(final Map<String, String> properties)
	{
		final Map<String, LintPattern> patterns = new HashMap<>();

		//Search for enabled patterns
		final Set<String> patternNames = new HashSet<>();
		for (final Entry<String, String> entry : properties.entrySet())
		{
			final String key = entry.getKey();
			final String value = entry.getValue();

			final LintPattern pattern = parseLintPattern(key);
			if (pattern != null)
			{
				patternNames.add(key);
				if ("on".equalsIgnoreCase(value))
					patterns.put(key, pattern);
			}
			else
				if ("on".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value))
				{
					patternNames.add(key);
					LOGGER.severe("Cannot find and construct pattern " + key);
				}
		}

		for (final String patternName : patternNames)
			properties.remove(patternName);

		//Search and set parameters for patterns
		for (final Entry<String, String> entry : properties.entrySet())
		{
			final String key = entry.getKey();
			final String value = entry.getValue();
			final int lastDot = key.lastIndexOf('.');
			if (lastDot < 0 || lastDot > key.length())
			{
				LOGGER.severe("Cannot find field name " + key);
				continue;
			}

			final String className = key.substring(0, lastDot);
			final String fieldName = key.substring(lastDot + 1);
			if (!patternNames.contains(className))
			{
				LOGGER.severe("Cannot find pattern " + className + " to set its parameter " + fieldName);
				continue;
			}

			final LintPattern pattern = patterns.get(className);
			if (pattern != null)
				setParameter(pattern, className, fieldName, value);
		}

		return patterns.values();
	}

	private static LintPattern parseLintPattern(final String str)
	{
		try
		{
			final Class<?> clazz = Class.forName(str);
			final Class<? extends LintPattern> lpClazz = clazz.asSubclass(LintPattern.class);
			final Constructor<? extends LintPattern> ctor = lpClazz.getConstructor();
			return ctor.newInstance();
		}
		catch (final Exception e)
		{
			// No error logging here because properties file have entries for
			// the configuration patterns
			// LOGGER.severe( e );
		}

		return null;
	}

	private static void setParameter(final LintPattern pattern, final String className, final String fieldName, final String value)
	{
		final String setter = "set" + fieldName;
		final Class<? extends LintPattern> clazz = pattern.getClass();

		try
		{
			final Method method = clazz.getMethod(setter, int.class);
			try
			{
				final int intValue = Integer.parseInt(value);
				method.invoke(pattern, intValue);
				return;
			}
			catch (final NumberFormatException e)
			{
				LOGGER.log(Level.FINE, value + " is not an integer", e);
			}
			catch (final IllegalArgumentException e)
			{
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			}
			catch (final IllegalAccessException e)
			{
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			}
			catch (final InvocationTargetException e)
			{
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			}
		}
		catch (final SecurityException e)
		{
			LOGGER.log(Level.FINE, "Error accessing method " + setter + "(int) on lint pattern " + className, e);
		}
		catch (final NoSuchMethodException e)
		{
			LOGGER.log(Level.FINE, "Method " + setter + "(int) not found on lint pattern " + className, e);
		}

		try
		{
			final Method method = clazz.getMethod(setter, String.class);
			try
			{
				method.invoke(pattern, value);
				return;
			}
			catch (final IllegalArgumentException e)
			{
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			}
			catch (final IllegalAccessException e)
			{
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			}
			catch (final InvocationTargetException e)
			{
				LOGGER.log(Level.FINE, "Error invoking method " + method + " with parameter " + value, e);
			}
		}
		catch (final SecurityException e)
		{
			LOGGER.log(Level.FINE, "Error accessing method " + setter + "(String) on lint pattern " + className, e);
		}
		catch (final NoSuchMethodException e)
		{
			LOGGER.log(Level.FINE, "Method " + setter + "(String) not found on lint pattern " + className, e);
		}

		LOGGER.severe("Cannot set paramater " + fieldName + "=" + value + " for lint pattern " + className);
	}
}
