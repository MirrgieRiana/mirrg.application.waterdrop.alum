package mirrg.application.waterdrop.alum;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;

public class PhasePause extends PhaseBase
{

	private PhaseBase phase;

	public PhasePause(PhaseBase phase)
	{
		this.phase = phase;
	}

	@Override
	protected void render()
	{
		phase.render();

		Alum.make2D();
		{
			glBegin(GL_QUADS);
			glColor3f(1, 1, 0);
			{
				rect(10, 10, Alum.getWidth() - 20, 10);
				rect(10, 10, 10, Alum.getHeight() - 20);
				rect(10, Alum.getHeight() - 20, Alum.getWidth() - 20, 10);
				rect(Alum.getWidth() - 20, 10, 10, Alum.getHeight() - 20);
			}
			glEnd();
		}
		Alum.make3D();
	}

	private void rect(int x, int y, int w, int h)
	{
		glVertex3d(x, y, 0);
		glVertex3d(x + w, y, 0);
		glVertex3d(x + w, y + h, 0);
		glVertex3d(x, y + h, 0);
	}

	@Override
	protected IPhase input()
	{
		if (InputState.kb[Keyboard.KEY_ESCAPE] == 1) return phase;
		if (InputState.kb[Keyboard.KEY_E] == 1) return phase;
		return this;
	}

}
