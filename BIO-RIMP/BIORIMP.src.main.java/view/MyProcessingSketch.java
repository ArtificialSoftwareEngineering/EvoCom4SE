/**
 *
 */
package view;

import processing.core.*;

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
 */
public class MyProcessingSketch extends PApplet {

    HashGrid<Dot> hashGrid;
    static int rad = 15;
    static final float RADIUS = rad;  // Search radius for hash grid
    MetaphorCode metaphor;
    HierarchyBuilder builder;


    double step = 0.001;    // Size of each step along the path

    float beginX;  // Initial x-coordinate
    float beginY;  // Initial y-coordinate
    float endX;   // Final x-coordinate
    float endY;   // Final y-coordinate
    float distX;          // X-axis distance to move
    float distY;          // Y-axis distance to move
    float x = (float) 0.0;        // Current x-coordinate
    float y = (float) 0.0;        // Current y-coordinate
    float spring = (float) 0.05;
    float friction = (float) -0.9;

    public void settings() {

        //First Step: Calculate Actual Metrics
        String userPath = System.getProperty("user.dir");
        String[] args = {"-l", "Java", "-p", userPath + "\\test_data\\code\\optimization\\src", "-s", "     optimization      "};

        //Second Step: Create the structures for the prediction
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        metaphor = new MetaphorCode(init);
        size(1200, 600);
        //size(640, 360, processing.opengl.PGraphics3D);
    }

    public void setup() {
        //noFill();
        hashGrid = new HashGrid<Dot>(width, height, RADIUS);
        List<TypeDeclaration> childrenList;


        for (TypeDeclaration systype : metaphor.getSysTypeDcls()) {
            childrenList = metaphor.getBuilder().getChildClasses().get(systype.getQualifiedName());
            hashGrid.add(new Dot(random(width), random(height), systype, childrenList));
        }

        //Listing children
        List<Dot> dotchildren;
        for (Dot d : hashGrid) {
            if (d.getchildren() != null) {
                dotchildren = new ArrayList<Dot>();
                for (TypeDeclaration dotChild : d.getchildren()) {
                    for (Dot dChild : hashGrid) //dotchild for
                    {
                        if (dChild.systype.equals(dotChild)) {
                            dChild.setEndLocation(d.getLocation().x, d.getLocation().y);
                            dotchildren.add(dChild);
                        }
                    }
                }
                d.setdotChildren(dotchildren);
            }
        }
        fill(255, 204);
    }

    public void draw() {
        background(0);
        stroke(255);
        strokeWeight(1);
        textSize(20);

        motion_move_child();
        collide();
        move();
        move_3();

        for (Dot d : hashGrid) {
            ellipse(d.getLocation().x, d.getLocation().y, rad, rad);
            point(d.getLocation().x, d.getLocation().y);
            //text( d.getName().getName() , d.getLocation().x, d.getLocation().y );
            if (d.getdotchildren() != null)
                for (Dot dotChild : d.getdotchildren())
                    line(d.getLocation().x, d.getLocation().y,
                            dotChild.getLocation().x, dotChild.getLocation().y);
        }

        Set<Dot> dotsNearMouse = hashGrid.get(new PVector(mouseX, mouseY));
        if (mousePressed) {
            //hashGrid.removeAll(dotsNearMouse);
            //motion_move_parent();
            collide();
            move();
            move_2();

        } else {
            strokeWeight(15);
            stroke(120, 20, 20, 200);

            for (Dot d : dotsNearMouse) {
                text(d.getSystype().getName(), d.getLocation().x, d.getLocation().y);
                point(d.getLocation().x, d.getLocation().y);
            }
        }
    }


