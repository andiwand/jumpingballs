package at.andiwand.jumpingballs.physic;

import at.andiwand.commons.math.vector.Vector2d;

public class Wall {

    Vector2d start;
    Vector2d direction;

    public Wall(Vector2d start, Vector2d direction) {
	this.start = start;
	this.direction = direction;
    }

    public boolean behind(Vector2d point) {
	Vector2d sp = point.sub(start);

	return sp.dot(direction) < 0;
    }

}