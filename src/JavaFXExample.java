import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class JavaFXExample extends Application {
	private int count = 0;
	private Label label = new Label("0");

	public static void main(String[] args) {
		launch(args);
	}

	private void updateLabel() {
		Platform.runLater(new Runnable() {
			public void run() {
				label.setText("" + count);
			}
		});
	}

	@Override
	public void start(Stage stage) {
		stage.setScene(new Scene(label));
		stage.show();
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					Util.justSleep(1000);
					count++;
					updateLabel();
				}
			}
		}).start();
	}
}