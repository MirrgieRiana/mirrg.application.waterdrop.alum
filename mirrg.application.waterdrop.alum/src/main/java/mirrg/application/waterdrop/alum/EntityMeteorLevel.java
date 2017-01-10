package mirrg.application.waterdrop.alum;

import static org.lwjgl.opengl.GL11.*;

public class EntityMeteorLevel extends EntityMeteorBoard
{

	public EntityMeteorLevel(PhaseMain phaseMain, double x, double y, double z, double dx, double dy, double dz, double power, int range)
	{
		super(phaseMain, x, y, z, dx, dy, dz, power, range);
	}

	@Override
	protected void getColor()
	{
		glColor3f(1, 0, 0);
	}

	@Override
	protected void onCollide2(int x2, int y2)
	{
		phaseMain.map.setLevel(x2, y2, phaseMain.map.getLevel(x2, y2) + power);
	}

}
