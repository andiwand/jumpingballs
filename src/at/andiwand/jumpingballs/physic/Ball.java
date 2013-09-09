package at.andiwand.jumpingballs.physic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.stefl.commons.graphics.GraphicsUtil;
import at.stefl.commons.math.vector.Vector2d;

public class Ball extends Body {

    private MassPlot middle;
    private List<MassPlot> vertices = new ArrayList<MassPlot>();
    private List<MassPlot> outerRing = new ArrayList<MassPlot>();
    private List<DumpedSpring> springs = new ArrayList<DumpedSpring>();

    private Color mainColor = Color.RED;

    public Ball(int vertexCount, double radius, double mass, double vertexMass,
	    double radialSpringConstant, double springConstant,
	    double radialDamping, double damping, Vector2d middle,
	    Vector2d velocity) {
	double vertexAngle = 2 * Math.PI / vertexCount;

	this.middle = new MassPlot(mass, middle, velocity);
	vertices.add(this.middle);

	for (int i = 0; i < vertexCount; i++) {
	    Vector2d position = middle.add(new Vector2d(Math.sin(vertexAngle
		    * i), Math.cos(vertexAngle * i)).mul(radius));
	    MassPlot massPlot = new MassPlot(vertexMass, position, velocity);

	    vertices.add(massPlot);
	    outerRing.add(massPlot);
	}

	DumpedSpring dumpedSpring;
	for (int i = 0; i < vertexCount; i++) {
	    dumpedSpring = new DumpedSpring(this.middle, outerRing.get(i),
		    radialSpringConstant, radialDamping);
	    springs.add(dumpedSpring);
	}

	dumpedSpring = new DumpedSpring(outerRing.get(0),
		outerRing.get(vertexCount - 1), springConstant, damping);
	springs.add(dumpedSpring);
	for (int i = 0; i < vertexCount - 1; i++) {
	    dumpedSpring = new DumpedSpring(outerRing.get(i),
		    outerRing.get(i + 1), springConstant, damping);
	    springs.add(dumpedSpring);
	}

	// for (int i = 0; i < vertexCount - 1; i++) {
	// MassPlot massPlotA = outerRing.get(i);
	// Vector2d pointA = massPlotA.position;
	// Vector2d middleDirection = middle.sub(pointA).normalize();
	//
	// for (int j = i + 1; j < vertexCount; j++) {
	// MassPlot massPlotB = outerRing.get(j);
	// Vector2d pointB = massPlotB.position;
	// Vector2d direction = pointB.sub(pointA).normalize();
	// double springFactor = middleDirection.dot(direction);
	//
	// dumpedSpring = new DumpedSpring(massPlotA, massPlotB, springFactor,
	// 0.001);
	// springs.add(dumpedSpring);
	// }
	// }
    }

    public void paint(Graphics g) {
	GraphicsUtil graphicsUtil = new GraphicsUtil(g);

	List<Vector2d> vertices = new ArrayList<Vector2d>();
	for (MassPlot massPlot : outerRing) {
	    vertices.add(massPlot.getPosition());
	}

	g.setColor(mainColor);
	graphicsUtil.fillPolygon(vertices);

	g.setColor(Color.BLACK);
	graphicsUtil.drawPolygon(vertices);
    }

    public boolean intersects(Vector2d point) {
	List<Vector2d> vertices = new ArrayList<Vector2d>();
	for (MassPlot massPlot : outerRing) {
	    vertices.add(massPlot.getPosition());
	}

	for (int i = 0; i < vertices.size(); i++) {
	    Vector2d am = middle.position.sub(vertices.get(i));
	    Vector2d ap = point.sub(vertices.get(i));

	    if (am.dot(ap) <= 0)
		return false;
	}

	return true;
    }

    public MassPlot nearMassPlot(Vector2d point) {
	MassPlot result = vertices.get(0);
	double minDistance = result.position.sub(point).length();

	for (int i = 1; i < vertices.size(); i++) {
	    double distance = vertices.get(i).position.sub(point).length();

	    if (minDistance > distance) {
		result = vertices.get(i);
		minDistance = distance;
	    }
	}

	return result;
    }

    public MassPlot getMiddle() {
	return middle;
    }

    public List<MassPlot> getVertices() {
	return Collections.unmodifiableList(vertices);
    }

    public List<MassPlot> getOuterRing() {
	return Collections.unmodifiableList(outerRing);
    }

    public List<DumpedSpring> getSprings() {
	return Collections.unmodifiableList(springs);
    }

}