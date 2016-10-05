/**
 *
 */
package java.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.gicentre.utils.geom.HashGrid;
import org.gicentre.utils.geom.Locatable;

import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation;
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringParameter;
import edu.wayne.cs.severe.redress2.main.MainPredFormulasBIoRIPM;
import java.optmodel.mappings.metaphor.MetaphorCode;
import java.optmodel.operators.RefOperMutation;
import java.optmodel.fitness.GeneralizedImpactQuality;
import java.optmodel.fitness.RefactorArrayPlainWrite;
import processing.core.PApplet;
import processing.core.PVector;
import java.optmodel.space.RefactoringOperationSpace;
import unalcol.descriptors.Descriptors;
import unalcol.descriptors.WriteDescriptors;
import unalcol.io.Write;
import unalcol.optimization.OptimizationFunction;
import unalcol.optimization.OptimizationGoal;
import unalcol.optimization.hillclimbing.HillClimbing;
import unalcol.random.real.UniformGenerator;
import unalcol.search.Goal;
import unalcol.search.Solution;
import unalcol.search.SolutionDescriptors;
import unalcol.search.space.Space;
import unalcol.tracer.ConsoleTracer;

/**
 * @author Daavid
 */
public class ProcessingRefactor extends PApplet {

    static int rad = 10;
    MetaphorCode metaphor;
    double step = 0.001;    // Size of each step along the path
    HashGrid<Dot> hashGrid;
    Space<List<RefactoringOperation>> space;
    OptimizationFunction<List<RefactoringOperation>> function;
    Goal<List<RefactoringOperation>> goal;
    RefOperMutation variation;
    HillClimbing<List<RefactoringOperation>> search;
    Solution<List<RefactoringOperation>> solution;

    float friction = (float) -0.9;
    float spring = (float) 0.05;
    float distX;          // X-axis distance to move
    float distY;          // Y-axis distance to move
    float x = (float) 0.0;        // Current x-coordinate
    float y = (float) 0.0;        // Current y-coordinate

    List<Solution<List<RefactoringOperation>>> ListSolution = new ArrayList<Solution<List<RefactoringOperation>>>();

    public void settings() {

        //First Step: Calculate Actual Metrics
        String userPath = System.getProperty("user.dir");
        String[] args = {"-l", "Java", "-p", userPath + "\\test_data\\code\\optimization\\src", "-s", "java/optmodel/fitness      "};

        //Second Step: Create the structures for the prediction
        MainPredFormulasBIoRIPM init = new MainPredFormulasBIoRIPM();
        init.main(args);
        metaphor = new MetaphorCode(init);

        //Third Step: Optimization
        // Search Space definition
        int DIM = 7;
        space = new RefactoringOperationSpace(DIM);

        // Optimization Function
        function = new GeneralizedImpactQuality(metaphor, "OPTIMIZATION");
        goal = new OptimizationGoal<List<RefactoringOperation>>(function); // maximizing, remove the parameter false if minimizing


        // Variation definition
        variation = new RefOperMutation(0.5);

        // Search method in RefactorSpace
        int MAXITERS = 10;
        boolean neutral = true; // Accepts movements when having same function value
        search = new HillClimbing<List<RefactoringOperation>>(variation, neutral, MAXITERS);


        size(1200, 600);

    }

    public void setup() {

        // Tracking the goal evaluations
        SolutionDescriptors<List<RefactoringOperation>> desc = new SolutionDescriptors<List<RefactoringOperation>>();
        Descriptors.set(Solution.class, desc);
        RefactorArrayPlainWrite write = new RefactorArrayPlainWrite(false);
        List<RefactoringOperation> ref = new ArrayList<RefactoringOperation>();
        Write.set(ref, write);
        WriteDescriptors w_desc = new WriteDescriptors();
        Write.set(Solution.class, w_desc);

        ConsoleTracer tracer = new ConsoleTracer();
        //Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
        //Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm

        // Apply the search method
        ListSolution.add(search.apply(space, goal));
        solution = ListSolution.get(ListSolution.size() - 1);

        System.out.println("QUALITY_:" + solution.quality() + "=" + "VALUE_:" + solution.value());

        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (RefactoringOperation refOper : solution.value()) {
            hashGrid.add(new Dot(random(width), random(height), refOper, solution));
        }

        fill(255, 204);
    }

