package projectCodes2800;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Hashtable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.Locale;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.PositionInterpolator;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Cone;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.java3d.utils.universe.MultiTransformGroup;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.Viewer;
import org.jogamp.java3d.utils.universe.ViewerAvatar;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Project extends JPanel implements KeyListener, MouseListener, ActionListener {

	
	private static final long serialVersionUID = 1L;
	static final int width = 600;                            // size of each Canvas3D
	static final int height = 600;
	private Canvas3D[] canvas3D;
    private static JFrame frame;
    private static Canvas3D canvas;
    public static BranchGroup sceneBG;
    private static PositionInterpolator posInterpolator;
    private static Transform3D t3d = null;
    private static TransformGroup meteorTG = null;
    public static double speed = 0;
    private static PickTool pickTool;
    private static boolean isPaused = false; //flag for position interpolator state
	public static Satellite satellite = new Satellite();

    Hashtable<String, MouseListener> m_MouseHashtable = null;
    Hashtable<String, KeyNavigatorBehavior>	m_KeyHashtable = null;
	
	public static BranchGroup create_Scene() {
		sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();
		TransformGroup[] rotations = new TransformGroup[10];
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
		Rocket rocket = new Rocket();

		rotations[0] = Commons.rotation(k,'y', 0f,(float)Math.PI * 2);
		rotations[0].addChild(earth.position_Object());
		rotations[0].setCollidable(true);
		

		rotations[1]= Commons.rotation((int)(k*0.25), 'y', 0f,(float)Math.PI * 2);
		rotations[1].addChild(mercury.position_Object());
		rotations[1].setCollidable(true);
		
		rotations[2] = Commons.rotation((int)(k*0.62), 'y', 0f, (float)Math.PI * 2);
		rotations[2].addChild(venus.position_Object());
		rotations[2].setCollidable(true);
		
		rotations[3] = Commons.rotation((int)(k*1.88), 'y', 0f, (float)Math.PI * 2);
		rotations[3].addChild(mars.position_Object());
		rotations[3].setCollidable(true);
		
		rotations[4] = Commons.rotation((int)(k*11.86), 'y', 0f, (float)Math.PI * 2);
		rotations[4].addChild(jupiter.position_Object());
		rotations[4].setCollidable(true);
		
		rotations[5] = Commons.rotation((int)(k*29.46), 'y', 0f, (float)Math.PI * 2);
		rotations[5].addChild(saturn.position_Object());
		rotations[5].setCollidable(true);

		rotations[6] = Commons.rotation((int)(k*84), 'y', 0f, (float)Math.PI * 2);
		rotations[6].addChild(uranus.position_Object());
		rotations[6].setCollidable(true);

		rotations[7] = Commons.rotation((int)(k*164.8), 'y', 0f, (float)Math.PI * 2);
		rotations[7].addChild(neptune.position_Object());
		rotations[7].setCollidable(true);
		
		rotations[8] = Commons.rotation(k,'y', 0f,(float)Math.PI * 2);
		rotations[8].addChild(rocket.position_Object());
		rotations[8].setCollidable(true);
		


		TransformGroup sunTG = new TransformGroup();
		sunTG.addChild(sun.position_Object());

		for(int i = 0; i < 9; i++)
			sunTG.addChild(rotations[i]);

			//sunTG.addChild(rotations[9]);
		//add meteor and collision detection
		TransformGroup mTG = new TransformGroup();
		mTG.addChild(pathInterpolator(create_meteors()));
		mTG.setCollidable(true);
		Transform3D translate = new Transform3D();
		translate.setTranslation(new Vector3f(-10f, 0, -2));
		mTG.setTransform(translate);
		sceneTG.addChild(mTG);
		CollisionDetection cd = new CollisionDetection(mTG, Commons.hundredBS);
		cd.setSchedulingBounds(Commons.hundredBS);
		sceneTG.addChild(cd);
		
			
		sceneTG.addChild(sunTG);
		pickTool = new PickTool( sceneBG );              
		pickTool.setMode(PickTool.GEOMETRY);  

		sceneBG.setCapability(Group.ALLOW_CHILDREN_EXTEND);
	 	sceneBG.addChild(sceneTG);
	 	BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);
	 	sceneTG.addChild(createBackground(Commons.Black, bounds));
	 	sceneBG.addChild(Commons.add_Lights(Commons.White, 1));		
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
	
	public static TransformGroup pathInterpolator(Node node)
	{
		TransformGroup myTG = new TransformGroup();
		myTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D axisPosition = new Transform3D();
		axisPosition.rotX(-Math.PI/2);
		posInterpolator = new PositionInterpolator(new Alpha(-1,12000), myTG, axisPosition, 0, 15f);
		posInterpolator.setSchedulingBounds(new BoundingSphere(new Point3d(), 100.0));
		myTG.addChild(posInterpolator);
		myTG.addChild(node);
		return myTG;
	}
	
	public static BranchGroup create_meteors() {
		BranchGroup meteorBG = new BranchGroup();
		meteorTG = new TransformGroup();
		t3d = new Transform3D();
		t3d.setTranslation(new Vector3d(1,0,0));
		t3d.setScale(0.5);
		meteorTG.setTransform(t3d);
		
		meteorTG.addChild(create_meteor(0.05*Math.random(), -0.05*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), -0.05*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), 0.05*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), 0.05*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), -0.05*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), -0.05*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), 0.05*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), 0.05*Math.random(), -0.05*Math.random(), Math.random()));

		meteorTG.addChild(create_meteor(0.15*Math.random(), -0.05*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), -0.05*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), 0.05*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), 0.05*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), -0.05*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), -0.05*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), 0.05*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), 0.05*Math.random(), -0.05*Math.random(), Math.random()));

		meteorTG.addChild(create_meteor(0.05*Math.random(), -0.15*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), -0.15*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), 0.15*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), 0.15*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), -0.15*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), -0.15*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), 0.15*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), 0.15*Math.random(), -0.05*Math.random(), Math.random()));

		meteorTG.addChild(create_meteor(0.05*Math.random(), -0.05*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), -0.05*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), 0.05*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), 0.05*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), -0.05*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), -0.05*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), 0.05*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), 0.05*Math.random(), -0.15*Math.random(), Math.random()));

		meteorTG.addChild(create_meteor(0.15*Math.random(), -0.15*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), -0.15*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), 0.15*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), 0.15*Math.random(), 0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), -0.15*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), -0.15*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), 0.15*Math.random(), -0.05*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), 0.15*Math.random(), -0.05*Math.random(), Math.random()));

		meteorTG.addChild(create_meteor(0.15*Math.random(), -0.05*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), -0.05*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), 0.05*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), 0.05*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), -0.05*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), -0.05*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), 0.05*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), 0.05*Math.random(), -0.15*Math.random(), Math.random()));

		meteorTG.addChild(create_meteor(0.05*Math.random(), -0.15*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), -0.15*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), 0.15*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), 0.15*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), -0.15*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), -0.15*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.05*Math.random(), 0.15*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.05*Math.random(), 0.15*Math.random(), -0.15*Math.random(), Math.random()));

		meteorTG.addChild(create_meteor(0.15*Math.random(), -0.15*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), -0.15*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), 0.15*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), 0.15*Math.random(), 0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), -0.15*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), -0.15*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(0.15*Math.random(), 0.15*Math.random(), -0.15*Math.random(), Math.random()));
		meteorTG.addChild(create_meteor(-0.15*Math.random(), 0.15*Math.random(), -0.15*Math.random(), Math.random()));
		
		meteorBG.addChild(meteorTG);
		meteorBG.compile();
		
		return meteorBG;
		
	}
	
	public static BranchGroup create_meteor(double x, double y, double z,double random) {
		BranchGroup objRoot = new BranchGroup();
		TransformGroup tg = new TransformGroup();
		Transform3D t3d = new Transform3D();
		
		t3d.setTranslation(new Vector3d(x, y, z));
		t3d.setScale(0.3);
		tg.setTransform(t3d);
		
		MeteorCollision cd = new MeteorCollision(x, y, z, random);
		tg.addChild(cd.tg);
		tg.addChild(cd);
		objRoot.addChild(tg);
		objRoot.compile();
		return objRoot;
		
	}
	
	//method to play audio clip
	public static void playAudio(String fileName)
	{
		javax.sound.sampled.Clip clip = null;
		URL url = null;
		String filename = "src/projectCodes2800/sounds/" + fileName + ".wav";
		try {
			url = new URL("file", "localhost", filename);
			AudioInputStream audio = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
		} catch (Exception e) {
			System.out.println("Error opening file: " + filename);
		}	
	}
	
	/* NOTE: Keep the constructor for each of the labs and assignments */
	public Project(BranchGroup sceneBG) {
		m_KeyHashtable = new Hashtable<String, KeyNavigatorBehavior>( );
		canvas3D = new Canvas3D[3];
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration( );
		for (int i = 0; i < 3; i++) {
			canvas3D[i] = new Canvas3D( config );
			canvas3D[i].setSize( width, height );
			add( canvas3D[i] );                            // add 3 Canvas3D to Frame
			canvas3D[i].addKeyListener(this);
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
		lcl.addBranchGraph( createViewer( canvas3D[1], "Left", Commons.Orange, -3.5, 1, 3.5 ) );
		lcl.addBranchGraph( createViewer( canvas3D[2] , "Right", Commons.Cyan, 3.5, 1, -3.5 ) );
		
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
		key.setEnable( true );		
		vp.addChild( key );                                   // add KeyNavigatorBehavior to VP
		viewer.setViewingPlatform( vp );                      // set VP for the Viewer	
		m_KeyHashtable.put(name, key);
		Button button = new Button(name);
		button.addActionListener(this);
		add(button);
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
		
		tg.addChild( new Cone( 0.1f, 0.25f, Primitive.GENERATE_NORMALS, app ) );
		viewerAvatar.addChild( tg );                         // add Cone to parent BranchGroup

		return viewerAvatar;
	}
	
	public void actionPerformed( ActionEvent event ) {
		KeyNavigatorBehavior key = (KeyNavigatorBehavior)m_KeyHashtable.get(event.getActionCommand());
		Object[] keysArray = m_KeyHashtable.values( ).toArray( );
		for( int n = 0; n < keysArray.length; n++ )	{
			KeyNavigatorBehavior keyAtIndex = (KeyNavigatorBehavior) keysArray[n];
			keyAtIndex.setEnable( keyAtIndex == key );
			if( keyAtIndex == key ) {
				System.out.println("Enabled: " + event.getActionCommand());
				if( keyAtIndex == key ) {
					if (n == 1) {
						System.out.println("Right");
						key.setEnable( true );	
					}
					else {
						System.out.println("Left");
						key.setEnable( true );	
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Group Project - Galaxy");                // NOTE: change XY to student's initials
		frame.getContentPane().add(new Project(create_Scene()));  // create an instance of the class
		frame.setSize(1920, height + 40);                         // set the size of the JFrame
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	//clicking meteor enables/disables position interpolator 
	public void mouseClicked(MouseEvent event) {
		int x = event.getX(); int y = event.getY();        
		Point3d point3d = new Point3d(), center = new Point3d();
		canvas.getPixelLocationInImagePlate(x, y, point3d);
		canvas.getCenterEyeInImagePlate(center);   
		
		Transform3D transform3D = new Transform3D();
		canvas.getImagePlateToVworld(transform3D);   
		transform3D.transform(point3d);    
		transform3D.transform(center);    
		
		Vector3d mouseVec = new Vector3d();
		mouseVec.sub(point3d, center);
		mouseVec.normalize();
		pickTool.setShapeRay(point3d, mouseVec);           // send a PickRay for intersection

		if (pickTool.pickClosest() != null) {
			PickResult pickResult = pickTool.pickClosest();
			Node spaceObject = pickResult.getNode(PickResult.PRIMITIVE);
			if((int) spaceObject.getUserData() == 0) // meteor clicked
			{
				if(!isPaused) {
					posInterpolator.setEnable(false);
					isPaused = true;
				} else {
					posInterpolator.setEnable(true);
					isPaused = false;
				}	
			}
		}	
	}

	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void keyPressed(KeyEvent event)	{
		if ((event.getKeyCode() == KeyEvent.VK_SPACE)) {	//if space bar is pressed
			System.out.println("Space bar was pressed.");
			playAudio("rocket");
			 
			//enter logic for what happens to rocket when space bar is pressed here:
			
			if(Rocket.movementAlpha.isPaused()){
				Rocket.movementAlpha.resume();
            }else{
            	Rocket.movementAlpha.pause();
            } 
		}


		if((event.getKeyCode()== KeyEvent.VK_X)){
			if(Satellite.satelliteSwitch.getWhichChild()==1){
				Satellite.satelliteSwitch.setWhichChild(0);
				Satellite.satelliteAlpha.pause();
			}else{
				Satellite.satelliteSwitch.setWhichChild(1);
				Satellite.satelliteAlpha.resume();
			} 
		}
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

}
