
package astrolabe;

public interface Graduation {
	public java.util.Vector<Vector> cartesianList() ;
	public void initPS( PostscriptStream ps ) ;
	public void emitPS( PostscriptStream ps ) ;
}
