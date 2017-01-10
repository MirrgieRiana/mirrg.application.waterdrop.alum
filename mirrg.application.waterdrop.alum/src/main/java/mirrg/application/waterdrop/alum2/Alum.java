package mirrg.application.waterdrop.alum2;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import mirrg.helium.swing.nitrogen.util.HSwing;
import mirrg.helium.swing.nitrogen.wrapper.artifacts.logging.HLog;

public class Alum
{

	public static Robot ROBOT;
	static {
		try {
			ROBOT = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static Point getMouseLocation()
	{
		return MouseInfo.getPointerInfo().getLocation();
	}

	public static Cursor cursorDefault;

	public static void setCursor(Cursor cursor)
	{
		try {
			Mouse.setNativeCursor(cursor);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	public static int getWidth()
	{
		return canvas.getWidth();
	}

	public static int getHeight()
	{
		return canvas.getHeight();
	}

	public static JFrame frame;
	public static Canvas canvas;

	public static void main(String[] args) throws LWJGLException
	{
		HSwing.setWindowsLookAndFeel();

		// create window
		{
			frame = new JFrame();
			{
				canvas = new Canvas();
				canvas.setPreferredSize(new Dimension(600, 600));
				frame.add(canvas);
			}
			{
				JMenuBar menuBar = new JMenuBar();
				{
					JMenu menu = new JMenu("キャンバスサイズ(C)");
					menu.setMnemonic('C');
					{
						JMenuItem menuItem = new JMenuItem("最大化(M)");
						menuItem.setMnemonic('M');
						menuItem.addActionListener(e -> {
							frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
							frame.validate();
						});
						menu.add(menuItem);
					}
					menu.addSeparator();
					{
						JMenuItem menuItem = new JMenuItem("600x600(1)");
						menuItem.setMnemonic('1');
						menuItem.addActionListener(e -> {
							canvas.setPreferredSize(new Dimension(600, 600));
							frame.pack();
						});
						menu.add(menuItem);
					}
					{
						JMenuItem menuItem = new JMenuItem("900x900(2)");
						menuItem.setMnemonic('2');
						menuItem.addActionListener(e -> {
							canvas.setPreferredSize(new Dimension(900, 900));
							frame.pack();
						});
						menu.add(menuItem);
					}
					menuBar.add(menu);
				}
				menuBar.add(Box.createHorizontalGlue());
				frame.setJMenuBar(menuBar);
			}
			frame.setTitle("Alum");
			a:
			{
				URL url = Alum.class.getResource("logo.png");
				if (url == null) break a;
				BufferedImage image;
				try {
					image = ImageIO.read(url);
				} catch (IOException e) {
					e.printStackTrace();
					break a;
				}
				frame.setIconImage(image);
			}

			frame.pack();
			frame.setLocationByPlatform(true);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setVisible(true);

			Display.setParent(canvas);
			Display.create();
			cursorDefault = Mouse.getNativeCursor();
		}

		/*
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle("Alum");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			return;
		}
		*/

		try {

			// init
			{
				//  ポリゴンの片面（表 or 裏）表示を有効にする
				glDisable(GL_CULL_FACE);
				//  ポリゴンの表示面を表（裏を表示しない）のみに設定する
				glCullFace(GL_BACK);

				glPushAttrib(GL_ENABLE_BIT);
				make3D();
			}

			// main loop
			IPhase phase = new PhaseMain();
			phase.onSwitched();
			while (!Display.isCloseRequested()) {
				IPhase phase2 = phase.loop();
				if (!phase.equals(phase2)) phase2.onSwitched();
				phase = phase2;
				Display.update();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Display.destroy();
		}

	}

	public static void doScreenShot()
	{

		// 画素データ抜き出し
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(getWidth() * getHeight() * 4);
		glReadBuffer(GL_BACK);
		glReadPixels(0, 0, getWidth(), getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		int[] array = new int[getWidth() * getHeight()];
		intBuffer.get(array);

		// 変換
		int[] array2 = new int[getWidth() * getHeight()];
		for (int y = 0; y < getHeight(); y++) {
			int y2 = y * getWidth();
			int y3 = (getHeight() - 1 - y) * getWidth();
			for (int x = 0; x < getWidth(); x++) {
				array2[x + y3] = array[x + y2] >> 8;
			}
		}

		// 格納先バッファ生成
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

		// 格納
		image.getRaster().setDataElements(0, 0, getWidth(), getHeight(), array2);

		// 保存
		String filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss-SSS")) + ".png";
		try {
			ImageIO.write(image, "png", new File(filename));
		} catch (IOException e1) {
			HLog.processException(e1);
		}

	}

	public static void make2D()
	{
		glPopAttrib();
		glPushAttrib(GL_ENABLE_BIT);

		glViewport(0, 0, Alum.getWidth(), Alum.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Alum.getWidth(), Alum.getHeight(), 0, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public static void make3D()
	{
		glPopAttrib();
		glPushAttrib(GL_ENABLE_BIT);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);

		glViewport(0, 0, Alum.getWidth(), Alum.getHeight());
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		gluPerspective(60, (float) Alum.getWidth() / Alum.getHeight(), 0.1f, 10000);

		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public static double getArea(Vector3D a, Vector3D b, Vector3D c)
	{
		ArrayList<Vector3D> points = new ArrayList<>();
		points.add(a);
		points.add(b);
		points.add(c);
		ArrayList<int[]> facets = new ArrayList<>();
		PolyhedronsSet polyhedronsSet = new PolyhedronsSet(points, facets, 1e-5);
		return polyhedronsSet.getBoundarySize();
	}

}
