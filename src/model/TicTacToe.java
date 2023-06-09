package model;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;

public class TicTacToe {
    private JRadioButton[][] board;
    private ButtonGroup buttonGroup;

    private User user;
    private Icon player;
    private Icon computer;
    private boolean gameOver;
    private GameStatus gameStatus;
    private LocalDateTime gameTime;
    private DateTimeFormatter formatter;

    private String path = "Triki/src/resources/History/history_game.txt";

    private ImageIcon circle = new ImageIcon("Triki/src/resources/Figures/O.png");
    private Icon circleIcon = new ImageIcon(circle.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH));
    private ImageIcon cross = new ImageIcon("Triki/src/resources/Figures/X.png");
    private Icon crossIcon = new ImageIcon(cross.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH));
    private ImageIcon background = new ImageIcon("Triki/src/resources/Marco.png");
    private Icon backgroundIcon = new ImageIcon(background.getImage().getScaledInstance(140, 100, Image.SCALE_SMOOTH));

    public TicTacToe() {
        board = new JRadioButton[3][3];
        buttonGroup = new ButtonGroup();
        gameOver = false;
        gameTime = LocalDateTime.now();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getGameStatus() {
        return gameStatus.getStatusName();
    }

    public boolean getGameOver() {
        return gameOver;
    }

    public JRadioButton[][] getBoard() {
        return board;
    }

    public Icon getPlayer() {
        return player;
    }

    public Icon getComputer() {
        return computer;
    }

    public void initBoard() {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = new JRadioButton();
                board[row][col].setIcon(backgroundIcon);
                buttonGroup.add(board[row][col]);
            }
        }
    }

    public void chooseFigure(User user) {
        this.user = user;
        player = (user.getUserFigure().equals(Figure.CIRCLE.getFigureText())) ? circleIcon : crossIcon;
        computer = (player == circleIcon) ? crossIcon : circleIcon;
    }

    public boolean isCellEmpty(int row, int col) {
        return board[row][col].getIcon() == backgroundIcon;
    }

    public boolean isFullBoard() {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col].getIcon() == backgroundIcon) {
                    return false;
                }
            }
        }
        return true;
    }

    public void removeFigure(int row, int col) {
        board[row][col].setIcon(backgroundIcon);
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[row].length;
    }

    public void playerTurn(int row, int col) {
        placeFigure(row, col, player);
    }

    public void machineTurn(Random row, Random column) {
        boolean machinePlaced = tryToWin(computer);
        if (!machinePlaced) {
            machinePlaced = tryToBlock(player);
        }
        if (!machinePlaced) {
            placeRandomFigure(row, column);
        }
    }

    private boolean tryToWin(Icon figure) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (isCellEmpty(row, col)) {
                    placeFigure(row, col, figure);
                    if (checkForWinner(figure)) {
                        return true;
                    } else {
                        removeFigure(row, col);
                    }
                }
            }
        }
        return false;
    }

    private boolean tryToBlock(Icon figure) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (isCellEmpty(row, col)) {
                    placeFigure(row, col, player);
                    boolean playerWinningMove = checkForWinner(player);
                    removeFigure(row, col);
                    if (playerWinningMove) {
                        placeFigure(row, col, computer);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void placeRandomFigure(Random rows, Random column) {
        int row = rows.nextInt(board.length);
        int col = column.nextInt(board.length);
        if (isCellEmpty(row, col)) {
            placeFigure(row, col, computer);
        }
    }

    public void placeFigure(int row, int col, Icon figure) {
        if (isValidPosition(row, col) && isCellEmpty(row, col)) {
            board[row][col].setIcon(figure);
        } else {

        }
    }

    public boolean checkForWinner(Icon figure) {
        for (int row = 0; row < board.length; row++) {
            if (board[row][0].getIcon() == figure && board[row][1].getIcon() == figure
                    && board[row][2].getIcon() == figure) {
                return true;
            }
        }
        for (int col = 0; col < board[0].length; col++) {
            if (board[0][col].getIcon() == figure && board[1][col].getIcon() == figure
                    && board[2][col].getIcon() == figure) {
                return true;
            }
        }
        if (board[0][0].getIcon() == figure && board[1][1].getIcon() == figure && board[2][2].getIcon() == figure) {
            return true;
        }
        if (board[0][2].getIcon() == figure && board[1][1].getIcon() == figure && board[2][0].getIcon() == figure) {
            return true;
        }
        return false;
    }

    public boolean checkGameOver() {
        if (checkForWinner(player)) {
            setGameStatus(GameStatus.WINNER);
            System.out.println(getGameStatus());
            gameOver = true;
            return true;
        } else if (checkForWinner(computer)) {
            setGameStatus(GameStatus.LOSER);
            System.out.println(getGameStatus());
            gameOver = true;
            return true;
        } else if (isFullBoard()) {
            setGameStatus(GameStatus.TIE);
            System.out.println(getGameStatus());
            gameOver = true;
            return true;
        }
        return gameOver;
    }

    public void saveResults() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path))) {
            bufferedWriter.write(user.getUserName() + ";" + getGameStatus() + ";" + user.getUserFigure() + ";"
                    + gameTime.format(formatter));
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void game(int row, int col) {
        while (getGameOver() == false && isFullBoard() == false) {
            playerTurn(row, col);
            if (checkForWinner(player)) {
                checkGameOver();
                saveResults();
            }
            machineTurn(new Random(), new Random());
            if (checkForWinner(computer)) {
                checkGameOver();
                saveResults();
            }
        }
    }
}