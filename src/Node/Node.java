package Node;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import jfxtras.labs.util.event.MouseControlUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class Node {
    protected final Pane desktop;
    protected final String FXMLUrl;
    protected final List<Node> nodes;
    protected final List<Node> inputs = new ArrayList<>();
    protected final List<Line> inputsWire = new ArrayList<>();
    protected final ObservableList<Node> outputs = FXCollections.observableList(new ArrayList<>());
    protected final List<Line> outputsWire = new ArrayList<>();
    protected AnchorPane mainPane;
    private BooleanProperty Locked = new SimpleBooleanProperty(false);

    protected Node(List<Node> nodes, Pane desktop, double x, double y, String FXMLUrl) throws IOException {
        this.desktop = desktop;
        this.FXMLUrl = FXMLUrl;
        loadFXMLModel(x, y);
        this.nodes = nodes;
        nodes.add(this);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Node> getInputs() {
        return inputs;
    }

    public List<Line> getInputsWire() {
        return inputsWire;
    }

    public ObservableList<Node> getOutputs() {
        return outputs;
    }

    public List<Line> getOutputsWire() {
        return outputsWire;
    }

    protected void loadFXMLModel(double x, double y) throws IOException {
        mainPane = FXMLLoader.load(getClass().getResource(FXMLUrl));
        desktop.getChildren().add(mainPane);
        mainPane.setLayoutX(x);
        mainPane.setLayoutY(y);
        ((NodeController) mainPane.getUserData()).setThisNode(this);
    }

    public void lineCanvas(int sourceIndex) {
        var line = new Line();
        outputsWire.add(line);
        nodes.get(sourceIndex).getInputsWire().add(line);
        desktop.getChildren().add(line);
        line.setMouseTransparent(true);
        line.setDisable(true);
    }

    public void resetWires() {
        IntStream.range(0, inputsWire.size()).forEach(x -> {
            var wire = inputsWire.get(x);
            wire.endXProperty().unbind();
            wire.endYProperty().unbind();
            wire.endXProperty().bind(getInputXProperty(x));
            wire.endYProperty().bind(getInputYProperty(x));
        });
        IntStream.range(0, outputsWire.size()).forEach(x -> {
            var wire = outputsWire.get(x);
            wire.startXProperty().unbind();
            wire.startYProperty().unbind();
            wire.startXProperty().bind(getOutputXProperty());
            wire.startYProperty().bind(getOutputYProperty());
        });
    }

    protected abstract ObservableValue<? extends Number> getInputYProperty(int inputIndex);

    protected abstract ObservableValue<? extends Number> getInputXProperty(int inputIndex);

    protected abstract ObservableValue<? extends Number> getOutputYProperty();

    protected abstract ObservableValue<? extends Number> getOutputXProperty();

    public AnchorPane getMainPane() {
        return mainPane;
    }

    public BooleanProperty isLocked() {
        return Locked;
    }

    public void makeDraggable() {
        MouseControlUtil.makeDraggable(mainPane);
        Locked.setValue(false);
    }

    public void disableDraggable() throws IOException {
        var x = mainPane.getLayoutX();
        var y = mainPane.getLayoutY();
        desktop.getChildren().remove(mainPane);
        Locked.setValue(true);
        loadFXMLModel(x, y);
    }

    public void delete() {
        IntStream.range(0, outputs.size()).forEach(this::deleteOutputDependencies);
        IntStream.range(0, inputs.size()).forEach(this::deleteInputDependencies);
        nodes.remove(this);
        desktop.getChildren().remove(mainPane);
    }

    protected void deleteInputDependencies(int i){
        var input = inputs.get(i);
        input.outputs.remove(this);
        var inputWire = inputsWire.get(i);
        input.outputsWire.remove(inputWire);
        desktop.getChildren().remove(inputWire);
    }

    protected void deleteOutputDependencies(int i){
        var output = outputs.get(i);
        output.inputs.remove(this);
        var outputWire = outputsWire.get(i);
        output.inputsWire.remove(outputWire);
        desktop.getChildren().remove(outputWire);
    }

    protected void setLayoutX(double X){
        mainPane.setLayoutX(X);
    }

    protected void setLayoutY(double Y){
        mainPane.setLayoutY(Y);
    }

}