    void collide() {

        HashGrid<Dot> hashGrid_ = new HashGrid<Dot>(width, height, RADIUS);
        for (Dot d : hashGrid)
            hashGrid_.add(d);

        for (Dot dotReal : hashGrid_) {
            for (Dot dot : hashGrid_) {

                if (!dotReal.equals(dot)) {
                    float dx = dot.getLocation().x - dotReal.getLocation().x;
                    float dy = dot.getLocation().y - dotReal.getLocation().y;
                    float distance = sqrt(dx * dx + dy * dy);
                    float minDist = rad * 3;

                    if (distance < minDist) {
                        float angle = atan2(dy, dx);
                        float targetX = x + cos(angle) * minDist;
                        float targetY = y + sin(angle) * minDist;
                        float ax = (targetX - dot.getLocation().x) * spring;
                        float ay = (targetY - dot.getLocation().y) * spring;
                        dotReal.setVX(dotReal.getVX() - ax);
                        dotReal.setVY(dotReal.getVY() - ay);

                        dot.setVX(dot.getVX() + ax);
                        dot.setVY(dot.getVY() + ay);
                    }
                }
            }
        }


        List<TypeDeclaration> childrenList;
        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (Dot d : hashGrid_) {
            childrenList = d.getchildren();
            hashGrid.add(new Dot(
                    d.getLocation().x,
                    d.getLocation().y,
                    d.getSystype(), childrenList));

        }

        //Update Dot Children
        List<Dot> dotchildren;
        for (Dot d : hashGrid) {
            if (d.getchildren() != null) {
                dotchildren = new ArrayList<Dot>();
                for (TypeDeclaration dotChild : d.getchildren()) {
                    for (Dot dChild : hashGrid) {
                        if (dChild.systype.equals(dotChild))
                            dotchildren.add(dChild);
                    }
                }
                d.setdotChildren(dotchildren);
            }
        }


    }

    public void motion_move_parent() {
        boolean bandera = false;

        HashGrid<Dot> hashGrid_ = new HashGrid<Dot>(width, height, RADIUS);
        for (Dot d : hashGrid)
            hashGrid_.add(d);

        for (Dot d : hashGrid_) {
            for (Dot dotChild : d.getdotchildren()) {
                for (Dot dotReal : hashGrid_) {
                    if (dotChild.getSystype().equals(dotReal.getSystype())) {

                        // dotReal.getPCT() < 1.0 &&
                        if (d.getPCT() < 1 &&
                                dist(dotReal.getLocation().x, dotReal.getLocation().y,
                                        d.getLocation().x, d.getLocation().y) > rad) {

                            d.setPCTincrement();

                            distX = dotReal.getLocation().x - d.getLocation().x;
                            distY = dotReal.getLocation().y - d.getLocation().y;

                            x = d.getLocation().x + (d.getPCT() * distX * d.xdirection);
                            y = d.getLocation().y + (d.getPCT() * distY * d.ydirection);

                            d.setLocation(x, y);

                            if (x > width - rad || x < rad) {
                                d.setXdirect();
                            }
                            if (y > height - rad || y < rad) {
                                d.setYdirect();
                            }
                            bandera = true;
                            break;
                        }
                    }
                }
                if (bandera)
                    break;
            }
        }

        List<TypeDeclaration> childrenList;
        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (Dot d : hashGrid_) {
            childrenList = d.getchildren();
            hashGrid.add(new Dot(
                    d.getLocation().x,
                    d.getLocation().y,
                    d.getSystype(), childrenList));

        }

        //Update Dot Children
        List<Dot> dotchildren;
        for (Dot d : hashGrid) {
            if (d.getchildren() != null) {
                dotchildren = new ArrayList<Dot>();
                for (TypeDeclaration dotChild : d.getchildren()) {
                    for (Dot dChild : hashGrid) {
                        if (dChild.systype.equals(dotChild))
                            dotchildren.add(dChild);
                    }
                }
                d.setdotChildren(dotchildren);
            }
        }

    }

