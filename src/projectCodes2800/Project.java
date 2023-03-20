package projectCodes2800;


import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;


public class Project extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	
	public static BranchGroup create_Scene() {
		BranchGroup sceneBG = new BranchGroup();
		TransformGroup sceneTG = new TransformGroup();
		
		Sun sun = new Sun();
		Earth earth = new Earth();
		Mercury mercury = new Mercury();
		Venus venus = new Venus();
		Mars mars = new Mars();
		Jupiter jupiter = new Jupiter();
		Saturn saturn = new Saturn();
		Uranus uranus = new Uranus();
		Neptune neptune = new Neptune();
		
		sceneTG.addChild(sun.position_Object());
		sceneTG.addChild(earth.position_Object());
		sceneTG.addChild(mercury.position_Object());
		sceneTG.addChild(venus.position_Object());
		sceneTG.addChild(mars.position_Object());
		sceneTG.addChild(jupiter.position_Object());
		sceneTG.addChild(saturn.position_Object());
		sceneTG.addChild(uranus.position_Object());
		sceneTG.addChild(neptune.position_Object());
		
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);
		sceneTG.addChild(createBackground(Commons.Black, bounds));
		

	 	sceneBG.addChild(sceneTG);
	 	
	 	sceneBG.addChild(Commons.add_Lights(Commons.White, 1));
	 	sceneBG.addChild(Commons.rotate_Behavior(7500, sceneTG));
		
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
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		
		SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
		Commons.define_Viewer(su, new Point3d(5.0d, 5.0d, 4.0d));
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
		frame.getContentPane().add(new Project(create_Scene()));  // create an instance of the class
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	

}