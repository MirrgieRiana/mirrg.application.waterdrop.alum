package mirrg.application.waterdrop.alum2;

import org.apache.commons.math3.util.FastMath;
import org.lwjgl.input.Keyboard;

public class Map2
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
		//if (value < 0) throw new IllegalArgumentException("" + value);
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

	public double getBoardAngle()
	{
		return boardAngle;
	}

	//

	public void randomize()
	{
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				setLevel(x, y, Math.random() + 0.5);
				setVelocity(x, y, 0);
			}
		}
	}

	// 運動エネルギー = w v^2
	// 密度エネルギー = w log(|l|) - l
	// 位置エネルギー = w g l
	public void move()
	{

		//validate();

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

				/*
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
											double v2 = FastMath.sqrt((B - A) / w + v1 * v1);
											if (Double.isNaN(v2)) v2 = 0;

											setLevel(x, y, l2);
											setVelocity(x, y, v2);
										}
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
				*/
			}
		}

		//validate();
/*
		// move
		for (int i = 0; i < 10000; i++) {
			optimize();
		}
*/
		//validate();

	}

	/*
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
	*/
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

		//if (getWeight(x, y) == 0) return;
		/*
				// density
				{
					double a = getWeight(x, y) / getLevel(x, y) - 1;

					double delta = a * -0.1;
					setVelocity(x, y, getVelocity(x, y) - delta);
				}
		*/
		//if (getWeight(x + xi, y + yi) == 0) return;
		/*
				// area
				{
					double length1 = calculateNeighbourLineLength(x, y);

					setLevel(x, y, getLevel(x, y) + 0.001);
					double length2 = calculateNeighbourLineLength(x, y);
					setLevel(x, y, getLevel(x, y) - 0.001);

					double d = (length2 - length1) * 1000 * -0.01;
					setVelocity(x, y, getVelocity(x, y) + d);
				}
		*/
		/*
				// area
				{
					double length1 = calculateNeighbourLineLength(x, y, xi, yi);

					setLevel(x, y, getLevel(x, y) + 0.001);
					setLevel(x + xi, y + yi, getLevel(x + xi, y + yi) - 0.001);
					double length2 = calculateNeighbourLineLength(x, y, xi, yi);
					setLevel(x, y, getLevel(x, y) - 0.001);
					setLevel(x + xi, y + yi, getLevel(x + xi, y + yi) + 0.001);

					double d = (length2 - length1) * 1000 * -0.005;
					setVelocity(x, y, getVelocity(x, y) + d);
					setVelocity(x + xi, y + yi, getVelocity(x + xi, y + yi) - d);
				}*/
		/*
						// weight
						{
							double a = getWeight(x + xi, y + yi) - getWeight(x, y);

							double delta = a * -0.0001;
							setVelocity(x, y, getVelocity(x, y) + delta);
							setVelocity(x + xi, y + yi, getVelocity(x + xi, y + yi) - delta);
						}
				*/
		/*
		// gravity
		{
			double a = 0;
			a += getLevel(x + xi, y + yi) - getLevel(x, y);
			a += yi * FastMath.tan(boardAngle / 180 * Math.PI);

			double d = a * 0.0001;
			setVelocity(x, y, getVelocity(x, y) + d);
			setVelocity(x + xi, y + yi, getVelocity(x + xi, y + yi) - d);
		}*/
		/*
				// gravity
				{
					double a = 0;
					a += getLevel(x + xi, y + yi) - getLevel(x, y);
					a += yi * FastMath.tan(boardAngle / 180 * Math.PI);

					double d = a * -0.01;
					if (d > getLevel(x, y)) d = getLevel(x, y);
					if (-d > getLevel(x + xi, y + yi)) d = -getLevel(x + xi, y + yi);
					setLevel(x, y, getLevel(x, y) - d);
					setLevel(x + xi, y + yi, getLevel(x + xi, y + yi) + d);
					validate(x, y);
				}
		*/
		/*
		// 密度の低いほうに質量が移動する
		a:
		{
			if (x + xi < 0) break a;
			if (y + yi < 0) break a;
			if (x + xi > width) break a;
			if (y + yi > height) break a;

			double l1 = getLevel(x, y);
			double l2 = getLevel(x + xi, y + yi);
			if (l1 > 0 && l2 > 0) {
				double w1 = getWeight(x, y);
				double w2 = getWeight(x + xi, y + yi);
				double move = w1 - (w1 + w2) * l1 / (l1 + l2);

				double d = move * 0.5;
				if (d > getWeight(x, y)) d = getWeight(x, y);
				if (-d > getWeight(x + xi, y + yi)) d = -getWeight(x + xi, y + yi);
				setWeight(x, y, getWeight(x, y) - d);
				setWeight(x + xi, y + yi, getWeight(x + xi, y + yi) + d);
				validate(x, y);
			}
		}
		*/
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
