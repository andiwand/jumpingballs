package at.andiwand.jumpingballs.physic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.andiwand.commons.math.vector.Vector2d;

public class Physics extends Thread {

    private List<MassPlot> massPlots = new ArrayList<MassPlot>();
    private List<DumpedSpring> springs = new ArrayList<DumpedSpring>();

    private List<Wall> walls = new ArrayList<Wall>();
    private List<Body> bodies = new ArrayList<Body>();

    private Vector2d gravity = new Vector2d(0, 0);

    private long last;

    public List<MassPlot> getMassPlots() {
	return Collections.unmodifiableList(massPlots);
    }

    public List<DumpedSpring> getSprings() {
	return Collections.unmodifiableList(springs);
    }

    public List<Body> getBodies() {
	return Collections.unmodifiableList(bodies);
    }

    public void setGravity(Vector2d gravity) {
	this.gravity = gravity;
    }

    public synchronized void addMassPlot(MassPlot massPlot) {
	massPlots.add(massPlot);
    }

    public synchronized void addSpring(DumpedSpring spring) {
	springs.add(spring);
    }

    public synchronized void addWall(Wall wall) {
	walls.add(wall);
    }

    public synchronized void addBody(Body body) {
	bodies.add(body);

	for (MassPlot massPlot : body.getVertices()) {
	    addMassPlot(massPlot);
	}

	for (DumpedSpring spring : body.getSprings()) {
	    addSpring(spring);
	}
    }

    public synchronized void removeMassPlot(MassPlot massPlot) {
	massPlots.remove(massPlot);
    }

    public synchronized void removeSpring(DumpedSpring spring) {
	springs.remove(spring);
    }

    public synchronized void removeWall(Wall wall) {
	walls.remove(wall);
    }

    public synchronized void removeBody(Body body) {
	for (MassPlot massPlot : body.getVertices()) {
	    removeMassPlot(massPlot);
	}

	for (DumpedSpring spring : body.getSprings()) {
	    removeSpring(spring);
	}

	bodies.remove(body);
    }

    public void run() {
	last = System.nanoTime();

	try {
	    while (true) {
		Thread.sleep(1);

		synchronized (this) {
		    long now = System.nanoTime();
		    double delta = (now - last) / 1000000000d;
		    last = now;

		    for (DumpedSpring spring : springs) {
			spring.update();
		    }

		    handleWallCollision();
		    handleBodyCollision();

		    for (MassPlot massPlot : massPlots) {
			massPlot.forceCache = massPlot.forceCache.add(gravity);

			massPlot.update(delta);
		    }
		}
	    }
	} catch (InterruptedException e) {
	}
    }

    private void handleWallCollision() {
	for (Wall wall : walls) {
	    for (MassPlot massPlot : massPlots) {
		Vector2d position = massPlot.position;
		Vector2d velocity = massPlot.velocity;

		if (!wall.behind(position))
		    continue;

		Vector2d sp = position.sub(wall.start);

		double distance = wall.direction.dot(sp)
			/ wall.direction.length();
		position = position.add(wall.direction.normalize().mul(
			Math.abs(distance)));

		double wallVelocity = wall.direction.dot(velocity)
			/ wall.direction.length();
		velocity = velocity.add(wall.direction.normalize().mul(
			Math.abs(wallVelocity)));

		massPlot.position = position;
		massPlot.velocity = velocity;
	    }
	}
    }

    private void handleBodyCollision() {
	for (int x = 0; x < bodies.size() - 1; x++) {
	    List<MassPlot> massPlots = bodies.get(x).getOuterRing();

	    for (int y = x + 1; y < bodies.size(); y++) {
		Body body = bodies.get(y);

		for (int i = 0; i < massPlots.size(); i++) {
		    MassPlot massPlotA = massPlots.get(i);
		    Vector2d a = massPlotA.position;

		    if (!body.intersects(a))
			continue;

		    MassPlot massPlotB = body.nearMassPlot(a);
		    Vector2d b = massPlotB.position;
		    Vector2d distance = b.sub(a);
		    Vector2d direction = distance.normalize();

		    Vector2d va = massPlotA.velocity;
		    Vector2d vb = massPlotB.velocity;

		    double distanceVelocityA = direction.dot(va);
		    double distanceVelocityB = direction.dot(vb);

		    va = va.sub(direction.mul(distanceVelocityA));
		    vb = vb.sub(direction.mul(distanceVelocityB));

		    va = va.add(direction.mul(distanceVelocityB));
		    vb = vb.add(direction.mul(distanceVelocityA));

		    massPlotA.velocity = va;
		    massPlotB.velocity = vb;

		    a = a.add(distance.div(2));
		    b = a;

		    massPlotA.position = a;
		    massPlotB.position = b;

		    Vector2d fa = massPlotA.forceCache;
		    Vector2d fb = massPlotB.forceCache;

		    double distanceForceA = direction.dot(fa);
		    double distanceForceB = direction.dot(fb);

		    fa = fa.sub(direction.mul(distanceForceA));
		    fb = fb.sub(direction.mul(distanceForceB));

		    double distanceForce = distanceForceA + distanceForceB;

		    fa = fa.add(direction.mul(distanceForce));
		    fb = fb.add(direction.mul(distanceForce));

		    massPlotA.forceCache = fa;
		    massPlotB.forceCache = fb;
		}
	    }
	}
    }

}