    public void draw() {
        background(0);
        stroke(255);

        strokeWeight(1);
        textSize(17);

        for (Dot d : hashGrid) {
            x = d.getLocation().x % width;
            y = d.getLocation().y % height;
            ellipse(x, y, rad, rad);
            point(x, y);
            text(d.getrefOper().getRefType().getAcronym(), x, y);
        }


        //Motion
        motion_refactor();
        motion_refactor_collition();

        //Collide
        collide();
        move();

        //DrawLine
        draw_line();
        draw_point();

        //Draw params
        draw_classes();
        draw_line_params();


        if (mousePressed) {
            //hashGrid.removeAll(dotsNearMouse);
            //motion_move_parent();
            //New Refactor
            newRefactor();

        } else {
            Set<Dot> dotsNearMouse = hashGrid.get(new PVector(mouseX, mouseY));
            strokeWeight(15);
            stroke(120, 20, 20, 200);

            for (Dot d : dotsNearMouse) {
                x = d.getLocation().x % width;
                y = d.getLocation().y % height;
                text(d.getrefOper().toString(), x, y);
                point(x, y);
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
            } else if (x - rad < 0) {
                x = rad;
                dotReal.setVX(dotReal.getVX() * friction);
            }

            if (y + rad > height) {
                y = height - rad;
                dotReal.setVY(dotReal.getVY() * friction);
            } else if (y - rad < 0) {
                y = rad;
                dotReal.setVY(dotReal.getVY() * friction);
            }

            dotReal.setLocation(x, y);

            if (dotReal.getLocation().x > width - rad || dotReal.getLocation().x < rad) {
                dotReal.setXdirect();
            }
            if (dotReal.getLocation().y > height - rad || dotReal.getLocation().y < rad) {
                dotReal.setYdirect();
            }

        }
        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (Dot d : hashGrid_) {
            hashGrid.add(new Dot(
                    d.getLocation().x,
                    d.getLocation().y,
                    d.getrefOper(), d.others,
                    d.getdotchildren()));

        }
    }

    public void motion_refactor() {
        HashGrid<Dot> hashGrid_ = new HashGrid<Dot>(width, height, RADIUS);
        for (Dot d : hashGrid)
            hashGrid_.add(d);

        for (Solution<List<RefactoringOperation>> sol : ListSolution) {
            List<Dot> dotsofaSet = new ArrayList<Dot>();
            for (RefactoringOperation refOper : sol.value()) {
                //Traduction
                for (Dot d : hashGrid) {
                    if (d.getrefOper().equals(refOper))
                        dotsofaSet.add(d);
                }

            }

            for (int i = 0; i < dotsofaSet.size(); i++) {
                if ((i + 1) != dotsofaSet.size()) {
                    if (dist(dotsofaSet.get(i).getLocation().x, dotsofaSet.get(i).getLocation().y,
                            dotsofaSet.get(i + 1).getLocation().x, dotsofaSet.get(i + 1).getLocation().y) > rad * 2 * rad
                            && dotsofaSet.get(i + 1).getPCT() < 1
                            ) {
                        dotsofaSet.get(i + 1).setPCTincrement();

                        distX = dotsofaSet.get(i).getLocation().x - dotsofaSet.get(i + 1).getLocation().x;
                        distY = dotsofaSet.get(i).getLocation().y - dotsofaSet.get(i + 1).getLocation().y;

                        x = dotsofaSet.get(i + 1).getLocation().x + (dotsofaSet.get(i + 1).getPCT() * distX * dotsofaSet.get(i + 1).xdirection);
                        y = dotsofaSet.get(i + 1).getLocation().y + (dotsofaSet.get(i + 1).getPCT() * distY * dotsofaSet.get(i + 1).ydirection);

                        dotsofaSet.get(i + 1).setLocation(x, y);
                    }
                }
            }


        }

        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (Dot d : hashGrid_) {
            hashGrid.add(new Dot(
                    d.getLocation().x,
                    d.getLocation().y,
                    d.getrefOper(), d.others,
                    d.getdotchildren()));

        }

    }

    public void motion_refactor_collition() {
        HashGrid<Dot> hashGrid_ = new HashGrid<Dot>(width, height, RADIUS);
        for (Dot d : hashGrid)
            hashGrid_.add(d);

        for (Dot dotReal : hashGrid_) {
            for (Dot dotother : hashGrid_) {
                if (dist(dotReal.getLocation().x, dotReal.getLocation().y,
                        dotother.getLocation().x, dotother.getLocation().y) < rad * rad) {

                    dotReal.setPCT_apartdecrement();

                    distX = dotother.getLocation().x - dotReal.getLocation().x;
                    distY = dotother.getLocation().y - dotReal.getLocation().y;

                    x = dotReal.getLocation().x - (dotReal.pct_apart * distX / rad);
                    y = dotReal.getLocation().y - (dotReal.pct_apart * distY / rad);

                    dotReal.setLocation(x, y);
                }

            }

        }

        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (Dot d : hashGrid_) {
            hashGrid.add(new Dot(
                    d.getLocation().x,
                    d.getLocation().y,
                    d.getrefOper(), d.others,
                    d.getdotchildren()));

        }

    }

    public void collide() {
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

        hashGrid = new HashGrid<Dot>(width, height, RADIUS);

        for (Dot d : hashGrid_) {
            hashGrid.add(new Dot(
                    d.getLocation().x,
                    d.getLocation().y,
                    d.getrefOper(), d.others,
                    d.getdotchildren()));

        }

    }

