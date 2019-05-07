package ui;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;

public class Arrow extends Line {

    private Polygon triangle;
    private TextField text;
    private Pane Parent;


    public Arrow(Pane Parent) {
        this.Parent = Parent;
        Parent.getChildren().addAll(this);
        canvas();
    }

    private DoubleBinding dx;
    private DoubleBinding dy;

    private void canvas(){
        dx = endXProperty().add(startXProperty().negate());
        dy = endYProperty().add(startYProperty().negate());
        triangle = new Polygon(getEndX(), getEndY(), getEndX() - 16, getEndY() + 8, getEndX() - 16, getEndY() - 8);
        var rotate = new Rotate(0,0,0,1,Rotate.Z_AXIS);
        triangle.getTransforms().add(rotate);
        dx.addListener((observable, oldValue, newValue) -> {
            rotate.setAngle(getAngle(dy.doubleValue(), newValue.doubleValue()));
        });
        dy.addListener((observable, oldValue, newValue) -> {
            rotate.setAngle(getAngle(newValue.doubleValue(), dx.doubleValue()));
        });
        triangle.layoutXProperty().bind(endXProperty());
        triangle.layoutYProperty().bind(endYProperty());
        Parent.getChildren().add(triangle);
        text = new TextField(String.valueOf(0));
        text.setPrefWidth(25);
        text.setPrefHeight(20);
        text.setDisable(true);
        text.setFocusTraversable(true);
        Parent.getChildren().add(text);
        var midX = endXProperty().add(startXProperty()).divide(2);
        var midY = endYProperty().add(startYProperty()).divide(2);
        text.layoutXProperty().bind(midX.add(endXProperty()).divide(2));
        text.layoutYProperty().bind(midY.add(endYProperty()).divide(2));
    }

    private double getAngle(double dy ,double dx){
        return Math.toDegrees(Math.atan2(dy, dx));
    }

    public void setWeight(int weight){
        text.setText(String.valueOf(weight));
    }

    public int getWeight(){
        return Integer.valueOf(text.getText());
    }

    public void delete(){
        Parent.getChildren().removeAll(this,triangle,text);
    }


}
