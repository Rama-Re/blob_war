package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int c1 = 0;
        while (c1 != 2){
            System.out.println("1- New Game");
            System.out.println("2- Exit");
            c1 = input.nextInt();
            switch (c1){
                case 1: {
                    int c2 = 0;
                    Level level = null;
                    System.out.println("Game Level: ");
                    System.out.println("1- Easy Level");
                    System.out.println("2- Medium Level");
                    System.out.println("3- Hard Level");
                    Player player = null;
                    c2 = input.nextInt();
                    switch (c2){
                        case 1:{
                            level = new EasyLevel();break;
                        }
                        case 2:{
                            level = new MedLevel();break;
                        }
                        case 3:{
                            level = new HardLevel();break;
                        }
                    }
                    System.out.println("Choose your color:");
                    System.out.println("1- Blue");
                    System.out.println("2- Orange");
                    c2 = input.nextInt();
                    switch (c2){
                        case 1:{
                            player = Player.B;
                            break;
                        }
                        case 2:{
                            player = Player.O;
                            break;
                        }
                    }
                    System.out.println("Choose algorithm:");
                    System.out.println("1- MinMax");
                    System.out.println("2- AlphaBeta");
                    c2 = input.nextInt();
                    Algorithm algorithm = null;
                    switch (c2){
                        case 1:{
                            algorithm = Algorithm.MinMax;
                            break;
                        }
                        case 2:{
                            algorithm = Algorithm.AlphaBeta;
                            break;
                        }
                    }
                    Board game = new Board(level,player,algorithm);
                    game.play();
                    break;
                }
            }
        }
    }
}
