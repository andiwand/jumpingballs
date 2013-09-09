package at.andiwand.jumpingballs.physic;

import at.stefl.commons.math.vector.Vector2d;

public class DumpedSpring extends ForceLink {

    double springConstant;
    double damping;
    double length;

    public DumpedSpring(MassPlot massPlotA, MassPlot massPlotB,
	    double springConstant) {
	this(massPlotA, massPlotB, springConstant, 0);
    }

    public DumpedSpring(MassPlot massPlotA, MassPlot massPlotB,
	    double springConstant, double damping) {
	this(massPlotA, massPlotB, springConstant, damping, massPlotB.position
		.sub(massPlotA.position).length());
    }

    public DumpedSpring(MassPlot massPlotA, MassPlot massPlotB,
	    double springConstant, double damping, double length) {
	super(massPlotA, massPlotB);

	this.springConstant = springConstant;
	this.length = length;
	this.damping = damping;
    }

    public MassPlot getMassPlotA() {
	return massPlotA;
    }

    public MassPlot getMassPlotB() {
	return massPlotB;
    }

    public void update() {
	Vector2d distance = massPlotB.position.sub(massPlotA.position);
	Vector2d direction = distance.normalize();

	double springForceA = -springConstant * (length - distance.length())
		/ 2;
	double springForceB = -springForceA;

	double springVelocityA = massPlotA.velocity.dot(direction);
	double springVelocityB = massPlotB.velocity.dot(direction);
	double springVelocity = 0;
	if (Math.signum(springVelocityA) == Math.signum(springVelocityB)) {
	    springVelocity = Math.min(Math.abs(springVelocityA),
		    Math.abs(springVelocityB))
		    * Math.signum(springVelocityA);
	}

	double dampingForceA = -damping * (springVelocityA - springVelocity);
	double dampingForceB = -damping * (springVelocityB - springVelocity);

	double forceA = springForceA + dampingForceA;
	double forceB = springForceB + dampingForceB;

	massPlotA.forceCache = massPlotA.forceCache.add(direction.mul(forceA));
	massPlotB.forceCache = massPlotB.forceCache.add(direction.mul(forceB));
    }

}