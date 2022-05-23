package qwirkle.view.newGameFrame;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import qwirkle.model.GameSession;
import qwirkle.model.computer.Computer;
import qwirkle.view.gamePlayFrame.GamePlayPresenter;
import qwirkle.view.gamePlayFrame.GamePlayView;
import qwirkle.view.popupFrame.PopupPresenter;
import qwirkle.view.popupFrame.PopupView;
import qwirkle.view.rulesFrame.RulesPresenterNG;
import qwirkle.view.rulesFrame.RulesView;
import qwirkle.view.welcomeFrame.WelcomePresenter;
import qwirkle.view.welcomeFrame.WelcomeView;

//From this class the difficulty level of the computer and the chosen user name are passed to the GamePlayPresenter
public class NewGamePresenter {
    private NewGameView view;
    private boolean isPlayerStarting;
    private String name;
    private Computer.LevelOfDifficulty difficultyLvl;

    public NewGamePresenter(Stage stage, NewGameView view) {
        this.view = view;
        isPlayerStarting = true;
        difficultyLvl = Computer.LevelOfDifficulty.EASY;
        addEventHandler(stage);
        updateView(stage);
    }

    private void addEventHandler(Stage stage) {
        view.getQuit().setOnAction(event -> setBackToWelcomeView(stage));
        view.getChangeName().setOnAction(event -> replaceNodes());
        view.getRadioHPF1().setOnAction(event -> isPlayerStarting = true);
        view.getRadioHPF2().setOnAction(event -> isPlayerStarting = false);
        view.getRadioMode1().setOnAction(event -> difficultyLvl = Computer.LevelOfDifficulty.EASY);
        view.getRadioMode2().setOnAction(event -> difficultyLvl = Computer.LevelOfDifficulty.AI);
        view.getRules().setOnAction(event -> setRulesView(stage, isPlayerStarting, name));
        view.getPlay().setOnAction((ActionEvent event) -> {
            name = view.getRadioHPF1().getText();
            setPLayView(stage, getIsPlayerStarting(), getName());
        });
        view.getSubmit().setOnAction(event -> {
            if (view.getPlaceholder().getText().length() > 12) {
                String text = """
                        The maximum allowed
                           characters is 11!""";
                PopupView view = new PopupView();
                new PopupPresenter(stage, view, text, 660, 300, 2);
                return;
            }
            if (view.getPlaceholder().getText().length() == 0) {
                String text = """
                        Please insert a name!""";
                PopupView view = new PopupView();
                new PopupPresenter(stage, view, text, 660, 300, 2);
                return;
            }
            if (view.getPlaceholder().getText().length() > 1 && view.getPlaceholder().getText().length() < 12) {
                restoreNodes();
                name = view.getPlaceholder().getText();
            }
        });
    }

    private void setBackToWelcomeView(Stage stage) {
        WelcomeView welcomeView = new WelcomeView();
        new WelcomePresenter(stage, welcomeView);
        view.getScene().setRoot(welcomeView);
    }

    private void setRulesView(Stage stage, boolean isPlayerStarting, String name) {
        RulesView rulesView = new RulesView();
        new RulesPresenterNG(stage, rulesView, view, isPlayerStarting, name);
        view.getScene().setRoot(rulesView);
    }

    private void setPLayView(Stage stage, boolean isPlayerStarting, String name) {
        GamePlayView gamePlayView = new GamePlayView();
        GameSession newGameSession = new GameSession(name, getDifficultyLvl(), isPlayerStarting);
        new GamePlayPresenter(stage, gamePlayView, newGameSession);
        view.getScene().setRoot(gamePlayView);
    }
    //Changes the area of the left field of choices
    private void replaceNodes() {
        view.getRadioHPF1().setStyle("-fx-opacity: 0;");
        view.getRadioHPF2().setStyle("-fx-opacity: 0;");
        view.getChangeName().setStyle("-fx-opacity: 0;");
        view.getWhoPlaysFirst().setText("Type your name:");
        view.getVbox1().getChildren().set(1, view.getPlaceholder());
        view.getVbox1().getChildren().set(2, view.getSubmit());
    }
    //It restores the previous view of that field without the choice to change the name and with the chosen new name shown
    private void restoreNodes() {
        view.getRadioHPF1().setStyle("-fx-opacity: 1;");
        view.getRadioHPF2().setStyle("-fx-opacity: 1;");
        view.getChangeName().setStyle("-fx-opacity: 1;");
        view.getRadioHPF1().setText(view.getPlaceholder().getText());
        name = view.getPlaceholder().getText();
        view.getVbox1().getChildren().clear();
        view.getPlaceholder().deselect();
        view.getWhoPlaysFirst().setText("Who plays first?");
        view.getWhoPlaysFirst().setStyle("-fx-background-color: rgba(255,255,255,0); -fx-text-fill: #fff; -fx-padding: 20;" +
                " -fx-font-size: 25;");
        view.getVbox1().setAlignment(Pos.TOP_CENTER);
        view.getVbox1().setStyle("-fx-background-color: rgb(24,24,24); -fx-text-fill: #fff; -fx-background-radius: 25; " +
                "-fx-font-family: 'Comic Sans MS';");
        view.getVbox1().setMaxWidth(300);
        view.getVbox1().setMinWidth(300);
        view.getVbox1().setMaxHeight(250);
        view.getVbox1().setMinHeight(250);
        view.getRadioHPF1().setToggleGroup(view.getGroup1());
        view.getRadioHPF1().setPrefWidth(175);
        view.getRadioHPF1().setStyle("-fx-text-fill: #fff;");
        view.getRadioHPF2().setToggleGroup(view.getGroup1());
        view.getRadioHPF2().setPrefWidth(175);
        view.getRadioHPF2().setStyle("-fx-text-fill: #fff;");
        view.getVbox1().getChildren().addAll(view.getWhoPlaysFirst(), view.getRadioHPF1(), view.getRadioHPF2());
    }

    public void setIsPlayerStarting(boolean isPlayerStarting) {
        this.isPlayerStarting = isPlayerStarting;
    }

    public void setName(String name) {
        this.name = name;
    }

    private boolean getIsPlayerStarting() {
        return isPlayerStarting;
    }

    private String getName() {
        return name;
    }
    //Difficulty level set already and when chosen it passes to the model on the GamePlayPresenter once "play" button is pressed
    private Computer.LevelOfDifficulty getDifficultyLvl() {
        return difficultyLvl;
    }

    private void updateView(Stage stage) {
        if (isPlayerStarting) {
            view.getGroup1().selectToggle(view.getRadioHPF1());
        } else view.getGroup1().selectToggle(view.getRadioHPF2());
    }
}
