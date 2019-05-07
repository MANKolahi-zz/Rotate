package extentions;

import Node.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.Pane;

import java.util.List;

@FunctionalInterface
public interface nodeCreator {

    boolean create(DragEvent dragEvent, Pane parent, List<Node> nodeList);

}
