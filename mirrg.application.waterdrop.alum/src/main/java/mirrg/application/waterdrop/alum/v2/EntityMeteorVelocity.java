package mirrg.application.waterdrop.alum.v2;

import static org.lwjgl.opengl.GL11.*;

import mirrg.application.waterdrop.alum.core.EntityMeteorBoard;

public class EntityMeteorVelocity extends EntityMeteorBoard
{

	protected Map2 map;

	public EntityMeteorVelocity(Map2 map, double x, double y, double z, double dx, double dy, double dz, double power, int range)
	{
		super(x, y, z, dx, dy, dz, power, range);
		this.map = map;
	}

	@Override
	protected void getColor()
	{
		glColor3f(0, 0, 1);
	}

	@Override
	protected void onCollide2(int x2, int y2)
	{
		map.setLevel(x2, y2, map.getLevel(x2, y2) + power);
	}

}
