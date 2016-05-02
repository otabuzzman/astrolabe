
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
	private final static String CK_PSUNIT		= "psunit" ;
	private final static String CK_DPI			= "dpi" ;
	private final static String CK_INTERVAL		= "interval" ;
	private final static String CK_ALPHA		= "alpha" ;
	private final static String CN_DEF			= "def" ;
	private final static String CK_UDOT			= "udot" ;
	private final static String CK_UPIX			= "upix" ;


	private final static double DEFAULT_PSUNIT	= 2.834646 ;
	private final static double DEFAULT_DPI		= 72 ;
	private final static int DEFAULT_INTERVAL	= 1 ;
	private final static String DEFAULT_ALPHA	= "0:0:0" ;
	private final static String DEFAULT_UDOT	= "{gsave currentpoint translate 0 0 .5 0 359 .5 0 moveto arc closepath fill grestore}" ;
	private final static String DEFAULT_UPIX	= "{gsave currentpoint translate -.5 .5 .5 .5 .5 -.5 -.5 -.5 moveto lineto lineto lineto closepath fill grestore}" ;
	private final static String DEFAULT_TONEMAP	= "0:0:0,255:255:255" ;

	private Projector projector ;

	private int[] image ;
	private int dimx ;
	private int dimy ;

	private int alpha ;

	public Artwork( Projector projector ) {
		this.projector = projector ;
	}

	public void headPS( ApplicationPostscriptStream ps ) {
		String def ;

		def = Configuration.getValue( this, CN_DEF+"/"+CK_UDOT, DEFAULT_UDOT ) ;
		ps.push( "/"+CK_UDOT ) ;
		for ( String token : def.trim().split( "\\p{Space}+" ) )
			ps.push( token ) ;
		ps.operator.def() ;
		def = Configuration.getValue( this, CN_DEF+"/"+CK_UPIX, DEFAULT_UPIX ) ;
		ps.push( "/"+CK_UPIX ) ;
		for ( String token : def.trim().split( "\\p{Space}+" ) )
			ps.push( token ) ;
		ps.operator.def() ;
	}

	public void emitPS( ApplicationPostscriptStream ps ) {
		InputStream input ;
		BufferedImage img ;
		String[] av, tmv, tma, tmo ;
		int tonemap[], rgb ;
		Coordinate popC[], popH[], zH, popI[] ;
		RealMatrix T, H, TH, HT = null ;
		RealMatrix C, TC, CT = null ;
		RealMatrix M, P, MP ;
		Geometry p, mbr ;
		Coordinate[] pc, mbrc ;
		Geometry fov, fot ;
		ChartPage page ;
		double u, d, interval ;
		double maxs, maxt ;
		Coordinate c, eq, xyz ;
		double[] xy, uv ;
		Plane texture = null ;
		Vector3D A = null, B, X ;

		try {
			input = reader() ;
			img = ImageIO.read( input ) ;

			dimx = img.getWidth() ;
			dimy = img.getHeight() ;
			image = new int[ dimx*dimy ] ;

			img.getRGB( 0, 0, dimx, dimy, image, 0, dimx ) ;
		} catch ( URISyntaxException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( MalformedURLException e ) {
			throw new RuntimeException( e.toString() ) ;
		} catch ( IOException e ) {
			throw new RuntimeException( e.toString() ) ;
		}

		u = Configuration.getValue( this, CK_PSUNIT, DEFAULT_PSUNIT ) ;
		d = Configuration.getValue( this, CK_DPI, DEFAULT_DPI) ;
		interval = 72/( u*d ) ;

		av = Configuration.getValue( this, CK_ALPHA, DEFAULT_ALPHA )
		.split( ":" ) ;
		alpha = Integer.parseInt( av[0] )%256<<16
		|Integer.parseInt( av[1] )%256<<8
		|Integer.parseInt( av[2] )%256 ;

		tmv = Configuration.getValue( this, getTonemap(), DEFAULT_TONEMAP )
		.split( "," ) ;
		tma = tmv[0].split( ":" ) ;
		tmo = tmv[1].split( ":" ) ;
		tonemap = new int[] {
				Integer.parseInt( tma[0] )%256<<16
				|Integer.parseInt( tma[1] )%256<<8
				|Integer.parseInt( tma[2] )%256,
				Integer.parseInt( tmo[0] )%256<<16
				|Integer.parseInt( tmo[1] )%256<<8
				|Integer.parseInt( tmo[2] )%256
		} ;

		applyTM( tonemap ) ;

		popC = prepPopper4Canvas() ;
		popI = prepPopperOfImage() ;

		if ( getHeaven() ) {
			popH = prepPopper4Heaven() ;

			texture = new Plane(
					new Vector3D( popH[0].x, popH[0].y, popH[0].z ),
					new Vector3D( popH[1].x, popH[1].y, popH[1].z ),
					new Vector3D( popH[2].x, popH[2].y, popH[2].z )
			) ;
			A = new Vector3D( 0, 0, 0 ) ;

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
					{ popI[0].x, -1+dimy-popI[0].y, 0, 1 },
					{ popI[1].x, -1+dimy-popI[1].y, 0, 1 },
					{ popI[2].x, -1+dimy-popI[2].y, 0, 1 },
					{ popI[0].x, -1+dimy-popI[0].y, -1+dimx, 1 }
			} )
			.transpose() ;

			TH = H.multiply( new LUDecomposition( T ).getSolver().getInverse() ) ;
			HT = new LUDecomposition( TH ).getSolver().getInverse() ;

			pc = calcP4Heaven( TH, dimx, dimy ) ;
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
					{ popI[0].x, -1+dimy-popI[0].y, 0, 1 },
					{ popI[1].x, -1+dimy-popI[1].y, 0, 1 },
					{ popI[2].x, -1+dimy-popI[2].y, 0, 1 },
					{ 0, 0, 1, 1 }
			} )
			.transpose() ;

			TC = C.multiply( new LUDecomposition( T ).getSolver().getInverse() ) ;
			CT = new LUDecomposition( TC ).getSolver().getInverse() ;

			pc = calcP4Canvas( TC, dimx, dimy ) ;
		}

		mbrc = calcMBR( pc ) ;
		maxs = mbrc[0].distance( mbrc[1] ) ;
		maxt = mbrc[0].distance( mbrc[3] ) ;
		mbr = new GeometryFactory().createPolygon( mbrc ) ;

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

			p = new GeometryFactory().createPolygon( pc ) ;
			if ( .15>fot.getArea()/p.getArea() )
				return ;
		}

		if ( verbose ) {
			linePS( ps, popC ) ;
			ps.operator.setrgbcolor( 1, 0, 0 ) ;
			linePS( ps, pc ) ;
			ps.operator.setrgbcolor( 0, 1, 0 ) ;
			linePS( ps, mbrc ) ;
		}

		for ( double t=interval/2 ; maxt>t ; t+=interval )
			for ( double s=interval/2 ; maxs>s ; s+=interval ) {
				xy = MP.operate( new double[] { s, t, 1 } ) ;
				c = new Coordinate( xy[0], xy[1] ) ;

				if ( ! fot.covers( new GeometryFactory().createPoint( c ) ) )
					continue ;

				if ( getHeaven() ) {
					eq = projector.project( c, true ) ;
					xyz = projector.cartesian( eq, false ) ;

					B = new Vector3D( xyz.x, xyz.y, xyz.z ) ;
					X = texture.intersection( new Line( A, B ) ) ;
					uv = HT.operate( new double[] { X.getX(), X.getY(), X.getZ(), 1 } ) ;
				} else
					uv = CT.operate( new double[] { xy[0], xy[1], 0, 1 } ) ;

				if ( 0>uv[0] || 0>uv[1] || uv[0]>=dimx || uv[1]>=dimy )
					continue ;

				rgb = calcRGB( uv ) ;

				if ( rgb == alpha )
					continue ;

				ps.operator.setrgbcolor(
						( rgb>>16&0xff )/256.,
						( rgb>>8&0xff )/256.,
						( rgb&0xff )/256. ) ;
				ps.operator.moveto( xy ) ;

				ps.operator.gsave() ;
				ps.operator.scale( interval ) ;

				ps.push( CK_UPIX ) ;
				ps.operator.grestore() ;
			}
	}

	public void tailPS( ApplicationPostscriptStream ps ) {
		ps.operator.currentdict() ;
		ps.push( "/"+CK_UDOT ) ;
		ps.operator.undef() ;
		ps.operator.currentdict() ;
		ps.push( "/"+CK_UPIX ) ;
		ps.operator.undef() ;
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

	private Coordinate[] calcMBR( Coordinate[] texture ) {
		Coordinate[] m, r ;
		double la, lm ;
		int li = 0 ;

		m = new MinimumDiameter( new GeometryFactory().createLinearRing( texture ) )
		.getMinimumRectangle()
		.getCoordinates() ;

		lm = texture[0].distance( m[0] ) ;
		for ( int i=1 ; i<m.length-1 ; i++ ) {
			la = texture[0].distance( m[i] ) ;
			if ( lm>la ) {
				lm = la ;
				li = i ;
			}
		}

		r = new Coordinate[ m.length ] ;
		for ( int i=0 ; i<m.length-1 ; i++ )
			r[i] = m[( li+i )%( m.length-1 )] ;
		r[m.length-1] = r[0] ;

		return r ;
	}

	private int calcRGB( double[] uv ) {
		return calcRGB( uv[0], uv[1] ) ;
	}

	private int calcRGB( double u, double v ) {
		int x, y ;
		double x0, y0, x1, y1 ;
		int w1, w2, w3, w4 ;
		int topy, p1, p2, p3, p4 ;
		int r, g, b ;

		x = (int) u ;
		y = (int) v ;

		x0 = u-x ;
		y0 = v-y ;
		x1 = 1-x0 ;
		y1 = 1-y0 ;

		w1 = (int) ( x1*y1*256 ) ;
		w2 = (int) ( x0*y1*256 ) ;
		w3 = (int) ( x1*y0*256 ) ;
		w4 = (int) ( x0*y0*256 ) ;

		topy = -1+dimy ;

		p1 = image[x+( topy-y )*dimx]&0xffffff ;		
		p2 = image[( x+1 )%dimx+( topy-y )*dimx]&0xffffff ;
		p3 = image[x+( topy-( y+1 )%dimy )*dimx]&0xffffff ;
		p4 = image[( x+1 )%dimx+( topy-( y+1 )%dimy )*dimx]&0xffffff ;

		if ( p1 != alpha || p2 != alpha || p3 != alpha || p4 != alpha ) {
			if ( p1 == alpha )
				p1 = 0xffffff ; // set background color
			if ( p2 == alpha )
				p2 = 0xffffff ;
			if ( p3 == alpha )
				p3 = 0xffffff ;
			if ( p4 == alpha )
				p4 = 0xffffff ;
		}

		r = (int) ( ( p1>>16&0xff )/256.*w1
				+( p2>>16&0xff )/256.*w2
				+( p3>>16&0xff )/256.*w3
				+( p4>>16&0xff )/256.*w4 ) ;

		g = (int) ( ( p1>>8&0xff )/256.*w1
				+( p2>>8&0xff )/256.*w2
				+( p3>>8&0xff )/256.*w3
				+( p4>>8&0xff )/256.*w4 ) ;

		b = (int) ( ( p1&0xff )/256.*w1
				+( p2&0xff )/256.*w2
				+( p3&0xff )/256.*w3
				+( p4&0xff )/256.*w4 ) ;

		return r<<16|g<<8|b ;
	}

	private void applyTM( int[] tonemap ) {
		int iar = 255, iag = 255, iab = 255 ;
		int ior = 0, iog = 0, iob = 0 ;
		int ip, ipr, ipg, ipb ;
		int tar, tag, tab, tor, tog, tob ;
		int tp, tpr, tpg, tpb ;
		int s ;

		s = image.length ;

		for ( int p=0 ; s>p ; p++ ) {
			ip = image[p]&0xffffff ;

			if ( ip == alpha )
				continue ;

			ipr = ip>>16&0xff ;
			ipg = ip>>8&0xff ;
			ipb = ip&0xff ;

			if ( iar>ipr )
				iar = ipr ;
			if ( iag>ipg )
				iag = ipg ;
			if ( iab>ipb )
				iab = ipb ;

			if ( ipr>ior )
				ior = ipr ;
			if ( ipg>iog )
				iog = ipg ;
			if ( ipb>iob )
				iob = ipb ;
		}

		tar = tonemap[0]>>16&0xff ;
		tag = tonemap[0]>>8&0xff ;
		tab = tonemap[0]&0xff ;
		tor = tonemap[1]>>16&0xff ;
		tog = tonemap[1]>>8&0xff ;
		tob = tonemap[1]&0xff ;

		for ( int p=0 ; s>p ; p++ ) {
			ip = image[p]&0xffffff ;

			if ( ip == alpha )
				continue ;

			ipr = ip>>16&0xff ;
			ipg = ip>>8&0xff ;
			ipb = ip&0xff ;

			tpr = ( ipr-iar )*( tor-tar )/( ior-iar )+tar ;
			tpg = ( ipg-iag )*( tog-tag )/( iog-iag )+tag ;
			tpb = ( ipb-iab )*( tob-tab )/( iob-iab )+tab ;

			tp = ip&0xff000000
			|tpr<<16
			|tpg<<8
			|tpb ;

			image[p] = tp ;
		}
	}

	private void linePS( ApplicationPostscriptStream ps, Coordinate[] list ) {
		ps.array( true ) ;
		for ( Coordinate c : list ) {
			ps.push( c.x ) ;
			ps.push( c.y ) ;
		}
		ps.array( false ) ;

		ps.operator.newpath() ;
		ps.gdraw() ;

		ps.operator.stroke() ;
	}
}
