package sample;


import Node.Linker.Linker;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import ui.Desktop;
import ui.MenuCard;
import extentions.NodeGenerator;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Controller{


    @FXML
    VBox menu;

    @FXML
    Desktop desktop;

    @FXML
    Button Calculate;

    @FXML
    void CalculateOnAction(ActionEvent event){
        var nodeList = desktop.getNodeList();
        ArrayList<ArrayList<Integer>> w = new ArrayList<>();
        nodeList.forEach(x -> {
            var xList = new ArrayList<Integer>();
            nodeList.forEach(node -> {
                if(x.getOutputs().stream().anyMatch(out -> out == node)){
                    var outIndex = x.getOutputs().indexOf(node);
                    xList.add(((Linker)x).getOutputWight().get(outIndex).num);
                }else if(node == x){
                    xList.add(0);
                }else{
                    xList.add(Integer.MAX_VALUE);
                }
            });
            w.add(xList);
        });
        tableCreate(floyd(w));
    }

    TextArea table = new TextArea();

    private void tableCreate(ArrayList<ArrayList<Integer>> values){
        var text = new StringBuilder();
        for (List<Integer> row :values) {
            for (int cell : row) {
                if(cell == Integer.MAX_VALUE){
                    text.append("  inf");
                } else text.append(String.format("%5d" , cell));
            }
            text.append("\n");
        }
        System.out.println(values);
        table.setText(text.toString());
        try {
            menu.getChildren().add(table);
        }catch (IllegalArgumentException ignore){}
    }

    private ArrayList<ArrayList<Integer>> floyd(ArrayList<ArrayList<Integer>> w){
        var d = new ArrayList<ArrayList<Integer>>();
        for (ArrayList<Integer> list : w) {
            d.add((ArrayList<Integer>) list.clone());
        }
        for (int i = 1; i < d.size(); i++) {
            for (int j = 1; j < d.size(); j++) {
                for (int k = 0; k < d.size() ; k++) {
                    d.get(j).set(k,
                            (int) Math.min(d.get(j).get(k).doubleValue(),(d.get(j).get(i).doubleValue() + d.get(i).get(k).doubleValue())));
                }
            }
        }
        return d;
    }

    private MenuCard nodeCard;

    private final List<NodeGenerator> nodeGenerators = new ArrayList<>(10);

    @FXML
    private void initialize() {
        var nodeImage = new ImageView(new Image("./node.png"));
        nodeImage.setFitHeight(40);
        nodeImage.setFitWidth(60);
        nodeImage.preserveRatioProperty().setValue(true);
        nodeCard = new MenuCard("Node","./node.png",nodeImage, menu);
        var nc = new NodeGenerator("Node",((dragEvent, parent, nodeList) -> {
            try {
                var node = new Linker(nodeList,parent,dragEvent.getX(), dragEvent.getY());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }));
        nodeGenerators.add(nc);
        desktop.setNodeGenerators(nodeGenerators);
    }


}