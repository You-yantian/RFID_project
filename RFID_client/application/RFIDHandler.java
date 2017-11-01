package application;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.io.IOException;

public class RFIDHandler implements Initializable{
	private ServerConnection server;
	private String message;
	private Item item=new Item();
	@FXML
    private Label label;
	@FXML
    private Label label1;
	@FXML
    private Label label2;
    @FXML
    private Button VersionButton;
    @FXML
    private Button TagButton;
    @FXML
    private Button ReadButton;
    @FXML
    private Button RecordButton;
    @FXML
    private TextField itemName;
    @FXML
    private TextField boughtDate;
    @FXML
    private TextField expireDate;
    @FXML
    private TextArea showResult;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
       // GuessButton.disableProperty().bind(textToGuess.textProperty().isEqualTo(""));
    }

    @FXML
    private void VersionButtonhandler(ActionEvent event) {
        new ConnectService().start();
    }

    @FXML
    private void TagButtonhandler(ActionEvent event) {
        new TagDetailService().start();
    }

    @FXML
    private void readButtonhandler(ActionEvent event) {
        new readService().start();
    }

    @FXML
    private void recordButtonhandler(ActionEvent event) {
        new recordService().start();
    }

    @FXML
    private void WriteButtonhandler(ActionEvent event) {
        new writeService().start();
    }
////reader Version///
    private class ConnectService extends Service<ServerConnection> {

        private ConnectService() {
            setOnSucceeded((WorkerStateEvent event) -> {
                server = getValue();
                try {
                    showResult.clear();
                    message = server.StartServer();
                    showResult.appendText(message + "\n");
                    VersionButton.setDisable(false);
                } catch (IOException e) {
                    System.err.println("Unexpected Error");
                }
            });
            setOnFailed((WorkerStateEvent event) -> {
                showResult.appendText(getException().getMessage());
            });
        }

        @Override
        protected Task<ServerConnection> createTask() {
            return new Task<ServerConnection>() {
                @Override
                protected ServerConnection call() {

                    return new ServerConnection();
                }
            };
        }
    }
/////TagDetail
    private class TagDetailService extends Service<ServerConnection> {

        private TagDetailService() {
            setOnSucceeded((WorkerStateEvent event) -> {
                server = getValue();
                try {
                    showResult.clear();
                    message = server.TagDetail();
                    showResult.appendText(message + "\n");
                    VersionButton.setDisable(false);
                } catch (IOException e) {
                    System.err.println("Unexpected Error");
                }
            });
            setOnFailed((WorkerStateEvent event) -> {
                showResult.appendText(getException().getMessage());
            });
        }

        @Override
        protected Task<ServerConnection> createTask() {
            return new Task<ServerConnection>() {
                @Override
                protected ServerConnection call() {

                    return new ServerConnection();
                }
            };
        }
    }
/////read
    private class readService extends Service<ServerConnection> {

        private readService() {
            setOnSucceeded((WorkerStateEvent event) -> {
                server = getValue();
                try {
                    showResult.clear();
                    item = server.read();

                    showResult.appendText("Achieve times of this item is: "+ item.times+"\n");
                    showResult.appendText("Bought date of this item is: "+ item.boughtDate+"\n");
                    showResult.appendText("Expire date of this item is: "+ item.expireDate+"\n");
                    VersionButton.setDisable(false);
                } catch (IOException e) {
                    System.err.println("Unexpected Error");
                }
            });
            setOnFailed((WorkerStateEvent event) -> {
                showResult.appendText(getException().getMessage());
            });
        }

        @Override
        protected Task<ServerConnection> createTask() {
            return new Task<ServerConnection>() {
                @Override
                protected ServerConnection call() {

                    return new ServerConnection();
                }
            };
        }
    }
/////record
    private class recordService extends Service<ServerConnection> {

        private recordService() {
            setOnSucceeded((WorkerStateEvent event) -> {
                server = getValue();
                try {
                    showResult.clear();
                    message = server.record();

                    showResult.appendText(message+"\n");
                    VersionButton.setDisable(false);
                } catch (IOException e) {
                    System.err.println("Unexpected Error");
                }
            });
            setOnFailed((WorkerStateEvent event) -> {
                showResult.appendText(getException().getMessage());
            });
        }

        @Override
        protected Task<ServerConnection> createTask() {
            return new Task<ServerConnection>() {
                @Override
                protected ServerConnection call() {

                    return new ServerConnection();
                }
            };
        }
    }
/////initial
    private class writeService extends Service<ServerConnection> {

        private writeService() {
            setOnSucceeded((WorkerStateEvent event) -> {
                server = getValue();
                String data=itemName.getText().length()+itemName.getText()+
                		boughtDate.getText().length()+boughtDate.getText()+
                		expireDate.getText().length()+expireDate.getText();
                System.out.println(data);
                try {
                    showResult.clear();
                    message = server.write(data);

                    showResult.appendText(message+"\n");
                    VersionButton.setDisable(false);
                } catch (IOException e) {
                    System.err.println("Unexpected Error");
                }
            });
            setOnFailed((WorkerStateEvent event) -> {
                showResult.appendText(getException().getMessage());
            });
        }

        @Override
        protected Task<ServerConnection> createTask() {
            return new Task<ServerConnection>() {
                @Override
                protected ServerConnection call() {

                    return new ServerConnection();
                }
            };
        }
    }
}
