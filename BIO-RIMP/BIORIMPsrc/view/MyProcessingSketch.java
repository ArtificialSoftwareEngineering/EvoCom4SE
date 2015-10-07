/**
 * 
 */
package view;
import processing.core.*;
import unalcol.clone.Clone;

import org.gicentre.utils.geom.*;

import edu.wayne.cs.severe.redress2.controller.HierarchyBuilder;
import edu.wayne.cs.severe.redress2.entity.TypeDeclaration;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import entity.MetaphorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Daavid
 *
 */
public class MyProcessingSketch extends PApplet {
	 
	HashGrid<Dot> hashGrid; 
	static final float RADIUS = 50;  // Search radius for hash grid 
	MetaphorCode metaphor;
	HierarchyBuilder builder;

	int rad = 20;
	float xspeed = (float) 2.0;  // Speed of the shape
	float yspeed = (float) 2.0;  // Speed of the shape

	int xdire;
	int ydire;

	
	public void settings() {
		 
		//First Step: Calculate Actual Metrics
		String userPath = System.getProperty("user.dir");
		String[] args = { "-l", "Java", "-p", userPath+"\\test_data\\code\\optimization\\src","-s", "     optimization      " };

		//Second Step: Create the structures for the prediction
		MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM ();
		init.main(args);
		metaphor = new MetaphorCode(init);
		size(1200, 600);
		//size(640, 360, processing.opengl.PGraphics3D);
	}

	public void setup() {
		noFill(); 
		hashGrid = new HashGrid<Dot>(width, height, RADIUS); 
		List<TypeDeclaration> childrenList;
		for (TypeDeclaration systype : metaphor.getSysTypeDcls() ){
			childrenList = metaphor.getBuilder().getChildClasses().get(systype.getQualifiedName());	
			hashGrid.add( new Dot(random(width), random(height), systype, childrenList ));
		}
		List<Dot> dotchildren;
		for (Dot d : hashGrid) 
		{
			if(d.getchildren() != null){
				dotchildren = new ArrayList<Dot>();
				for( TypeDeclaration dotChild : d.getchildren() ){
					for (Dot dChild : hashGrid) 
					{
						if(dChild.systype.equals(dotChild))
							dotchildren.add(dChild);
					}
				}
				d.setdotChildren(dotchildren);
			}
		} 

	}

	public void draw() {
		background(0); 
		stroke(255); 
		strokeWeight(1); 
		textSize(10);
		
		move();
		
		for (Dot d : hashGrid) 
		{ 
			ellipse(d.getLocation().x, d.getLocation().y, rad, rad);
			point(d.getLocation().x, d.getLocation().y);
			//text( d.getName().getName() , d.getLocation().x, d.getLocation().y );
			if( d.getdotchildren() != null)
				for( Dot dotChild : d.getdotchildren())
					line(d.getLocation().x, d.getLocation().y, 
						dotChild.getLocation().x , dotChild.getLocation().y	);
		} 

		Set<Dot> dotsNearMouse = hashGrid.get(new PVector(mouseX, mouseY)); 
		if (mousePressed) 
		{ 
			//hashGrid.removeAll(dotsNearMouse);
			move();
		} 
		else
		{ 
			strokeWeight(7); 
			stroke(120, 20, 20, 200); 

			for (Dot d : dotsNearMouse)
			{ 
				text( d.getSystype().getName() , d.getLocation().x, d.getLocation().y );
				point(d.getLocation().x, d.getLocation().y);
			}
		}
	}

	public void move() {
		HashGrid<Dot> hashGrid_ = hashGrid;
	
		hashGrid = new HashGrid<Dot>(width, height, RADIUS); 
		
		List<TypeDeclaration> childrenList;
		
		for (Dot d : hashGrid_) 
		{
			
			childrenList = d.getchildren();
			hashGrid.add( new Dot(
					d.getLocation().x + ( xspeed * d.xdirection ), 
					d.getLocation().y + ( yspeed * d.ydirection ), 
					d.getSystype(), childrenList,
					xdire, ydire));
			
			// Test to see if the shape exceeds the boundaries of the screen
			// If it does, reverse its direction by multiplying by -1
			if (d.getLocation().x  > width-rad || d.getLocation().x < rad ) {
				//if (d.getLocation().x > width-rad || d.getLocation().x < rad) {
				xdire = d.xdirection * -1;
			}
			if (d.getLocation().y  > height-rad || d.getLocation().y < rad ) {
				//if (d.getLocation().y >= height-rad || d.getLocation().y < rad) {
				ydire = d.ydirection * -1;
			}
			

		

		}



		List<Dot> dotchildren;
		for (Dot d : hashGrid) 
		{
			if(d.getchildren() != null){
				dotchildren = new ArrayList<Dot>();
				for( TypeDeclaration dotChild : d.getchildren() ){
					for (Dot dChild : hashGrid) 
					{
						if(dChild.systype.equals(dotChild))
							dotchildren.add(dChild);
					}
				}
				d.setdotChildren(dotchildren);
			}
		} 
	}

	/*
	public void mousePressed() {
		for (Dot d : hashGrid) 
			d.setLocation(new PVector(random(width), random(height)));
	}*/

	// Class for storing a point value. It must implement the Locatable 
	// interface since objects of this type will be added to the hash grid. 
	class Dot implements Locatable 
	{ 
		PVector d; 
		TypeDeclaration systype;
		List<TypeDeclaration> children;
		List<Dot> dotchildren;
		
		int xdirection = 1;  // Left or Right
		int ydirection = 1;  // Top to Bottom
	 
	  Dot(float x, float y, TypeDeclaration systype, List<TypeDeclaration> children,
			  int xdire, int ydire) 
	  { 
	    d = new PVector(x, y);
	    this.systype = systype;
	    this.children = children;
	    this.xdirection = xdire;
	    this.ydirection = ydire;
	  }
	  
	  Dot(float x, float y, TypeDeclaration systype, List<TypeDeclaration> children) 
	  { 
	    d = new PVector(x, y);
	    this.systype = systype;
	    this.children = children;
	  }
	  
	  public void setLocation(PVector d){
		  this.d = d;
	  }
	 
	  public PVector getLocation() 
	  { 
	    return d;
	  }
	  
	  public TypeDeclaration getSystype(){
		  return systype;
	  }
	  
	  public void setdotChildren(List<Dot> dotchildren){
		  this.dotchildren = dotchildren;
	  }
	  
	  public List<TypeDeclaration> getchildren(){
		  return this.children;
	  }
	  
	  public List<Dot> getdotchildren(){
		  return this.dotchildren;
	  }
	  
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "view.MyProcessingSketch" });
	}
}
