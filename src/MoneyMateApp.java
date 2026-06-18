package app;

import dao.BudgetDao;
import dao.TransactionDao;
import db.DatabaseManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Transaction;
import model.TransactionType;
import service.CsvService;
import service.FinanceService;
import service.StatisticsService;
import util.MoneyFormatter;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MoneyMateApp extends Application {

    private final TransactionDao transactionDao = new TransactionDao();
    private final BudgetDao budgetDao = new BudgetDao();
    private final FinanceService financeService = new FinanceService(transactionDao, budgetDao);
    private final StatisticsService statisticsService = new StatisticsService(transactionDao);
    private final CsvService csvService = new CsvService(transactionDao);

    private final ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    private Label incomeLabel;
    private Label expenseLabel;
    private Label balanceLabel;
    private Label budgetWarningLabel;
    private TableView<Transaction> tableView;
    private PieChart pieChart;

    private DatePicker datePicker;
    private ComboBox<TransactionType> typeBox;
    private ComboBox<String> categoryBox;
    private TextField amountField;
    private TextField memoField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        DatabaseManager.initializeDatabase();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        VBox topArea = createDashboard();
        root.setTop(topArea);

        tableView = createTableView();
        root.setCenter(tableView);

        VBox rightArea = createRightPanel();
        root.setRight(rightArea);

        HBox bottomArea = createBottomButtons(stage);
        root.setBottom(bottomArea);

        refreshAll();

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("MoneyMate - Java 가계부 시스템");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createDashboard() {
        Label title = new Label("MoneyMate");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        incomeLabel = new Label();
        expenseLabel = new Label();
        balanceLabel = new Label();
        budgetWarningLabel = new Label();
        budgetWarningLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-weight: bold;");

        HBox summaryBox = new HBox(25, incomeLabel, expenseLabel, balanceLabel);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-background-color: #f2f6ff; -fx-background-radius: 12;");

        return new VBox(10, title, summaryBox, budgetWarningLabel);
    }

    private TableView<Transaction> createTableView() {
        TableView<Transaction> table = new TableView<>();

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Transaction, String> dateCol = new TableColumn<>("날짜");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(110);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("타입");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(90);

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("카테고리");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(120);

        TableColumn<Transaction, Integer> amountCol = new TableColumn<>("금액");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(120);

        TableColumn<Transaction, String> memoCol = new TableColumn<>("메모");
        memoCol.setCellValueFactory(new PropertyValueFactory<>("memo"));
        memoCol.setPrefWidth(250);

        table.getColumns().addAll(idCol, dateCol, typeCol, categoryCol, amountCol, memoCol);
        table.setItems(transactionList);

        return table;
    }

    private VBox createRightPanel() {
        Label inputTitle = new Label("수입 / 지출 등록");
        inputTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        datePicker = new DatePicker(LocalDate.now());

        typeBox = new ComboBox<>();
        typeBox.getItems().addAll(TransactionType.INCOME, TransactionType.EXPENSE);
        typeBox.setValue(TransactionType.EXPENSE);

        categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("식비", "교통", "쇼핑", "문화", "생활", "월급", "용돈", "기타");
        categoryBox.setValue("식비");

        amountField = new TextField();
        amountField.setPromptText("금액 입력");

        memoField = new TextField();
        memoField.setPromptText("메모 입력");

        Button addButton = new Button("등록");
        addButton.setMaxWidth(Double.MAX_VALUE);
        addButton.setOnAction(e -> addTransaction());

        Button deleteButton = new Button("선택 삭제");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setOnAction(e -> deleteSelectedTransaction());

        Button setBudgetButton = new Button("식비 예산 300,000원 설정");
        setBudgetButton.setMaxWidth(Double.MAX_VALUE);
        setBudgetButton.setOnAction(e -> {
            budgetDao.saveOrUpdateBudget("식비", LocalDate.now().toString().substring(0, 7), 300000);
            refreshAll();
            showInfo("예산 설정", "이번 달 식비 예산을 300,000원으로 설정했습니다.");
        });

        pieChart = new PieChart();
        pieChart.setTitle("카테고리별 지출 통계");
        pieChart.setLegendVisible(true);
        pieChart.setPrefSize(330, 330);

        VBox form = new VBox(
                8,
                inputTitle,
                new Label("날짜"), datePicker,
                new Label("타입"), typeBox,
                new Label("카테고리"), categoryBox,
                new Label("금액"), amountField,
                new Label("메모"), memoField,
                addButton,
                deleteButton,
                setBudgetButton,
                pieChart
        );

        form.setPadding(new Insets(0, 0, 0, 15));
        form.setPrefWidth(350);

        return form;
    }

    private HBox createBottomButtons(Stage stage) {
        Button csvImportButton = new Button("CSV 예시 데이터 가져오기");
        csvImportButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("CSV 파일 선택");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                csvService.importCsv(file);
                refreshAll();
                showInfo("CSV 가져오기 완료", "예시 데이터를 성공적으로 불러왔습니다.");
            }
        });

        Button csvExportButton = new Button("CSV 내보내기");
        csvExportButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("CSV 저장 위치 선택");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = chooser.showSaveDialog(stage);
            if (file != null) {
                csvService.exportCsv(file);
                showInfo("CSV 내보내기 완료", "거래 내역을 CSV로 저장했습니다.");
            }
        });

        Button refreshButton = new Button("새로고침");
        refreshButton.setOnAction(e -> refreshAll());

        HBox box = new HBox(10, csvImportButton, csvExportButton, refreshButton);
        box.setPadding(new Insets(12, 0, 0, 0));
        return box;
    }

    private void addTransaction() {
        try {
            LocalDate date = datePicker.getValue();
            TransactionType type = typeBox.getValue();
            String category = categoryBox.getValue();
            int amount = Integer.parseInt(amountField.getText().trim());
            String memo = memoField.getText().trim();

            if (amount <= 0) {
                showError("입력 오류", "금액은 0보다 커야 합니다.");
                return;
            }

            Transaction transaction = new Transaction(0, date.toString(), type, category, amount, memo);
            transactionDao.insert(transaction);

            amountField.clear();
            memoField.clear();
            refreshAll();

        } catch (NumberFormatException e) {
            showError("입력 오류", "금액은 숫자로 입력해야 합니다.");
        }
    }

    private void deleteSelectedTransaction() {
        Transaction selected = tableView.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("삭제 오류", "삭제할 내역을 선택하세요.");
            return;
        }

        transactionDao.deleteById(selected.getId());
        refreshAll();
    }

    private void refreshAll() {
        List<Transaction> all = transactionDao.findAll();
        transactionList.setAll(all);

        int income = financeService.getMonthlyIncome(LocalDate.now());
        int expense = financeService.getMonthlyExpense(LocalDate.now());
        int balance = income - expense;

        incomeLabel.setText("이번 달 수입: " + MoneyFormatter.format(income));
        expenseLabel.setText("이번 달 지출: " + MoneyFormatter.format(expense));
        balanceLabel.setText("이번 달 잔액: " + MoneyFormatter.format(balance));

        budgetWarningLabel.setText(financeService.getBudgetWarning("식비", LocalDate.now()));

        refreshPieChart();
    }

    private void refreshPieChart() {
        Map<String, Integer> data = statisticsService.getExpenseByCategory(LocalDate.now());

        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

        data.forEach((category, amount) -> {
            if (amount > 0) {
                chartData.add(new PieChart.Data(category + " " + MoneyFormatter.format(amount), amount));
            }
        });

        pieChart.setData(chartData);
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
