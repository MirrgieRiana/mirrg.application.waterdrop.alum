package mirrg.application.waterdrop.alum;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.awt.Point;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class PhaseMain extends PhaseBase
{

	private Cursor cursor;

	double cameraX;
	double cameraY;
	double cameraZ;
	double cameraH;
	double cameraV;

	Map1 map;

	private boolean showGrid = true;
	private boolean showWire = true;
	private boolean showFloor = true;

	public PhaseMain()
	{
		map = new Map1(100, 100);
		map.randomize();

		resetView();

		try {
			cursor = new Cursor(1, 1, 0, 0, 1, IntBuffer.allocate(1), IntBuffer.allocate(1));
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
	}

	private void resetView()
	{
		cameraX = 0;
		cameraY = 1000;
		cameraZ = -1000;
		cameraH = 0;
		cameraV = -45;
	}

	private boolean ignore;

	@Override
	public void onSwitched()
	{
		setMouseLocation();
		ignore = true;
	}

	@Override
	protected Cursor getCursor()
	{
		return cursor;
	}

	@Override
	protected void render()
	{

		// background
		glClearColor(0.5f, 1.0f, 1.0f, 1.0f);

		// fog
		{
			glFogi(GL_FOG_MODE, GL_LINEAR);
			glFogi(GL_FOG_START, 0);
			glFogi(GL_FOG_END, 10000);
			glEnable(GL_FOG);
		}

		glPushMatrix();
		{
			{
				double x = Math.cos(cameraV / 180 * Math.PI) * Math.sin(cameraH / 180 * Math.PI);
				double y = Math.sin(cameraV / 180 * Math.PI);
				double z = Math.cos(cameraV / 180 * Math.PI) * Math.cos(cameraH / 180 * Math.PI);

				gluLookAt(
					(float) cameraX, (float) cameraY, (float) cameraZ,
					(float) (cameraX + x), (float) (cameraY + y), (float) (cameraZ + z),
					0, 1, 0);
			}

			//

			glPushMatrix();
			{
				glPushMatrix();
				{
					glRotated(-map.getBoardAngle(), 1, 0, 0);
					glTranslated(-1000, 0, -1000);
					glScaled(20, 20, 20);

					if (showGrid) {
						glColor3f(0.0f, 1.0f, 0.0f);
						glBegin(GL_LINES);
						{
							for (int x = 0; x < map.getWidth(); x++) {
								glVertex3f(x, 0, 0);
								glVertex3f(x, 0, map.getHeight() - 1);
							}
							for (int y = 0; y < map.getHeight(); y++) {
								glVertex3f(0, 0, y);
								glVertex3f(map.getWidth() - 1, 0, y);
							}
						}
						glEnd();
					}

					if (showWire) {
						glBegin(GL_LINES);
						{
							for (int x = 0; x < map.getWidth(); x++) {
								for (int y = 0; y < map.getHeight(); y++) {
									if (map.getWeight(x, y) == 0) {
										glColor3f(0, 0, 0);
									} else {
										double density = map.getWeight(x, y) / map.getLevel(x, y);
										glColor3f((float) ((density - 1) / 10), 0.0f, (float) (1 - density));
									}
									if (x != map.getWidth() - 1) {
										glVertex3f(x, (float) map.getLevel(x, y) + 0.05f, y);
										glVertex3f(x + 1, (float) map.getLevel(x + 1, y) + 0.05f, y);
									}
									if (y != map.getHeight() - 1) {
										glVertex3f(x, (float) map.getLevel(x, y) + 0.05f, y);
										glVertex3f(x, (float) map.getLevel(x, y + 1) + 0.05f, y + 1);
									}
								}
							}
						}
						glEnd();
					}

					if (showFloor) {
						glBegin(GL_QUADS);
						for (int x = 0; x < map.getWidth() - 1; x++) {
							for (int y = 0; y < map.getHeight() - 1; y++) {
								float c;

								if (map.getWeight(x, y) == 0) {
									c = 0;
								} else {
									c = (float) fold(map.getWeight(x, y) / 100) * 0.8f + 0.2f;
								}
								glColor3f(c, c, c);
								glVertex3f(x, (float) map.getLevel(x, y), y);

								if (map.getWeight(x + 1, y) == 0) {
									c = 0;
								} else {
									c = (float) fold(map.getWeight(x + 1, y) / 100) * 0.8f + 0.2f;
								}
								glColor3f(c, c, c);
								glVertex3f(x + 1, (float) map.getLevel(x + 1, y), y);

								if (map.getWeight(x + 1, y + 1) == 0) {
									c = 0;
								} else {
									c = (float) fold(map.getWeight(x + 1, y + 1) / 100) * 0.8f + 0.2f;
								}
								glColor3f(c, c, c);
								glVertex3f(x + 1, (float) map.getLevel(x + 1, y + 1), y + 1);

								if (map.getWeight(x, y + 1) == 0) {
									c = 0;
								} else {
									c = (float) fold(map.getWeight(x, y + 1) / 100) * 0.8f + 0.2f;
								}
								glColor3f(c, c, c);
								glVertex3f(x, (float) map.getLevel(x, y + 1), y + 1);

							}
						}
						glEnd();
					}

				}
				glPopMatrix();

				entities.forEach(IEntity::render);

			}
			glPopMatrix();
		}
		glPopMatrix();

		Alum.make2D();
		{
			glBegin(GL_LINES);
			{
				double length = 10;
				double length2 = length / Math.sqrt(2);

				glColor3f(0, 0, 0);
				glVertex3d(Alum.getWidth() / 2 - length, Alum.getHeight() / 2, 0);
				glVertex3d(Alum.getWidth() / 2 + length, Alum.getHeight() / 2, 0);
				glVertex3d(Alum.getWidth() / 2, Alum.getHeight() / 2 - length, 0);
				glVertex3d(Alum.getWidth() / 2, Alum.getHeight() / 2 + length, 0);

				glColor3f(1, 1, 1);
				glVertex3d(Alum.getWidth() / 2 - length2, Alum.getHeight() / 2 - length2, 0);
				glVertex3d(Alum.getWidth() / 2 + length2, Alum.getHeight() / 2 + length2, 0);
				glVertex3d(Alum.getWidth() / 2 + length2, Alum.getHeight() / 2 - length2, 0);
				glVertex3d(Alum.getWidth() / 2 - length2, Alum.getHeight() / 2 + length2, 0);
			}
			glEnd();
		}
		Alum.make3D();
	}

	private double fold(double c)
	{
		c /= 2;
		c = c - (int) c;
		if (c > 0.5) c = 1 - c;
		c *= 2;
		return c;
	}

	@Override
	protected IPhase input()
	{
		setMouseLocation();

		if (ignore) {
			ignore = false;
			return this;
		}

		double speed = 4;
		if (InputState.kb[Keyboard.KEY_LCONTROL] > 0) speed *= 4;

		if (InputState.kb[Keyboard.KEY_SPACE] > 0) cameraY += speed;
		if (InputState.kb[Keyboard.KEY_LSHIFT] > 0) cameraY -= speed;
		map.input(speed);
		if (InputState.kb[Keyboard.KEY_W] > 0) {
			cameraX += speed * Math.cos((cameraH + 270) / 180 * Math.PI);
			cameraZ -= speed * Math.sin((cameraH + 270) / 180 * Math.PI);
		}
		if (InputState.kb[Keyboard.KEY_D] > 0) {
			cameraX += speed * Math.cos((cameraH + 180) / 180 * Math.PI);
			cameraZ -= speed * Math.sin((cameraH + 180) / 180 * Math.PI);
		}
		if (InputState.kb[Keyboard.KEY_S] > 0) {
			cameraX += speed * Math.cos((cameraH + 90) / 180 * Math.PI);
			cameraZ -= speed * Math.sin((cameraH + 90) / 180 * Math.PI);
		}
		if (InputState.kb[Keyboard.KEY_A] > 0) {
			cameraX += speed * Math.cos((cameraH + 0) / 180 * Math.PI);
			cameraZ -= speed * Math.sin((cameraH + 0) / 180 * Math.PI);
		}

		if (InputState.kb[Keyboard.KEY_F12] == 1) resetView();

		if (InputState.kb[Keyboard.KEY_ESCAPE] == 1) return new PhasePause(this);
		if (InputState.kb[Keyboard.KEY_E] == 1) return new PhasePause(this);
		if (!Alum.frame.isActive()) return new PhasePause(this);
		//if (Alum.getMouseLocation().x < Alum.frame.getX()) return new PhasePause(this);
		//if (Alum.getMouseLocation().y < Alum.frame.getY()) return new PhasePause(this);
		//if (Alum.getMouseLocation().x >= Alum.frame.getX() + Alum.frame.getWidth()) return new PhasePause(this);
		//if (Alum.getMouseLocation().y >= Alum.frame.getY() + Alum.frame.getHeight()) return new PhasePause(this);

		if (InputState.kb[Keyboard.KEY_F1] == 1) showGrid = !showGrid;
		if (InputState.kb[Keyboard.KEY_F2] == 1) showWire = !showWire;
		if (InputState.kb[Keyboard.KEY_F3] == 1) showFloor = !showFloor;

		{
			cameraH -= 0.5 * InputState.dx;
			cameraV -= 0.5 * InputState.dy;
			if (cameraV > 89.99) cameraV = 89.99;
			if (cameraV < -89.99) cameraV = -89.99;
		}
		if (InputState.dw != 0) {
			double x = Math.cos(cameraV / 180 * Math.PI) * Math.sin(cameraH / 180 * Math.PI);
			double y = Math.sin(cameraV / 180 * Math.PI);
			double z = Math.cos(cameraV / 180 * Math.PI) * Math.cos(cameraH / 180 * Math.PI);
			cameraX += speed * 0.5 * x * InputState.dw;
			cameraY += speed * 0.5 * y * InputState.dw;
			cameraZ += speed * 0.5 * z * InputState.dw;
		}
		{
			double x = cameraX;
			double y = cameraY;
			double z = cameraZ;
			double dx = 30 * Math.cos(cameraV / 180 * Math.PI) * Math.sin(cameraH / 180 * Math.PI);
			double dy = 30 * Math.sin(cameraV / 180 * Math.PI);
			double dz = 30 * Math.cos(cameraV / 180 * Math.PI) * Math.cos(cameraH / 180 * Math.PI);

			if (InputState.mb[0] > 0 && (InputState.mb[0] - 1) % 20 == 0) {
				addEntity(new EntityMeteor(this, x, y, z, dx, dy, dz, 100));
			}
			if (InputState.mb[1] > 0 && (InputState.mb[1] - 1) % 20 == 0) {
				addEntity(new EntityMeteor(this, x, y, z, dx, dy, dz, -100));
			}
			if (InputState.kb[Keyboard.KEY_1] > 0 && (InputState.kb[Keyboard.KEY_1] - 1) % 20 == 0) {
				addEntity(new EntityMeteor(this, x, y, z, dx, dy, dz, 1000));
			}
			if (InputState.kb[Keyboard.KEY_2] > 0 && (InputState.kb[Keyboard.KEY_2] - 1) % 20 == 0) {
				addEntity(new EntityMeteor(this, x, y, z, dx, dy, dz, 10000));
			}
		}
		if (InputState.mb[2] == 1) {
			map.randomize();
		}

		//map.copy();
		synchronized (this) {
			Iterator<IEntity> i = entities.iterator();
			while (i.hasNext()) {
				IEntity entity = i.next();
				if (!entity.move()) i.remove();
			}
		}
		//map.flip();

		map.move();

		return this;
	}

	private ArrayList<IEntity> entities = new ArrayList<>();

	private void addEntity(IEntity entity)
	{
		synchronized (this) {
			entities.add(entity);
		}
	}

	private void setMouseLocation()
	{
		Point a = Alum.getMouseLocation();
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		Point b = Alum.getMouseLocation();

		InputState.x += b.x - a.x;
		InputState.y += b.y - a.y;
	}

}
