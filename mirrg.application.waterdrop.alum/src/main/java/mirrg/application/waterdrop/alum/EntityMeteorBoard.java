package mirrg.application.waterdrop.alum;

public abstract class EntityMeteorBoard extends EntityMeteor
{

	protected double power;
	protected int range;

	public EntityMeteorBoard(PhaseMain phaseMain, double x, double y, double z, double dx, double dy, double dz, double power, int range)
	{
		super(phaseMain, x, y, z, dx, dy, dz);
		this.power = power;
		this.range = range;
	}

	@Override
	protected void onCollide(int x2, int y2)
	{
		for (int x = x2 - range; x <= x2 + range; x++) {
			for (int y = y2 - range; y <= y2 + range; y++) {
				onCollide2(x, y);
			}
		}
	}

	protected abstract void onCollide2(int x2, int y2);

}
