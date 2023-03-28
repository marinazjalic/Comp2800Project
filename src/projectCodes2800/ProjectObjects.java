package projectCodes2800;

import java.io.FileNotFoundException;
import java.util.Timer;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.PositionInterpolator;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Switch;
import org.jogamp.java3d.Texture;
import org.jogamp.java3d.Texture2D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.geometry.Primitive;
import org.jogamp.java3d.utils.geometry.Sphere;
import org.jogamp.java3d.utils.image.TextureLoader;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Vector3f;


public abstract class ProjectObjects {
	protected abstract Node create_Object();
	protected abstract Node position_Object();
	protected static int k = 4;//rotation scaling 
	
	protected static BranchGroup loadShape(String filename){
		return loadShape(filename, new Appearance());
	}

	protected static BranchGroup loadShape(String filename, Appearance app) {
		int flags = ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY;
		ObjectFile f = new ObjectFile(flags, (float)(60 * Math.PI/180.0));
		Scene s = null;
		try {
			s = f.load("src/projectCodes2800/images/" + filename + ".obj"); //load ring.obj file.
			
		}catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		}catch (ParsingErrorException e) {
			System.err.println(e);
			System.exit(1);
		}catch (IncorrectFormatException e) {
			System.err.println(e);
			System.exit(1);
		}

		f.setBasePath("src/projectCodes2800/images/");
		
		BranchGroup objBG = s.getSceneGroup();
		Shape3D ringShape = (Shape3D)objBG.getChild(0);
		ringShape.setAppearance(app);
		
		return objBG;
	}

	//method for loading planet textures
	public static Texture loadTextures(String imageName)
	{
		TextureLoader loader = new TextureLoader("src/projectCodes2800/images/" + imageName +".jpg", null);
		ImageComponent2D image = loader.getImage();
		if(image == null)
		{
			System.out.println("Error loading file.");
		}
		Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
		texture.setImage(0, image);
		return texture;
	}
	
	public static Appearance create_Appearance(String imageName, Color3f color)
	{
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes(color, ColoringAttributes.FASTEST);
		app.setColoringAttributes(ca);
		app.setTexture(loadTextures(imageName));
		return app;
	}
	
}

class Satellite extends ProjectObjects {
	public static Alpha satelliteAlpha = new Alpha();
	public static Switch satelliteSwitch = new Switch();

	private TransformGroup createWings(){

		TransformGroup rotTG =	Commons.rotation(366*k,'y', 0f,(float) Math.PI*2, satelliteAlpha);


		Transform3D wing3D = new Transform3D();
		wing3D.setScale(1.1);
		wing3D.setTranslation(new Vector3f(0f, -0.5f, 0f));
		TransformGroup wingTG = new TransformGroup(wing3D);
		wingTG.addChild(loadShape("Satellite", Commons.obj_Appearance(Commons.Blue)));
		
		rotTG.addChild(wingTG);
		return rotTG;
	}

	protected Node create_Object() {
		
		TransformGroup[] trfm = new TransformGroup[3];
		BranchGroup body = loadShape("Satellite-Body", Commons.obj_Appearance(new Color3f(99, 90, 73)));
		BranchGroup sphereRed = loadShape("Sphere", Commons.obj_Appearance(Commons.Red));
		BranchGroup sphereGreen = loadShape("Sphere", Commons.obj_Appearance(Commons.Green));

		Transform3D[] tr3D = new Transform3D[3];
		// Create Body
		tr3D[0] = new Transform3D();
		tr3D[0].setScale(-1.1);
		tr3D[0].rotX(Math.PI/2);
		tr3D[0].setTranslation(new Vector3f(-2f, 1f, 1));
		trfm[0] = new TransformGroup(tr3D[0]);
		trfm[0].addChild(body);
		

		// create sphere
		tr3D[2] = new Transform3D();
		tr3D[2].setScale(-0.1);
		tr3D[2].setTranslation(new Vector3f(0f, 1.1f, 0));		
		trfm[2] = new TransformGroup(tr3D[2]);

		satelliteSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
		satelliteSwitch.addChild(sphereRed);
		satelliteSwitch.addChild(sphereGreen);

		satelliteSwitch.setWhichChild(1);
		trfm[2].addChild(satelliteSwitch);


		//attach satellite and sphere to body
		trfm[0].addChild(createWings());
		trfm[0].addChild(trfm[2]);

		Transform3D satellite3D = new Transform3D();
		satellite3D.setScale(0.4);
		TransformGroup  satelliteTG = new TransformGroup(satellite3D);
		satelliteTG.addChild(trfm[0]);
		return satelliteTG;
	}


