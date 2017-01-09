package mirrg.application.waterdrop.alum;

import org.apache.commons.math3.util.FastMath;
import org.lwjgl.input.Keyboard;

public class Map
{

	private int width;
	private int height;
	private double[][] v;
	private double[][] dx;
	private double[][] dy;
	//private boolean[][] wet;
	private double boardAngle = 0;

	public Map(int width, int height)
	{
		this.width = width;
		this.height = height;
		v = new double[width][height];
		dx = new double[width][height];
		dy = new double[width][height];
		v2 = new double[width][height];
		dx2 = new double[width][height];
		dy2 = new double[width][height];
		//wet = new boolean[width][height];
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public double getV(int x, int y)
	{
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x >= width) x = width - 1;
		if (y >= height) y = height - 1;
		return v[x][y];
	}

	public double getDX(int x, int y)
	{
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x >= width) x = width - 1;
		if (y >= height) y = height - 1;
		return dx[x][y];
	}

	public double getDY(int x, int y)
	{
		if (x < 0) x = 0;
		if (y < 0) y = 0;
		if (x >= width) x = width - 1;
		if (y >= height) y = height - 1;
		return dy[x][y];
	}

	/*
		public boolean getWet(int x, int y)
		{
			if (x < 0) x = 0;
			if (y < 0) y = 0;
			if (x >= width) x = width - 1;
			if (y >= height) y = height - 1;
			return wet[x][y];
		}
	*/
	public void setV(int x, int y, double value)
	{
		if (x < 0) return;
		if (y < 0) return;
		if (x >= width) return;
		if (y >= height) return;
		v[x][y] = value;
	}

	public void setDX(int x, int y, double value)
	{
		if (x < 0) return;
		if (y < 0) return;
		if (x >= width) return;
		if (y >= height) return;
		dx[x][y] = value;
	}

	public void setDY(int x, int y, double value)
	{
		if (x < 0) return;
		if (y < 0) return;
		if (x >= width) return;
		if (y >= height) return;
		dy[x][y] = value;
	}

	/*
		public void setWet(int x, int y, boolean value)
		{
			if (x < 0) return;
			if (y < 0) return;
			if (x >= width) return;
			if (y >= height) return;
			wet[x][y] = value;
		}
	*/
	public double getBoardAngle()
	{
		return boardAngle;
	}

	// -------------------------------------------------

	public void randomize()
	{
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				setV(x, y, Math.random());
				setDX(x, y, 0);
				setDY(x, y, 0);
			}
		}
	}

	private double[][] v2;
	private double[][] dx2;
	private double[][] dy2;

	public void move()
	{

		// free move
		for (int i = 0; i < 10000; i++) {
			optimize();
		}
/*
		// run
		{
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					v2[x][y] = 0;
					dx2[x][y] = 0;
					dy2[x][y] = 0;
				}
			}

			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					double velocity = Math.abs(getDX(x, y)) + Math.abs(getDY(x, y));
					if (velocity > getV(x, y)) {
						double rx = getDX(x, y) / velocity;
						double ry = getDY(x, y) / velocity;
						if (getDX(x, y) > 0) {
							if (x < width - 1) {
								v2[x + 1][y] += getV(x, y) * rx;
								dx2[x + 1][y] += getDX(x, y);
							} else {
								v2[x][y] += getV(x, y) * rx;
								dx2[x][y] += getDX(x, y);
							}
						} else {
							if (x > 0) {
								v2[x - 1][y] -= getV(x, y) * rx;
								dx2[x - 1][y] += getDX(x, y);
							} else {
								v2[x][y] -= getV(x, y) * rx;
								dx2[x][y] += getDX(x, y);
							}
						}
						if (getDY(x, y) > 0) {
							if (y < height - 1) {
								v2[x][y + 1] += getV(x, y) * ry;
								dy2[x][y + 1] += getDY(x, y);
							} else {
								v2[x][y] += getV(x, y) * ry;
								dy2[x][y] += getDY(x, y);
							}
						} else {
							if (y > 0) {
								v2[x][y - 1] -= getV(x, y) * ry;
								dy2[x][y - 1] += getDY(x, y);
							} else {
								v2[x][y] -= getV(x, y) * ry;
								dy2[x][y] += getDY(x, y);
							}
						}
					} else {
						v2[x][y] += getV(x, y) - velocity;
						if (getDX(x, y) > 0) {
							if (x < width - 1) {
								v2[x + 1][y] += getDX(x, y);
								dx2[x + 1][y] += getDX(x, y);
							} else {
								v2[x][y] += getDX(x, y);
								dx2[x][y] += getDX(x, y);
							}
						} else {
							if (x > 0) {
								v2[x - 1][y] -= getDX(x, y);
								dx2[x - 1][y] += getDX(x, y);
							} else {
								v2[x][y] -= getDX(x, y);
								dx2[x][y] += getDX(x, y);
							}
						}
						if (getDY(x, y) > 0) {
							if (y < height - 1) {
								v2[x][y + 1] += getDY(x, y);
								dy2[x][y + 1] += getDY(x, y);
							} else {
								v2[x][y] += getDY(x, y);
								dy2[x][y] += getDY(x, y);
							}
						} else {
							if (y > 0) {
								v2[x][y - 1] -= getDY(x, y);
								dy2[x][y - 1] += getDY(x, y);
							} else {
								v2[x][y] -= getDY(x, y);
								dy2[x][y] += getDY(x, y);
							}
						}
					}
				}
			}
		}

		// swap
		{
			double[][] tmp;

			tmp = v2;
			v2 = v;
			v = tmp;

			tmp = dx2;
			dx2 = dx;
			dx = tmp;

			tmp = dy2;
			dy2 = dy;
			dy = tmp;
		}
*/
		/*
		// dry/wet
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (getWet(x, y)) {
					if (getV(x, y) < 0.000001) {
						if (getV(x - 1, y) < 0.5) {
							if (getV(x, y - 1) < 0.5) {
								if (getV(x + 1, y) < 0.5) {
									if (getV(x, y + 1) < 0.5) {
										setWet(x, y, false);
									}
								}
							}
						}
					}
				} else {
					if (getV(x - 1, y) > 2) setWet(x, y, true);
					if (getV(x, y - 1) > 2) setWet(x, y, true);
					if (getV(x + 1, y) > 2) setWet(x, y, true);
					if (getV(x, y + 1) > 2) setWet(x, y, true);
				}
			}
		}
		*/

		// friction
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				setDX(x, y, getDX(x, y) * 0.95);
				setDY(x, y, getDY(x, y) * 0.95);
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

		/*
				if (getWeight(x, y) == 0) return;

				// density
				{
					double a = Math.min(getWeight(x, y) / getV(x, y), 100) - 1;

					double delta = a * 0.0001;
					setVelocity(x, y, getVelocity(x, y) + delta);
				}

				if (getWeight(x + xi, y + yi) == 0) return;

				// area
				{
					double length1 = calculateNeighbourLineLength(x, y, xi, yi);

					setLevel(x, y, getV(x, y) + 0.001);
					setLevel(x + xi, y + yi, getV(x + xi, y + yi) - 0.001);
					double length2 = calculateNeighbourLineLength(x, y, xi, yi);
					setLevel(x, y, getV(x, y) - 0.001);
					setLevel(x + xi, y + yi, getV(x + xi, y + yi) + 0.001);

					double delta = (length2 - length1) * 1000 * -0.001;
					setVelocity(x, y, getVelocity(x, y) + delta);
					setVelocity(x + xi, y + yi, getVelocity(x + xi, y + yi) - delta);
				}
				*/
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
			a += getV(x + xi, y + yi) - getV(x, y);
			a += yi * FastMath.tan(boardAngle / 180 * Math.PI);

			double d = a * -0.001; // 5;
			switch (direction) {
				case 0:
					setDX(x, y, getDX(x, y) - d);
					break;
				case 1:
					setDX(x, y, getDX(x, y) + d);
					break;
				case 2:
					setDY(x, y, getDY(x, y) - d);
					break;
				case 3:
					setDY(x, y, getDY(x, y) + d);
					break;
				default:
					throw new RuntimeException();
			}
		}
		/*
				// weight adjust
				{
					double l1 = getV(x, y);
					double l2 = getV(x + xi, y + yi);
					if (l1 > 0 && l2 > 0) {
						double w1 = getWeight(x, y);
						double w2 = getWeight(x + xi, y + yi);
						double move = w1 - (w1 + w2) * l1 / (l1 + l2);

						double d = move * 0.2;
						setWeight(x, y, getWeight(x, y) - d);
						setWeight(x + xi, y + yi, getWeight(x + xi, y + yi) + d);
					}
				}
				*/
	}

	private double calculateNeighbourLineLength(int x, int y, int xx, int yy)
	{
		double l = 0;
		{
			double o = getV(x, y);
			double a1 = getV(x - 1, y) - o;
			double b1 = getV(x, y - 1) - o;
			double c1 = getV(x + 1, y) - o;
			double d1 = getV(x, y + 1) - o;

			l += FastMath.sqrt(1 + a1 * a1);
			l += FastMath.sqrt(1 + b1 * b1);
			l += FastMath.sqrt(1 + c1 * c1);
			l += FastMath.sqrt(1 + d1 * d1);
		}
		{
			double o = getV(x + xx, y + yy);
			double a1 = getV(x + xx - 1, y + yy) - o;
			double b1 = getV(x + xx, y + yy - 1) - o;
			double c1 = getV(x + xx + 1, y + yy) - o;
			double d1 = getV(x + xx, y + yy + 1) - o;

			l += FastMath.sqrt(1 + a1 * a1);
			l += FastMath.sqrt(1 + b1 * b1);
			l += FastMath.sqrt(1 + c1 * c1);
			l += FastMath.sqrt(1 + d1 * d1);
		}
		{
			double o = getV(x, y);
			double a1 = getV(x + xx, y + yy) - o;

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
