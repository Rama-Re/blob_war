package com.company;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Scanner;

public class Board {
    int ScoreO = 2;
    int ScoreB = 2;
    Player lastMove = Player.none;
    Player user = Player.none;
    Cell[][] matrix;
    Level level;
    Algorithm algorithm;

    public Board(Level level, Player user, Algorithm algorithm) {
        this.lastMove = Player.B;
        this.user = user;
        this.level = level;
        matrix = new Cell[level.size][level.size];
        this.algorithm = algorithm;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (i == j && i == 0) matrix[0][0] = new Cell(0, 0, Player.B, level.size);
                else if (i == level.size - 1 && j == 0)
                    matrix[level.size - 1][0] = new Cell(level.size - 1, 0, Player.O, level.size);
                else if (j == level.size - 1 && i == 0)
                    matrix[0][level.size - 1] = new Cell(0, level.size - 1, Player.B, level.size);
                else if (i == level.size - 1 && j == level.size - 1)
                    matrix[level.size - 1][level.size - 1] = new Cell(level.size - 1, level.size - 1, Player.O, level.size);
                else matrix[i][j] = new Cell(i, j, Player.none, level.size);
            }
        }
    }

    public Board(Board board) {
        this.lastMove = board.lastMove;
        this.user = board.user;
        this.level = board.level;
        matrix = new Cell[level.size][level.size];
        for (int i = 0; i < level.size; i++) {
            for (int j = 0; j < level.size; j++) {
                this.matrix[i][j] = new Cell(board.matrix[i][j]);
            }
        }
    }

    public Boolean isFall() {
        for (int i = 0; i < level.size; i++) {
            for (int j = 0; j < level.size; j++) {
                if (matrix[i][j].player == Player.none)
                    return false;
            }
        }
        return true;
    }

    public Player otherPlayer(Player player) {
        return player == Player.B ? Player.O : Player.B;
    }

    public Board nextState(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
        Board board = new Board(this);
        board.matrix[from.getKey()][from.getValue()].move(board, to);
        return board;
    }

    public void print() {
        for (int i = 0; i < matrix.length; i++) {
            if (i == 0) {
                for (int j = 0; j < matrix.length; j++) {
                    if (j == 0) System.out.print("x\\y\t");
                    System.out.print(j + ",\t");
                }
                System.out.println();
            }
            for (int j = 0; j < matrix.length; j++) {
                if (j == 0) System.out.print(i + ",\t");
                if (matrix[i][j].player == Player.B)
                    System.out.print("B,\t");
                else if (matrix[i][j].player == Player.O)
                    System.out.print("O,\t");
                else System.out.print("_,\t");
            }
            System.out.println();
        }
        System.out.println("--------------------------------------------------------------");
    }

    public void executeMove(Pair<Integer, Integer> from, Pair<Integer, Integer> to) {
        matrix[from.getKey()][from.getValue()].move(this, to);
    }

    public void getFrom() {
        Scanner input = new Scanner(System.in);
        int i = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        while (true) {
            System.out.println("Enter player position:");
            System.out.print("x:");
            i = input.nextInt();
            System.out.print("y:");
            j = input.nextInt();
            if (i < 0 || i >= matrix.length) continue;
            if (j < 0 || j >= matrix.length) continue;
            if (matrix[i][j].player == user)
                break;
        }
        while (true) {
            System.out.println("move to:");
            System.out.print("x:");
            k = input.nextInt();
            System.out.print("y:");
            l = input.nextInt();
            if (k < 0 || k >= matrix.length) continue;
            if (l < 0 || l >= matrix.length) continue;
            if (matrix[i][j].checkPosition(matrix, k, l))
                break;
        }
        executeMove(new Pair<Integer, Integer>(i, j), new Pair<Integer, Integer>(k, l));
    }

    public void selectPlayer(boolean round) {
        if (round) {
            getFrom();
        } else {
            long startTime = System.nanoTime();
            Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> pair = null;
            if (algorithm == Algorithm.MinMax) {
                pair = MinMax(new Board(this), level.depth);
            } else pair = MinMaxAlphaBeta(new Board(this), level.depth);
            executeMove(pair.getValue().getKey(), pair.getValue().getValue());
            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;
            System.out.println("It takes time: " + totalTime * 1.0 / 1000000000);
        }
        print();
    }

    public void play() {
        print();
        boolean round = false;
        if (user == Player.B)
            round = true;
        while (true) {
            if (isFall() && isWinner() == Player.none) {
                System.out.println("Draw");
                break;
            }
            if (isWinner() == user) {
                System.out.println("You are the Winner 🔥");
                break;
            }
            if (isWinner() == otherPlayer(user)) {
                System.out.println("You are the Loser 😢");
                break;
            }
            selectPlayer(round);
            round = !round;
        }
    }

    public void append(ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> list, ArrayList<Pair<Integer, Integer>> list2, Pair<Integer, Integer> from) {
        for (Pair<Integer, Integer> pair : list2) {
            list.add(new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(new Pair<Integer, Integer>(from.getKey(), from.getValue()), new Pair<Integer, Integer>(pair.getKey(), pair.getValue())));
        }
    }

    public ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> getAllNextMoves(Player player) {
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> moves = new ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[i][j].player == player) {
                    matrix[i][j].updateL(matrix);
                    append(moves, matrix[i][j].nearL, new Pair<Integer, Integer>(i, j));
                    append(moves, matrix[i][j].farL, new Pair<Integer, Integer>(i, j));
                }

            }
        }
        return moves;
    }

    public Player isWinner() {
        if (lastMove == Player.B && ScoreO == 0 || lastMove == Player.O && ScoreB == 0)
            return lastMove;
        else if (isFall() && ((lastMove == Player.B && ScoreB > ScoreO) || (lastMove == Player.O && ScoreB < ScoreO)))
            return lastMove;
        else if (isFall() && ((lastMove == Player.B && ScoreB < ScoreO) || (lastMove == Player.O && ScoreB > ScoreO)))
            return otherPlayer(lastMove);
        else if ((lastMove == Player.B && getAllNextMoves(Player.B).size() == 0) ||
                (lastMove == Player.O && getAllNextMoves(Player.O).size() == 0))
            return otherPlayer(lastMove);
        return Player.none;
    }

    public int evaluate() {
        int ev = 0;
        if (isWinner() == Player.B) {
            ev = 20;
        } else if (isWinner() == Player.O) {
            ev = -20;
        } else if (ScoreB > ScoreO) {
            ev = 10;
        } else if (ScoreB < ScoreO) {
            ev = -10;
        }
        return ev;
    }

    public Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> max(Board board, Player player, int depth) {
        if (board.isFall() || board.isWinner() != Player.none || depth == 0) {
            int ev = board.evaluate();
            return new Pair<>(ev, null);
        }
        int maxVal = Integer.MIN_VALUE;
        Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> best = null;
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> actions = board.getAllNextMoves(player);
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> action : actions) {
            Board board1 = nextState(action.getKey(), action.getValue());
            Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> min = min(board1, otherPlayer(player), depth - 1);
            if (maxVal < min.getKey()) {
                best = new Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>(min.getKey(), new Pair<>(action.getKey(), action.getValue()));
                maxVal = min.getKey();
            }
        }
        return best;
    }

    public Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> min(Board board, Player player, int depth) {
        if (board.isFall() || board.isWinner() != Player.none || depth == 0) {
            int ev = board.evaluate();
            //System.out.println(ev);
            return new Pair<>(ev, null);
        }
        int minVal = Integer.MAX_VALUE;
        Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> best = null;
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> actions = board.getAllNextMoves(player);
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> action : actions) {
            Board board1 = nextState(action.getKey(), action.getValue());
            Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> max = max(board1, otherPlayer(player), depth - 1);

            if (minVal > max.getKey()) {
                best = new Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>(max.getKey(), new Pair<>(action.getKey(), action.getValue()));
                minVal = max.getKey();
            }
        }
        return best;
    }

    public Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> MinMax(Board board, int depth) {
        if (board.isFall() || board.isWinner() != Player.none) {
            return null;
        }
        if (user == Player.B)
            return min(new Board(board), otherPlayer(user), depth - 1);
        else return max(new Board(board), otherPlayer(user), depth - 1);
    }

    public Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> maxAlphaBeta(Board board, Player player, int alpha, int beta, int depth) {
        if (board.isFall() || board.isWinner() != Player.none || depth == 0) {
            int ev = board.evaluate();
            return new Pair<>(ev, null);
        }
        int maxVal = Integer.MIN_VALUE;
        Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> best = null;
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> actions = board.getAllNextMoves(player);
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> action : actions) {
            Board board1 = nextState(action.getKey(), action.getValue());
            Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> min = min(board1, otherPlayer(player), depth - 1);
            if (maxVal < min.getKey()) {
                best = new Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>(min.getKey(), new Pair<>(action.getKey(), action.getValue()));
                maxVal = min.getKey();
            }
            alpha = Math.max(alpha, maxVal);
            if (beta <= alpha)
                break;
        }
        return best;
    }

    public Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> minAlphaBeta(Board board, Player player, int alpha, int beta, int depth) {
        if (board.isFall() || board.isWinner() != Player.none || depth == 0) {
            int ev = board.evaluate();
            //System.out.println(ev);
            return new Pair<>(ev, null);
        }
        int minVal = Integer.MAX_VALUE;
        Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> best = null;
        ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> actions = board.getAllNextMoves(player);
        for (Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> action : actions) {
            Board board1 = nextState(action.getKey(), action.getValue());
            Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> max = max(board1, otherPlayer(player), depth - 1);

            if (minVal > max.getKey()) {
                best = new Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>>(max.getKey(), new Pair<>(action.getKey(), action.getValue()));
                minVal = max.getKey();
            }
            beta = Math.min(beta, minVal);
            if (beta <= alpha)
                break;
        }
        return best;
    }

    public Pair<Integer, Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> MinMaxAlphaBeta(Board board, int depth) {
        if (board.isFall() || board.isWinner() != Player.none) {
            return null;
        }
        if (user == Player.B)
            return minAlphaBeta(new Board(board), otherPlayer(user), Integer.MIN_VALUE, Integer.MAX_VALUE, depth - 1);
        else return maxAlphaBeta(new Board(board), otherPlayer(user), Integer.MIN_VALUE, Integer.MAX_VALUE, depth - 1);
    }
}
