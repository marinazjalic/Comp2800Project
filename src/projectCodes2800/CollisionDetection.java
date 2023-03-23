package projectCodes2800;

import java.util.Iterator;
import org.jogamp.java3d.Behavior;
import org.jogamp.java3d.Bounds;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.WakeupCriterion;
import org.jogamp.java3d.WakeupOnCollisionEntry;
import org.jogamp.java3d.WakeupOnCollisionExit;

public class CollisionDetection extends Behavior{
	
	private boolean inCollision;
	private WakeupOnCollisionEntry wEnter;
	private WakeupOnCollisionExit wExit;
	private static Node collidingPlanet;
	
	public CollisionDetection(Node planet, Bounds bounds) {
		collidingPlanet = planet;
		setSchedulingBounds(bounds);
		inCollision = false;
	}

	@Override
	public void initialize() {
		wEnter = new WakeupOnCollisionEntry(collidingPlanet, WakeupOnCollisionEntry.USE_GEOMETRY);
		wExit = new WakeupOnCollisionExit(collidingPlanet, WakeupOnCollisionExit.USE_GEOMETRY);
		wakeupOn(wEnter); 	
	}

	@Override
	public void processStimulus(Iterator<WakeupCriterion> arg0) {
		inCollision = !inCollision; 
		
		if(inCollision) { 
			Project.speed = 0.5; //change speed to trigger explosion
			wakeupOn(wExit);
		}
	}
}
