package projectCodes2800;


import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.geometry.Box;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;


public class Project extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	
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
	 	
	 	sceneBG.addChild(Commons.add_Lights(Commons.White, 1));
	 	//sceneBG.addChild(Commons.rotate_Behavior(7500, sceneTG));
		
		return sceneBG;
		
	}
	
	/* NOTE: Keep the constructor for each of the labs and assignments */
	public Project(BranchGroup sceneBG) {
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
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
		frame.getContentPane().add(new Project(create_Scene()));  // create an instance of the class
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	

}