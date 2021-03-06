package profiler.utils;

// ----------------------------------------------------------------------------
/**
 * A shell pseudo-_node implementation for a non-array class.
 *
 * @author (C) <a href="http://www.javaworld.com/columns/jw-qna-_index.shtml">Vlad Roubtsov</a>, 2003
 */
final class ObjectShellProfileNode extends AbstractShellProfileNode
{
	// public: ................................................................    

	@Override
	public String name()
	{
		return "<shell: " + m_primitiveFieldCount + " prim/" + m_refFieldCount + " ref fields>";
	}

	// protected: .............................................................

	// package: ...............................................................

	ObjectShellProfileNode(final IObjectProfileNode parent, final int primitiveFieldCount, final int refFieldCount)
	{
		super(parent);

		m_primitiveFieldCount = primitiveFieldCount;
		m_refFieldCount = refFieldCount;
	}

	// private: ...............................................................

	private final int m_primitiveFieldCount, m_refFieldCount;

} // _end of class
// ----------------------------------------------------------------------------