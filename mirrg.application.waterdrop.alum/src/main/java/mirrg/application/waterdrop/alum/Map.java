package mirrg.application.waterdrop.alum;

import org.apache.commons.math3.util.FastMath;
import org.lwjgl.input.Keyboard;

public class Map
{

	private int width;
	private int height;
	private double[] level;
	private double[] weight;
	private double[] velocity;
	private double boardAngle = 0;

	public Map(int width, int height)
	{
		this.width = width;
		this.height = height;
		level = new double[width * height];
		weight = new double[width * height];
		velocity = new double[width * height];
		weight2 = new double[width * height];
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public double getLevel(int x, int y)
	{
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x >= width) x = width - 1;
		if (y >= height) y = height - 1;
		return level[x + y * width];
	}

	public double getLevel(double x, double y)
	{
		int x1 = (int) x;
		int y1 = (int) y;
		int x2 = (int) x + 1;
		int y2 = (int) y + 1;

		double a = getLevel(x1, y1);
		double b = getLevel(x2, y1);
		double c = getLevel(x1, y2);
		double d = getLevel(x2, y2);

		double rx = x - x1;
		double ry = y - y1;

		double e = a * (1 - rx) + b * rx;
		double f = c * (1 - rx) + d * rx;

		double g = e * (1 - ry) + f * ry;

		return g;
	}

	public double getWeight(int x, int y)
	{
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x >= width) x = width - 1;
		if (y >= height) y = height - 1;
		return weight[x + y * width];
	}

	public double getWeight(double x, double y)
	{
		int x1 = (int) x;
		int y1 = (int) y;
		int x2 = (int) x + 1;
		int y2 = (int) y + 1;

		double a = getWeight(x1, y1);
		double b = getWeight(x2, y1);
		double c = getWeight(x1, y2);
		double d = getWeight(x2, y2);

		double rx = x - x1;
		double ry = y - y1;

		double e = a * (1 - rx) + b * rx;
		double f = c * (1 - rx) + d * rx;

		double g = e * (1 - ry) + f * ry;

		return g;
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
		if (x < 0) return;
		if (y < 0) return;
		if (x >= width) return;
		if (y >= height) return;
		level[x + y * width] = value;
	}

	public void setWeight(int x, int y, double value)
	{
		if (x < 0) return;
		if (y < 0) return;
		if (x >= width) return;
		if (y >= height) return;
		weight[x + y * width] = value;
	}

	public void setVelocity(int x, int y, double value)
	{
		if (x < 0) return;
		if (y < 0) return;
		if (x >= width) return;
		if (y >= height) return;
		velocity[x + y * width] = value;
	}

	public double getBoardAngle()
	{
		return boardAngle;
	}

	//

	public void randomize()
	{
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				setWeight(x, y, Math.random());
				setLevel(x, y, 1);
				setVelocity(x, y, 0);
			}
		}
	}

	private double[] weight2;

	public void move()
	{

		validate();

		// move
		for (int i = 0; i < 10000; i++) {
			optimize();
		}

		validate();

		// run
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				setLevel(x, y, getLevel(x, y) + getVelocity(x, y));
			}
		}

		// friction
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				setVelocity(x, y, getVelocity(x, y) * 0.95);
			}
		}

		validate();

	}

	private void validate()
	{
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (getWeight(x, y) <= 0) {
					setLevel(x, y, 0);
					setWeight(x, y, 0);
					setVelocity(x, y, 0);
				} else if (getLevel(x, y) <= 0) {
					setLevel(x, y, 0.0001);
					setVelocity(x, y, 0);
				}
			}
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
				xi = 0;
				yi = -1;
				break;
			case 2:
				xi = 1;
				yi = 0;
				break;
			case 3:
				xi = 0;
				yi = 1;
				break;
			default:
				throw new RuntimeException();
		}

		if (getWeight(x, y) == 0) return;

		// density
		{
			double a = Math.min(getWeight(x, y) / getLevel(x, y), 100) - 1;

			double delta = a * 0.0001;
			setVelocity(x, y, getVelocity(x, y) + delta);
		}

		if (getWeight(x + xi, y + yi) == 0) return;

		// area
		{
			double length1 = calculateNeighbourLineLength(x, y, xi, yi);

			setLevel(x, y, getLevel(x, y) + 0.001);
			setLevel(x + xi, y + yi, getLevel(x + xi, y + yi) - 0.001);
			double length2 = calculateNeighbourLineLength(x, y, xi, yi);
			setLevel(x, y, getLevel(x, y) - 0.001);
			setLevel(x + xi, y + yi, getLevel(x + xi, y + yi) + 0.001);

			double delta = (length2 - length1) * 1000 * -0.001;
			setVelocity(x, y, getVelocity(x, y) + delta);
			setVelocity(x + xi, y + yi, getVelocity(x + xi, y + yi) - delta);
		}
		/*
						// weight
						{
							double a = getWeight(x + xi, y + yi) - getWeight(x, y);

							double delta = a * -0.0001;
							setVelocity(x, y, getVelocity(x, y) + delta);
							setVelocity(x + xi, y + yi, getVelocity(x + xi, y + yi) - delta);
						}
				*/
		// gravity
		{
			double a = 0;
			a += getLevel(x + xi, y + yi) - getLevel(x, y);
			a += yi * FastMath.tan(boardAngle / 180 * Math.PI);

			double d = a * -5;
			if (d > getWeight(x, y)) d = getWeight(x, y);
			if (-d > getWeight(x + xi, y + yi)) d = -getWeight(x + xi, y + yi);
			setWeight(x, y, getWeight(x, y) - d);
			setWeight(x + xi, y + yi, getWeight(x + xi, y + yi) + d);
		}

		// weight adjust
		{
			double l1 = getLevel(x, y);
			double l2 = getLevel(x + xi, y + yi);
			if (l1 > 0 && l2 > 0) {
				double w1 = getWeight(x, y);
				double w2 = getWeight(x + xi, y + yi);
				double move = w1 - (w1 + w2) * l1 / (l1 + l2);

				double d = move * 0.2;
				setWeight(x, y, getWeight(x, y) - d);
				setWeight(x + xi, y + yi, getWeight(x + xi, y + yi) + d);
			}
		}

	}

	private double calculateNeighbourLineLength(int x, int y, int xx, int yy)
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
		{
			double o = getLevel(x + xx, y + yy);
			double a1 = getLevel(x + xx - 1, y + yy) - o;
			double b1 = getLevel(x + xx, y + yy - 1) - o;
			double c1 = getLevel(x + xx + 1, y + yy) - o;
			double d1 = getLevel(x + xx, y + yy + 1) - o;

			l += FastMath.sqrt(1 + a1 * a1);
			l += FastMath.sqrt(1 + b1 * b1);
			l += FastMath.sqrt(1 + c1 * c1);
			l += FastMath.sqrt(1 + d1 * d1);
		}
		{
			double o = getLevel(x, y);
			double a1 = getLevel(x + xx, y + yy) - o;

			l -= FastMath.sqrt(1 + a1 * a1);
		}
		return l;
	}

	/*
		public void copy()
		{
			System.arraycopy(map, 0, map2, 0, map.length);
		}

		public void flip()
		{
			double[] tmp = map2;
			map2 = map;
			map = tmp;
		}
	*/
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

	/*
	public void flatten()
	{
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				set(x, y, (get(x, y) + get(x + 1, y) + get(x, y + 1) + get(x - 1, y) + get(x, y - 1)) / 5);
			}
		}
	}
	*/

}
