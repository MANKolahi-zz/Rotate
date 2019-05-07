package Node.Linker;

import Node.Node;
import extentions.BoxedInteger;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import ui.Arrow;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Linker extends Node {

    private Circle hub;

    private List<BoxedInteger> outputWight = new LinkedList<>();

    private List<BoxedInteger> inputWight = new LinkedList<>();

    public List<BoxedInteger> getOutputWight() {
        return outputWight;
    }

    public List<BoxedInteger> getInputWight() {
        return inputWight;
    }

    public Linker(List<Node> nodes, Pane desktop, double x, double y) throws IOException {
        super(nodes, desktop, x, y, "../Linker/linkerModel.fxml");
        this.hub = ((LinkerModel) mainPane.getUserData()).getHub();
    }

    @Override
    public void disableDraggable() throws IOException {
        super.disableDraggable();
        resetWires();
    }

    @Override
    protected ObservableValue<? extends Number> getInputYProperty(int inputIndex) {
        return mainPane.layoutYProperty().add(10);
    }

    @Override
    protected ObservableValue<? extends Number> getInputXProperty(int inputIndex) {
        return mainPane.layoutXProperty().add(10);
    }

    @Override
    protected ObservableValue<? extends Number> getOutputYProperty() {
        return getMainPane().layoutYProperty().add(10);
    }

    @Override
    protected ObservableValue<? extends Number> getOutputXProperty() {
        return getMainPane().layoutXProperty().add(10);
    }


    @Override
    protected void deleteInputDependencies(int i) {
        var input = inputs.get(i);
        input.getOutputs().remove(this);
        var inputWire = inputsWire.get(i);
        input.getOutputsWire().remove(inputWire);
        try {
            BoxedInteger wight = this.inputWight.get(i);
            ((Linker) input).getInputWight().remove(wight);
            ((Arrow)inputWire).delete();
        }catch (ClassCastException ex){
            desktop.getChildren().remove(inputWire);
        }
    }

    @Override
    protected void deleteOutputDependencies(int i) {
        var output = outputs.get(i);
        output.getInputs().remove(this);
        var outputWire = outputsWire.get(i);
        output.getInputsWire().remove(outputWire);
        try {
            BoxedInteger wight = this.outputWight.get(i);
            ((Linker) output).getOutputWight().remove(wight);
            ((Arrow)outputWire).delete();
        }catch (ClassCastException ex){
            desktop.getChildren().remove(outputWire);
        }
    }

    @Override
    public void lineCanvas(int sourceIndex) {
        var line = new Arrow(desktop);
        inputsWire.add(line);
        nodes.get(sourceIndex).getOutputsWire().add(line);
        var weight = inputWight.get(inputWight.size() - 1);
        line.setWeight(weight.num);
    }
}

