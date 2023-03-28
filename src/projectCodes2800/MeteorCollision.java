package projectCodes2800;


import java.util.Iterator;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnElapsedFrames;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;

public class MeteorCollision extends Behavior{
	Project project;
	
	public TransformGroup tg = null;
	public Transform3D t3d = null;
	private Transform3D t3dstep = new Transform3D();
	private WakeupOnElapsedFrames wakeFrame = null;
	
	private double x, y, z, distance, random;	
	
	public MeteorCollision(double xcoor, double ycoor, double zcoor, double randomcube) {
		x = xcoor;
		y = ycoor;
		z = zcoor;
		Meteor meteor = new Meteor();
		distance = Math.sqrt(x * x + y * y +z *z);
		random = randomcube;
		t3d = new Transform3D();	
		tg = new TransformGroup(t3d);
		t3d.setScale(1.0);
		t3d.setTranslation(new Vector3d(0, 0, 0));
		tg.addChild(meteor.position_Object());
		tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);	
		BoundingSphere bounds = new BoundingSphere(new Point3d(0, 0, 0), 1000);
		this.setSchedulingBounds(bounds);
	}
	
	@Override
	public void initialize() {
		wakeFrame = new WakeupOnElapsedFrames(0);
		wakeupOn(wakeFrame);		
	}

	@Override
	public void processStimulus(Iterator<WakeupCriterion> arg0) {
		t3dstep.set(new Vector3d(Project.speed * x / distance, Project.speed * y / distance, Project.speed * z/ distance));
		tg.setTransform(t3d);
		t3d.mul(t3dstep);
		tg.setTransform(t3d);
		wakeupOn(wakeFrame);		
	}
	

}


