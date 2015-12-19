/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package folo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author brent
 */
public class Puzzle4by4 {

    private Field[][] puzzle;
    private Map<Character, Integer> var_number;
    private int clauses;

    public Puzzle4by4(int[] values, Direction[] directions) {
        char[] names = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p'};
        if (values.length != 16 || directions.length != 16) {
            throw new RuntimeException("Lengte komt niet overeen!");
        }
        puzzle = new Field[4][4];
        var_number = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            puzzle[Math.floorDiv(i, 4)][i % 4] = new Field(directions[i], values[i], names[i]);
            var_number.put(names[i], i + 1);
        }
    }

    public File createCNFFile() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        File file = new File("C:\\folo\\4x4.cnf");
        file.getParentFile().mkdirs();
        file.createNewFile();
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        String function = generateCNFFunction();
        writer.println("c CNF format voor Puzzle 4X4");
        writer.println("p cnf " + var_number.size() + " " + clauses);
        writer.println(function);
        writer.close();
        return file;
    }

    public String generateCNFFunction() {
        String function = "";
        clauses = 0;
        for (int i = 0; i < puzzle.length; i++) {
            for (int j = 0; j < puzzle[i].length; j++) {
                Field curr = puzzle[i][j];
                switch (curr.getValue()) {
                    case 0:
                        function += calculateColorEquasion0(curr);
                        break;
                    case 1:
                        if (amountFieldsInRange(curr, i, j) == 1) {
                            function += calculateColorEquasion1_range1(curr, i, j) + "^";
                        } else if (amountFieldsInRange(curr, i, j) == 2) {
                            function += calculateColorEquasion1_range2(curr, i, j) + "^";
                        } else if (amountFieldsInRange(curr, i, j) == 3) {
                            function += calculateColorEquasion1_range3(curr, i, j) + "^";
                        }
                        break;
                    case 2:
                        if (amountFieldsInRange(curr, i, j) == 2) {
                            function += calculateColorEquasion2_range2(curr, i, j) + "^";
                        } else if (amountFieldsInRange(curr, i, j) == 3) {
                            function += calculateColorEquasion2_range3(curr, i, j) + "^";
                        }
                        break;
                    case 3:
                        break;
                    default:
                        throw new RuntimeException("Verkeerde waarde");
                }
            }
        }
        function = function.substring(0, function.length() - 1);
        for (Character c : var_number.keySet()) {
            function = function.replaceAll(c.toString(), var_number.get(c).toString());
        }
        function = function.replace('!', '-');
        function = function.replace('^', '0');
        function = function.replace('v', ' ');
        function = function.replace('(', ' ');
        function = function.replace(')', ' ');
        function = function.trim();
        return function;
    }

    public int amountFieldsInRange(Field f, int row_pos, int col_pos) {
        switch (f.getDirection()) {
            case UP:
                return row_pos;
            case DOWN:
                return 3 - row_pos;
            case LEFT:
                return col_pos;
            case RIGHT:
                return 3 - col_pos;
            case UP_RIGHT:
                return Math.min(row_pos, 3 - col_pos);
            case DOWN_RIGHT:
                return Math.min(3 - row_pos, 3 - col_pos);
            case UP_LEFT:
                return Math.min(row_pos, col_pos);
            case DOWN_LEFT:
                return Math.min(3 - row_pos, col_pos);
            default:
                throw new RuntimeException("Direction not found!");
        }
    }

    public String calculateColorEquasion0(Field f) {
        clauses += 0;
        return "";
    }

    public String calculateColorEquasion1_range1(Field f, int row_pos, int col_pos) {
        char a = f.getName();
        char b;
        switch (f.getDirection()) {
            case UP:
                b = puzzle[row_pos - 1][col_pos].getName();
                break;
            case DOWN:
                b = puzzle[row_pos + 1][col_pos].getName();
                break;
            case LEFT:
                b = puzzle[row_pos][col_pos - 1].getName();
                break;
            case RIGHT:
                b = puzzle[row_pos][col_pos + 1].getName();
                break;
            case UP_RIGHT:
                b = puzzle[row_pos - 1][col_pos + 1].getName();
                break;
            case DOWN_RIGHT:
                b = puzzle[row_pos + 1][col_pos + 1].getName();
                break;
            case UP_LEFT:
                b = puzzle[row_pos - 1][col_pos - 1].getName();
                break;
            case DOWN_LEFT:
                b = puzzle[row_pos + 1][col_pos - 1].getName();
                break;
            default:
                throw new RuntimeException("Wrong direction given!");
        }
        clauses += 2;
        return "(!" + a + "v" + b + ")^(" + a + "v!" + b + ")";
    }

    public String calculateColorEquasion1_range2(Field f, int row_pos, int col_pos) {
        char a = f.getName();
        char b, c;
        switch (f.getDirection()) {
            case UP:
                b = puzzle[row_pos - 1][col_pos].getName();
                c = puzzle[row_pos - 2][col_pos].getName();
                break;
            case DOWN:
                b = puzzle[row_pos + 1][col_pos].getName();
                c = puzzle[row_pos + 2][col_pos].getName();
                break;
            case LEFT:
                b = puzzle[row_pos][col_pos - 1].getName();
                c = puzzle[row_pos][col_pos - 2].getName();
                break;
            case RIGHT:
                b = puzzle[row_pos][col_pos + 1].getName();
                c = puzzle[row_pos][col_pos + 2].getName();
                break;
            case UP_RIGHT:
                b = puzzle[row_pos - 1][col_pos + 1].getName();
                c = puzzle[row_pos - 2][col_pos + 2].getName();
                break;
            case DOWN_RIGHT:
                b = puzzle[row_pos + 1][col_pos + 1].getName();
                c = puzzle[row_pos + 2][col_pos + 2].getName();
                break;
            case UP_LEFT:
                b = puzzle[row_pos - 1][col_pos - 1].getName();
                c = puzzle[row_pos - 2][col_pos - 2].getName();
                break;
            case DOWN_LEFT:
                b = puzzle[row_pos + 1][col_pos - 1].getName();
                c = puzzle[row_pos + 2][col_pos - 2].getName();
                break;
            default:
                throw new RuntimeException("Wrong direction given!");
        }
        clauses += 4;
        return "(!" + a + "v!" + b + "v!" + c + ")^(!" + a + "v" + b + "v" + c + ")^(" + a + "v!" + b + "v" + c + ")^(" + a + "v" + b + "v!" + c + ")";
    }

    public String calculateColorEquasion1_range3(Field f, int row_pos, int col_pos) {
        char a = f.getName();
        char b, c, d;
        switch (f.getDirection()) {
            case UP:
                b = puzzle[row_pos - 1][col_pos].getName();
                c = puzzle[row_pos - 2][col_pos].getName();
                d = puzzle[row_pos - 3][col_pos].getName();
                break;
            case DOWN:
                b = puzzle[row_pos + 1][col_pos].getName();
                c = puzzle[row_pos + 2][col_pos].getName();
                d = puzzle[row_pos + 3][col_pos].getName();
                break;
            case LEFT:
                b = puzzle[row_pos][col_pos - 1].getName();
                c = puzzle[row_pos][col_pos - 2].getName();
                d = puzzle[row_pos][col_pos - 3].getName();
                break;
            case RIGHT:
                b = puzzle[row_pos][col_pos + 1].getName();
                c = puzzle[row_pos][col_pos + 2].getName();
                d = puzzle[row_pos][col_pos + 3].getName();
                break;
            case UP_RIGHT:
                b = puzzle[row_pos - 1][col_pos + 1].getName();
                c = puzzle[row_pos - 2][col_pos + 2].getName();
                d = puzzle[row_pos - 3][col_pos + 3].getName();
                break;
            case DOWN_RIGHT:
                b = puzzle[row_pos + 1][col_pos + 1].getName();
                c = puzzle[row_pos + 2][col_pos + 2].getName();
                d = puzzle[row_pos + 3][col_pos + 3].getName();
                break;
            case UP_LEFT:
                b = puzzle[row_pos - 1][col_pos - 1].getName();
                c = puzzle[row_pos - 2][col_pos - 2].getName();
                d = puzzle[row_pos - 3][col_pos - 3].getName();
                break;
            case DOWN_LEFT:
                b = puzzle[row_pos + 1][col_pos - 1].getName();
                c = puzzle[row_pos + 2][col_pos - 2].getName();
                d = puzzle[row_pos + 3][col_pos - 3].getName();
                break;
            default:
                throw new RuntimeException("Wrong direction given!");
        }
        clauses += 8;
        return "(!" + a + "v!" + b + "v!" + c + ")^(!" + a + "v!" + b + "v!" + d + ")^(!" + a + "v!" + d + "v!" + c + ")^(!" + a + "v" + b + "v!" + c + "v!" + d + ")^(!" + a + "v" + b + "v" + c + "v" + d + ")^(" + a + "v!" + b + "v" + c + "v" + d + ")^(" + a + "v" + b + "v!" + c + "v" + d + ")^(" + a + "v" + b + "v" + c + "v!" + d + ")";
    }

    public String calculateColorEquasion2_range2(Field f, int row_pos, int col_pos) {
        char a = f.getName();
        char b, c;
        switch (f.getDirection()) {
            case UP:
                b = puzzle[row_pos - 1][col_pos].getName();
                c = puzzle[row_pos - 2][col_pos].getName();
                break;
            case DOWN:
                b = puzzle[row_pos + 1][col_pos].getName();
                c = puzzle[row_pos + 2][col_pos].getName();
                break;
            case LEFT:
                b = puzzle[row_pos][col_pos - 1].getName();
                c = puzzle[row_pos][col_pos - 2].getName();
                break;
            case RIGHT:
                b = puzzle[row_pos][col_pos + 1].getName();
                c = puzzle[row_pos][col_pos + 2].getName();
                break;
            case UP_RIGHT:
                b = puzzle[row_pos - 1][col_pos + 1].getName();
                c = puzzle[row_pos - 2][col_pos + 2].getName();
                break;
            case DOWN_RIGHT:
                b = puzzle[row_pos + 1][col_pos + 1].getName();
                c = puzzle[row_pos + 2][col_pos + 2].getName();
                break;
            case UP_LEFT:
                b = puzzle[row_pos - 1][col_pos - 1].getName();
                c = puzzle[row_pos - 2][col_pos - 2].getName();
                break;
            case DOWN_LEFT:
                b = puzzle[row_pos + 1][col_pos - 1].getName();
                c = puzzle[row_pos + 2][col_pos - 2].getName();
                break;
            default:
                throw new RuntimeException("Wrong direction given!");
        }
        clauses += 3;
        return "(!" + a + "v" + b + ")^(!" + a + "v" + c + ")^(" + a + "v!" + b + "v!" + c + ")";
    }

    public String calculateColorEquasion2_range3(Field f, int row_pos, int col_pos) {
        char a = f.getName();
        char b, c, d;
        switch (f.getDirection()) {
            case UP:
                b = puzzle[row_pos - 1][col_pos].getName();
                c = puzzle[row_pos - 2][col_pos].getName();
                d = puzzle[row_pos - 3][col_pos].getName();
                break;
            case DOWN:
                b = puzzle[row_pos + 1][col_pos].getName();
                c = puzzle[row_pos + 2][col_pos].getName();
                d = puzzle[row_pos + 3][col_pos].getName();
                break;
            case LEFT:
                b = puzzle[row_pos][col_pos - 1].getName();
                c = puzzle[row_pos][col_pos - 2].getName();
                d = puzzle[row_pos][col_pos - 3].getName();
                break;
            case RIGHT:
                b = puzzle[row_pos][col_pos + 1].getName();
                c = puzzle[row_pos][col_pos + 2].getName();
                d = puzzle[row_pos][col_pos + 3].getName();
                break;
            case UP_RIGHT:
                b = puzzle[row_pos - 1][col_pos + 1].getName();
                c = puzzle[row_pos - 2][col_pos + 2].getName();
                d = puzzle[row_pos - 3][col_pos + 3].getName();
                break;
            case DOWN_RIGHT:
                b = puzzle[row_pos + 1][col_pos + 1].getName();
                c = puzzle[row_pos + 2][col_pos + 2].getName();
                d = puzzle[row_pos + 3][col_pos + 3].getName();
                break;
            case UP_LEFT:
                b = puzzle[row_pos - 1][col_pos - 1].getName();
                c = puzzle[row_pos - 2][col_pos - 2].getName();
                d = puzzle[row_pos - 3][col_pos - 3].getName();
                break;
            case DOWN_LEFT:
                b = puzzle[row_pos + 1][col_pos - 1].getName();
                c = puzzle[row_pos + 2][col_pos - 2].getName();
                d = puzzle[row_pos + 3][col_pos - 3].getName();
                break;
            default:
                throw new RuntimeException("Wrong direction given!");
        }
        clauses += 8;
        return "(!" + a + "v" + b + "v" + c + ")^(!" + a + "v" + b + "v" + d + ")^(!" + a + "v" + c + "v" + d + ")^(!" + a + "v!" + b + "v!" + c + "v!" + d + ")^(!" + a + "v!" + b + "v" + c + "v" + d + ")^(" + a + "v!" + b + "v!" + c + "v" + d + ")^(" + a + "v!" + b + "v" + c + "v!" + d + ")^(" + a + "v" + b + "v!" + c + "v!" + d + ")";
    }

    public String calculateColorEquasion3(Field f, int row_pos, int col_pos) {
        char a = f.getName();
        char b, c, d;
        switch (f.getDirection()) {
            case UP:
                b = puzzle[row_pos - 1][col_pos].getName();
                c = puzzle[row_pos - 2][col_pos].getName();
                d = puzzle[row_pos - 3][col_pos].getName();
                break;
            case DOWN:
                b = puzzle[row_pos + 1][col_pos].getName();
                c = puzzle[row_pos + 2][col_pos].getName();
                d = puzzle[row_pos + 3][col_pos].getName();
                break;
            case LEFT:
                b = puzzle[row_pos][col_pos - 1].getName();
                c = puzzle[row_pos][col_pos - 2].getName();
                d = puzzle[row_pos][col_pos - 3].getName();
                break;
            case RIGHT:
                b = puzzle[row_pos][col_pos + 1].getName();
                c = puzzle[row_pos][col_pos + 2].getName();
                d = puzzle[row_pos][col_pos + 3].getName();
                break;
            case UP_RIGHT:
                b = puzzle[row_pos - 1][col_pos + 1].getName();
                c = puzzle[row_pos - 2][col_pos + 2].getName();
                d = puzzle[row_pos - 3][col_pos + 3].getName();
                break;
            case DOWN_RIGHT:
                b = puzzle[row_pos + 1][col_pos + 1].getName();
                c = puzzle[row_pos + 2][col_pos + 2].getName();
                d = puzzle[row_pos + 3][col_pos + 3].getName();
                break;
            case UP_LEFT:
                b = puzzle[row_pos - 1][col_pos - 1].getName();
                c = puzzle[row_pos - 2][col_pos - 2].getName();
                d = puzzle[row_pos - 3][col_pos - 3].getName();
                break;
            case DOWN_LEFT:
                b = puzzle[row_pos + 1][col_pos - 1].getName();
                c = puzzle[row_pos + 2][col_pos - 2].getName();
                d = puzzle[row_pos + 3][col_pos - 3].getName();
                break;
            default:
                throw new RuntimeException("Wrong direction given!");
        }
        clauses += 4;
        return "(!" + a + "v" + b + ")^(!" + a + "v" + c + ")^(!" + a + "v" + d + ")^(" + a + "v!" + b + "v!" + c + "v!" + d + ")";
    }

}
