package Node.Linker;

import Node.Node;
import Node.NodeController;
import extentions.BoxedInteger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import ui.Arrow;

import java.io.IOException;
import java.util.Arrays;


public class LinkerModel extends NodeController {

    @FXML
    private Circle hub;

    Circle getHub() {
        return hub;
    }

    @Override
    protected void setContextMenuList() {
        var lock = new MenuItem("Lock");
        lock.setOnAction(event -> {
            try {
                thisNode.disableDraggable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        var unlock = new MenuItem("Unlock");
        unlock.setOnAction(event -> {
            thisNode.makeDraggable();
            hub.setOnDragDetected(null);
            mainPane.setOnDragDetected(null);
        });
        lock.disableProperty().bind(thisNode.isLocked());
        unlock.disableProperty().bind(thisNode.isLocked().not());
        var delete = new MenuItem("Delete");
        delete.setOnAction(event -> thisNode.delete());
        MenuItem items[] = {lock, unlock, delete};
        menuItems.addAll(Arrays.asList(items));
    }

    @Override
    protected void onDragDropped(DragEvent dragEvent) {
        super.onDragDropped(dragEvent);
    }

    private BoxedInteger setWeight(){
        BoxedInteger weight = new BoxedInteger();
        var root = new VBox(20);
        root.setPrefSize(300,150);
        root.setAlignment(Pos.CENTER);
        var okButton = new Button("OK");
        var cancelButton = new Button("Cancel");
        var text = new TextField();
        text.setPrefWidth(25);
        text.setPrefHeight(20);
        var hBox = new HBox(cancelButton,okButton);
        root.getChildren().addAll(text,hBox);
        var scene = new Scene(root,300,150);
        var stage = new Stage();
        stage.setScene(scene);
        okButton.setOnAction(event -> {
            try {
                weight.num = Integer.valueOf(text.getText());
                stage.close();
            }catch (Exception ex){ex.printStackTrace();}
        });
        cancelButton.setOnAction(event -> stage.close());
        stage.showAndWait();
        return weight;
    }


    @FXML
    protected void initialize() {
        Platform.runLater(() -> {
            if (thisNode.isLocked().get()) {
                EventHandler<? super MouseEvent> onDragDetectedHandler = event -> {
                    var dragBoard = hub.startDragAndDrop(TransferMode.ANY);
                    var content = new ClipboardContent();
                    content.putString("linkerWire");
                    var nodeIndex = thisNode.getNodes().indexOf(thisNode);
                    hub.setUserData(nodeIndex);
                    dragBoard.setContent(content);
                };
                hub.setOnDragDetected(onDragDetectedHandler);
                mainPane.setOnDragDetected(onDragDetectedHandler);
            }
            surfaceNodes.add(hub);
            super.initialize();
        });
    }

    @Override
    protected void makeConnection(int sourceNodeIndex, Node sourceNode) {
        thisNode.getInputs().add(sourceNode);
        sourceNode.getOutputs().add(thisNode);
        try {
            var weight = setWeight();
            ((Linker)sourceNode).getOutputWight().add(weight);
            ((Linker)thisNode).getInputWight().add(weight);
        }catch (ClassCastException ignore){
            ignore.printStackTrace();
        }
        thisNode.lineCanvas(sourceNodeIndex);
        thisNode.resetWires();
        sourceNode.resetWires();
    }
}
