
public class X {

	private String v = "X" ;
	
	public void a() {
		b() ;
		c() ;
	}

	public void b() {
		System.out.println( "in X.b(), v="+v+", this.v="+this.v ) ;
	}

	private void c() {
		System.out.println( "in X.c(), v="+v+", this.v="+this.v ) ;
	}
}
