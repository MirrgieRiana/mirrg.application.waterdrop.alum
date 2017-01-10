package mirrg.application.waterdrop.alum2;

import static org.lwjgl.opengl.GL11.*;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public abstract class EntityMeteor implements IEntity
{

	protected PhaseMain phaseMain;
	protected double x;
	protected double y;
	protected double z;
	protected double dx;
	protected double dy;
	protected double dz;

	public EntityMeteor(PhaseMain phaseMain, double x, double y, double z, double dx, double dy, double dz)
	{
		this.phaseMain = phaseMain;
		this.x = x;
		this.y = y;
		this.z = z;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;

		this.x += this.dx * 5;
		this.y += this.dy * 5;
		this.z += this.dz * 5;
	}

	@Override
	public void render()
	{
		glPushMatrix();
		{
			glTranslated(x, y, z);

			glScaled(10, 10, 10);
			glTranslated(-0.5, -0.5, -0.5);
			glBegin(GL_QUADS);
			{

				getColor();

				glVertex3f(0, 0, 0);
				glVertex3f(1, 0, 0);
				glVertex3f(1, 0, 1);
				glVertex3f(0, 0, 1);

				glVertex3f(0, 1, 0);
				glVertex3f(1, 1, 0);
				glVertex3f(1, 1, 1);
				glVertex3f(0, 1, 1);

				glVertex3f(0, 0, 0);
				glVertex3f(0, 1, 0);
				glVertex3f(0, 1, 1);
				glVertex3f(0, 0, 1);

				glVertex3f(1, 0, 0);
				glVertex3f(1, 1, 0);
				glVertex3f(1, 1, 1);
				glVertex3f(1, 0, 1);

				glVertex3f(0, 0, 0);
				glVertex3f(1, 0, 0);
				glVertex3f(1, 1, 0);
				glVertex3f(0, 1, 0);

				glVertex3f(0, 0, 1);
				glVertex3f(1, 0, 1);
				glVertex3f(1, 1, 1);
				glVertex3f(0, 1, 1);

			}
			glEnd();
		}
		glPopMatrix();
	}

	@Override
	public boolean move()
	{
		x += dx;
		y += dy;
		z += dz;

		Vector3D point = new Vector3D(x, y, z);

		if (point.getNormSq() > 5000 * 5000) return false;

		Vector3D intersection = new Plane(
			new Vector3D(-1000, 0, -1000),
			new Vector3D(1000, 0, -1000),
			new Vector3D(-1000, 0, 1000), 1e-5).intersection(new Line(
				point,
				new Vector3D(x + dx * 10000, y + dy * 10000, z + dz * 10000), 1e-5));

		if (intersection == null) return false;

		if (intersection.distance(point) < 50) {
			int x2 = (int) Math.round((intersection.getX() + 1000) / 20);
			int y2 = (int) Math.round((intersection.getZ() + 1000) / 20);

			onCollide(x2, y2);

			return false;
		}

		return true;
	}

	protected void getColor()
	{
		glColor3f(0, 0, 0);
	}

	protected abstract void onCollide(int x2, int y2);

}
