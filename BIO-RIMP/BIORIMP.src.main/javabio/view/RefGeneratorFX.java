package javabio.view;/**
 * Created by david on 9/12/16.
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RefGeneratorFX extends Application{

    Button button;
    Stage windows;
    Scene scene1, scene2;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        windows = primaryStage;
        Label label1 = new Label("WEKLCOME");
        Button button1 = new Button("GOTO TWO");
        button1.setOnAction(e ->windows.setScene(scene2));

        VBox layout1 = new VBox(20);
        layout1.getChildren().addAll(label1,button1);
        scene1 = new Scene(layout1, 200, 200);

        primaryStage.setTitle("Refactoring Generator");
        button = new Button("Test");

        button.setOnAction(e -> System.out.print("Hello Bitches"));

        StackPane refLayout = new StackPane();
        refLayout.getChildren().add(button);

        Scene scene = new Scene(refLayout,300,250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


}