    public void draw_line() {
        stroke(0, 255, 17);
        for (Solution<List<RefactoringOperation>> sol : ListSolution) {
            List<Dot> dotsofaSet = new ArrayList<Dot>();
            for (RefactoringOperation refOper : sol.value()) {
                //Traduction
                for (Dot d : hashGrid) {
                    if (d.getrefOper().equals(refOper))
                        dotsofaSet.add(d);
                }

            }

            for (int i = 0; i < dotsofaSet.size(); i++) {
                if ((i + 1) != dotsofaSet.size()) {
                    line(dotsofaSet.get(i).getLocation().x % width, dotsofaSet.get(i).getLocation().y % height,
                            dotsofaSet.get(i + 1).getLocation().x % width, dotsofaSet.get(i + 1).getLocation().y % height);
                }
            }
        }

    }

    public void draw_line_params() {
        stroke(0, 128, 255);

        for (Dot d : hashGrid) {
            for (PVector pChild : d.getdotchildren()) {
                if (d.getdotchildren() != null)
                    line(d.getLocation().x % width, d.getLocation().y % height,
                            d.getLocation().x + pChild.x % width, d.getLocation().y + pChild.y % height);
            }

        }

    }


    public void draw_point() {
        strokeWeight(15);
        stroke(255, 51, 0, 200);
        for (Solution<List<RefactoringOperation>> sol : ListSolution) {
            List<Dot> dotsofaSet = new ArrayList<Dot>();
            for (RefactoringOperation refOper : sol.value()) {
                //Traduction
                for (Dot d : hashGrid) {
                    if (d.getrefOper().equals(refOper))
                        dotsofaSet.add(d);
                }

            }
            point(dotsofaSet.get(0).getLocation().x % width, dotsofaSet.get(0).getLocation().y % height);
            /*
			for(int i = 0; i < dotsofaSet.size(); i++){
				if((i + 1) != dotsofaSet.size() )
					line(dotsofaSet.get(i).getLocation().x, dotsofaSet.get(i).getLocation().y, 
							dotsofaSet.get(i+1).getLocation().x , dotsofaSet.get(i+1).getLocation().y	);
			}*/
        }

    }

    public void draw_classes() {
        stroke(199);
        strokeWeight(1);

        for (Dot d : hashGrid) {
            for (PVector p : d.getdotchildren()) {
                x = d.getLocation().x + p.x % width;
                y = d.getLocation().y + p.y % height;
                ellipse(x, y, rad / 2, rad / 2);
            }
        }
    }

    public void newRefactor() {
        // Apply the search method
        ListSolution.add(search.apply(space, goal));
        solution = ListSolution.get(ListSolution.size() - 1);

        System.out.println("QUALITY_:" + solution.quality() + "=" + "VALUE_:" + solution.value());

        for (RefactoringOperation refOper : solution.value()) {
            hashGrid.add(new Dot(random(width), random(height), refOper, solution));
        }
    }

    // Class for storing a point value. It must implement the Locatable
    // interface since objects of this type will be added to the hash grid.
    class Dot implements Locatable {
        PVector d;
        PVector end;
        RefactoringOperation refOper;
        Solution<List<RefactoringOperation>> others;
        List<PVector> dotchildren;
        float pct = 0;
        float pct_apart = 1;
        float vx = 0;
        float vy = 0;

        int xdirection = 1;  // Left or Right
        int ydirection = 1;  // Top to Bottom


        UniformGenerator g = new UniformGenerator(-rad * 5, rad * 5);

        Dot(float x, float y, RefactoringOperation refOper, Solution<List<RefactoringOperation>> solution, List<PVector> dotchildren) {
            d = new PVector(x, y);
            this.refOper = refOper;
            this.others = solution;
            this.dotchildren = dotchildren;
        }

        Dot(float x, float y, RefactoringOperation refOper, Solution<List<RefactoringOperation>> solution) {
            d = new PVector(x, y);
            this.refOper = refOper;
            this.others = solution;
            dotchildren = new ArrayList<PVector>();
            if (!(getrefOper().getParams() == null || getrefOper() == null || getrefOper().getParams().entrySet() == null))
                for (Entry<String, List<RefactoringParameter>> params : getrefOper().getParams().entrySet()) {
                    if (params.getValue() != null)
                        for (RefactoringParameter refParam : params.getValue()) {
                            dotchildren.add(new PVector((float) g.generate(), (float) g.generate()));
                        }
                }

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

        public RefactoringOperation getrefOper() {
            return refOper;
        }

        public void setdotChildren(List<PVector> dotchildren) {
            this.dotchildren = dotchildren;
        }

        public void setEndLocation(float x, float y) {
            this.end = new PVector(x, y);
        }

        public Solution getSolution() {
            return this.others;
        }

        public List<PVector> getdotchildren() {
            return this.dotchildren;
        }

    }


    public static void main(String args[]) {
        PApplet.main(new String[]{"--present", "view.ProcessingRefactor"});
    }
}
