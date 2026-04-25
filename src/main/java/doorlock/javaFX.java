package doorlock;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class javaFX extends Application {

    private ArrayList<Door> doors = new ArrayList<>();

    private String[] defaultStatuses = {
        "Locked",
        "Locked",
        "Unlocked",
        "Locked",
        "Unlocked",
        "Locked"
    };

    private ArrayList<DoorRow> doorRows = new ArrayList<>();
    private Label    statusLabel;
    private Label    modeLabel;
    private TextArea eventLog;
    private String   currentMode = "Default";
    private HashMap<String, String[]> modeExpected;
    private VBox     leftPanel;

    @Override
    public void start(Stage primaryStage) {

        String[] doorNames = {
            "Main Entrance", "Side Door A", "Side Door B",
            "Gym Door",      "Library Door", "Office Door"
        };
        for (int i = 0; i < doorNames.length; i++) {
            doors.add(new Door(doorNames[i], defaultStatuses[i]));
        }

        modeExpected = new HashMap<>();
        modeExpected.put("AM",          new String[]{"Unlocked", "Unlocked", "Locked",   "Locked",   "Unlocked", "Unlocked"});
        modeExpected.put("PM",          new String[]{"Locked",   "Locked",   "Locked",   "Unlocked", "Locked",   "Locked"});
        modeExpected.put("Lockdown",    new String[]{"Locked",   "Locked",   "Locked",   "Locked",   "Locked",   "Locked"});
        modeExpected.put("Fire Alarm",  new String[]{"Unlocked", "Unlocked", "Unlocked", "Unlocked", "Unlocked", "Unlocked"});
        modeExpected.put("Maintenance", new String[]{"Unlocked", "Unlocked", "Unlocked", "Unlocked", "Unlocked", "Locked"});

        Label titleLabel = new Label("Door Lock Monitoring System");
        titleLabel.getStyleClass().add("title-label");

        HBox topBar = new HBox(titleLabel);
        topBar.setAlignment(Pos.CENTER);
        topBar.getStyleClass().add("top-bar");

        modeLabel = new Label("Mode: Default");
        modeLabel.getStyleClass().add("mode-label");

        Button btnAM          = new Button("AM Mode");
        Button btnPM          = new Button("PM Mode");
        Button btnLockdown    = new Button("Lockdown");
        Button btnFire        = new Button("Fire Alarm");
        Button btnMaintenance = new Button("Maintenance");
        Button btnReset       = new Button("Reset");

        btnAM.getStyleClass().add("mode-button");
        btnPM.getStyleClass().add("mode-button");
        btnLockdown.getStyleClass().add("mode-button");
        btnFire.getStyleClass().add("fire-button");
        btnMaintenance.getStyleClass().add("maintenance-button");
        btnReset.getStyleClass().add("reset-button");

        for (Button b : new Button[]{ btnAM, btnPM, btnLockdown, btnFire, btnMaintenance, btnReset }) {
            b.setMaxWidth(Double.MAX_VALUE);
        }

        Button btnAddMode = new Button("+ Add Mode");
        btnAddMode.getStyleClass().add("add-mode-button");
        btnAddMode.setMaxWidth(Double.MAX_VALUE);
        btnAddMode.setOnAction(e -> openAddModeDialog());

        leftPanel = new VBox(10, modeLabel, btnAM, btnPM, btnLockdown, btnFire, btnMaintenance, new Separator(), btnReset, btnAddMode);
        leftPanel.setPadding(new Insets(15));
        leftPanel.getStyleClass().add("left-panel");

        VBox centerPanel = new VBox(6);
        centerPanel.setPadding(new Insets(15));
        centerPanel.getStyleClass().add("center-panel");

        centerPanel.getChildren().add(buildHeaderRow());

        for (int i = 0; i < doors.size(); i++) {
            DoorRow row = new DoorRow(doors.get(i), i);
            doorRows.add(row);
            centerPanel.getChildren().add(row);
        }

        Label logLabel = new Label("Event Log:");
        logLabel.getStyleClass().add("log-label");

        eventLog = new TextArea();
        eventLog.setEditable(false);
        eventLog.setPrefHeight(130);
        eventLog.getStyleClass().add("event-log");

        centerPanel.getChildren().addAll(logLabel, eventLog);
        log("System started. Default mode active.");

        statusLabel = new Label("Status: System ready. Select a mode to begin.");
        statusLabel.getStyleClass().add("status-label");

        HBox bottomBar = new HBox(statusLabel);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.getStyleClass().add("bottom-bar");

        btnAM.setOnAction(e -> applyMode("AM"));
        btnPM.setOnAction(e -> applyMode("PM"));
        btnLockdown.setOnAction(e -> applyMode("Lockdown"));
        btnFire.setOnAction(e -> applyMode("Fire Alarm"));
        btnMaintenance.setOnAction(e -> applyMode("Maintenance"));
        btnReset.setOnAction(e -> resetAll());

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(leftPanel);
        root.setCenter(centerPanel);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 820, 610);
        scene.getStylesheets().add(
            getClass().getResource("/doorlock/style.css").toExternalForm()
        );

        primaryStage.setTitle("Door Lock Monitoring System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox buildHeaderRow() {
        Label h1 = new Label("Door Name");
        Label h2 = new Label("Expected Status");
        Label h3 = new Label("Current Status");
        Label h4 = new Label("Result");

        h1.setPrefWidth(160);
        h2.setPrefWidth(140);
        h3.setPrefWidth(140);
        h4.setPrefWidth(90);

        for (Label h : new Label[]{ h1, h2, h3, h4 }) {
            h.getStyleClass().add("header-cell");
        }

        HBox row = new HBox(10, h1, h2, h3, h4);
        row.setPadding(new Insets(6, 10, 6, 10));
        row.getStyleClass().add("door-header");
        return row;
    }

    private void applyMode(String mode) {
        currentMode = mode;
        modeLabel.setText("Mode: " + mode);

        String[] expected = modeExpected.get(mode);
        for (int i = 0; i < doors.size(); i++) {
            doors.get(i).setExpectedStatus(expected[i]);
            doors.get(i).checkStatus();
        }

        for (DoorRow row : doorRows) {
            row.refresh();
        }

        log("Mode set to " + mode + ". All doors checked automatically.");
        updateStatusBar();
    }

    private void updateStatusBar() {
        int issueCount = 0;
        for (Door door : doors) {
            if (door.getResult().equals("Issue")) {
                issueCount++;
            }
        }
        if (issueCount == 0) {
            statusLabel.setText("Status: All " + doors.size() + " doors are SAFE.");
        } else {
            statusLabel.setText("Status: " + issueCount + " door(s) have ISSUES!");
        }
    }

    private void resetAll() {
        for (int i = 0; i < doors.size(); i++) {
            doors.get(i).reset(defaultStatuses[i]);
        }
        for (DoorRow row : doorRows) {
            row.refresh();
        }
        currentMode = "Default";
        modeLabel.setText("Mode: Default");
        statusLabel.setText("Status: System reset to default.");
        eventLog.clear();
        log("System reset to default state.");
    }

    private void log(String message) {
        eventLog.appendText("> " + message + "\n");
    }

    private HBox createCustomModeRow(String modeName) {
        String[] nameRef = { modeName };

        Button modeBtn = new Button(nameRef[0]);
        modeBtn.getStyleClass().add("mode-button");
        HBox.setHgrow(modeBtn, Priority.ALWAYS);
        modeBtn.setMaxWidth(Double.MAX_VALUE);
        modeBtn.setOnAction(e -> applyMode(nameRef[0]));

        modeBtn.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                TextField tf = new TextField(nameRef[0]);
                HBox.setHgrow(tf, Priority.ALWAYS);
                tf.setMaxWidth(Double.MAX_VALUE);
                HBox row = (HBox) modeBtn.getParent();
                int idx = row.getChildren().indexOf(modeBtn);
                row.getChildren().set(idx, tf);
                tf.selectAll();
                tf.requestFocus();
                Runnable commit = () -> {
                    String newName = tf.getText().trim();
                    if (!newName.isEmpty() && !newName.equals(nameRef[0])) {
                        if (modeExpected.containsKey(newName)) {
                            tf.setStyle("-fx-border-color: red;");
                            return;
                        }
                        String[] expected = modeExpected.remove(nameRef[0]);
                        if (currentMode.equals(nameRef[0])) {
                            currentMode = newName;
                            modeLabel.setText("Mode: " + newName);
                        }
                        nameRef[0] = newName;
                        modeExpected.put(newName, expected);
                        modeBtn.setText(newName);
                        log("Custom mode renamed to '" + newName + "'.");
                    }
                    if (row.getChildren().contains(tf)) {
                        row.getChildren().set(row.getChildren().indexOf(tf), modeBtn);
                    }
                };
                tf.setOnAction(ev -> commit.run());
                tf.focusedProperty().addListener((obs, was, now) -> { if (!now) commit.run(); });
            } else if (e.getClickCount() == 1) {
                applyMode(nameRef[0]);
            }
        });

        Button deleteBtn = new Button("×");
        deleteBtn.getStyleClass().add("delete-mode-button");

        HBox row = new HBox(4, modeBtn, deleteBtn);
        row.setMaxWidth(Double.MAX_VALUE);

        deleteBtn.setOnAction(e -> {
            modeExpected.remove(nameRef[0]);
            leftPanel.getChildren().remove(row);
            if (currentMode.equals(nameRef[0])) {
                currentMode = "Default";
                modeLabel.setText("Mode: Default");
                statusLabel.setText("Status: Active mode deleted. Select a mode.");
            }
            log("Custom mode '" + nameRef[0] + "' deleted.");
        });

        return row;
    }

    private void openAddModeDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Custom Mode");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(20));
        grid.getStyleClass().add("dialog-grid");

        Label namePrompt = new Label("Mode Name:");
        namePrompt.getStyleClass().add("dialog-label");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Night Mode");
        nameField.getStyleClass().add("dialog-field");
        grid.add(namePrompt, 0, 0);
        grid.add(nameField, 1, 0);

        Label doorColLabel = new Label("Door");
        doorColLabel.getStyleClass().add("dialog-label-bold");
        Label doorHeader = new Label("Expected Status:");
        doorHeader.getStyleClass().add("dialog-label-bold");
        grid.add(doorColLabel, 0, 1);
        grid.add(doorHeader, 1, 1);

        ArrayList<ComboBox<String>> combos = new ArrayList<>();
        for (int i = 0; i < doors.size(); i++) {
            Label doorLabel = new Label(doors.get(i).getName() + ":");
            doorLabel.getStyleClass().add("dialog-label");
            ComboBox<String> combo = new ComboBox<>();
            combo.getItems().addAll("Locked", "Unlocked");
            combo.setValue("Locked");
            combos.add(combo);
            grid.add(doorLabel, 0, i + 2);
            grid.add(combo, 1, i + 2);
        }

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("dialog-error");
        grid.add(errorLabel, 0, doors.size() + 2, 2, 1);

        Button btnSave   = new Button("Save Mode");
        btnSave.getStyleClass().add("add-mode-button");
        Button btnCancel = new Button("Cancel");
        btnCancel.getStyleClass().add("reset-button");
        HBox btnRow = new HBox(10, btnSave, btnCancel);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        grid.add(btnRow, 0, doors.size() + 3, 2, 1);

        btnCancel.setOnAction(e -> dialog.close());

        btnSave.setOnAction(e -> {
            String modeName = nameField.getText().trim();
            if (modeName.isEmpty()) {
                errorLabel.setText("Please enter a mode name.");
                return;
            }
            if (modeExpected.containsKey(modeName)) {
                errorLabel.setText("Mode '" + modeName + "' already exists.");
                return;
            }
            String[] expected = new String[doors.size()];
            for (int i = 0; i < combos.size(); i++) {
                expected[i] = combos.get(i).getValue();
            }
            modeExpected.put(modeName, expected);

            HBox modeRow = createCustomModeRow(modeName);
            leftPanel.getChildren().add(leftPanel.getChildren().size() - 2, modeRow);

            log("Custom mode '" + modeName + "' added.");
            dialog.close();
        });

        Scene dialogScene = new Scene(grid, 320, 420);
        dialogScene.getStylesheets().add(
            getClass().getResource("/doorlock/style.css").toExternalForm()
        );
        dialog.setScene(dialogScene);
        dialog.show();
    }

    class DoorRow extends HBox {

        private Door door;
        private int index;
        private Label nameLabel;
        private ComboBox<String> expectedCombo;
        private Label currentLabel;
        private Label resultLabel;

        public DoorRow(Door door, int index) {
            this.door = door;
            this.index = index;
            this.setSpacing(10);
            this.setPadding(new Insets(6, 10, 6, 10));
            this.getStyleClass().add("door-row");

            nameLabel = new Label(door.getName());
            nameLabel.setPrefWidth(160);
            nameLabel.getStyleClass().add("row-cell");
            nameLabel.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    TextField tf = new TextField(door.getName());
                    tf.setPrefWidth(160);
                    int idx = this.getChildren().indexOf(nameLabel);
                    this.getChildren().set(idx, tf);
                    tf.selectAll();
                    tf.requestFocus();
                    tf.setOnAction(ev -> saveDoorName(tf));
                    tf.focusedProperty().addListener((obs, was, now) -> {
                        if (!now) saveDoorName(tf);
                    });
                }
            });

            expectedCombo = new ComboBox<>();
            expectedCombo.getItems().addAll("Locked", "Unlocked");
            expectedCombo.setValue(door.getExpectedStatus());
            expectedCombo.setPrefWidth(140);

            currentLabel = new Label(door.getCurrentStatus());
            currentLabel.setPrefWidth(140);
            currentLabel.getStyleClass().add("row-cell");

            resultLabel = new Label(door.getResult());
            resultLabel.setPrefWidth(90);

            expectedCombo.setOnAction(e -> {
                String newExpected = expectedCombo.getValue();
                door.setExpectedStatus(newExpected);
                if (!currentMode.equals("Default")) {
                    modeExpected.get(currentMode)[index] = newExpected;
                }
                door.checkStatus();
                refresh();
                log("'" + door.getName() + "' expected set to " + newExpected + ". Result: " + door.getResult());
                updateStatusBar();
            });

            this.getChildren().addAll(nameLabel, expectedCombo, currentLabel, resultLabel);
            applyResultStyle();
        }

        public void refresh() {
            expectedCombo.setValue(door.getExpectedStatus());
            currentLabel.setText(door.getCurrentStatus());
            resultLabel.setText(door.getResult());
            applyResultStyle();
        }

        private void saveDoorName(TextField tf) {
            String newName = tf.getText().trim();
            if (!newName.isEmpty()) {
                door.setName(newName);
                nameLabel.setText(newName);
            }
            if (this.getChildren().contains(tf)) {
                int idx = this.getChildren().indexOf(tf);
                this.getChildren().set(idx, nameLabel);
            }
        }

        private void applyResultStyle() {
            resultLabel.getStyleClass().removeAll("safe-result", "issue-result", "pending-result");
            if (door.getResult().equals("Safe")) {
                resultLabel.getStyleClass().add("safe-result");
            } else if (door.getResult().equals("Issue")) {
                resultLabel.getStyleClass().add("issue-result");
            } else {
                resultLabel.getStyleClass().add("pending-result");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
