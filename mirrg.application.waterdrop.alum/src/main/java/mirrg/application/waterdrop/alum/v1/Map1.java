package mirrg.application.waterdrop.alum.v1;

import org.apache.commons.math3.util.FastMath;
import org.lwjgl.input.Keyboard;

import mirrg.application.waterdrop.alum.core.IMap;
import mirrg.application.waterdrop.alum.core.InputState;
import mirrg.application.waterdrop.alum.g.PhaseMain;

public class Map1 implements IMap
{

	private int width;
	private int height;
	private double[] level;
	private double[] weight;
	private double[] velocity;
	private double boardAngle = 0;

	public Map1(int width, int height)
	{
		this.width = width;
		this.height = height;
		level = new double[width * height];
		weight = new double[width * height];
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

	public double getWeight(int x, int y)
	{
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x >= width) x = width - 1;
		if (y >= height) y = height - 1;
		return weight[x + y * width];
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
		if (value < 0) throw new IllegalArgumentException("" + value);
		if (x < 0) return;
		if (y < 0) return;
		if (x >= width) return;
		if (y >= height) return;
		level[x + y * width] = value;
	}

	public void setWeight(int x, int y, double value)
	{
		if (!Double.isFinite(value)) throw new IllegalArgumentException("" + value);
		if (value < 0) throw new IllegalArgumentException("" + value);
		if (x < 0) return;
		if (y < 0) return;
		if (x >= width) return;
		if (y >= height) return;
		weight[x + y * width] = value;
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
				setWeight(x, y, Math.random() + 0.5);
				setLevel(x, y, 1);
				setVelocity(x, y, 0);
			}
		}
	}

	// 運動エネルギー = w v^2
	// 密度エネルギー = w log(|l|) - l
	// 位置エネルギー = w g l
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
				phase.addEntity(new EntityMeteorMap1(this, x, y, z, dx, dy, dz, 100, 0));
			}
			if (InputState.mb[1] > 0 && (InputState.mb[1] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorMap1(this, x, y, z, dx, dy, dz, -100, 0));
			}
			if (InputState.kb[Keyboard.KEY_1] > 0 && (InputState.kb[Keyboard.KEY_1] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorMap1(this, x, y, z, dx, dy, dz, 1000, 0));
			}
			if (InputState.kb[Keyboard.KEY_2] > 0 && (InputState.kb[Keyboard.KEY_2] - 1) % 20 == 0) {
				phase.addEntity(new EntityMeteorMap1(this, x, y, z, dx, dy, dz, 10000, 0));
			}
		}

		validate();

		// run
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {

				if (getWeight(x, y) != 0) {

					// 密度から速度に
					// v' = v + c1(d - 1)
					{
						double l = getLevel(x, y);
						double w = getWeight(x, y);
						double d = w / l;
						double f = d - 1;

						setVelocity(x, y, getVelocity(x, y) + f);
					}

					// 収縮
					{
						double l = getLevel(x, y);
						double w = getWeight(x, y);
						double d = w / l;
						if (d < 1) {
							double f = (l - d) * -0.5;

							setVelocity(x, y, getVelocity(x, y) + f);
						}
					}

					// 速度から位置に
					// l' = l + v
					{
						double v1 = getVelocity(x, y);
						double l1 = getLevel(x, y);
						double w = getWeight(x, y);

						double l2 = l1 + 0.01 * v1;

						if (l2 == 0) {
							setLevel(x, y, 0.001);
							setVelocity(x, y, -v1);
						} else if (l2 < 0) {
							setLevel(x, y, -l2);
							setVelocity(x, y, -v1);
						} else {
							double A = w * FastMath.log(l1) - l1;
							double B = w * FastMath.log(l2) - l2;
							double v2 = FastMath.sqrt((A - B) / w + v1 * v1);
							if (v1 < 0) v2 = -v2;
							if (Double.isNaN(v2)) v2 = 0;

							setLevel(x, y, l2);
							setVelocity(x, y, v2);
						}
					}

					// 面積最小
					{
						double length1 = calculateNeighbourLineLength(x, y);

						setLevel(x, y, getLevel(x, y) + 0.001);
						double length2 = calculateNeighbourLineLength(x, y);
						setLevel(x, y, getLevel(x, y) - 0.001);

						double d = (length2 - length1) * 1000 * -1;
						setVelocity(x, y, getVelocity(x, y) + d);
					}

					// 抵抗
					{
						double v = getVelocity(x, y);
						setVelocity(x, y, v * 0.95);
					}

					// 乾く
					{
						if (getWeight(x, y) < 0.00001) setWeight(x, y, 0);
					}

				} else {

					// 濡れる
					a:
					{
						int xi;
						int yi;
						{
							xi = -1;
							yi = 0;
							if (getWeight(x + xi, y + yi) > 2) {
								setWeight(x, y, getWeight(x + xi, y + yi) / 2);
								setWeight(x + xi, y + yi, getWeight(x + xi, y + yi) / 2);
								setLevel(x, y, getLevel(x + xi, y + yi) / 2);
								setLevel(x + xi, y + yi, getLevel(x + xi, y + yi) / 2);
								break a;
							}
						}
						{
							xi = 1;
							yi = 0;
							if (getWeight(x + xi, y + yi) > 2) {
								setWeight(x, y, getWeight(x + xi, y + yi) / 2);
								setWeight(x + xi, y + yi, getWeight(x + xi, y + yi) / 2);
								setLevel(x, y, getLevel(x + xi, y + yi) / 2);
								setLevel(x + xi, y + yi, getLevel(x + xi, y + yi) / 2);
								break a;
							}
						}
						{
							xi = 0;
							yi = -1;
							if (getWeight(x + xi, y + yi) > 2) {
								setWeight(x, y, getWeight(x + xi, y + yi) / 2);
								setWeight(x + xi, y + yi, getWeight(x + xi, y + yi) / 2);
								setLevel(x, y, getLevel(x + xi, y + yi) / 2);
								setLevel(x + xi, y + yi, getLevel(x + xi, y + yi) / 2);
								break a;
							}
						}
						{
							xi = 0;
							yi = 1;
							if (getWeight(x + xi, y + yi) > 2) {
								setWeight(x, y, getWeight(x + xi, y + yi) / 2);
								setWeight(x + xi, y + yi, getWeight(x + xi, y + yi) / 2);
								setLevel(x, y, getLevel(x + xi, y + yi) / 2);
								setLevel(x + xi, y + yi, getLevel(x + xi, y + yi) / 2);
								break a;
							}
						}
					}

				}

			}
		}

		validate();

		// move
		for (int i = 0; i < 10000; i++) {
			optimize();
		}

		validate();

	}

	private void validate()
	{
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (getWeight(x, y) == 0) {
					setLevel(x, y, 0);
					setWeight(x, y, 0);
					setVelocity(x, y, 0);
				} else if (getLevel(x, y) == 0) {
					setLevel(x, y, 0.001);
					setVelocity(x, y, 0);
				}
			}
		}
	}

	private void validate(int x, int y)
	{
		if (getWeight(x, y) == 0) {
			setLevel(x, y, 0);
			setWeight(x, y, 0);
			setVelocity(x, y, 0);
		} else if (getLevel(x, y) == 0) {
			setLevel(x, y, 0.001);
			setVelocity(x, y, 0);
		}
	}

	public void optimize()
	{
		int x = (int) (Math.random() * width);
		int y = (int) (Math.random() * height);
		int direction = (int) (Math.random() * 4);
		int xi;
		int yi;
		switch (direction) {
			case 0:
				xi = -1;
				yi = 0;
				break;
			case 1:
				xi = 1;
				yi = 0;
				break;
			case 2:
				xi = 0;
				yi = -1;
				break;
			case 3:
				xi = 0;
				yi = 1;
				break;
			default:
				throw new RuntimeException();
		}

		if (getWeight(x, y) == 0) return;
		if (getWeight(x + xi, y + yi) == 0) return;

		// gravity
		a:
		{
			if (x + xi < 0) break a;
			if (y + yi < 0) break a;
			if (x + xi >= width) break a;
			if (y + yi >= height) break a;

			double a = 0;
			a += getLevel(x + xi, y + yi) - getLevel(x, y);
			a += yi * FastMath.tan(boardAngle / 180 * Math.PI);

			double d = a * -0.2;
			if (d > getWeight(x, y)) d = getWeight(x, y);
			if (-d > getWeight(x + xi, y + yi)) d = -getWeight(x + xi, y + yi);
			setWeight(x, y, getWeight(x, y) - d);
			setWeight(x + xi, y + yi, getWeight(x + xi, y + yi) + d);
			validate(x, y);
		}

		// 密度の低いほうに質量が移動する
		a:
		{
			if (x + xi < 0) break a;
			if (y + yi < 0) break a;
			if (x + xi >= width) break a;
			if (y + yi >= height) break a;

			double l1 = getLevel(x, y);
			double l2 = getLevel(x + xi, y + yi);
			if (l1 > 0 && l2 > 0) {
				double w1 = getWeight(x, y);
				double w2 = getWeight(x + xi, y + yi);
				double move = l1 * (w1 + w2) / (l1 + l2) - w1;

				double d = move * -0.5;
				if (d > getWeight(x, y)) d = getWeight(x, y);
				if (-d > getWeight(x + xi, y + yi)) d = -getWeight(x + xi, y + yi);
				setWeight(x, y, getWeight(x, y) - d);
				setWeight(x + xi, y + yi, getWeight(x + xi, y + yi) + d);
				validate(x, y);
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
		if (getWeight(x, y) == 0) {
			dest[0] = 0;
			dest[1] = 0;
			dest[2] = 0;
		} else {
			double density = getWeight(x, y) / getLevel(x, y);
			dest[0] = (float) ((density - 1) / 10);
			dest[1] = 0;
			dest[2] = (float) (1 - density);
		}
	}

	@Override
	public void getBoardColor(float[] dest, int x, int y)
	{
		if (getLevel(x, y) == 0) {
			dest[0] = 0;
			dest[1] = 0;
			dest[2] = 0;
		} else {
			float c = (float) fold(getWeight(x, y) / 100) * 0.5f + 0.5f;
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
