package com.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryUserInterface extends Application{
    private TableView<Book> table;
    private TextField titleInput;
    private TextField authorInput;
    private CheckBox availableInput;
    private TextField searchField;
    public static void main(String[] args) {
        launch(args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) {
        table = new TableView<>();
        table.setId("tableView");
        titleInput = new TextField();
        titleInput.setId("titleInput");
        titleInput.setPromptText("Title");
        authorInput = new TextField();
        authorInput.setId("authorInput");
        authorInput.setPromptText("Author");
        availableInput = new CheckBox();
        availableInput.setId("availableInput");
        searchField = new TextField();
        searchField.setPromptText("Search by title or author");
        searchField.setId("searchField");

        //Define Colums for TableView
        TableColumn<Book, Number> idColumn = new TableColumn<>("ID");
        idColumn.setPrefWidth(100);
        idColumn.setCellValueFactory(data -> data.getValue().bookIdProperty());

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setPrefWidth(200);
        titleColumn.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setPrefWidth(200);
        authorColumn.setCellValueFactory(data -> data.getValue().authorProperty());

        TableColumn<Book, Boolean> availableColumn = new TableColumn<>("Available");
        availableColumn.setPrefWidth(100);
        availableColumn.setCellValueFactory(data -> data.getValue().availableProperty());

        table.getColumns().add(availableColumn);
        table.getColumns().add(authorColumn);
        table.getColumns().add(titleColumn);
        table.getColumns().add(idColumn);

        //Buttons

        Button addButton = new Button("Add");
        addButton.setId("addButton");
        Button searchButton = new Button("Search");
        searchButton.setId("searchButton");
        Button deleteButton = new Button("Delete");
        deleteButton.setId("deleteButton");
        Button updateButton = new Button("Update");
        updateButton.setId("updateButton");
        Button insertButton = new Button("Insert");
        insertButton.setId("insertButton");
        Button refreshButton = new Button("Refresh");
        refreshButton.setId("refreshButton");
        Button sortButton = new Button("Sort by Title");
        sortButton.setId("sortButton");

        searchButton.setOnAction(e -> searchBooks());
        addButton.setOnAction(e -> addBook());
        deleteButton.setOnAction(e -> deleteBook());
        updateButton.setOnAction(e -> updateBook());
        insertButton.setOnAction(e -> insertBook());
        refreshButton.setOnAction(e -> getBooksFromDatabase());
        sortButton.setOnAction(e -> sortBooksByTitle());

        HBox buttonBox = new HBox(10,searchButton,insertButton,updateButton,deleteButton,refreshButton,sortButton);
        buttonBox.setPadding(new Insets(10,10,10,10));
        
        VBox root = new VBox(10, searchField,titleInput,authorInput,availableInput,buttonBox,table);
        root.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene(root, 650, 600);
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshTable();
        }
        //Load Books from Database (Creating immediately)

        private ObservableList<Book> getBooksFromDatabase() {
            ObservableList<Book> books = FXCollections.observableArrayList();
            String query = "SELECT * FROM books";
            try (Connection conn = connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    boolean available = rs.getBoolean("available");
                    books.add(new Book(id, title, author, available));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return books;
        }

        private void refreshTable() {
            table.setItems(getBooksFromDatabase());
        }
        private void searchBooks() {
            String keyword = searchField.getText().toLowerCase();
            if(keyword.isEmpty()) {
                showAlert(Alert.AlertType.ERROR,"Search Error", "Please enter a keyword to search.");
                return;
            }

            ObservableList<Book> filteredBooks = FXCollections.observableArrayList();

            for (Book book : getBooksFromDatabase()) {
                if (book.getTitle().toLowerCase().contains(keyword) || book.getAuthor().toLowerCase().contains(keyword)) {
                    filteredBooks.add(book);
                }
            }
            table.setItems(filteredBooks);
            if(filteredBooks.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION,"No Results", "No books found matching the keyword.");
            }
    }
        private void insertBook(){
            String title = titleInput.getText();
            String author = authorInput.getText();
            boolean available = availableInput.isSelected();

            if(title.isEmpty() || author.isEmpty()) {
                showAlert(Alert.AlertType.ERROR,"Input Error", "Title and Author fields cannot be empty.");
                return;
            }

            String insertQuery = "INSERT INTO books (title, author, available) VALUES (?, ?, ?)";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, title);
                pstmt.setString(2, author);
                pstmt.setBoolean(3, available);
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book inserted successfully.");
                refreshTable();
                clearInputs();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR,"Database Error", "Failed to insert book into the database.");
            }
    }
        private void updateBook(){
            Book selectedBook = table.getSelectionModel().getSelectedItem();
            if (selectedBook == null) {
                showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a book to update.");
                return;
            }

            String title = titleInput.getText();
            String author = authorInput.getText();
            boolean available = availableInput.isSelected();

            if (title.isEmpty() || author.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Title and Author fields cannot be empty.");
                return;
            }

            String updateQuery = "UPDATE books SET title = ?, author = ?, available = ? WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                pstmt.setString(1, title);
                pstmt.setString(2, author);
                pstmt.setBoolean(3, available);
                pstmt.setInt(4, selectedBook.getBookId());
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully.");
                refreshTable();
                clearInputs();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update book in the database.");
            }
        }
        private void deleteBook(){
            Book selectedBook = table.getSelectionModel().getSelectedItem();
            if (selectedBook == null) {
                showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a book to delete.");
                return;
            }

            String deleteQuery = "DELETE FROM books WHERE id = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                pstmt.setInt(1, selectedBook.getBookId());
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted successfully.");
                refreshTable();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete book from the database.");
            }
        }
        private void addBook(){
            String title = titleInput.getText();
            String author = authorInput.getText();
            boolean available = availableInput.isSelected();

            if(title.isEmpty() || author.isEmpty()) {
                showAlert(Alert.AlertType.ERROR,"Input Error", "Title and Author fields cannot be empty.");
                return;
            }

            Book newBook = new Book(0, title, author, available); // ID will be set by the database
            table.getItems().add(newBook);
            clearInputs();
        }
        private void sortBooksByTitle() {
            ObservableList<Book> books = table.getItems();
            FXCollections.sort(books, Comparator.comparing(Book::getTitle));
        
        }
        private void showAlert(Alert.AlertType alertType, String title, String message) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }
        private void clearInputs() {
            titleInput.clear();
            authorInput.clear();
            availableInput.setSelected(false);
        }
        private Connection connect() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/librarydb";
            String user = "root";
            String password = "password";
            return DriverManager.getConnection(url, user, password);
        }

}


