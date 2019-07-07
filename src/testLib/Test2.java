package testLib;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class Test2 extends Application implements Runnable {
	
	private static Canvas canvas;
	
	public static void main(String args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		canvas = new Canvas(250,250);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		 
		gc.setFill(Color.BLUE);
		gc.fillRect(50,50,100,100);
		
		Scene scene = new Scene(new BorderPane(canvas), 500, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
		Platform.runLater(this);
	}

	@Override
	public void run() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		setTransformation(gc, new Translate(20, 20));
		setTransformation(gc, new Scale(2, 2));
//		gc.setTransform(t.getMxx(), t.getMxy(), t.getMxy(), t.getMyy(), t.getTx(), t.getTy());
		gc.fillRect(50, 50,100,100);
		
	}
	
	private static void setTransformation(GraphicsContext gc, Transform trasnformation) {
		gc.setTransform(trasnformation.getMxx(), trasnformation.getMxy(), trasnformation.getMxy(), 
				trasnformation.getMyy(), trasnformation.getTx(), trasnformation.getTy());
	}
}
