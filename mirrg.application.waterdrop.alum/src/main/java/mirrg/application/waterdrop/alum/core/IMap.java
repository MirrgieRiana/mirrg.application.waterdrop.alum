package mirrg.application.waterdrop.alum.core;

import mirrg.application.waterdrop.alum.g.PhaseMain;

public interface IMap
{

	public int getWidth();

	public int getHeight();

	public double getBoardAngle();

	public double getLevel(int x, int y);

	public void getWireColor(float[] dest, int x, int y);

	public void getBoardColor(float[] dest, int x, int y);

	public void randomize();

	public void input(double speed);

	public void move(PhaseMain phase);

}
