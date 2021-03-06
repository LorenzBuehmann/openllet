// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KRSSLoader;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.TimeoutException;
import org.mindswap.pellet.output.TableData;
import org.mindswap.pellet.utils.AlphaNumericComparator;
import org.mindswap.pellet.utils.PatternFilter;
import org.mindswap.pellet.utils.Timer;

/**
 * Parse and test the cases from DL benchmark suite. This class provides parsing for KRSS files.
 *
 * @author Evren Sirin
 */
public class DLBenchmarkTest
{
	public static Logger log = Logger.getLogger(DLBenchmarkTest.class.getName());

	public static boolean PRINT_TIME = false;
	public static boolean PRINT_TREE = false;

	// time limits for different kind of tests
	public static int SAT_LIMIT = 10;
	public static int TBOX_LIMIT = 20;
	public static int ABOX_LIMIT = 50;

	public static boolean FAST = false;
	public static boolean FORCE_UPPERCASE = true;

	private final KRSSLoader loader;
	private KnowledgeBase kb;

	public DLBenchmarkTest()
	{
		loader = new KRSSLoader();
		loader.setForceUppercase(FORCE_UPPERCASE);
	}

	public KnowledgeBase getKB()
	{
		return kb;
	}

	public KnowledgeBase initKB(final long timeout)
	{
		final KnowledgeBase kb = new KnowledgeBase();
		kb.setTimeout(timeout * 1000);

		return kb;
	}

	public void doAllTBoxTests(final String dirName)
	{
		doAllTBoxTests(dirName, new PatternFilter("*.akb"));
	}

	public void doAllTBoxTests(final String dirName, final FileFilter filter)
	{
		final File dir = new File(dirName);
		final File[] files = dir.listFiles(filter);
		Arrays.sort(files, AlphaNumericComparator.CASE_INSENSITIVE);

		final TableData table = new TableData(Arrays.asList(new String[] { "Name", "Size", "Time" }));
		for (int i = 0; i < files.length; i++)
		{
			System.out.print((i + 1) + ") ");

			final List data = new ArrayList();
			data.add(files[i]);
			try
			{
				doTBoxTest(files[i].toString());
				data.add(Integer.valueOf(kb.getClasses().size()));
				data.add(kb.timers.getTimer("test").getTotal() + "");
			}
			catch (final TimeoutException e)
			{
				System.out.println(" ** Timeout: " + e.getMessage() + " ** ");
			}
			catch (final Exception e)
			{
				e.printStackTrace(System.err);
				System.out.println();
			}
			catch (final OutOfMemoryError e)
			{
				System.out.println(" ** Out of Memory ** ");
			}
			catch (final StackOverflowError e)
			{
				System.out.println(" ** Stack Overflow ** ");
			}
			catch (final Error e)
			{
				e.printStackTrace(System.err);
			}
			table.add(data);
		}

		System.out.print(table);
	}

	public boolean doTBoxTest(String file) throws Exception
	{
		String ext = ".tkb";
		int index = file.lastIndexOf('.');
		if (index != -1)
		{
			ext = file.substring(index);
			file = file.substring(0, index);
		}
		index = file.lastIndexOf(File.separator);
		final String displayName = (index == -1) ? file : file.substring(index + 1);

		if (log.isLoggable(Level.INFO))
			System.out.print(displayName + " ");

		loader.clear();
		loader.getKB().timers.resetAll();
		kb = loader.createKB(file + ext);
		kb.setTimeout(TBOX_LIMIT * 1000);

		final Timer t = kb.timers.startTimer("test");

		if (log.isLoggable(Level.INFO))
			System.out.print("preparing...");

		kb.prepare();

		if (log.isLoggable(Level.INFO))
			System.out.print("classifying...");

		kb.classify();

		t.stop();

		if (PRINT_TREE)
			kb.printClassTree();

		if (log.isLoggable(Level.INFO))
			System.out.print("verifying...");

		loader.verifyTBox(file + ".tree", kb);

		if (log.isLoggable(Level.INFO))
			System.out.print("done");

		if (log.isLoggable(Level.INFO))
		{
			System.out.print(" Prepare " + kb.timers.getTimer("preprocessing").getTotal());
			System.out.print(" Classify " + kb.timers.getTimer("classify").getTotal());

			System.out.println(" " + t.getTotal());
		}

		if (PRINT_TIME)
			kb.timers.print();

		return true;
	}

	public void doAllSatTests(final String dirName)
	{
		final File dir = new File(dirName);
		final String[] files = dir.list();

		for (int i = 0; i < files.length; i++)
		{
			System.out.print((i + 1) + ") " + files[i] + " ");

			try
			{
				final int count = doSatTest(dirName + files[i]);
				System.out.println(count);
			}
			catch (final TimeoutException e)
			{
				System.out.println(" ** Timeout ** ");
				System.out.println();
			}
			catch (final Exception e)
			{
				e.printStackTrace(System.err);
				System.out.println();
			}
			catch (final OutOfMemoryError e)
			{
				System.out.println(" ** Out of Memory ** ");
				System.out.println();
			}
			catch (final Error e)
			{
				e.printStackTrace(System.err);
			}
		}
	}

