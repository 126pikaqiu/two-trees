package client;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.*;
import server.MyDictionary;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Dictionary extends Application{
    private File selectedFile;
    private int type = 0;
    private MyDictionary myDictionary = new MyDictionary();
    private Button btBrowser = new Button("Browser");
    private Button btSub = new Button("Submit");
    private Button btAdd = new Button("Add");
    private Button btDelete = new Button("Delete");
    private RadioButton rb2 = new RadioButton("red-black tree");
    private RadioButton rb3 = new RadioButton("both");
    private RadioButton rb1 = new RadioButton("B+ tree");
    private TextField tfTransEn = new TextField();
    private Button btTrans = new Button("Translate");
    private TextField tfFrom = new TextField();
    private TextField tfto = new TextField();
    private Button btSubmit = new Button("  Submit  ");
    private Label lbSearch = new Label("Search From");
    private TextArea txShow = new TextArea("Here show the result!");
    private TextField ttfFilePath = new TextField();
    private TextField tfEnglish = new TextField();
    private TextField tfChinese = new TextField();
    private Button btWalk = new Button("Preorder walk");
    private Stage stage = new Stage();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("English-Chinese Dictionary");
        initFrame(primaryStage);
        setEventListener();
        primaryStage.show();
    }

    private void initFrame(Stage primaryStage) throws IOException {
        try{
            myDictionary.init();//初始化字典
        }catch (Exception e){
            e.printStackTrace();
        }

        ttfFilePath.setPrefHeight(30);
        GridPane gridPane1 = new GridPane();
        gridPane1.setVgap(10);
        gridPane1.setHgap(10);
        gridPane1.setPadding(new Insets(10,25,10,25));
        btBrowser.setPrefWidth(120);
        btBrowser.setPrefHeight(40);
        gridPane1.add(btBrowser,0,0);
        btSub.setPrefWidth(120);
        btSub.setPrefHeight(40);
        gridPane1.add(btSub,1,0);

        GridPane gridPane5 = new GridPane();
        btAdd.setPrefWidth(120);
        btAdd.setPrefHeight(40);
        btDelete.setPrefWidth(120);
        btDelete.setPrefHeight(40);
        Label lbEnglish = new Label("English:");
        Label lbChinese = new Label("       Chinese:");
        gridPane5.add(lbEnglish,0,0);
        gridPane5.add(tfEnglish,1,0);
        gridPane5.add(lbChinese,2,0);
        gridPane5.add(tfChinese,3,0);
        gridPane5.add(btAdd,1,1);
        gridPane5.add(btDelete,2,1);
        gridPane5.setVgap(10);
        gridPane5.setHgap(10);
        gridPane5.setPadding(new Insets(10,10,10,10));
        VBox vBox8 = new VBox();
        vBox8.getChildren().add(gridPane5);
        vBox8.setPadding(new Insets(0,0,10,0));
        vBox8.setStyle("-fx-border-width: 1 1 1 1;-fx-border-color:cbcbcb;");

        VBox vBox6 = new VBox();
        vBox6.getChildren().addAll(ttfFilePath,gridPane1);
        vBox6.setPadding(new Insets(10,10,10,10));
        vBox6.setStyle("-fx-border-width: 1 1 1 1;-fx-border-color:cbcbcb;");
        VBox.setMargin(vBox6,new Insets(0,0,10,0));

        VBox vBox1 = new VBox();
        GridPane gridPane7 = new GridPane();
        gridPane7.setHgap(10);
        gridPane7.add(vBox1,0,0);

        VBox vBox3 = new VBox();
        VBox.setMargin(vBox3,new Insets(10,0,0,0));
        vBox3.getChildren().addAll(vBox6,vBox8);

        Label label = new Label("MANAGEMENT");
        VBox vBox2 = new VBox();
        vBox2.setPadding(new Insets(0,10,10,10));
        vBox2.getChildren().addAll(label,vBox3);
        VBox.setMargin(vBox2,new Insets(-10,0,0,0));
        vBox2.setStyle("-fx-border-width: 1 1 1 1;-fx-border-color:a8ced9;");

        final ToggleGroup group = new ToggleGroup();//三个单选按钮
        rb1.setToggleGroup(group);
        rb3.setSelected(true);
        rb3.setToggleGroup(group);
        rb2.setToggleGroup(group);

        GridPane gridPane2 = new GridPane();
        gridPane2.add(rb1,0,0);
        gridPane2.add(rb2,1,0);
        gridPane2.add(rb3,2,0);
        gridPane2.add(btWalk,3,0);
        gridPane2.setPadding(new Insets(10,40,20,40));
        gridPane2.setVgap(10);
        gridPane2.setHgap(10);

        VBox vBox4 = new VBox();
        Label lbLook = new Label("LOOK-UP");
        GridPane gridPane3 = new GridPane();
        Label lbto = new Label("to");
        gridPane3.add(tfTransEn,0,0);
        gridPane3.add(btTrans,4,0);
        gridPane3.add(lbSearch,0,1);
        gridPane3.add(tfFrom,1,1);
        gridPane3.add(lbto,2,1);
        gridPane3.add(tfto,3,1);
        gridPane3.add(btSubmit,4,1);
        gridPane3.setVgap(10);
        gridPane3.setHgap(30);

        VBox vBox7 = new VBox();
        vBox7.getChildren().add(gridPane3);
        vBox7.setPadding(new Insets(20,10,20,10));

        VBox vBox9 = new VBox();
        txShow.setStyle("-fx-background-color: #ffffff");
        txShow.setPrefColumnCount(30);
        txShow.setPrefRowCount(16);
        txShow.setScrollTop(40);
        txShow.setScrollLeft(40);
        txShow.setEditable(false);
        vBox9.getChildren().add(txShow);

        vBox4.getChildren().addAll(lbLook,vBox7,vBox9);
        vBox4.setPadding(new Insets(0,10,10,10));
        vBox4.setPrefHeight(215);
        vBox4.setStyle("-fx-border-width: 1 1 1 1;-fx-border-color:a8ced9;");

        VBox vBox5 = new VBox();
        vBox5.setPadding(new Insets(0,0,0,10));
        vBox5.getChildren().add(vBox4);

        vBox1.getChildren().addAll(gridPane2,vBox5);
        GridPane.setColumnSpan(tfTransEn,4);

        HBox hBox1 = new HBox();
        hBox1.getChildren().add(label);
        hBox1.setPadding(new Insets(0,0,0,10));
        VBox vBox11 = new VBox();
        vBox11.getChildren().addAll(hBox1,vBox2);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10,10,0,10));
        hBox.getChildren().addAll(vBox11,vBox1);
        Pane pane = new Pane(hBox);

        Scene scene = new Scene(pane,primaryStage.getWidth(),primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
    }

    private void setEventListener(){
        btTrans.setOnMouseClicked(mouseEvent -> {
            String key = tfTransEn.getText();
            String value = myDictionary.get(key,type);
            if(value == null || value.trim().equals("")){
                txShow.setText("Not found!");
            }else{
                txShow.setText(value);
            }
        });

        tfTransEn.setOnKeyPressed(event -> {
            String key = tfTransEn.getText();
            String value = myDictionary.get(key,type);
            if(value == null || value.trim().equals("")){
                txShow.setText("Not found!");
            }else{
                txShow.setText(value);
            }
        });

        btAdd.setOnMouseClicked(mouseEvent -> {
            txShow.setText("");
            if(tfEnglish.getText() == null || tfEnglish.getText().trim().equals("") || tfEnglish.getText() == null || tfEnglish.getText().trim().equals("")){
                txShow.setText("Please input the English and the Chinese!");
            }else{
                myDictionary.insert(tfEnglish.getText(),tfChinese.getText(),type);
                txShow.setText("Add successfully");
                tfChinese.setText("");
                tfEnglish.setText("");
            }
        });


        btDelete.setOnMouseClicked(mouseEvent -> {
            txShow.setText("");
            String key = tfEnglish.getText();
            if(tfEnglish.getText() == null || tfEnglish.getText().trim().equals("")){
                txShow.setText("Please input the English and the Chinese!");
            }else{
                String value = myDictionary.get(key,type);
                if(value == null || "".equals(value.trim())){
                    txShow.setText("Not found!");
                }else{
                    myDictionary.delete(key,type);
                    txShow.setText("Delete successfully");
                    tfChinese.setText("");
                    tfEnglish.setText("");
                }
            }
        });

        rb1.setOnMouseClicked(mouseEvent -> {
            type = 1;//b+树模式
        });
        rb2.setOnMouseClicked(mouseEvent -> {
            type = 0;//红黑树模式
        });
        rb3.setOnMouseClicked(event -> {
            type = 2;//通用模式
        });
        btBrowser.setOnMouseClicked(mouseEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("字典-选择操作文件");
            String cwd = System.getProperty("user.dir");
            File file = new File(cwd);
            fileChooser.setInitialDirectory(file);
            selectedFile = fileChooser.showOpenDialog(null);
            if(selectedFile != null){
                ttfFilePath.setText(selectedFile.getAbsolutePath());
            }
        });
        btSub.setOnMouseClicked(mouseEvent -> {
            File file = new File(ttfFilePath.getText());
            if(ttfFilePath.getText() == null || ttfFilePath.getText().trim().equals("") || !file.exists()){
                txShow.setText("Please input the legal file.");
                return;
            }

            try {
                boolean handle = myDictionary.handle(file,type);
                if(!handle){
                    txShow.setText("Illegal file!");
                }else {
                    txShow.setText("Handle file successfully!");
                    ttfFilePath.setText("");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        tfto.setOnKeyPressed(event -> {
            txShow.setText("");
            if(tfFrom.getText() == null || tfFrom.getText().trim().equals("") || tfto.getText() == null || tfto.getText().trim().equals("")){
                txShow.setText("Please input the first and the last two words!");
            }else{
                String result = myDictionary.getF2(tfFrom.getText(),tfto.getText(),type);
                if(result.equals("")){
                    txShow.setText("not found!");
                }else{
                    txShow.setText(result);
                }
            }
        });

        btSubmit.setOnMouseClicked(event -> {
            txShow.setText("");
            if(tfFrom.getText() == null || tfFrom.getText().trim().equals("") || tfto.getText() == null || tfto.getText().trim().equals("")){
                txShow.setText("Please input the first and the last two words!");
            }else{
                String result = myDictionary.getF2(tfFrom.getText(),tfto.getText(),type);
                if(result.equals("")){
                    txShow.setText("not found!");
                }else{
                    txShow.setText(result);
                }
            }
        });
        btWalk.setOnMouseClicked(event -> {
            stage.close();
            Pane pane = new Pane();
            TextArea textArea = new TextArea();
            textArea.setText(myDictionary.preorderWalk(type));
            pane.getChildren().add(textArea);
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setTitle((type == 0?"红黑树":"B+树") + "前序遍历结果");
            stage.show();
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}

