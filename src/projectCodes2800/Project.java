package projectCodes2800;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Locale;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Cone;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.MultiTransformGroup;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.Viewer;
import org.jogamp.java3d.utils.universe.ViewerAvatar;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;


public class Project extends JPanel {
	
	private static final long serialVersionUID = 1L;
	static final int width = 600;                            // size of each Canvas3D
	static final int height = 600;
	private Canvas3D[] canvas3D;
	
	public static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();
		TransformGroup[] rotations = new TransformGroup[8];
		int k = 2650;//"earth speed" - each rotation is set to be relative to earth days
		Sun sun = new Sun();
		Earth earth = new Earth();
		Mercury mercury = new Mercury();
		Venus venus = new Venus();
		Mars mars = new Mars();
		Jupiter jupiter = new Jupiter();
		Saturn saturn = new Saturn();
		Uranus uranus = new Uranus();
		Neptune neptune = new Neptune();
		
		
		rotations[0] = Commons.rotation(k,'y', 0f,(float)Math.PI * 2);
		rotations[0].addChild(earth.position_Object());
		
		rotations[1]= Commons.rotation((int)(k*0.25), 'y', 0f,(float)Math.PI * 2);
		rotations[1].addChild(mercury.position_Object());
		
		rotations[2] = Commons.rotation((int)(k*0.62), 'y', 0f, (float)Math.PI * 2);
		rotations[2].addChild(venus.position_Object());
		
		rotations[3] = Commons.rotation((int)(k*1.88), 'y', 0f, (float)Math.PI * 2);
		rotations[3].addChild(mars.position_Object());
		
		rotations[4] = Commons.rotation((int)(k*11.86), 'y', 0f, (float)Math.PI * 2);
		rotations[4].addChild(jupiter.position_Object());
		
		rotations[5] = Commons.rotation((int)(k*29.46), 'y', 0f, (float)Math.PI * 2);
		rotations[5].addChild(saturn.position_Object());
		

		rotations[6] = Commons.rotation((int)(k*84), 'y', 0f, (float)Math.PI * 2);
		rotations[6].addChild(uranus.position_Object());

		rotations[7] = Commons.rotation((int)(k*164.8), 'y', 0f, (float)Math.PI * 2);
		rotations[7].addChild(neptune.position_Object());
		
		
		TransformGroup sunTG = new TransformGroup();
		sunTG.addChild(sun.position_Object());
		
		for(int i =0; i<8; i++)
			sunTG.addChild(rotations[i]);
		
		
		sceneTG.addChild(sunTG);


	 	sceneBG.addChild(sceneTG);
	 	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);
	 	sceneTG.addChild(createBackground(Commons.Black, bounds));
	 	sceneBG.addChild(Commons.add_Lights(Commons.White, 1));
	 	//sceneBG.addChild(Commons.rotate_Behavior(7500, sceneTG));
		
		return sceneBG;
		
	}
	
	//method to create background
	private static Background createBackground(Color3f clr, BoundingSphere bounds)
	{
		Background bg = new Background();
		bg.setImage(new TextureLoader("src/projectCodes2800/images/background.jpg", null).getImage());
		bg.setImageScaleMode(Background.SCALE_FIT_MAX);
		bg.setApplicationBounds(bounds);
		bg.setColor(clr);		
		return bg;
	}
	
	/* NOTE: Keep the constructor for each of the labs and assignments */
	public Project(BranchGroup sceneBG) {
		canvas3D = new Canvas3D[3];
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration( );
		for (int i = 0; i < 3; i++) {
			canvas3D[i] = new Canvas3D( config );
			canvas3D[i].setSize( width, height );
			add( canvas3D[i] );                            // add 3 Canvas3D to Frame
		}		
		ViewingPlatform vp = new ViewingPlatform(2);       // a VP with 2 TG about it		
		Viewer viewer = new Viewer( canvas3D[0] );         // point 1st Viewer to c3D[0]
		Transform3D t3d = new Transform3D( );
		t3d.rotX( Math.PI / 2.0 );                         // rotate and position the 1st ~
		t3d.setTranslation( new Vector3d( 0, 0, -20 ) );   // viewer looking down from top
		t3d.invert( );
		MultiTransformGroup mtg = vp.getMultiTransformGroup( );
		mtg.getTransformGroup(0).setTransform( t3d );

		SimpleUniverse su = new SimpleUniverse(vp, viewer);
		Locale lcl = su.getLocale();                        // point 2nd/3rd Viewer to c3D[1,2]
		lcl.addBranchGraph( createViewer( canvas3D[1], "F-L", Commons.Orange, -5, 5, 0 ) );
		lcl.addBranchGraph( createViewer( canvas3D[2] , "B-R", Commons.Cyan, 5, 5, 0 ) );
		
		sceneBG.compile();
		su.addBranchGraph( sceneBG );

	}
	
	ViewingPlatform createViewer(Canvas3D canvas3D, String name, Color3f clr, 
			double x, double y, double z) {		
		// a Canvas3D can only be attached to a single Viewer
		Viewer viewer = new Viewer( canvas3D );	             // attach a Viewer to its canvas
		ViewingPlatform vp = new ViewingPlatform( 1 );       // 1 VP with 1 TG above
		                                                     // assign PG to the Viewer
		viewer.setAvatar( createViewerAvatar( name, clr ) ); // assign VA to the Viewer

		Point3d center = new Point3d(0, 0, 0);               // define where the eye looks at
		Vector3d up = new Vector3d(0, 1, 0);                 // define camera's up direction
		Transform3D viewTM = new Transform3D();
		Point3d eye = new Point3d(x, y, z);                  // define eye's location
		viewTM.lookAt(eye, center, up);
		viewTM.invert();  
		vp.getViewPlatformTransform().setTransform(viewTM);  // set VP with 'viewTG'

		// set TG's capabilities to allow KeyNavigatorBehavior modify the Viewer's position
		vp.getViewPlatformTransform( ).setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );
		vp.getViewPlatformTransform( ).setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
		KeyNavigatorBehavior key = new KeyNavigatorBehavior( vp.getViewPlatformTransform( ) );
		key.setSchedulingBounds( new BoundingSphere() );          // enable viewer navigation
		key.setEnable( false );		
		vp.addChild( key );                                   // add KeyNavigatorBehavior to VP
		viewer.setViewingPlatform( vp );                      // set VP for the Viewer	
		return vp;
	}

	/* a function to create and position a simple Cone to represent the Viewer */
	ViewerAvatar createViewerAvatar( String szText, Color3f objColor ) {
		ViewerAvatar viewerAvatar = new ViewerAvatar( );
		// lay down the Cone, pointing sharp-end towards the Viewer's field of view
		TransformGroup tg = new TransformGroup( );
		Transform3D t3d = new Transform3D( );
		t3d.setEuler( new Vector3d( Math.PI / 2.0, Math.PI, 0 ) );
		tg.setTransform( t3d );
		
		Appearance app = Commons.obj_Appearance(objColor);
		
		tg.addChild( new Cone( 0.5f, 1.5f, Primitive.GENERATE_NORMALS, app ) );
		viewerAvatar.addChild( tg );                         // add Cone to parent BranchGroup

		return viewerAvatar;
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Group Project - Galaxy");                // NOTE: change XY to student's initials
		frame.getContentPane().add(new Project(create_Scene()));  // create an instance of the class
		frame.setSize(1910, height + 40);                         // set the size of the JFrame
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	

}