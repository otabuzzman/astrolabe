
package astrolabe;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import com.vividsolutions.jts.algorithm.MinimumDiameter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

@SuppressWarnings("serial")
public class Artwork extends astrolabe.model.Artwork implements PostscriptEmitter {

	private static boolean verbose = false ;

	// configuration key (CK_), node (CN_)
	private final static String CK_PSUNIT			= "psunit" ;
	private final static String CK_DPI				= "dpi" ;
	private final static String CK_INTERVAL			= "interval" ;
	private final static String CK_IMAGEOP			= "imageop" ;
	private final static String CK_NOIMAGE			= "noimage" ;
	private final static String CK_MINSECTION		= "minsection" ;
	private final static String CK_ALPHA			= "alpha" ;

	private final static double DEFAULT_PSUNIT		= 2.834646 ;
	private final static double DEFAULT_DPI			= 72 ;
	private final static boolean DEFAULT_IMAGEOP	= false ;
	private final static boolean DEFAULT_NOIMAGE	= false ;
	private final static double DEFAULT_MINSECTION	= 25 ;
	private final static int DEFAULT_INTERVAL		= 1 ;
	private final static String DEFAULT_ALPHA		= "0:0:0,0:0:0" ;

	private Projector projector ;

	public Artwork( Projector projector ) {
		this.projector = projector ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		InputStream input ;
		BufferedImage source, image ;
		int sdimx, sdimy, idimx, idimy, x, y ;
		Coordinate popC[], popH[], zH, popI[] ;
		RealMatrix T, H, TH, HT = null ;
		RealMatrix C, TC, CT = null ;
		RealMatrix M, P, MP ;
		Geometry p, mbr ;
		Coordinate[] pc, mbrc ;
		Geometry fov, fot ;
		double minsection ;
		ChartPage page ;
		double u, d, r, interval ;
		double maxs, maxt ;
		String av[] ;
		int a0, a1, a2, a ;
		Coordinate c, eq, xyz ;
		double[] xy, uv ;
		Plane texture = null ;
		Vector3D O = null, V, X ;
		Vector2D A, B, R ;
		PostscriptEmitter artwork ;

		try {
			input = reader() ;
			source = ImageIO.read( input ) ;

			sdimx = source.getWidth() ;
			sdimy = source.getHeight() ;
		} catch ( URISyntaxException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( MalformedURLException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		u = Configuration.getValue( this, CK_PSUNIT, DEFAULT_PSUNIT ) ;
		d = Configuration.getValue( this, CK_DPI, DEFAULT_DPI) ;
		d = 36 ;
		interval = 72/( u*d ) ;

		popC = prepPopper4Canvas() ;
		popI = prepPopperOfImage() ;

		if ( getHeaven() ) {
			popH = prepPopper4Heaven() ;

			texture = new Plane(
					new Vector3D( popH[0].x, popH[0].y, popH[0].z ),
					new Vector3D( popH[1].x, popH[1].y, popH[1].z ),
					new Vector3D( popH[2].x, popH[2].y, popH[2].z )
			) ;
			O = new Vector3D( 0, 0, 0 ) ;

			zH = calcVectorZ( popH ) ;
			H = MatrixUtils
			.createRealMatrix( new double[][] {
					{ popH[0].x, popH[0].y, popH[0].z, 1 },
					{ popH[1].x, popH[1].y, popH[1].z, 1 },
					{ popH[2].x, popH[2].y, popH[2].z, 1 },
					{ zH.x, zH.y, zH.z, 1 }
			} )
			.transpose() ;
			T = MatrixUtils
			.createRealMatrix( new double[][] {
					{ popI[0].x, -1+sdimy-popI[0].y, 0, 1 },
					{ popI[1].x, -1+sdimy-popI[1].y, 0, 1 },
					{ popI[2].x, -1+sdimy-popI[2].y, 0, 1 },
					{ popI[0].x, -1+sdimy-popI[0].y, -1+sdimx, 1 }
			} )
			.transpose() ;

			TH = H.multiply( new LUDecomposition( T ).getSolver().getInverse() ) ;
			HT = new LUDecomposition( TH ).getSolver().getInverse() ;

			pc = calcP4Heaven( TH, sdimx, sdimy ) ;
		} else {
			C = MatrixUtils
			.createRealMatrix( new double[][] {
					{ popC[0].x, popC[0].y, 0, 1 },
					{ popC[1].x, popC[1].y, 0, 1 },
					{ popC[2].x, popC[2].y, 0, 1 },
					{ 0, 0, 1, 1 }
			} )
			.transpose() ;
			T = MatrixUtils
			.createRealMatrix( new double[][] {
					{ popI[0].x, -1+sdimy-popI[0].y, 0, 1 },
					{ popI[1].x, -1+sdimy-popI[1].y, 0, 1 },
					{ popI[2].x, -1+sdimy-popI[2].y, 0, 1 },
					{ 0, 0, 1, 1 }
			} )
			.transpose() ;

			TC = C.multiply( new LUDecomposition( T ).getSolver().getInverse() ) ;
			CT = new LUDecomposition( TC ).getSolver().getInverse() ;

			pc = calcP4Canvas( TC, sdimx, sdimy ) ;
		}

		mbrc = new MinimumDiameter( new GeometryFactory().createLinearRing( pc ) )
		.getMinimumRectangle()
		.getCoordinates() ;
		mbr = new GeometryFactory().createPolygon( mbrc ) ;
		maxs = mbrc[0].distance( mbrc[1] ) ;
		maxt = mbrc[0].distance( mbrc[3] ) ;

		fov = (Geometry) Registry.retrieve( Geometry.class.getName() ) ;
		if ( fov == null ) {
			page = (ChartPage) Registry.retrieve( ChartPage.class.getName() ) ;
			if ( page != null )
				fov = page.getViewGeometry() ;
		}

		if ( fov != null && ! fov.intersects( mbr ) )
			return ;
		if ( fov.contains( mbr ) )
			fot = mbr ;
		else {
			fot = fov.intersection( mbr ) ;

			minsection = Configuration.getValue( this, CK_MINSECTION, DEFAULT_MINSECTION )/100 ;
			p = new GeometryFactory().createPolygon( pc ) ;
			if ( minsection>fot.getArea()/p.getArea() )
				return ;

			mbrc = new MinimumDiameter( new GeometryFactory().createLinearRing( fot.getCoordinates() ) )
			.getMinimumRectangle()
			.getCoordinates() ;
			maxs = mbrc[0].distance( mbrc[1] ) ;
			maxt = mbrc[0].distance( mbrc[3] ) ;
		}

		P = MatrixUtils
		.createRealMatrix( new double[][] {
				{ mbrc[1].x, mbrc[1].y, 1 },
				{ mbrc[3].x, mbrc[3].y, 1 },
				{ mbrc[0].x, mbrc[0].y, 1 }
		} )
		.transpose() ;
		M = MatrixUtils
		.createRealMatrix( new double[][] {
				{ maxs, 0, 1 },
				{ 0, maxt, 1 },
				{ 0, 0, 1 }
		} )
		.transpose() ;

		MP = P.multiply( new LUDecomposition( M ).getSolver().getInverse() ) ;

		if ( verbose ) {
			ps.op( "gsave" ) ;

			ps.push( 0 ) ;
			ps.op( "setgray" ) ;
			linePS( ps, popC ) ;
			ps.push( 1 ) ;
			ps.push( 0 ) ;
			ps.push( 0 ) ;
			ps.op( "setrgbcolor" ) ;
			linePS( ps, pc ) ;
			ps.push( 0 ) ;
			ps.push( 1 ) ;
			ps.push( 0 ) ;
			ps.op( "setrgbcolor" ) ;
			linePS( ps, mbrc ) ;
			ps.push( 0 ) ;
			ps.push( 0 ) ;
			ps.push( 1 ) ;
			ps.op( "setrgbcolor" ) ;
			linePS( ps, fot.getCoordinates() ) ;

			ps.push( 0 ) ;
			ps.op( "setgray" ) ;
			linePS( ps, new Coordinate[] { new Coordinate( 0, 0 ), mbrc[0] }  ) ;
			ps.push( .4 ) ;
			ps.op( "setgray" ) ;
			linePS( ps, new Coordinate[] { new Coordinate( 0, 0 ), mbrc[1] }  ) ;
			ps.push( .8 ) ;
			ps.op( "setgray" ) ;
			linePS( ps, new Coordinate[] { new Coordinate( 0, 0 ), mbrc[2] }  ) ;

			ps.push( 0 ) ;
			ps.push( 1 ) ;
			ps.push( 1 ) ;
			ps.op( "setrgbcolor" ) ;
			ps.push( 0 ) ;
			ps.push( 0 ) ;
			ps.op( "moveto" ) ;
			ps.push( maxs ) ;
			ps.push( maxt ) ;
			ps.op( "lineto" ) ;
			ps.op( "stroke" ) ;

			ps.op( "grestore" ) ;
		}

		if ( Configuration.getValue( this, CK_NOIMAGE, DEFAULT_NOIMAGE ) )
			return ;

		idimx = (int) ( maxs/interval+1 ) ;
		idimy = (int) ( maxt/interval+1 ) ;
		image = new BufferedImage( idimx, idimy, BufferedImage.TYPE_INT_ARGB ) ;

		av = Configuration.getValue( this, CK_ALPHA, DEFAULT_ALPHA )
		.split( "," )[0].split( ":" ) ;
		a0 = (int) ( Double.parseDouble( av[0] )*255 )&0xff ;
		a1 = (int) ( Double.parseDouble( av[1] )*255 )&0xff ;
		a2 = (int) ( Double.parseDouble( av[2] )*255 )&0xff ;
		a = a0<<16|a1<<8|a2 ;

		x = 0 ;
		y = idimy-1 ;

		for ( double t=0 ; maxt>t ; t+=interval ) {
			for ( double s=0 ; maxs>s ; s+=interval ) {
				xy = MP.operate( new double[] { s, t, 1 } ) ;
				c = new Coordinate( xy[0], xy[1] ) ;

				image.setRGB( x, y, a ) ;

				if ( fot.covers( new GeometryFactory().createPoint( c ) ) ) {
					if ( getHeaven() ) {
						eq = projector.project( c, true ) ;
						xyz = projector.cartesian( eq, false ) ;

						V = new Vector3D( xyz.x, xyz.y, xyz.z ) ;
						X = texture.intersection( new Line( O, V ) ) ;

						uv = HT.operate( new double[] { X.getX(), X.getY(), X.getZ(), 1 } ) ;
					} else
						uv = CT.operate( new double[] { xy[0], xy[1], 0, 1 } ) ;

					if ( uv[0]>=0 && uv[1]>=0 && sdimx>uv[0] && sdimy>uv[1] )
						image.setRGB( x, y, source.getRGB( (int) uv[0], (int) -uv[1]+sdimy-1 ) ) ;
				}

				x = ( x+1 )%idimx ;
			}

			y-- ;
		}

		A = new Vector2D( mbrc[0].x, mbrc[0].y ) ;
		B = new Vector2D( mbrc[1].x, mbrc[1].y ) ;
		R = B.subtract( A ) ;
		r = Math.atan2( R.getY(), R.getX() ) ;

		ps.push( mbrc[0].x ) ;
		ps.push( mbrc[0].y ) ;
		ps.op( "translate" ) ;
		ps.push( r ) ;
		ps.op( "rotate" ) ;

		if ( Configuration.getValue( this, CK_IMAGEOP, DEFAULT_IMAGEOP ) ) {
			artwork = new ImageOperator( image ) ;

			ps.push( maxs ) ;
			ps.push( maxt ) ;
		} else {
			artwork = new ImageDiscrete( image ) ;

			ps.push( interval ) ;
			ps.push( interval ) ;
		}
		ps.op( "scale" ) ;

		ps.op( "gsave" ) ;

		artwork.headPS( ps ) ;
		artwork.emitPS( ps ) ;
		artwork.tailPS( ps ) ;			

		ps.op( "grestore" ) ;
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
	}

	public static boolean verbose() {
		return ! ( verbose = ! verbose ) ;
	}

	private InputStream reader() throws URISyntaxException, MalformedURLException {
		URI uri ;
		URL url ;
		File file ;
		InputStream in ;
		GZIPInputStream gz ;

		uri = new URI( getUrl() ) ;
		if ( uri.isAbsolute() ) {
			file = new File( uri ) ;	
		} else {
			file = new File( uri.getPath() ) ;
		}
		url = file.toURL() ;

		try {
			in = url.openStream() ;

			gz = new GZIPInputStream( in ) ;
			return new BufferedInputStream( gz ) ;
		} catch ( IOException egz ) {
			try {
				in = url.openStream() ;

				return new BufferedInputStream( in ) ;
			} catch ( IOException ein ) {
				throw new RuntimeException ( egz.toString() ) ;
			}
		}
	}

	private Coordinate[] prepPopper4Heaven() {
		return new Coordinate[] {
				projector.cartesian( valueOf( getPopper( 0 ).getPosition() ), false ),
				projector.cartesian( valueOf( getPopper( 1 ).getPosition() ), false ),
				projector.cartesian( valueOf( getPopper( 2 ).getPosition() ), false )
		} ;
	}

	private Coordinate[] prepPopper4Canvas() {
		return new Coordinate[] {
				projector.project( valueOf( getPopper( 0 ).getPosition() ), false ),
				projector.project( valueOf( getPopper( 1 ).getPosition() ), false ),
				projector.project( valueOf( getPopper( 2 ).getPosition() ), false )
		} ;
	}

	private Coordinate[] prepPopperOfImage() {
		return new Coordinate[] {
				valueOf( getPopper( 0 ).getCartesian() ),
				valueOf( getPopper( 1 ).getCartesian() ),
				valueOf( getPopper( 2 ).getCartesian() )
		} ;
	}

	private Coordinate calcVectorZ( Coordinate[] popper ) {
		Vector v0, va, vb, vz ;

		v0 = new Vector( popper[0] ) ;
		va = new Vector( popper[1] ).sub( v0 ) ;
		vb = new Vector( popper[2] ).sub( v0 ) ;
		vz = v0.add( new Vector( va ).cross( vb ) ) ;

		return vz.toCoordinate() ;
	}

	private Coordinate[] calcP4Heaven( RealMatrix TH, int dimx, int dimy ) {
		java.util.Vector<Coordinate> r = new java.util.Vector<Coordinate>() ;
		double c[], m, n ;
		Vector v0, v1, v2, va, vb ;
		int dx, dy, pref, interval ;

		dx = -1+dimx ;
		dy = -1+dimy ;

		pref = Configuration.getValue( this, CK_INTERVAL, DEFAULT_INTERVAL ) ;

		// vector AB
		c = TH.operate( new double[] { 0, 0, 0, 1 } ) ;
		v0 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		c = TH.operate( new double[] { dx/2, 0, 0, 1 } ) ;
		v1 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		c = TH.operate( new double[] { dx, 0, 0, 1 } ) ;
		v2 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;

		va = new Vector( v1 ).sub( v0 ) ;
		vb = new Vector( v2 ).sub( v1 ) ;

		m = new Vector( va ).dot( vb ) ;
		n = va.abs()*vb.abs() ;

		interval = pref ;
		if ( (int) m-(int) n == 0 )
			interval = dx ;

		r.add( v0 ) ;
		for ( int x=interval ; dx-1>x ; x+=interval ) {
			c = TH.operate( new double[] { x, 0, 0, 1 } ) ;
			r.add( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		}

		// vector BC
		c = TH.operate( new double[] { dx, 0, 0, 1 } ) ;
		v0 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		c = TH.operate( new double[] { dx, dy/2, 0, 1 } ) ;
		v1 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		c = TH.operate( new double[] { dx, dy, 0, 1 } ) ;
		v2 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;

		va = new Vector( v1 ).sub( v0 ) ;
		vb = new Vector( v2 ).sub( v1 ) ;

		m = new Vector( va ).dot( vb ) ;
		n = va.abs()*vb.abs() ;

		interval = pref ;
		if ( (int) m-(int) n == 0 )
			interval = dy ;

		r.add( v0 ) ;
		for ( int y=interval ; dy-1>y ; y+=interval ) {
			c = TH.operate( new double[] { dx, y, 0, 1 } ) ;
			r.add( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		}

		// vector CD
		c = TH.operate( new double[] { dx, dy, 0, 1 } ) ;
		v0 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		c = TH.operate( new double[] { dx/2, dy, 0, 1 } ) ;
		v1 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		c = TH.operate( new double[] { 0, dy, 0, 1 } ) ;
		v2 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;

		va = new Vector( v1 ).sub( v0 ) ;
		vb = new Vector( v2 ).sub( v1 ) ;

		m = new Vector( va ).dot( vb ) ;
		n = va.abs()*vb.abs() ;

		interval = pref ;
		if ( (int) m-(int) n == 0 )
			interval = dx ;

		r.add( v0 ) ;
		for ( int x=dx-interval ; x+1>interval ; x-=interval ) {
			c = TH.operate( new double[] { x, dy, 0, 1 } ) ;
			r.add( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		}

		// vector DA
		c = TH.operate( new double[] { 0, dy, 0, 1 } ) ;
		v0 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		c = TH.operate( new double[] { 0, dy/2, 0, 1 } ) ;
		v1 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		c = TH.operate( new double[] { 0, 0, 0, 1 } ) ;
		v2 = new Vector( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;

		va = new Vector( v1 ).sub( v0 ) ;
		vb = new Vector( v2 ).sub( v1 ) ;

		m = new Vector( va ).dot( vb ) ;
		n = va.abs()*vb.abs() ;

		interval = pref ;
		if ( (int) m-(int) n == 0 )
			interval = dy ;

		r.add( v0 ) ;
		for ( int y=dy-interval ; y+1>interval ; y-=interval ) {
			c = TH.operate( new double[] { 0, y, 0, 1 } ) ;
			r.add( projector.project( projector.cartesian( new Coordinate( c[0], c[1], c[2] ), true ), false ) ) ;
		}

		r.add( r.firstElement() ) ;

		return r.toArray( new Coordinate[0] ) ;
	}

	private Coordinate[] calcP4Canvas( RealMatrix TC, int dimx, int dimy ) {
		Coordinate[] r = new Coordinate[5] ;
		double[] c ;
		int dx, dy ;

		dx = -1+dimx ;
		dy = -1+dimy ;

		c = TC.operate( new double[] { 0, 0, 0, 1 } ) ;
		r[0] = new Coordinate( c[0], c[1] ) ;
		c = TC.operate( new double[] { dx, 0, 0, 1 } ) ;
		r[1] = new Coordinate( c[0], c[1] ) ;
		c = TC.operate( new double[] { dx, dy, 0, 1 } ) ;
		r[2] = new Coordinate( c[0], c[1] ) ;
		c = TC.operate( new double[] { 0, dy, 0, 1 } ) ;
		r[3] = new Coordinate( c[0], c[1] ) ;
		r[4] = r[0] ;

		return r ;
	}

	private void linePS( ApplicationPostscriptStream ps, Coordinate[] list ) {
		ps.array( true ) ;
		for ( Coordinate c : list ) {
			ps.push( c.x ) ;
			ps.push( c.y ) ;
		}
		ps.array( false ) ;

		ps.op( "newpath" ) ;
		ps.op( "gdraw" ) ;

		ps.op( "stroke" ) ;
	}
}
