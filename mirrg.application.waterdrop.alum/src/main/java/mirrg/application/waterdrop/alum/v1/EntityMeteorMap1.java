package mirrg.application.waterdrop.alum.v1;

import static org.lwjgl.opengl.GL11.*;

import mirrg.application.waterdrop.alum.core.EntityMeteorBoard;

public class EntityMeteorMap1 extends EntityMeteorBoard
{

	protected Map1 map;

	public EntityMeteorMap1(Map1 map, double x, double y, double z, double dx, double dy, double dz, double power, int range)
	{
		super(x, y, z, dx, dy, dz, power, range);
		this.map = map;
	}

	@Override
	protected void getColor()
	{
		glColor3f(1, 0, 0);
	}

	@Override
	protected void onCollide2(int x2, int y2)
	{
		double weight = map.getWeight(x2, y2) + power;
		if (weight < 0) weight = 0;
		map.setWeight(x2, y2, weight);
	}

}
