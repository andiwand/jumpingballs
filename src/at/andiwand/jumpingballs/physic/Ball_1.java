package at.andiwand.jumpingballs.physic;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.andiwand.library.graphics.GraphicsUtil;
import at.andiwand.library.math.Vector2d;


public class Ball_1 {
	
	private List<MassPlot> vertices = new ArrayList<MassPlot>();
	private List<MassPlot> outerRing = new ArrayList<MassPlot>();
	private List<DumpedSpring> springs = new ArrayList<DumpedSpring>();
	
	private Color mainColor = Color.RED;
	
	
	public Ball_1(int vertexCount, double radius, double mass, double springConstant, double surfaceSpringConstant, double damping, double surfaceDamping, Vector2d middle) {
		this(vertexCount, radius, mass, springConstant, surfaceSpringConstant, damping, surfaceDamping, middle, new Vector2d());
	}
	public Ball_1(int vertexCount, double radius, double mass, double springConstant, double surfaceSpringConstant, double damping, double surfaceDamping, Vector2d middle, Vector2d velocity) {
		double vertexAngle = 2 * Math.PI / vertexCount;
		double vertexMass = mass / vertexCount;
		System.out.println(vertexMass);
		
		for (int i = 0; i < vertexCount; i++) {
			Vector2d position = middle.add(new Vector2d(Math.sin(vertexAngle * i), Math.cos(vertexAngle * i)).mul(radius));
			MassPlot massPlot = new MassPlot(vertexMass, position, velocity);
			
			vertices.add(massPlot);
		}
		
		outerRing = vertices;
		
		
		for (int i = 0; i < vertexCount - 1; i++) {
			MassPlot massPlotA = outerRing.get(i);
			Vector2d pointA = massPlotA.position;
			Vector2d middleDirection = middle.sub(pointA).normalize();
			
			for (int j = i + 1; j < vertexCount; j++) {
				MassPlot massPlotB = outerRing.get(j);
				Vector2d pointB = massPlotB.position;
				Vector2d direction = pointB.sub(pointA).normalize();
				double springFactor = middleDirection.dot(direction);
				
				DumpedSpring dumpedSpring = new DumpedSpring(massPlotA, massPlotB, springConstant * springFactor, damping);
				springs.add(dumpedSpring);
			}
		}
		
		DumpedSpring dumpedSpring = new DumpedSpring(vertices.get(0), vertices.get(vertexCount - 1), springConstant, surfaceDamping);
		springs.add(dumpedSpring);
		for (int i = 0; i < vertexCount - 1; i++) {
			dumpedSpring = new DumpedSpring(vertices.get(i), vertices.get(i + 1), springConstant, surfaceDamping);
			springs.add(dumpedSpring);
		}
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
		
		Vector2d middle = getMiddle();
		for (int i = 0; i < vertices.size(); i++) {
			Vector2d am = middle.sub(vertices.get(i));
			Vector2d ap = point.sub(vertices.get(i));
			
			if (am.dot(ap) <= 0) return false;
		}
		
		return true;
	}
	
	public MassPlot nearMassPlot(Vector2d point) {
		MassPlot result = outerRing.get(0);
		double minDistance = result.position.sub(point).length();
		
		for (int i = 1; i < outerRing.size(); i++) {
			double distance = outerRing.get(i).position.sub(point).length();
			
			if (minDistance > distance) {
				result = outerRing.get(i);
				minDistance = distance;
			}
		}
		
		return result;
	}
	
	public Vector2d getMiddle() {
		Vector2d result = new Vector2d();
		
		for (MassPlot massPlot : outerRing) {
			result = result.add(massPlot.position);
		}
		
		return result.div(outerRing.size());
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