    public void motion_move_child() {
        HashGrid<Dot> hashGrid_ = new HashGrid<Dot>(width, height, RADIUS);
        for (Dot d : hashGrid)
            hashGrid_.add(d);

        for (Dot d : hashGrid_) {
            for (Dot dotChild : d.getdotchildren()) {
                for (Dot dotReal : hashGrid_) {
                    if (dotChild.getSystype().equals(dotReal.getSystype())) {

                        // dotReal.getPCT() < 1.0 &&
                        if (dotReal.getPCT() < 1 &&
                                dist(dotReal.getLocation().x, dotReal.getLocation().y,
                                        d.getLocation().x, d.getLocation().y) > rad * 5) {

                            dotReal.setPCTincrement();

                            distX = d.getLocation().x - dotReal.getLocation().x;
                            distY = d.getLocation().y - dotReal.getLocation().y;

                            x = dotReal.getLocation().x + (dotReal.getPCT() * distX * dotReal.xdirection);
                            y = dotReal.getLocation().y + (dotReal.getPCT() * distY * dotReal.ydirection);

                            dotReal.setLocation(x, y);
                            //break;
                        }
                    }
                }
            }
        }

        List<TypeDeclaration> childrenList;
        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (Dot d : hashGrid_) {
            childrenList = d.getchildren();
            hashGrid.add(new Dot(
                    d.getLocation().x,
                    d.getLocation().y,
                    d.getSystype(), childrenList));

        }

        //Update Dot Children
        List<Dot> dotchildren;
        for (Dot d : hashGrid) {
            if (d.getchildren() != null) {
                dotchildren = new ArrayList<Dot>();
                for (TypeDeclaration dotChild : d.getchildren()) {
                    for (Dot dChild : hashGrid) {
                        if (dChild.systype.equals(dotChild))
                            dotchildren.add(dChild);
                    }
                }
                d.setdotChildren(dotchildren);
            }
        }

    }

    public void move() {


        HashGrid<Dot> hashGrid_ = new HashGrid<Dot>(width, height, RADIUS);
        for (Dot d : hashGrid)
            hashGrid_.add(d);

        for (Dot dotReal : hashGrid_) {

            x = dotReal.getLocation().x + dotReal.getVX();
            y = dotReal.getLocation().y + dotReal.getVY();


            if (x + rad > width) {
                x = width - rad;
                dotReal.setVX(dotReal.getVX() * friction);
                //vx *= friction;
            } else if (x - rad < 0) {
                x = rad;
                dotReal.setVX(dotReal.getVX() * friction);
                //vx *= friction;
            }

            if (y + rad > height) {
                y = height - rad;
                dotReal.setVY(dotReal.getVY() * friction);
                //vy *= friction;
            } else if (y - rad < 0) {
                y = rad;
                dotReal.setVY(dotReal.getVY() * friction);
                //vy *= friction;
            }

            dotReal.setLocation(x, y);

            if (dotReal.getLocation().x > width - rad || dotReal.getLocation().x < rad) {
                dotReal.setXdirect();
            }
            if (dotReal.getLocation().y > height - rad || dotReal.getLocation().y < rad) {
                dotReal.setYdirect();
            }

        }

        List<TypeDeclaration> childrenList;
        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (Dot d : hashGrid_) {
            childrenList = d.getchildren();
            hashGrid.add(new Dot(
                    d.getLocation().x,
                    d.getLocation().y,
                    d.getSystype(), childrenList));

        }

        //Update Dot Children
        List<Dot> dotchildren;
        for (Dot d : hashGrid) {
            if (d.getchildren() != null) {
                dotchildren = new ArrayList<Dot>();
                for (TypeDeclaration dotChild : d.getchildren()) {
                    for (Dot dChild : hashGrid) {
                        if (dChild.systype.equals(dotChild))
                            dotchildren.add(dChild);
                    }
                }
                d.setdotChildren(dotchildren);
            }
        }
    }

    public void move_2() {
        HashGrid<Dot> hashGrid_ = new HashGrid<Dot>(width, height, RADIUS);
        for (Dot d : hashGrid)
            hashGrid_.add(d);

        for (Dot dotReal : hashGrid_) {
            for (Dot dotother : hashGrid_) {
                if (dist(dotReal.getLocation().x, dotReal.getLocation().y,
                        dotother.getLocation().x, dotother.getLocation().y) < rad * 2) {

                    dotother.setPCT_apartdecrement();

                    distX = dotReal.getLocation().x - dotother.getLocation().x;
                    distY = dotReal.getLocation().y - dotother.getLocation().y;

                    x = dotother.getLocation().x - (dotother.pct_apart * distX / 2);
                    y = dotother.getLocation().y - (dotother.pct_apart * distY / 2);

                    dotother.setLocation(x, y);
                }

            }

        }

        List<TypeDeclaration> childrenList;
        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (Dot d : hashGrid_) {
            childrenList = d.getchildren();
            hashGrid.add(new Dot(
                    d.getLocation().x,
                    d.getLocation().y,
                    d.getSystype(), childrenList));

        }

        //Update Dot Children
        List<Dot> dotchildren;
        for (Dot d : hashGrid) {
            if (d.getchildren() != null) {
                dotchildren = new ArrayList<Dot>();
                for (TypeDeclaration dotChild : d.getchildren()) {
                    for (Dot dChild : hashGrid) {
                        if (dChild.systype.equals(dotChild))
                            dotchildren.add(dChild);
                    }
                }
                d.setdotChildren(dotchildren);
            }
        }
    }

