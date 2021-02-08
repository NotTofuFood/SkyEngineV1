package renderer;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.image.VolatileImage;
import java.util.Random;

import main.Window;
import map.Loader;
import maths.ThreeValue;
import rays.Camera;
import rays.Portal;
import scene.SceneManager;
import scene.Skybox;
import texture.ImageLoader;

public class Display extends Renderer {

	private static final long serialVersionUID = -6740323398587524531L;

	public static SceneManager manager = new SceneManager(9);
	
	private Loader map_loader = new Loader();
	
	private boolean up, down, left, right;
	
	private int FOV = 45; //128
	
	private float speed = 4;
	
	private Portal test_portal;
	
	private Camera camera;

	private Skybox skybox = new Skybox(0,0, "res/skybox/skybox.png");
	
	private VolatileImage screen;

	public Display() {
		map_loader.loadMap("maps/map_data.lvl");
		map_loader.createMap();
		camera = new Camera(400, 400, 1080, 720, FOV);
		camera.setSpeed(speed);
		Random r = new Random();
		manager.fog_settings(new ThreeValue(0, 0, 0), 4);
		for(int i = 0; i < manager.walls.size(); i++) {
			if(r.nextInt() >= 7) {
				manager.walls.get(i).setTexture(ImageLoader.loadImage("res/textures/wolf/eagle.png", manager.walls.get(i)));
			} else if(r.nextInt() >= 5) {
				manager.walls.get(i).setTexture(ImageLoader.loadImage("res/textures/wolf/wood.png", manager.walls.get(i)));
			} else {
				manager.walls.get(i).setTexture(ImageLoader.loadImage("res/glassfacete.png", manager.walls.get(i)));
			}
		}
		manager.image_size = ImageLoader.wall_textures.size();
		test_portal = new Portal(400, 610, 100, 100, 0, (int) Window.WIDTH, (int) Window.HEIGHT);
		manager.createPortal(test_portal, this);
		
		repaint();
	} 

	public void update() {
		camera.movement(isUp(), isDown(), isLeft(), isRight());
		camera.update();
		if(isLeft()) {
			skybox.move(speed);
		} else if(isRight()) {
			skybox.move(-speed);
		} 
	}

	public void render(Graphics2D g) {
		manager.renderFloor(g, this, camera);
		skybox.render(this, g);
		manager.renderScene3D(g, this, camera);
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		screen = this.getGraphicsConfiguration().createCompatibleVolatileImage((int)Window.WIDTH, (int)Window.HEIGHT);
		//manager.floor_buffer = this.getGraphicsConfiguration().createCompatibleVolatileImage((int)Window.WIDTH, (int)manager.floor_height);
		render(screen.createGraphics());
		g2.drawImage(screen, 0, 0, this);
		if(camera.move_rot) {
			for(int i = 0; i < 3; i++) {
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)i/10));
				g2.drawImage(screen, i*(int)manager.wall_width, 0, this);
			}
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		}
		g2.dispose();
		screen.flush();
	}

	public boolean isUp() {
		return up;
	}
 
	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public int getFOV() {
		return FOV;
	}

	public void setFOV(int fOV) {
		FOV = fOV;
	}
	
}