	protected Node position_Object() {
		return create_Object();
	}
	
}

class Sun extends ProjectObjects{
	
	public Node create_Object() {
		return new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("sun", Commons.Yellow));
	}
	
	public Node position_Object() {
		return create_Object();
	}
}

class Earth extends ProjectObjects {
	private TransformGroup objTG;
	
	public Earth() {
		Transform3D translator = new Transform3D();	
		translator.setTranslation(new Vector3f(2f, 0f, 0f));
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.4);
		Transform3D trfm = new Transform3D();
		trfm.mul(translator);
		trfm.mul(scaler);
		
		objTG = new TransformGroup(trfm);
		TransformGroup rotTG =	Commons.rotation(366*k,'y', 0f,(float) Math.PI*2);
		rotTG.addChild(create_Object());
		objTG.addChild(new Satellite().position_Object());
		objTG.addChild(rotTG);
	}
	
	public Node create_Object() {
		
		return new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("earth", Commons.Blue));
	}
	
	public Node position_Object() {
		return objTG;
	}
}

class Mercury extends ProjectObjects {
	private TransformGroup objTG;
	public Mercury() {
		Transform3D translator = new Transform3D();	
		translator.setTranslation(new Vector3f(1f, 0f, 0f));
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.2);
		Transform3D trfm = new Transform3D();
		trfm.mul(translator);
		trfm.mul(scaler);
		
		objTG = new TransformGroup(trfm);
		TransformGroup rotTG =	Commons.rotation(2*k,'y', 0f,(float) Math.PI*2);
		rotTG.addChild(create_Object());
		objTG.addChild(rotTG);
	}
	
	public Node create_Object() {
		return new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("mercury", Commons.Grey));
	}
	
	public Node position_Object() {
		return objTG;
	}
}

class Venus extends ProjectObjects {
	private TransformGroup objTG;
	public Venus() {
		Transform3D translator = new Transform3D();	
		translator.setTranslation(new Vector3f(3f, 0f, 0f));
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.4);
		Transform3D trfm = new Transform3D();
		trfm.mul(translator);
		trfm.mul(scaler);
		objTG = new TransformGroup(trfm);
		TransformGroup rotTG =	Commons.rotation(366*k,'y', 0f,(float) Math.PI*2);
		rotTG.addChild(create_Object());
		objTG.addChild(rotTG);
	}
	
	public Node create_Object() {
		return new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("venus", Commons.Orange));
	}
	
	public Node position_Object() {
		return objTG;
	}
}

class Mars extends ProjectObjects {
	private TransformGroup objTG;
	public Mars() {
		Transform3D translator = new Transform3D();	
		translator.setTranslation(new Vector3f(4f, 0f, 0f));
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.4);
		Transform3D trfm = new Transform3D();
		trfm.mul(translator);
		trfm.mul(scaler);
		objTG = new TransformGroup(trfm);
		TransformGroup rotTG =	Commons.rotation(366*k,'y', 0f,(float) Math.PI*2);
		rotTG.addChild(create_Object());
		objTG.addChild(rotTG);
	}
	
	public Node create_Object() {
		return new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("mars", Commons.Red));
	}
	
	public Node position_Object() {
		return objTG;
	}
}

class Jupiter extends ProjectObjects {
	private TransformGroup objTG;
	public Jupiter() {
		Transform3D translator = new Transform3D();	
		translator.setTranslation(new Vector3f(5f, 0f, 0f));
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.5);
		Transform3D trfm = new Transform3D();
		trfm.mul(translator);
		trfm.mul(scaler);
		objTG = new TransformGroup(trfm);
		TransformGroup rotTG =	Commons.rotation(366*k,'y', 0f,(float) Math.PI*2);
		rotTG.addChild(create_Object());
		objTG.addChild(rotTG);
	}
	
	public Node create_Object() {
		return new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("jupiter", Commons.Yellow));
	}
	
	public Node position_Object() {
		return objTG;
	}
}

class Saturn extends ProjectObjects {
	private TransformGroup objTG;
	public Saturn() {
		Transform3D translator = new Transform3D();	
		translator.setTranslation(new Vector3f(6f, 0f, 0f));
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.5);
		Transform3D trfm = new Transform3D();
		trfm.mul(translator);
		trfm.mul(scaler);
		objTG = new TransformGroup(trfm);

