package projectCodes2800;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.PositionInterpolator;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Galaxy extends JPanel implements KeyListener, MouseListener {

	
	private static final long serialVersionUID = 1L;
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

		for(int i =0; i<9; i++)
			sunTG.addChild(rotations[i]);
		
		
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
		t3d.setScale(1.3);
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
	public Galaxy(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		canvas = new Canvas3D(config);
		canvas.addKeyListener(this);                      
		canvas.addMouseListener(this);    
		SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
		Commons.define_Viewer(su, new Point3d(5.0d, 5.0d, 5.0d));
		sceneBG.addChild(Commons.key_Navigation(su));
		sceneBG.addChild(Commons.key_Navigation(su));     // allow key navigation
		
		sceneBG.compile();		                           // optimize the BranchGroup
		su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse
		
		setLayout(new BorderLayout());
		add("Center", canvas);
		frame.setSize(800, 800);                           // set the size of the JFrame
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		frame = new JFrame("Group Project - Galaxy");                   // NOTE: change XY to student's initials
		frame.getContentPane().add(new Galaxy(create_Scene()));  // create an instance of the class
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public Galaxy(Canvas3D canvas3D) {
		canvas = canvas3D;
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

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
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