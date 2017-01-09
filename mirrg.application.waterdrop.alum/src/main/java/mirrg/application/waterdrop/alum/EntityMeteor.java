package mirrg.application.waterdrop.alum;

import static org.lwjgl.opengl.GL11.*;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class EntityMeteor implements IEntity
{

	private PhaseMain phaseMain;
	private double x;
	private double y;
	private double z;
	private double dx;
	private double dy;
	private double dz;
	private double power;

	public EntityMeteor(PhaseMain phaseMain, double x, double y, double z, double dx, double dy, double dz, double power)
	{
		this.phaseMain = phaseMain;
		this.x = x;
		this.y = y;
		this.z = z;
		this.dx = dx;
		this.dy = dy;
		this.dz = dz;
		this.power = power;

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

			glScaled(50, 50, 50);
			glTranslated(-0.5, -0.5, -0.5);
			glBegin(GL_QUADS);
			{

				glColor3f(1, 0, 0);
				glVertex3f(0, 0, 0);
				glVertex3f(1, 0, 0);
				glVertex3f(1, 0, 1);
				glVertex3f(0, 0, 1);

				glColor3f(0, 1, 0);
				glVertex3f(0, 1, 0);
				glVertex3f(1, 1, 0);
				glVertex3f(1, 1, 1);
				glVertex3f(0, 1, 1);

				glColor3f(0, 0, 1);
				glVertex3f(0, 0, 0);
				glVertex3f(0, 1, 0);
				glVertex3f(0, 1, 1);
				glVertex3f(0, 0, 1);

				glColor3f(0, 1, 1);
				glVertex3f(1, 0, 0);
				glVertex3f(1, 1, 0);
				glVertex3f(1, 1, 1);
				glVertex3f(1, 0, 1);

				glColor3f(1, 0, 1);
				glVertex3f(0, 0, 0);
				glVertex3f(1, 0, 0);
				glVertex3f(1, 1, 0);
				glVertex3f(0, 1, 0);

				glColor3f(1, 1, 0);
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

			double weight = phaseMain.map.getWeight(x2, y2) + power;
			if (weight < 0) weight = 0;
			phaseMain.map.setWeight(x2, y2, weight);

			return false;
		}

		return true;
	}
}