		BranchGroup rings = loadShape("SaturnRing", Commons.obj_Appearance(new Color3f(99, 90, 73)));
		Transform3D ringT3 = new Transform3D();
		ringT3.rotY(Math.PI);
		TransformGroup ringTG = new TransformGroup(ringT3);
		ringTG.addChild(rings);

		TransformGroup rotTG =	Commons.rotation(366*k,'r', 0f,(float) Math.PI*2);
		rotTG.addChild(ringTG);
		rotTG.addChild(create_Object());
		objTG.addChild(rotTG);
	}
	
	public Node create_Object() {
		return new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("saturn", Commons.Yellow));
	}
	
	public Node position_Object() {
		return objTG;
	}
}

class Uranus extends ProjectObjects {
	private TransformGroup objTG;
	public Uranus() {
		Transform3D translator = new Transform3D();	
		translator.setTranslation(new Vector3f(7f, 0f, 0f));
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.4);
		Transform3D trfm = new Transform3D();
		trfm.mul(translator);
		trfm.mul(scaler);
		objTG = new TransformGroup(trfm);
		objTG.addChild(create_Object());
	}
	
	public Node create_Object() {
		return new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("uranus", Commons.Blue));
	}
	
	public Node position_Object() {
		return objTG;
	}
}

class Neptune extends ProjectObjects {
	private TransformGroup objTG;
	public Neptune() {
		Transform3D translator = new Transform3D();	
		translator.setTranslation(new Vector3f(8f, 0f, 0f));
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.4);
		Transform3D trfm = new Transform3D();
		trfm.mul(translator);
		trfm.mul(scaler);
		objTG = new TransformGroup(trfm);
		TransformGroup rotTG =	Commons.rotation(366*k,'y', 0f,(float) Math.PI*2);
		rotTG.addChild(create_Object());
		objTG.addChild(rotTG);
	}
	
	public Node create_Object() {
		return new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("neptune", Commons.Blue));
	}
	
	public Node position_Object() {
		return objTG;
	}
}

class Meteor extends ProjectObjects {
	private TransformGroup objTG;
	public Meteor() {
		Transform3D translator = new Transform3D();	
		translator.setTranslation(new Vector3f(0f, 0f, 0f));
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.5);
		Transform3D trfm = new Transform3D();
		trfm.mul(translator);
		trfm.mul(scaler);
		objTG = new TransformGroup(trfm);
		objTG.addChild(create_Object());
	}
		
		public Node create_Object() {
			Sphere sphere = new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("meteor", Commons.Grey));
			sphere.setUserData(0);
			return sphere;
		}
		
		public Node position_Object() {
			return objTG;
		}
	}

class Rocket extends ProjectObjects {
	private TransformGroup objTG;
	public static Alpha movementAlpha = new Alpha(1, Alpha.INCREASING_ENABLE | Alpha.DECREASING_ENABLE, 0, 0, 0, 2000, 1000, 4000, 2000, 1000);
	public Rocket() {
		Transform3D translator = new Transform3D();	
		translator.setTranslation(new Vector3f(2f, 0.75f, 0f));
		Transform3D scaler = new Transform3D();
		scaler.setScale(0.7);
		Transform3D trfm = new Transform3D();
		trfm.mul(translator);
		trfm.mul(scaler);
		objTG = new TransformGroup(trfm);
	}
	
	public Node create_Object() {
		Transform3D rocketT3 = new Transform3D();
		rocketT3.rotY(Math.PI);
		TransformGroup rocketTG = new TransformGroup(rocketT3);
		rocketTG.addChild(loadShape("Rocket", Commons.obj_Appearance(Commons.Red)));
		
		TransformGroup moveTG = new TransformGroup();
		moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		moveTG.addChild(rocketTG);
		
		Transform3D axisPosition = new Transform3D();
		axisPosition.rotZ(Math.PI / 2.0);
		PositionInterpolator path_beh = new PositionInterpolator(movementAlpha, moveTG, axisPosition, 60f, 0f);
		path_beh.setSchedulingBounds(Commons.hundredBS);
		
		objTG.addChild(path_beh);
		objTG.addChild(moveTG);
		
		movementAlpha.pause();
		
		return objTG;
	}
	
	public Node position_Object() {
		return create_Object();
	}
}