	public int doSatTest(final String file)
	{
		final int count = 0;

		System.err.println("Sat test currently disabled!");

		//		final StreamTokenizer in = initTokenizer(file);
		//		
		//		final boolean result = file.endsWith("_n.alc");
		//
		//		for(; count < 21; count ++) {			
		//			kb = initKB(SAT_LIMIT);
		//			
		//
		//			ATermAppl c = parseExpr(in);
		//
		//			long time = System.currentTimeMillis();
		//			boolean sat = kb.isSatisfiable(c);	
		//			time = System.currentTimeMillis() - time;
		//			
		//			if(sat != result)
		//			    throw new RuntimeException("Consistency error");
		//			else
		//			    System.out.print( "(" + (count+1) + ":" + time + ")" );
		//		}	

		return count;
	}

	public boolean doABoxTest(String file) throws Exception
	{
		String ext = ".tkb";
		int index = file.lastIndexOf('.');
		if (index != -1)
		{
			ext = file.substring(index);
			file = file.substring(0, index);
		}
		index = file.lastIndexOf(File.separator);
		final String displayName = (index == -1) ? file : file.substring(index + 1);
		System.out.print(displayName + " ");

		kb = loader.createKB(file + ext);
		kb.timers.resetAll();
		kb.setTimeout(ABOX_LIMIT * 1000);

		final Timer t = kb.timers.startTimer("test");

		System.out.print("preparing...");

		kb.prepare();

		if (!FAST)
		{
			System.out.print("classifying...");
			kb.realize();
		}

		t.stop();

		System.out.print("verifying...");
		loader.verifyABox(file + ".query", kb);

		System.out.print("done");

		System.out.print(" Prepare " + kb.timers.getTimer("preprocessing").getTotal());
		System.out.print(" Classify " + kb.timers.getTimer("classify").getTotal());

		System.out.println(" " + t.getTotal());

		if (PRINT_TIME)
			kb.timers.print();

		return true;
	}

	public void doAllABoxTests(final String dirName)
	{
		doAllABoxTests(dirName, "*.akb");
	}

	public void doAllABoxTests(final String dirName, final String pattern)
	{
		final File dir = new File(dirName);
		final File[] files = dir.listFiles(new PatternFilter(pattern));
		Arrays.sort(files, AlphaNumericComparator.CASE_INSENSITIVE);

		for (int i = 0; i < files.length; i++)
		{
			System.out.print((i + 1) + ") ");
			try
			{
				doABoxTest(files[i].getAbsolutePath());
			}
			catch (final TimeoutException e)
			{
				System.out.println(" ***** Timeout ***** ");
				System.out.println();
			}
			catch (final Exception e)
			{
				e.printStackTrace(System.err);
				System.out.println();
			}
			catch (final OutOfMemoryError e)
			{
				System.out.println(" ***** Out of Memory ***** ");
				System.out.println();
			}
			catch (final Error e)
			{
				e.printStackTrace(System.err);
			}
		}
	}

	public static void usage()
	{
		System.out.println("DLTest - Run the tests in DL-benchmark suite");
		System.out.println("");
		System.out.println("Usage: java DLTest [-timing] <input> <type>");
		System.out.println("   input    A single file or a directory that contains");
		System.out.println("            a set of test files");
		System.out.println("   type     Type of the test, one of [sat, _tbox, _abox]");
	}

	public final static void main(final String[] args) throws Exception
	{
		if (args.length == 0)
		{
			usage();
			return;
		}

		int base = 0;
		if (args[0].equals("-timing"))
		{
			DLBenchmarkTest.PRINT_TIME = true;
			base = 1;
		}
		else
			if (args.length != 2)
			{
				System.out.println("Invalid arguments");
				usage();
				return;
			}

		final String in = args[base + 0];
		final String type = args[base + 1];

		final File file = new File(in);

		if (!file.exists())
			throw new FileNotFoundException(file + " does not exist!");

		final boolean singleTest = file.isFile();

		final DLBenchmarkTest test = new DLBenchmarkTest();
		if (type.equals("sat"))
		{
			if (singleTest)
				test.doSatTest(in);
			else
				test.doAllSatTests(in);
		}
		else
			if (type.equals("_tbox"))
			{
				if (singleTest)
					test.doTBoxTest(in);
				else
					test.doAllTBoxTests(in);
			}
			else
				if (type.equals("_abox"))
				{
					if (singleTest)
						test.doABoxTest(in);
					else
						test.doAllABoxTests(in);
				}
				else
					usage();
	}
}
