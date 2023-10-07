package com.haogus;

import com.haogus.backend.CSVExporter;
import com.haogus.backend.CSVImporter;
import com.haogus.backend.Post;
import com.haogus.backend.Posts;
import com.haogus.dao.UserDao;
import com.haogus.entity.User;
import com.haogus.util.MysqlUtil;
import com.haogus.util.UserUtil;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


public class DataAnalyticsHubApp extends Application {

    private Posts posts = new Posts();


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Data Analytics Hub");

        BorderPane root = new BorderPane();

        // Create a menu bar
        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);
//        root.setStyle("-fx-background-color: #f4f;");
        // Create a VBox for the login and registration forms
        VBox loginForm = createLoginForm(primaryStage);
        VBox registerForm = createRegisterForm(primaryStage);

        // Create a TabPane to switch between login and registration
        Tab loginTab = new Tab("Login", loginForm);
        Tab registerTab = new Tab("Register", registerForm);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(loginTab, registerTab);

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();

        // Create a File menu
        Menu fileMenu = new Menu("File");

        // Create a New Post menu item
        MenuItem newPostMenuItem = new MenuItem("New Post");
        newPostMenuItem.setOnAction(e -> showNewPostDialog(primaryStage));
        fileMenu.getItems().add(newPostMenuItem);

        // Add the File menu to the menu bar
        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    private VBox createRegisterForm(Stage primaryStage) {
        VBox registrationForm = new VBox(10);
        registrationForm.setPadding(new Insets(20, 20, 20, 20));
        TextField firstNameTextField = new TextField();
        TextField lastNameTextField = new TextField();
        TextField userNameTextField = new TextField();
        TextField passwordTextField = new TextField();
        Button registerButton = new Button("Register");
        registrationForm.getChildren().addAll(
                new Label("First Name:"),
                firstNameTextField,
                new Label("Last Name:"),
                lastNameTextField,
                new Label("User Name:"),
                userNameTextField,
                new Label("Password:"),
                passwordTextField,
                registerButton
        );
        // Handle registration button action here
        registerButton.setOnAction(e -> {
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String userName = userNameTextField.getText();
            String password = passwordTextField.getText();

            if (userName.isEmpty()
                    || firstName.isEmpty()
                    || lastName.isEmpty()
                    || password.isEmpty()) {
                showAlert(AlertType.ERROR, "提示", "输入信息不能为空");
                return;
            }

            //判断用户是否注册过
            if (userIsExist(userName)) {
                showAlert(AlertType.ERROR, "注册失败", "该用户名已经被注册过,请换个用户名");
                return;
            }
            boolean operation = createUser(firstName, lastName, userName, password);
            userLogin(userName, password);
            showDashboard(primaryStage); // Redirect to the dashboard
        });

        return registrationForm;
    }


    private VBox createLoginForm(Stage primaryStage) {
        VBox loginForm = new VBox(10);
        loginForm.setPadding(new Insets(20, 20, 20, 20));
        TextField usernameTextField = new TextField();
        TextField passwordTextField = new PasswordField();
        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        loginForm.getChildren().addAll(
                new Label("Username:"),
                usernameTextField,
                new Label("Password:"),
                passwordTextField,
                loginButton
        );
        // Handle login button action here
        loginButton.setOnAction(e -> {
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();

            if (!userLogin(username, password)) {
                showAlert(AlertType.ERROR, "Login Failed", "Please enter the correct account number or password.");
                return;
            }
            showDashboard(primaryStage); // Redirect to the dashboard
        });
        return loginForm;
    }

    private void showDashboard(Stage primaryStage) {
        // Create a dashboard with profile editing, new post, and retrieve post functionalities
        BorderPane dashboard = createDashboard(primaryStage);

        primaryStage.setTitle("Data Analytics Hub - Dashboard");
        primaryStage.getScene().setRoot(dashboard);
    }

    private BorderPane createDashboard(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();

        // Create File menu
        Menu fileMenu = new Menu("File");
//        MenuItem openItem = new MenuItem("Open");
        MenuItem exportItem = new MenuItem("Export Post");
        exportItem.setOnAction(ae -> {
            // Create a FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");

            // Set the initial directory (optional)
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            // Show the file save dialog
            File selectedFile = fileChooser.showSaveDialog(primaryStage);

            if (selectedFile != null) {
                try {
                    // Check if the selected file exists, and if not, create it
                    if (!selectedFile.exists()) {
                        selectedFile.createNewFile();
                    }
                    CSVExporter csvExporter = new CSVExporter(posts.getPosts(), selectedFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        MenuItem importItem = new MenuItem("Import Post");
        importItem.setOnAction(ae -> {
            // Open the file dialog


            // Create a FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");

            // Set the initial directory (optional)
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                // Read and process the selected CSV file
                CSVImporter csvExporter = new CSVImporter(file.getAbsolutePath(), posts);

            }


        });

        MenuItem logoutMenuItem = new MenuItem("Logout");
        logoutMenuItem.setOnAction(e -> logout(primaryStage));


        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(ae -> {
            System.exit(0);
        });


        // Create Edit menu
        Menu helpMenu = new Menu("Help");
        MenuItem vip = new MenuItem("Upgrade to VIP");
        vip.setOnAction(ae -> showSubscriptionAlert());

        // Add menu items to the Edit menu
        helpMenu.getItems().addAll(vip);

        System.out.println("1111111111" + UserUtil.getCurrentUser());
        // Add menus to the MenuBar
        if (!UserUtil.getCurrentUser().getVip()) {
            // Add menu items to the File menu
            fileMenu.getItems().addAll(exportItem, exitItem, logoutMenuItem);
            menuBar.getMenus().addAll(fileMenu, helpMenu);
        } else {
            // Add menu items to the File menu
            fileMenu.getItems().addAll(importItem, exportItem, exitItem, logoutMenuItem);
            menuBar.getMenus().addAll(fileMenu);
        }


        BorderPane dashboard = new BorderPane();
        dashboard.setTop(menuBar);

        User currentUser = UserUtil.getCurrentUser();
        // wecome screen
        VBox welcome = new VBox(10);
        welcome.setPadding(new Insets(200));
        Label welcomeMessage = new Label("Hello: " + currentUser.getUserName() + "\n" + "Welcome to Data Analytics Hub Dashboard");
        welcomeMessage.setFont(new Font("Arial", 40));
        welcomeMessage.setWrapText(true);
        welcome.getChildren().addAll(welcomeMessage);

        // Profile Editing Form
        VBox profileEditingForm = new VBox(10);
        profileEditingForm.setPadding(new Insets(20));
        TextField firstNameTextField = new TextField(currentUser.getFirstName());
        TextField lastNameTextField = new TextField(currentUser.getLastName());
        TextField userNameTextField = new TextField(currentUser.getUserName());
        TextField passwordTextField = new TextField(currentUser.getPassword());
        Button updateProfile = new Button("Update Profile");
        profileEditingForm.getChildren().addAll(
                new Label("User Profile Editing"),
                firstNameTextField, lastNameTextField, userNameTextField, passwordTextField, updateProfile
        );
        updateProfile.setOnAction(ae -> {
            String firstName = firstNameTextField.getText();
            String lastName = lastNameTextField.getText();
            String userName = userNameTextField.getText();
            String password = passwordTextField.getText();

            if (userName.isEmpty()
                    || firstName.isEmpty()
                    || lastName.isEmpty()
                    || password.isEmpty()) {
                showAlert(AlertType.ERROR, "信息不完整", "请填写基本信息");
                return;
            }

            updateUser(firstName, lastName, userName, password);
            //自动重新登录
            userLogin(userName, password);
            showDashboard(primaryStage); // Redirect to the dashboard

        });

        // Add Post Form
        VBox addPostForm = new VBox(10);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime curentDate = LocalDateTime.now();
        String curDate = dtf.format(curentDate);
        addPostForm.setPadding(new Insets(20));
        TextField id = new TextField("#id");
        TextField content = new TextField("Content");
        TextField author = new TextField("Author");
        TextField likes = new TextField("likes");
        TextField shares = new TextField("shares");
        TextField date = new TextField(curDate);
        Button postAddButton = new Button("Add Post");
        postAddButton.setOnAction(ae -> {
            try {
                if (posts.addPost(new Post(Integer.parseInt(id.getText()),
                        content.getText(), author.getText(), Integer.parseInt(likes.getText()),
                        Integer.parseInt(shares.getText()), date.getText()))) {
                    showAlert(AlertType.INFORMATION, "Added", "Your Post Has Been Added");
                    id.setText("#ID");
                    content.setText("Content");
                    author.setText("Author");
                    likes.setText("#likes");
                    shares.setText("#shares");
                    date.setText(curDate);
                } else {
                    showAlert(AlertType.ERROR, "Failed", "");
                }
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Failed", "");
            }
        });
        addPostForm.getChildren().addAll(
                new Label("Add Social Media Post"),
                id, content, author, likes, shares, date, postAddButton
        );

        // Retrieve Post Form
        VBox retrievePostForm = new VBox(10);
        retrievePostForm.setPadding(new Insets(20));
        Button retrievePostButton = new Button("Retrieve Post");
        retrievePostButton.setDefaultButton(true);
        TextField postIdLabel = new TextField("#Post ID");
        retrievePostButton.setOnAction(ae -> {
            try {
                String text = posts.retrievePostByPostId(Integer.parseInt(postIdLabel.getText()));
                Label resultLabel = new Label(text);
                retrievePostButton.setText("Retrieve Post");
                postIdLabel.setText("#Post ID");
                retrievePostForm.getChildren().clear();
                retrievePostForm.getChildren().addAll(
                        new Label("Retrieve Post by ID"),
                        postIdLabel,
                        retrievePostButton,
                        resultLabel
                );
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Error", "Integer Required. String given!");
            }
        });
        retrievePostForm.getChildren().addAll(
                new Label("Retrieve Post by ID"),
                postIdLabel,
                retrievePostButton
        );


        // Remove Post Form
        VBox removePostForm = new VBox(10);
        removePostForm.setPadding(new Insets(20));
        TextField postIdToRemove = new TextField("#Post ID");
        Button removePostButton = new Button("Remove Post");
        removePostButton.setDefaultButton(true);
        removePostButton.setOnAction(ae -> {
            try {
                String text = posts.removePostByPostId(Integer.parseInt(postIdToRemove.getText()));
                Label resultLabel = new Label(text);
                removePostForm.getChildren().clear();
                removePostForm.getChildren().addAll(
                        new Label("Retrieve Post by ID"),
                        postIdToRemove,
                        removePostButton, resultLabel
                );
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Error", "Integer Required. String given!");

            }
        });
        removePostForm.getChildren().addAll(
                new Label("Remove Post by ID"),
                postIdToRemove, removePostButton
        );

        // Retrieve Posts By Most Like Form
        VBox retrievePostsByMostLikeForm = new VBox(10);
        retrievePostsByMostLikeForm.setPadding(new Insets(20));
        TextField numberOfPost = new TextField("#Number of post");
        Button retrievePostsByMostLikeButton = new Button("Retrieve Posts By Most Like");
        retrievePostsByMostLikeButton.setDefaultButton(true);
        retrievePostsByMostLikeButton.setOnAction(ae -> {
            try {
                String text = posts.retrievePostsByMostLike(Integer.parseInt(numberOfPost.getText()));
                Label resultLabel = new Label(text);
                retrievePostsByMostLikeForm.getChildren().clear();
                retrievePostsByMostLikeForm.getChildren().addAll(
                        new Label("Retrieve Post by ID"),
                        numberOfPost,
                        retrievePostsByMostLikeButton, resultLabel
                );
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Error", "Integer Required. String given!");

            }
        });
        retrievePostsByMostLikeForm.getChildren().addAll(
                new Label("Retrieve Post by ID"),
                numberOfPost, retrievePostsByMostLikeButton
        );


        // Share
        VBox shareChart = new VBox(10);
        shareChart.setPadding(new Insets(20));
        Button sharesButton = new Button("Get Shares Chart");
        sharesButton.setOnAction(ae -> {
            // Create data for the pie chart
            double total = 0;
            Map<String, Integer> share = posts.getShares();
            double low = (double) share.get("low");
            double lower = (double) share.get("lower");
            double lowest = (double) share.get("lowest");
            total = low + lower + lowest;
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("0-99 Shares", (lowest / total) * 100), // Change the actual share counts
                    new PieChart.Data("100-999 Shares", (lower / total) * 100), // Change the actual share counts
                    new PieChart.Data("1000+ Shares", (low / total) * 100)   // Change the actual share counts
            );
            // Create the pie chart
            PieChart pieChart = new PieChart(pieChartData);
            shareChart.getChildren().clear();
            shareChart.getChildren().addAll(
                    new Label("Shares "),
                    sharesButton,
                    pieChart
            );

        });


        shareChart.getChildren().addAll(
                new Label("Shares "),
                sharesButton
        );


        // Create a tab pane to switch between profile editing, adding posts, and retrieving posts
        TabPane tabPane = new TabPane();
        Tab welcomeTab = new Tab("Dashboard", welcome);
        Tab profileTab = new Tab("Profile Editing", profileEditingForm);
        Tab addPostTab = new Tab("Add Post", addPostForm);
        Tab retrievePostTab = new Tab("Retrieve Post", retrievePostForm);
        Tab removePostTab = new Tab("Remove Post", removePostForm);
        Tab retrievePostsByMostLikeTab = new Tab("Retrieve Post", retrievePostsByMostLikeForm);
        Tab shareChartTab = new Tab("Shares", shareChart);
        tabPane.getTabs().addAll(welcomeTab, profileTab, addPostTab, retrievePostTab, removePostTab,
                retrievePostsByMostLikeTab, shareChartTab);

        dashboard.setCenter(tabPane);

        return dashboard;
    }

    private void createPieChart() {
        // Create data for the pie chart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("0-99 Shares", 30),     // Change the actual share counts
                new PieChart.Data("100-999 Shares", 45), // Change the actual share counts
                new PieChart.Data("1000+ Shares", 25)   // Change the actual share counts
        );

        // Create the pie chart
        PieChart pieChart = new PieChart(pieChartData);

    }

    private void showNewPostDialog(Stage primaryStage) {
        // Implement the new post dialog here
        // You can use JavaFX dialogs or create a custom dialog
        System.out.println("New Post Dialog");
    }

    private void logout(Stage primaryStage) {
        UserUtil.currentUser = null;
        primaryStage.setTitle("Data Analytics Hub");
        BorderPane root = new BorderPane();

        // Create a menu bar
        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);
        // root.setStyle("-fx-background-color: #f4f;");
        // Create a VBox for the login and registration forms
        VBox loginForm = createLoginForm(primaryStage);
        VBox registrationForm = createRegisterForm(primaryStage);

        // Create a TabPane to switch between login and registration
        TabPane tabPane = new TabPane();
        Tab loginTab = new Tab("Login", loginForm);
        Tab registrationTab = new Tab("Registration", registrationForm);
        tabPane.getTabs().addAll(loginTab, registrationTab);

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
    }

    private void showSubscriptionAlert() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Subscription Confirmation");
        alert.setHeaderText("Would you like to subscribe to the application?");
        alert.setContentText("This subscription is available for a monthly fee of $0.");

        // Define the buttons in the dialog
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show the dialog and wait for a user response
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        // Handle the user's choice
        if (result == yesButton) {
            UserDao userDao = MysqlUtil.getDao(UserDao.class);
            userDao.updateVip(UserUtil.getCurrentUser().getId());
            showAlert(AlertType.INFORMATION, "恭喜", "恭喜,您已经成为会员了,Please log out and log in again to access VIP functionalities.");
        }
    }

    private boolean userLogin(String userName, String password) {
        UserDao userDao = MysqlUtil.getDao(UserDao.class);
        User user = userDao.selectOneByUserName(userName, password);
        UserUtil.currentUser = user;
        System.out.println(user);
        return user != null;
    }

    private boolean userIsExist(String userName) {
        UserDao userDao = MysqlUtil.getDao(UserDao.class);
        return userDao.userIsExist(userName) > 0;
    }

    private boolean createUser(String firstName, String lastName, String userName, String password) {
        UserDao userDao = MysqlUtil.getDao(UserDao.class);
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(userName);
        user.setPassword(password);
        user.setVip(false);
        return userDao.insert(user) > 0;

    }

    private boolean updateUser(String firstName, String lastName, String userName, String password) {
        UserDao userDao = MysqlUtil.getDao(UserDao.class);
        return userDao.updateUserById(UserUtil.getCurrentUser().getId(), firstName, lastName, userName, password) > 0;

    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
