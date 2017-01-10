package mirrg.application.waterdrop.alum.v2;

import org.apache.commons.math3.util.FastMath;
import org.lwjgl.input.Keyboard;

import mirrg.application.waterdrop.alum.core.IMap;
import mirrg.application.waterdrop.alum.core.InputState;
import mirrg.application.waterdrop.alum.g.PhaseMain;

public class Map2 implements IMap
{

	private int width;
	private int height;
	private double[] level;
	private double[] velocity;
	private double boardAngle = 0;

	public Map2(int width, int height)
	{
		this.width = width;
		this.height = height;
		level = new double[width * height];
		velocity = new double[width * height];

		randomize();
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public double getLevel(int x, int y)
	{
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x >= width) x = width - 1;
		if (y >= height) y = height - 1;
		return level[x + y * width];
	}

	public double getVelocity(int x, int y)
	{
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x >= width) x = width - 1;
		if (y >= height) y = height - 1;
		return velocity[x + y * width];
	}

	public void setLevel(int x, int y, double value)
	{
		if (!Double.isFinite(value)) throw new IllegalArgumentException("" + value);
		if (x < 0) return;
		if (y < 0) return;
		if (x >= width) return;
		if (y >= height) return;
		level[x + y * width] = value;
	}

	public void setVelocity(int x, int y, double value)
	{
		if (!Double.isFinite(value)) throw new IllegalArgumentException("" + value);
		if (x < 0) return;
		if (y < 0) return;
		if (x >= width) return;
		if (y >= height) return;
		velocity[x + y * width] = value;
	}

	@Override
	public double getBoardAngle()
	{
		return boardAngle;
	}

	//

	@Override
	public void randomize()
	{
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				setLevel(x, y, Math.random() + 0.5);
				setVelocity(x, y, 0);
			}
		}
	}

	@Override
	public void move(PhaseMain phase)
	{

		{
			double x = phase.cameraX;
			double y = phase.cameraY;
			double z = phase.cameraZ;
			double dx = 30 * Math.cos(phase.cameraV / 180 * Math.PI) * Math.sin(phase.cameraH / 180 * Math.PI);
			double dy = 30 * Math.sin(phase.cameraV / 180 * Math.PI);
			double dz = 30 * Math.cos(phase.cameraV / 180 * Math.PI) * Math.cos(phase.cameraH / 180 * Math.PI);

			if (InputState.mb[0] > 0 && (InputState.mb[0] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorVelocity(this, x, y, z, dx, dy, dz, 10, 0));
			}
			if (InputState.mb[1] > 0 && (InputState.mb[1] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorLevel(this, x, y, z, dx, dy, dz, 10, 0));
			}
			if (InputState.kb[Keyboard.KEY_1] > 0 && (InputState.kb[Keyboard.KEY_1] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorVelocity(this, x, y, z, dx, dy, dz, 10, 3));
			}
			if (InputState.kb[Keyboard.KEY_2] > 0 && (InputState.kb[Keyboard.KEY_2] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorVelocity(this, x, y, z, dx, dy, dz, 100, 5));
			}
			if (InputState.kb[Keyboard.KEY_3] > 0 && (InputState.kb[Keyboard.KEY_3] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorVelocity(this, x, y, z, dx, dy, dz, 1000, 10));
			}
			if (InputState.kb[Keyboard.KEY_4] > 0 && (InputState.kb[Keyboard.KEY_4] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorLevel(this, x, y, z, dx, dy, dz, 10, 3));
			}
			if (InputState.kb[Keyboard.KEY_5] > 0 && (InputState.kb[Keyboard.KEY_5] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorLevel(this, x, y, z, dx, dy, dz, 100, 5));
			}
			if (InputState.kb[Keyboard.KEY_6] > 0 && (InputState.kb[Keyboard.KEY_6] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorLevel(this, x, y, z, dx, dy, dz, 1000, 10));
			}
		}

		// run
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				// 速度から変位に
				setLevel(x, y, getLevel(x, y) + getVelocity(x, y));

				// 摩擦
				setLevel(x, y, (getLevel(x, y) - 1) * 0.98 + 1);
				setVelocity(x, y, getVelocity(x, y) * 0.98);

				// 表面積最小
				{
					double length1 = calculateNeighbourLineLength(x, y);

					setLevel(x, y, getLevel(x, y) + 0.001);
					double length2 = calculateNeighbourLineLength(x, y);
					setLevel(x, y, getLevel(x, y) - 0.001);

					double d = (length2 - length1) * 1000 * -0.01;
					setVelocity(x, y, getVelocity(x, y) + d);
				}

			}
		}

	}

	private double calculateNeighbourLineLength(int x, int y)
	{
		double l = 0;
		{
			double o = getLevel(x, y);
			double a1 = getLevel(x - 1, y) - o;
			double b1 = getLevel(x, y - 1) - o;
			double c1 = getLevel(x + 1, y) - o;
			double d1 = getLevel(x, y + 1) - o;

			l += FastMath.sqrt(1 + a1 * a1);
			l += FastMath.sqrt(1 + b1 * b1);
			l += FastMath.sqrt(1 + c1 * c1);
			l += FastMath.sqrt(1 + d1 * d1);
		}
		return l;
	}

	@Override
	public void input(double speed)
	{
		if (InputState.kb[Keyboard.KEY_0] == 1) boardAngle = 0;
		if (InputState.kb[Keyboard.KEY_ADD] > 0) boardAngle += speed * 0.1;
		if (InputState.kb[Keyboard.KEY_SUBTRACT] > 0) boardAngle -= speed * 0.1;
		if (InputState.kb[Keyboard.KEY_SEMICOLON] > 0) boardAngle += speed * 0.1;
		if (InputState.kb[Keyboard.KEY_MINUS] > 0) boardAngle -= speed * 0.1;
		if (boardAngle > 89.99) boardAngle = 89.99;
		if (boardAngle < -89.99) boardAngle = -89.99;
	}

	@Override
	public void getWireColor(float[] dest, int x, int y)
	{
		double value = getVelocity(x, y);
		dest[0] = (float) value;
		dest[1] = 0.0f;
		dest[2] = (float) (-value);
	}

	@Override
	public void getBoardColor(float[] dest, int x, int y)
	{
		if (getLevel(x, y) == 0) {
			dest[0] = 0;
			dest[1] = 0;
			dest[2] = 0;
		} else {
			float c = (float) fold(getLevel(x, y) / 1) * 0.8f + 0.2f;
			dest[0] = c;
			dest[1] = c;
			dest[2] = c;
		}
	}

	private double fold(double c)
	{
		c /= 2;
		c = c - (int) c;
		if (c > 0.5) c = 1 - c;
		c *= 2;
		return c;
	}

}
