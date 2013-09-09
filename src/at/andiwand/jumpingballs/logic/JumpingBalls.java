package at.andiwand.jumpingballs.logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import at.andiwand.jumpingballs.physic.Ball;
import at.andiwand.jumpingballs.physic.Body;
import at.andiwand.jumpingballs.physic.DumpedSpring;
import at.andiwand.jumpingballs.physic.MassPlot;
import at.andiwand.jumpingballs.physic.Physics;
import at.andiwand.jumpingballs.physic.Wall;
import at.stefl.commons.graphics.GraphicsUtil;
import at.stefl.commons.math.vector.Vector2d;

public class JumpingBalls extends JComponent {

    private static final long serialVersionUID = -2100367665225471901L;

    private Physics physics = new Physics();

    private Wall top = new Wall(Vector2d.NULL, Vector2d.NULL);
    private Wall bottom = new Wall(Vector2d.NULL, Vector2d.NULL);
    private Wall left = new Wall(Vector2d.NULL, Vector2d.NULL);
    private Wall right = new Wall(Vector2d.NULL, Vector2d.NULL);

    private Ball newBall;

    public JumpingBalls() {
	physics.setGravity(new Vector2d(0, 10));
	physics.start();

	MouseHandler mouseHandler = new MouseHandler();
	addMouseListener(mouseHandler);
	addMouseMotionListener(mouseHandler);

	addComponentListener(new ComponentHandler());
    }

    protected void paintComponent(Graphics g) {
	((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);

	g.setColor(Color.RED);
	for (Body body : physics.getBodies()) {
	    body.paint(g);
	}

	g.setColor(Color.RED);
	if (newBall != null)
	    newBall.paint(g);

	g.setColor(Color.BLACK);
	GraphicsUtil graphicsUtil = new GraphicsUtil(g);
	for (MassPlot massPlot : physics.getMassPlots()) {
	    graphicsUtil.fillCircle(massPlot.getPosition(), 3);
	}
	g.setColor(Color.GRAY);
	for (DumpedSpring dumpedSpring : physics.getSprings()) {
	    graphicsUtil.drawLine(dumpedSpring.getMassPlotA().getPosition(),
		    dumpedSpring.getMassPlotB().getPosition());
	}

	try {
	    Thread.sleep(10);
	} catch (InterruptedException e) {
	}
	repaint();
    }

    private class MouseHandler extends MouseAdapter {
	private static final int FLAG_NONE = 0;
	private static final int FLAG_DRAG = 1;
	private static final int FLAG_CREATE = 2;

	private MassPlot mousePlot = new MassPlot(Double.POSITIVE_INFINITY);
	private DumpedSpring dragSpring;

	private int flag;

	public void mousePressed(MouseEvent e) {
	    Vector2d mouse = new Vector2d(e.getPoint());

	    switch (e.getButton()) {
	    case MouseEvent.BUTTON1:
		for (Body body : physics.getBodies()) {
		    if (body.intersects(mouse)) {
			mousePlot.setPosition(mouse);
			dragSpring = new DumpedSpring(body.nearMassPlot(mouse),
				mousePlot, 200, 10);
			physics.addSpring(dragSpring);
			flag = FLAG_DRAG;

			return;
		    }
		}

		break;
	    case MouseEvent.BUTTON3:
		for (Body body : physics.getBodies()) {
		    if (body.intersects(mouse)) {
			physics.removeBody(body);

			return;
		    }
		}

		newBall = new Ball(20, 50, 0.8, 0.1, 80, 80, 0.1, 1.2, mouse,
			Vector2d.NULL);
		flag = FLAG_CREATE;

		break;
	    }
	}

	public void mouseDragged(MouseEvent e) {
	    Vector2d mouse = new Vector2d(e.getPoint());

	    switch (flag) {
	    case FLAG_DRAG:
		mousePlot.setPosition(mouse);

		break;
	    case FLAG_CREATE:
		double radius = newBall.getMiddle().getPosition().sub(mouse)
			.length();
		newBall = new Ball(20, radius, 0.8, 0.1, 100, 80, 0.1, 1,
			newBall.getMiddle().getPosition(), Vector2d.NULL);

		break;
	    }
	}

	public void mouseReleased(MouseEvent e) {
	    switch (flag) {
	    case FLAG_DRAG:
		physics.removeSpring(dragSpring);
		dragSpring = null;

		break;
	    case FLAG_CREATE:
		physics.addBody(newBall);
		newBall = null;

		break;
	    }

	    flag = FLAG_NONE;
	}
    }

    private class ComponentHandler extends ComponentAdapter {
	public void componentResized(ComponentEvent e) {
	    physics.removeWall(top);
	    physics.removeWall(bottom);
	    physics.removeWall(left);
	    physics.removeWall(right);

	    top = new Wall(new Vector2d(0, 0), new Vector2d(0, 1));
	    bottom = new Wall(new Vector2d(0, getHeight()), new Vector2d(0, -1));
	    left = new Wall(new Vector2d(0, 0), new Vector2d(1, 0));
	    right = new Wall(new Vector2d(getWidth(), 0), new Vector2d(-1, 0));

	    physics.addWall(top);
	    physics.addWall(bottom);
	    physics.addWall(left);
	    physics.addWall(right);
	}
    }

    public static void main(String[] args) {
	JFrame frame = new JFrame("Jumping Balls");

	JumpingBalls jumpingBalls = new JumpingBalls();

	frame.add(jumpingBalls);

	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(600, 600);
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);
    }

}