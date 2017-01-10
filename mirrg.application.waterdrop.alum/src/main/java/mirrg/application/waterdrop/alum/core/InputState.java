package mirrg.application.waterdrop.alum.core;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputState
{

	public static int[] kb = new int[2048];
	public static int[] mb = new int[64];
	public static int x;
	public static int y;
	public static int dx;
	public static int dy;
	public static int dw;

	public static void update()
	{
		for (int i = 0; i < Keyboard.getKeyCount(); i++) {
			if (kb[i] > 0) {
				kb[i]++;
			} else if (kb[i] < 0) {
				kb[i]--;
			}
			if (kb[i] <= 0 && Keyboard.isKeyDown(i)) kb[i] = 1;
			if (kb[i] >= 0 && !Keyboard.isKeyDown(i)) kb[i] = -1;
		}

		for (int i = 0; i < Mouse.getButtonCount(); i++) {
			if (mb[i] > 0) {
				mb[i]++;
			} else if (mb[i] < 0) {
				mb[i]--;
			}
			if (mb[i] <= 0 && Mouse.isButtonDown(i)) mb[i] = 1;
			if (mb[i] >= 0 && !Mouse.isButtonDown(i)) mb[i] = -1;
		}

		dx = Mouse.getX() - x;
		dy = Mouse.getY() - y;
		x = Mouse.getX();
		y = Mouse.getY();
		dw = Mouse.getDWheel();
	}

}
