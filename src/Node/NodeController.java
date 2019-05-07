package Node;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public abstract class NodeController{

    @FXML
    protected javafx.scene.Node mainPane;
    protected List<javafx.scene.Node> surfaceNodes;
    protected ContextMenu contextMenu;
    protected List<MenuItem> menuItems = new ArrayList<>();
    protected Node thisNode;
//    private final String name;

//    public String getName() {
//        return name;
//    }

    protected NodeController() {
        contextMenu = new ContextMenu();
        surfaceNodes = new LinkedList<>();
//        this.name = name;
    }

    public Node getThisNode() {
        return thisNode;
    }

    public void setThisNode(Node thisNode) {
        this.thisNode = thisNode;
    }

    public List<javafx.scene.Node> getSurfaceNodes() {
        return surfaceNodes;
    }

    protected void contextMenuHandel(MouseEvent mouseEvent) {
        if (mouseEvent.isSecondaryButtonDown()) {
            if (contextMenu.isShowing()) contextMenu.hide();
            contextMenu.show(thisNode.getMainPane(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
        }
    }

    protected abstract void setContextMenuList();

    protected void initialize() {
        Platform.runLater(() -> {
            setContextMenuList();
            contextMenu.getItems().addAll(menuItems);
            surfaceNodes.forEach(node -> {
                final EventHandler<MouseEvent> OpenHand = event -> node.setCursor(Cursor.OPEN_HAND);
                node.setOnMouseEntered(OpenHand);
                node.setOnMousePressed(event -> {
                    node.setCursor(Cursor.CLOSED_HAND);
                    contextMenuHandel(event);
                });
                node.setOnMouseDragged(event -> {
                    node.setCursor(Cursor.CLOSED_HAND);
                    if (contextMenu != null) contextMenu.hide();
                });
                node.setOnMouseReleased(OpenHand);
                node.setOnDragOver(this::dragOverAccept);
                node.setOnDragDropped(this::onDragDropped);
                if (!thisNode.isLocked().get()) thisNode.makeDraggable();
            });
        });
    }

    protected void dragOverAccept(DragEvent dragEvent) {
        int sourceNodeIndex;
        Node sourceNode;
        if ("linkerWire".equals(dragEvent.getDragboard().getString())) {
            sourceNodeIndex = ((int) ((Circle) dragEvent.getGestureSource()).getUserData());
            sourceNode = thisNode.getNodes().get(sourceNodeIndex);
        } else {
            return;
        }
        if (thisNode.getInputs().stream().parallel().noneMatch(input -> input == sourceNode)
                && thisNode != sourceNode) {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
    }

    protected void onDragDropped(DragEvent dragEvent) {
        int sourceNodeIndex;
        Node sourceNode;
        if ("linkerWire".equals(dragEvent.getDragboard().getString())) {
            sourceNodeIndex = ((Integer) ((Circle) dragEvent.getGestureSource()).getUserData());
            sourceNode = thisNode.nodes.get(sourceNodeIndex);
        } else {
            return;
        }
        makeConnection(sourceNodeIndex, sourceNode);
    }

    protected void makeConnection(int sourceNodeIndex, Node sourceNode) {
        thisNode.getOutputs().add(sourceNode);
        sourceNode.getInputs().add(thisNode);
        thisNode.lineCanvas(sourceNodeIndex);
        thisNode.resetWires();
        sourceNode.resetWires();
    }

}


