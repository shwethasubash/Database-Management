package DBMS;

import java.util.Scanner;

public class ReadInput {
    public static String readInput(String input){
        System.out.println(input);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
