/* Copyright material for the convenience of GA/TAs to help students working on Lab Exercises,
 * but NOT to be shown or distributed to the students. */

 package projectCodes2800;

 import java.awt.BorderLayout;
 import java.awt.GraphicsConfiguration;
 
 import javax.swing.JFrame;
 import javax.swing.JPanel;
 
 import org.jogamp.java3d.*;
 import org.jogamp.java3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
 import org.jogamp.java3d.utils.geometry.ColorCube;
 import org.jogamp.java3d.utils.universe.*;
 import org.jogamp.vecmath.*;
 
 public class test extends JPanel {
 
     private static final long serialVersionUID = 1L;
     private static JFrame frame;
    
     
     /* a function to create a rotation behavior and refer it to 'rotTG' */
     private static RotationInterpolator rotateBehavior(int r_num, TransformGroup rotTG) {
         rotTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         Transform3D yAxis = new Transform3D();
         Alpha rotationAlpha = new Alpha(-1, r_num);
         RotationInterpolator rot_beh = new RotationInterpolator(
                 rotationAlpha, rotTG, yAxis, 0.0f, (float) Math.PI * 2.0f);
         rot_beh.setSchedulingBounds(Commons.hundredBS);
         return rot_beh;
     }
     
     /* a function to add two point lights at the opposite locations of the scene */
     public static BranchGroup addLights(Color3f clr, int p_num) {
         BranchGroup lightBG = new BranchGroup();
         Point3f atn = new Point3f(0.5f, 0.0f, 0.0f);
         PointLight ptLight;
         float adjt = 1f;
         for (int i = 0; (i < p_num) && (i < 2); i++) {
             if (i > 0) 
                 adjt = -1f; 
             ptLight = new PointLight(clr, new Point3f(3.0f * adjt, 1.0f, 3.0f  * adjt), atn);
             ptLight.setInfluencingBounds(Commons.hundredBS);
             lightBG.addChild(ptLight);
         }
         return lightBG;
     }
 
     /* a function to position viewer to 'eye' location */
     public static void defineViewer(SimpleUniverse simple_U, Point3d eye) {
         TransformGroup viewTransform = simple_U.getViewingPlatform().getViewPlatformTransform();
         Point3d center = new Point3d(0, 0, 0);             // define the point where the eye looks at
         Vector3d up = new Vector3d(0, 1, 0);               // define camera's up direction
         Transform3D view_TM = new Transform3D();
         view_TM.lookAt(eye, center, up);
         view_TM.invert();
         viewTransform.setTransform(view_TM);               // set the TransformGroup of ViewingPlatform
     }
 
     /* a function to allow key navigation with the ViewingPlateform */
     public static KeyNavigatorBehavior keyNavigation(SimpleUniverse simple_U) {
         ViewingPlatform view_platfm = simple_U.getViewingPlatform();
         TransformGroup view_TG = view_platfm.getViewPlatformTransform();
         KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(view_TG);
         keyNavBeh.setSchedulingBounds(Commons.twentyBS);
         return keyNavBeh;
     }
 
     /* a function to build the content branch */
     public static BranchGroup createScene() {
         BranchGroup sceneBG = new BranchGroup();
         TransformGroup sceneTG = new TransformGroup();
         sceneTG.addChild(new Satellite().position_Object());
         sceneBG.addChild(addLights(Commons.White, 1));	
         //sceneBG.addChild(rotateBehavior(5000, sceneTG));
         
         sceneBG.addChild(sceneTG);
         return sceneBG;
     }
 
     /* NOTE: Keep the constructor for each of the labs and assignments */
     public test(BranchGroup sceneBG) {
         GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
         Canvas3D canvas = new Canvas3D(config);
         
         SimpleUniverse su = new SimpleUniverse(canvas);    // create a SimpleUniverse
         defineViewer(su, new Point3d(1.0d, 1.0d, 4.0d));   // set the viewer's location
         
         sceneBG.addChild(keyNavigation(su));               // allow key navigation
         sceneBG.compile();		                           // optimize the BranchGroup
         su.addBranchGraph(sceneBG);                        // attach the scene to SimpleUniverse
 
         setLayout(new BorderLayout());
         add("Center", canvas);
         frame.setSize(800, 800);                           // set the size of the JFrame
         frame.setVisible(true);
     }
 
     public static void main(String[] args) {
         frame = new JFrame("XY's Rotating Cube");          // NOTE: change XY to student's initials
         frame.getContentPane().add(new test(createScene()));  // create an instance of the class
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     }
 }
 