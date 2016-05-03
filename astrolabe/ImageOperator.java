
package astrolabe;

import java.awt.image.BufferedImage;

public class ImageOperator implements PostscriptEmitter {

	// configuration key (CK_), node (CN_)
	private final static String CK_ALPHA			= "alpha" ;
	private final static String CK_TONEMAP			= "tonemap" ;

	private final static String DEFAULT_ALPHA		= "0:0:0,0:0:0" ;
	private final static String DEFAULT_TONEMAP		= "0:0:0,1:1:1" ;

	private BufferedImage image ;

	public ImageOperator( BufferedImage image ) {
		this.image = image ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		String[] av, ava, avo ;
		int aa0, aa1, aa2 ;
		int ao0, ao1, ao2 ;		
		String[] tmv, tmva, tmvo ;
		double tma0, tma1, tma2 ;
		double tmo0, tmo1, tmo2 ;
		ASCII85StringBuilder a85 ;
		int w, h, p, r, g, b ;

		av = Configuration.getValue( this, CK_ALPHA, DEFAULT_ALPHA )
		.split( "," ) ;
		ava = av[0].split( ":" ) ;
		aa0 = (int) ( Double.parseDouble( ava[0] )*255 ) ;
		aa1 = (int) ( Double.parseDouble( ava[1] )*255 ) ;
		aa2 = (int) ( Double.parseDouble( ava[2] )*255 ) ;
		avo = av[1].split( ":" ) ;
		ao0 = (int) ( Double.parseDouble( avo[0] )*255 ) ;
		ao1 = (int) ( Double.parseDouble( avo[1] )*255 ) ;
		ao2 = (int) ( Double.parseDouble( avo[2] )*255 ) ;

		tmv = Configuration.getValue( this, CK_TONEMAP, DEFAULT_TONEMAP )
		.split( "," ) ;
		tmva = tmv[0].split( ":" ) ;
		tma0 = Double.parseDouble( tmva[0] ) ;
		tma1 = Double.parseDouble( tmva[1] ) ;
		tma2 = Double.parseDouble( tmva[2] ) ;
		tmvo = tmv[1].split( ":" ) ;
		tmo0 = Double.parseDouble( tmvo[0] ) ;
		tmo1 = Double.parseDouble( tmvo[1] ) ;
		tmo2 = Double.parseDouble( tmvo[2] ) ;

		w = image.getWidth() ;
		h = image.getHeight() ;

		ps.script( "/DeviceRGB" ) ;
		ps.op( "setcolorspace" ) ;

		ps.dict( true ) ;
		ps.script( "/ImageType" ) ;
		ps.push( 4 ) ;
		ps.script( "/Width" ) ;
		ps.push( w ) ;
		ps.script( "/Height" ) ;
		ps.push( h ) ;
		ps.script( "/ImageMatrix" ) ;
		ps.array( true ) ;
		ps.push( w ) ;
		ps.push( 0 ) ;
		ps.push( 0 ) ;
		ps.push( h ) ;
		ps.push( 0 ) ;
		ps.push( 0 ) ;
		ps.array( false ) ;
		ps.script( "/BitsPerComponent" ) ;
		ps.push( 8 ) ;
		ps.script( "/MaskColor" ) ;
		ps.array( true ) ;
		ps.push( aa0 ) ;
		ps.push( ao0 ) ;
		ps.push( aa1 ) ;
		ps.push( ao1 ) ;
		ps.push( aa2 ) ;
		ps.push( ao2 ) ;
		ps.array( false ) ;
		ps.script( "/Decode" ) ;
		ps.array( true ) ;
		ps.push( tma0 ) ;
		ps.push( tmo0 ) ;
		ps.push( tma1 ) ;
		ps.push( tmo1 ) ;
		ps.push( tma2 ) ;
		ps.push( tmo2 ) ;
		ps.array( false ) ;
		ps.script( "/DataSource" ) ;
		ps.op( "currentfile" ) ;
		ps.script( "/ASCII85Decode" ) ;
		ps.op( "filter" ) ;
		ps.dict( false ) ;

		ps.op( "image" ) ;

		a85 = new ASCII85StringBuilder() ;

		for ( int y=h-1 ; y>=0 ; y-- )
			for ( int x=0 ; w>x ; x++ ) {
				p = image.getRGB( x, y ) ;

				r = ( p>>16 )&0xff ;
				g = ( p>>8 )&0xff ;
				b = p&0xff ;

				a85.append( (byte) ( r ) ) ;
				a85.append( (byte) ( g ) ) ;
				a85.append( (byte) ( b ) ) ;
			}

		a85.finish() ;
		ps.script( a85.toString() ) ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}
}
