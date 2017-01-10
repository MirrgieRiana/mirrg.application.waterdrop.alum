package mirrg.application.waterdrop.alum;

import static org.lwjgl.opengl.GL11.*;

public class EntityMeteorVelocity extends EntityMeteorBoard
{

	public EntityMeteorVelocity(PhaseMain phaseMain, double x, double y, double z, double dx, double dy, double dz, double power, int range)
	{
		super(phaseMain, x, y, z, dx, dy, dz, power, range);
	}

	@Override
	protected void getColor()
	{
		glColor3f(0, 0, 1);
	}

	@Override
	protected void onCollide2(int x2, int y2)
	{
		phaseMain.map.setLevel(x2, y2, phaseMain.map.getLevel(x2, y2) + power);
	}

}
