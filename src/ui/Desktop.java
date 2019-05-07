package ui;

import Node.Node;
import extentions.NodeGenerator;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;


public class Desktop extends Pane {

    private List<NodeGenerator> nodeGenerators;
    private final List<Node> nodeList = new ArrayList<>();

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeGenerators(List<NodeGenerator> nodeGenerators) {
        this.nodeGenerators = nodeGenerators;
    }

    public List<NodeGenerator> getNodeGenerators() {
        return nodeGenerators;
    }

    public Desktop() {
        if(nodeGenerators == null) nodeGenerators = new ArrayList<>();
        super.setOnDragOver(event -> {
            if (event.getDragboard().hasString() && nodeGenerators.stream().anyMatch(nodeGenerator -> nodeGenerator.getName().equals(event.getDragboard().getString()))){
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        super.setOnDragDropped(event -> {
            var success = false;
            var nodeCreator = nodeGenerators.stream().filter(nc -> nc.getName().equals(event.getDragboard().getString())).findAny().orElse(null);
            if (nodeCreator != null){
                success = nodeCreator.getNodeCreator().create(event, this, nodeList);
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }



}
