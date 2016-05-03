
package astrolabe;

import java.awt.image.BufferedImage;

public class ImageDiscrete implements PostscriptEmitter {

	// configuration key (CK_), node (CN_)
	private final static String CK_ALPHA			= "alpha" ;
	private final static String CK_TONEMAP			= "tonemap" ;

	private final static String DEFAULT_ALPHA		= "0:0:0,0:0:0" ;
	private final static String DEFAULT_TONEMAP		= "0:0:0,1:1:1" ;

	private BufferedImage image ;

	public ImageDiscrete( BufferedImage image ) {
		this.image = image ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		String[] av ;
		int a0, a1, a2, a ;
		String[] tmv, tmva, tmvo ;
		double tma0, tma1, tma2 ;
		double tmo0, tmo1, tmo2 ;
		double tmd0, tmd1, tmd2 ;
		int w, h, p ;
		double r, g, b ;

		av = Configuration.getValue( this, CK_ALPHA, DEFAULT_ALPHA )
		.split( "," )[0].split( ":" ) ;
		a0 = (int) ( Double.parseDouble( av[0] )*255 )&0xff ;
		a1 = (int) ( Double.parseDouble( av[1] )*255 )&0xff ;
		a2 = (int) ( Double.parseDouble( av[2] )*255 )&0xff ;
		a = a0<<16|a1<<8|a2 ;

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
		tmd0 = tmo0-tma0 ;
		tmd1 = tmo1-tma1 ;
		tmd2 = tmo2-tma2 ;

		w = image.getWidth() ;
		h = image.getHeight() ;

		for ( int y=0 ; h>y ; y++ )
			for ( int x=0 ; w>x ; x++ ) {
				p = image.getRGB( x, -y+h-1 )&0xffffff ;

				if ( p == a )
					continue ;

				r = tma0+( ( p>>16)&0xff )*tmd0/255 ;
				g = tma1+( ( p>>8)&0xff )*tmd1/255 ;
				b = tma2+( p&0xff )*tmd2/255 ;

				ps.op( "gsave" ) ;
				ps.op( "currentpoint" ) ;
				ps.op( "translate" ) ;
				ps.push( r ) ;
				ps.push( g ) ;
				ps.push( b ) ;
				ps.op( "setrgbcolor" ) ;
				ps.op( "upix" ) ;
				ps.op( "grestore" ) ;

				ps.push( x ) ;
				ps.push( y ) ;
				ps.op( "moveto" ) ;
			}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}
}
