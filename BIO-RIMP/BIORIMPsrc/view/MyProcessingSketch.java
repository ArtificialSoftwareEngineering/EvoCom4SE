/**
 * 
 */
package view;
import processing.core.*;

/**
 * @author Daavid
 *
 */
public class MyProcessingSketch extends PApplet {
	
	public void settings() {
		  //fullScreen();
		//size(640, 360, P3D);
		size(640, 360, processing.opengl.PGraphics3D);
		}
	
	public void setup() {
		
		//noStroke();
	}

	public void draw() {
		lights();
		background(0);
		/*
		float cameraY = (float) (height/2.0);
		float fov = mouseX/(float)width * PI/2;
		float cameraZ = cameraY / tan((float) (fov / 2.0));
		float aspect = (float)width/(float)height;
		if (mousePressed) {
			aspect = (float) (aspect / 2.0);
		}
		
		perspective(fov, aspect, (float)(cameraZ/10.0), (float)(cameraZ*10.0));

		translate(width/2+30, height/2, 0);
		rotateX(-PI/6);
		rotateY(PI/3 + mouseY/(float)height * PI);
		box(45);
		translate(0, 0, -50);
		box(30);*/
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "view.MyProcessingSketch" });
	}
}
