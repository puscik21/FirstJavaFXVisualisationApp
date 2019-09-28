package grzegorz.test;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Table extends Application {

    private Stage stage;
    private Scene scene;
    private Button button;
    private TableView<Product> table;
    private TextField nameInput, priceInput, quantityInput;
    private boolean inputFieldsValidation;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        stage = primaryStage;
        stage.setTitle("This Is The Best Stage");
        button = new Button("CLick me!");

        // Columns
        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, String> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setMinWidth(100);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Inputs
        nameInput = new TextField();
        nameInput.setPromptText("Name");
        nameInput.setMinWidth(100);

        priceInput = new TextField();
        priceInput.setPromptText("Price");
        priceInput.setMinWidth(100);

        quantityInput = new TextField();
        quantityInput.setPromptText("Quantity");
        quantityInput.setMinWidth(100);

        //Button
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> addProduct());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteProduct());

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(nameInput, priceInput, quantityInput, addButton, deleteButton);

        table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setItems(getProduct());
        table.getColumns().addAll(nameColumn, priceColumn, quantityColumn);

        // Standard stage layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(table, hBox);

        scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    public ObservableList<Product> getProduct() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        products.add (new Product("Notebook", 1500.00, 20));
        products.add (new Product("TV", 1500.00, 15));
        products.add (new Product("Bauncy Ball", 2.49, 243));
        products.add (new Product("Bike", 500.00, 11));
        products.add (new Product("Smartphone", 800.00, 35));

        return products;
    }

    private void addProduct(){
        inputFieldsValidation = true;
        Product product = new Product();
        product.setName(nameInput.getText());
        setPrice(product);
        setQuantity(product);

        if (inputFieldsValidation){
            table.getItems().add(product);
            clearInputFields();
        }
    }

    private void setPrice(Product product){
        try{
            double price = Double.parseDouble(priceInput.getText());
            product.setPrice(price);
        } catch (IllegalArgumentException e){
            priceInput.setText("Wrong value!");
            inputFieldsValidation = false;
        }
    }

    private void setQuantity(Product product){
        try{
            int quantity = Integer.parseInt(quantityInput.getText());
            product.setQuantity(quantity);
        } catch (IllegalArgumentException e){
            quantityInput.setText("Wrong value!");
            inputFieldsValidation = false;
        }
    }

    private void clearInputFields(){
        nameInput.clear();
        priceInput.clear();
        quantityInput.clear();
    }

    private void deleteProduct() {
        ObservableList<Product> productsSelected, allProducts;

        allProducts = table.getItems();
        productsSelected = table.getSelectionModel().getSelectedItems();
        allProducts.removeAll(productsSelected);
    }
}