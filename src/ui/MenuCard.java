package ui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


public class MenuCard {


    private final Node cardSample;
    private final String name;
    private final String imageUrl;
    private final HBox card = new HBox();
    private WritableImage imageView;

    public MenuCard(String name, String imageUrl, Node cardSample){
        this.name = name;
        this.cardSample = cardSample;
        this.imageUrl = imageUrl;
        initializeCard();
        setImageView();
        setHandlers();
    }

    public MenuCard(String name, String imageUrl, Node cardSample, Pane Parent){
        this(name, imageUrl, cardSample);
        addToList(Parent);
    }

    public void addToList(Pane parent){
        parent.getChildren().add(card);
    }

    private void initializeCard(){
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMaxWidth(Region.USE_COMPUTED_SIZE);
        card.setNodeOrientation(NodeOrientation.INHERIT);
        card.setOpaqueInsets(new Insets(1,1,1,11));
        card.setPrefHeight(40);
        card.setSpacing(20);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setFillHeight(true);
        card.setBackground(new Background(new BackgroundFill(
                Paint.valueOf("white"),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        card.setBorder(new Border(new BorderStroke(
                Color.rgb(150,150,150),
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                BorderStroke.THIN
        )));
        final var textField = new TextField();
        textField.setText(name);
        textField.setMouseTransparent(true);
        textField.setPrefWidth(55);
        textField.setPrefHeight(26);
        textField.setAlignment(Pos.CENTER);
        textField.setFocusTraversable(false);
        card.getChildren().addAll(cardSample,textField);
        card.setPadding(new Insets(1,0,1,10));
    }

    private void setHandlers(){
        EventHandler<? super MouseEvent> openHand;
        openHand = event -> card.setCursor(Cursor.OPEN_HAND);
        card.setOnMouseEntered(openHand);
        card.setOnMouseReleased(openHand);
        card.setOnMousePressed(event -> card.setCursor(Cursor.CLOSED_HAND));
        card.setOnDragDetected(event -> {
            var dragBoard = card.startDragAndDrop(TransferMode.ANY);
            var content = new ClipboardContent();
            content.putString(name);
            dragBoard.setContent(content);
            dragBoard.setDragView(imageView);
            card.setCursor(Cursor.CLOSED_HAND);
            event.consume();
        });
    }

    private void setImageView(){
        var image = new ImageView(new Image(imageUrl));
        image.setFitWidth(120);
        image.setFitHeight(80);
        image.setPreserveRatio(true);
        var sp = new SnapshotParameters();
        imageView = image.snapshot(sp,null);
    }


}
