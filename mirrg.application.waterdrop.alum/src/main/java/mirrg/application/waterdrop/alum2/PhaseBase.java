package mirrg.application.waterdrop.alum2;

import static org.lwjgl.opengl.GL11.*;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;

public abstract class PhaseBase implements IPhase
{

	@Override
	public IPhase loop()
	{

		clear();

		render();

		InputState.update();

		IPhase phase = input();

		Alum.setCursor(getCursor());

		if (InputState.kb[Keyboard.KEY_P] == 1) {
			Alum.doScreenShot();
			SwingUtilities.invokeLater(() -> {
				Alum.frame.setTitle("Alum - Screen Shot OK");
				Timer timer = new Timer(1000, e -> {
					Alum.frame.setTitle("Alum");
				});
				timer.setRepeats(false);
				timer.start();
			});
		}

		return phase;

		//

		//glTranslated(x1, 0, y1);
		//glScaled(0.1, 0.1, 0.1);
		//glScaled(100, 100, 100);
		//glRotated(x2, 0.0, 1.0, 0.0);
		//glRotated(y2, 1.0, 0.0, 0.0);

		//glTranslated(-cameraX, -cameraY, -cameraZ);
		//glRotated(cameraH, 0.0, 1.0, 0.0);
		//glRotated(cameraV, 1.0, 0.0, 0.0);

		//

		//

		//gluPickMatrix(InputState.x, InputState.y, width, height, gl);

	}

	@Override
	public void onSwitched()
	{

	}

	protected Cursor getCursor()
	{
		return Alum.cursorDefault;
	}

	protected void clear()
	{
		glLoadIdentity();

		glClear(GL_COLOR_BUFFER_BIT);
		glClear(GL_DEPTH_BUFFER_BIT);
	}

	protected abstract void render();

	protected abstract IPhase input();

}
