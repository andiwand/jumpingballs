package at.andiwand.jumpingballs.physic;

import at.andiwand.commons.math.vector.Vector2d;

public class MassPlot {

    double mass;
    Vector2d position;
    Vector2d velocity;

    Vector2d forceCache = new Vector2d();

    public MassPlot(double mass) {
	this(mass, new Vector2d());
    }

    public MassPlot(double mass, Vector2d position) {
	this(mass, position, new Vector2d());
    }

    public MassPlot(double mass, Vector2d position, Vector2d velocity) {
	this.mass = mass;
	this.position = position;
	this.velocity = velocity;
    }

    public Vector2d getPosition() {
	return position;
    }

    public Vector2d getVelocity() {
	return velocity;
    }

    public void setPosition(Vector2d position) {
	this.position = position;
    }

    public void setVelocity(Vector2d velocity) {
	this.velocity = velocity;
    }

    public void update(double delta) {
	Vector2d acceleration = forceCache.div(mass);

	position = position.add(velocity.mul(delta)).add(
		acceleration.mul(delta * delta / 2));
	velocity = velocity.add(acceleration.div(mass).mul(delta));

	forceCache = Vector2d.NULL;
    }

    public Vector2d calcNewPosition(double delta) {
	Vector2d acceleration = forceCache.div(mass);

	return position.add(velocity.mul(delta)).add(
		acceleration.mul(delta * delta / 2));
    }

}