    public void move_3() {
        HashGrid<Dot> hashGrid_ = new HashGrid<Dot>(width, height, RADIUS);
        for (Dot d : hashGrid)
            hashGrid_.add(d);

        for (Dot dotReal : hashGrid_) {
            for (Dot dotother : hashGrid_) {
                if (dist(dotReal.getLocation().x, dotReal.getLocation().y,
                        dotother.getLocation().x, dotother.getLocation().y) < rad * 2) {

                    dotReal.setPCT_apartdecrement();

                    distX = dotother.getLocation().x - dotReal.getLocation().x;
                    distY = dotother.getLocation().y - dotReal.getLocation().y;

                    x = dotReal.getLocation().x - (dotReal.pct_apart * distX / 2);
                    y = dotReal.getLocation().y - (dotReal.pct_apart * distY / 2);

                    dotReal.setLocation(x, y);
                }

            }

        }

        List<TypeDeclaration> childrenList;
        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (Dot d : hashGrid_) {
            childrenList = d.getchildren();
            hashGrid.add(new Dot(
                    d.getLocation().x,
                    d.getLocation().y,
                    d.getSystype(), childrenList));

        }

        //Update Dot Children
        List<Dot> dotchildren;
        for (Dot d : hashGrid) {
            if (d.getchildren() != null) {
                dotchildren = new ArrayList<Dot>();
                for (TypeDeclaration dotChild : d.getchildren()) {
                    for (Dot dChild : hashGrid) {
                        if (dChild.systype.equals(dotChild))
                            dotchildren.add(dChild);
                    }
                }
                d.setdotChildren(dotchildren);
            }
        }
    }

    // Class for storing a point value. It must implement the Locatable
    // interface since objects of this type will be added to the hash grid.
    class Dot implements Locatable {
        PVector d;
        PVector end;
        TypeDeclaration systype;
        List<TypeDeclaration> children;
        List<Dot> dotchildren;
        float pct = 0;
        float pct_apart = 1;
        float vx = 0;
        float vy = 0;

        int xdirection = 1;  // Left or Right
        int ydirection = 1;  // Top to Bottom

        Dot(float x, float y, TypeDeclaration systype, List<TypeDeclaration> children,
            int xdire, int ydire) {
            d = new PVector(x, y);
            this.systype = systype;
            this.children = children;
            this.xdirection = xdire;
            this.ydirection = ydire;
        }

        Dot(float x, float y, TypeDeclaration systype, List<TypeDeclaration> children) {
            d = new PVector(x, y);
            this.systype = systype;
            this.children = children;
        }

        public void setVX(float vx) {
            this.vx = vx;
        }

        public float getVX() {
            return this.vx;
        }

        public void setVY(float vy) {
            this.vx = vy;
        }

        public float getVY() {
            return this.vy;
        }

        public void setXdirect() {
            this.xdirection *= -1;
        }

        public void setYdirect() {
            this.ydirection *= -1;
        }

        public float getPCT() {
            return this.pct;
        }

        public void setPCTincrement() {
            this.pct += step;
        }

        public void setPCT_apartdecrement() {
            this.pct_apart -= step;
        }

        public void setPCT(float pct) {
            this.pct = pct;
        }

        public void setLocation(float x, float y) {
            this.d = new PVector(x, y);
        }

        public PVector getEndLocation() {
            return end;
        }

        public PVector getLocation() {
            return d;
        }

        public TypeDeclaration getSystype() {
            return systype;
        }

        public void setdotChildren(List<Dot> dotchildren) {
            this.dotchildren = dotchildren;
        }

        public void setEndLocation(float x, float y) {
            this.end = new PVector(x, y);
        }

        public List<TypeDeclaration> getchildren() {
            return this.children;
        }

        public List<Dot> getdotchildren() {
            return this.dotchildren;
        }

    }

    public static void main(String args[]) {
        PApplet.main(new String[]{"--present", "view.MyProcessingSketch"});
    }
}
