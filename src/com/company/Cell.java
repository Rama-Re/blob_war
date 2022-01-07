package com.company;

import javafx.util.Pair;

import java.util.ArrayList;

public class Cell {
    Player player;
    int i = 0;
    int j = 0;
    ArrayList<Pair<Integer, Integer>> nearL;
    ArrayList<Pair<Integer, Integer>> farL;

    public Cell(Cell cell) {
        this.i = cell.i;
        this.j = cell.j;
        this.player = cell.player;
        nearL = new ArrayList<Pair<Integer, Integer>>();
        farL = new ArrayList<Pair<Integer, Integer>>();
        for (int k = 0; k < cell.nearL.size(); k++)
            nearL.add(new Pair<Integer, Integer>(cell.nearL.get(k).getKey(), cell.nearL.get(k).getValue()));
        for (int k = 0; k < cell.farL.size(); k++)
            farL.add(new Pair<Integer, Integer>(cell.farL.get(k).getKey(), cell.farL.get(k).getValue()));
    }

    public Cell(int i, int j, Player player, int size) {
        this.i = i;
        this.j = j;
        this.player = player;
        nearL = new ArrayList<Pair<Integer, Integer>>();
        farL = new ArrayList<Pair<Integer, Integer>>();
        for (int k = i - 1; k < i + 1; k++) {
            for (int l = j - 1; l < j + 1; l++) {
                if ((k >= 0 || k < size) && (l >= 0 || l < size) && (l != j || k != i))
                    nearL.add(new Pair<>(k, l));
            }
        }
        for (int k = i - 2; k < i + 2; k++) {
            for (int l = j - 2; l < j + 2; l++) {
                if ((k > i - 2 && k < i + 2) && (l > j - 2 && l < j + 2)) {
                    continue;
                }
                if ((k >= 0 || k < size) && (l >= 0 || l < size) && (l != j || k != i))
                    farL.add(new Pair<>(k, l));
            }
        }
    }

    public Cell(Cell[][] players, int i, int j) {
        this.i = i;
        this.j = j;
        nearL = new ArrayList<Pair<Integer, Integer>>();
        farL = new ArrayList<Pair<Integer, Integer>>();
        for (int k = i - 1; k <= i + 1; k++) {
            for (int l = j - 1; l <= j + 1; l++) {
                if ((k >= 0 && k < players.length) && (l >= 0 && l < players.length) && (l != j || k != i) && players[k][l].player == Player.none)
                    nearL.add(new Pair<>(k, l));
            }
        }
        for (int k = i - 2; k <= i + 2; k++) {
            for (int l = j - 2; l <= j + 2; l++) {
                if ((k > i - 2 && k < i + 2) && (l > j - 2 && l < j + 2)) {
                    continue;
                }
                if ((k >= 0 && k < players.length) && (l >= 0 && l < players.length) && (l != j || k != i) && players[k][l].player == Player.none)
                    farL.add(new Pair<>(k, l));
            }
        }
    }

    public void updateL(Cell[][] players) {
        nearL = new ArrayList<Pair<Integer, Integer>>();
        farL = new ArrayList<Pair<Integer, Integer>>();
        for (int k = i - 1; k <= i + 1; k++) {
            for (int l = j - 1; l <= j + 1; l++) {
                if (((k >= 0 && k < players.length)
                        && (l >= 0 && l < players.length))
                        && (l != j || k != i)
                        && players[k][l].player == Player.none) {
                    nearL.add(new Pair<>(k, l));
                }
            }
        }
        for (int k = i - 2; k <= i + 2; k++) {
            for (int l = j - 2; l <= j + 2; l++) {
                if ((k > i - 2 && k < i + 2) && (l > j - 2 && l < j + 2)) {
                    continue;
                }
                if ((k >= 0 && k < players.length)
                        && (l >= 0 && l < players.length)
                        && (l != j || k != i)
                        && players[k][l].player == Player.none)
                    farL.add(new Pair<>(k, l));
            }
        }
    }

    public boolean attack(Board board, int i, int j) {
        boolean done = false;
        for (int k = i - 1; k <= i + 1; k++) {
            for (int l = j - 1; l <= j + 1; l++) {
                if ((k >= 0 && k < board.matrix.length) &&
                        (l >= 0 && l < board.matrix.length) &&
                        (l != j || k != i) &&
                        board.matrix[k][l].player != Player.none &&
                        board.matrix[k][l].player != player) {
                    board.matrix[k][l].player = this.player;
                    done = true;
                    if (player == Player.B) {
                        board.ScoreB++;
                        board.ScoreO--;
                    }
                    if (player == Player.O) {
                        board.ScoreO++;
                        board.ScoreB--;
                    }
                }
            }
        }
        return done;
    }

    public Boolean checkPosition(Cell[][] matrix, int i, int j) {
        updateL(matrix);
        for (int k = 0; k < nearL.size(); k++) {
            if (nearL.get(k).getKey() == i && nearL.get(k).getValue() == j) return true;
        }
        for (int k = 0; k < farL.size(); k++) {
            if (farL.get(k).getKey() == i && farL.get(k).getValue() == j) return true;
        }
        return false;
    }

    public void move(Board board, Pair<Integer, Integer> to) {
        updateL(board.matrix);
        Cell cell = board.matrix[to.getKey()][to.getValue()];
        boolean exist = false;
        for (Pair<Integer, Integer> pair : nearL) {
            if (pair.getKey() == cell.i && pair.getValue() == cell.j) {
                exist = true;
            }
        }
        if (exist) {
            board.matrix[to.getKey()][to.getValue()].player = this.player;
            attack(board, to.getKey(), to.getValue());
            if (player == Player.B) {
                board.ScoreB++;
            }
            if (player == Player.O) {
                board.ScoreO++;
            }
            board.lastMove = board.otherPlayer(this.player);
        } else {
            board.matrix[to.getKey()][to.getValue()].player = this.player;
            attack(board, to.getKey(), to.getValue());
            board.matrix[this.i][this.j].player = Player.none;
            board.lastMove = board.otherPlayer(this.player);
        }

    }
}
