package projectCodes2800;

import java.io.FileNotFoundException;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.ImageComponent2D;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.Shape3D;
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
	
	protected static BranchGroup loadShape(String filename, Appearance app) {
		int flags = ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY;
		ObjectFile f = new ObjectFile(flags, (float)(60 * Math.PI/180.0));
		Scene s = null;
		try {
			s = f.load("src\\projectCodes2800\\images\\" + filename + ".obj"); //load ring.obj file.
			
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
			scaler.setScale(0.1);
			Transform3D trfm = new Transform3D();
			trfm.mul(translator);
			trfm.mul(scaler);
			objTG = new TransformGroup(trfm);
			objTG.addChild(create_Object());
		}
		
		public Node create_Object() {
			return new Sphere(0.5f, Primitive.GENERATE_TEXTURE_COORDS, create_Appearance("meteor", Commons.Grey));
		}
		
		public Node position_Object() {
			return objTG;
		}
	}

