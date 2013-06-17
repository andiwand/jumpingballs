package at.andiwand.jumpingballs.physic;

import java.awt.Graphics;
import java.util.List;

import at.andiwand.commons.math.vector.Vector2d;

public abstract class Body {

    public abstract List<MassPlot> getVertices();

    public abstract List<MassPlot> getOuterRing();

    public abstract List<DumpedSpring> getSprings();

    public abstract boolean intersects(Vector2d point);

    public abstract MassPlot nearMassPlot(Vector2d point);

    public abstract void paint(Graphics g);

}