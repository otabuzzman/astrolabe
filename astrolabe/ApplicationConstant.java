
package astrolabe;

public final class ApplicationConstant {

	private ApplicationConstant() {
	}

	// general constants (GC_), patterns (GP_)
	public final static String GC_APPLICATION	= "astrolabe" ;		// application name
	public final static String GC_NATLIB_CAA	= "cygcaa-1.17" ;		// native library
	public final static String GC_NS_CAT		= "cat:" ;				// name space, application generated
	public final static String GC_NS_CUT		= "cut:" ;				// name space, application generated
	public final static String GC_NS_REG		= "reg:" ;				// name space, application generated
	public final static String GC_EPOCH			= GC_NS_REG+"epoch" ;	// class global variable, internal usage
	public final static String GC_FOVUNI		= GC_NS_REG+"fovuni" ;	// class global variable, internal usage
	public final static String GC_FOVEFF		= GC_NS_REG+"foveff" ;	// class global variable, internal usage
	public final static String GC_LAYOUT		= GC_NS_REG+"layout" ;	// class global variable, internal usage
	public final static String GC_PARSER		= GC_NS_REG+"parser" ;	// class global variable, internal usage
}
