package mirrg.application.waterdrop.alum.core;

import static org.lwjgl.opengl.GL11.*;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;

import mirrg.application.waterdrop.alum.Alum;

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
	}

	@Override
	public void onSwitched()
	{

	}

	public Cursor getCursor()
	{
		return Alum.cursorDefault;
	}

	public void clear()
	{
		glLoadIdentity();

		glClear(GL_COLOR_BUFFER_BIT);
		glClear(GL_DEPTH_BUFFER_BIT);
	}

	public abstract void render();

	public abstract IPhase input();

}
