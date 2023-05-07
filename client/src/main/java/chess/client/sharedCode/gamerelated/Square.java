package chess.client.sharedCode.gamerelated;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public enum Square {
     A1(11), A2(12), A3(13), A4(14), A5(15), A6(16), A7(17), A8(18),
    B1(21), B2(22), B3(23), B4(24), B5(25), B6(26), B7(27), B8(28),
    C1(31), C2(32), C3(33), C4(34), C5(35), C6(36), C7(37), C8(38),
    D1(41), D2(42), D3(43), D4(44), D5(45), D6(46), D7(47), D8(48),
    E1(51), E2(52), E3(53), E4(54), E5(55), E6(56), E7(57), E8(58),
    F1(61), F2(62), F3(63), F4(64), F5(65), F6(66), F7(67), F8(68),
    G1(71), G2(72), G3(73), G4(74), G5(75), G6(76), G7(77), G8(78),
    H1(81), H2(82), H3(83), H4(84), H5(85), H6(86), H7(87), H8(88);
    private final int i;

    static final int rowSize = 8;
    static final int columnSize = 8;
    Square(int i) {
        this.i = i;
    }

    /*
     * Receive a TileNum and a movement (5 to right, 4 to down..)
     * Output the TileNum after applying the movement.
     */
    // TODO PROBABLY BUGS
    public static Square getMovement(Square square, PieceMovement move) {
        int num = move.getUp() % 8 + square.numVal() % 10;
        if (num > 8 || num < 1) return null;
        int digit = move.getRight() % 8 + square.numVal() / 10;
        if (digit > 8 || digit < 1) return null;
        return idToSquare(num + digit * 10);
    }
    public Square getMovement(int up, int right) {
        return  getMovement(this,new PieceMovement(up, right));
    }
    /*
     * receives id of a TileNum object, output corresponding object.
     * If not found throw error.
     */
    static Square idToSquare(int id) {
        for (Square tile : Square.values()) {
            if (tile.numVal() == id) return tile;
        }
        throw new RuntimeException("Illegal Id: " + id);
    }
    /*
     * Receives a string of a tile ("A1", "H4"..) and outputs corresponding
     * TileNum object
     * If not found return null.
     */
    public static Square strToSquare(String str) {
        str = str.toUpperCase();
        if (str.length() != 2) {
            throw new RuntimeException("Error");
        }
        for (Square tn: Square.values()) {
            if (str.equals(tn.name())) return tn;
        }
        return null;
    }
    public static Square toSquare(int row, int column) {
        if (row >= rowSize || column >= columnSize || row < 0 || column < 0) {
            //throw new RuntimeException("out of bound; row = " + row + "; column = " + column);
            return null;
        }
        return idToSquare(10*(column + 1) + row + 1);
    }
    public int getColumn() {
        return i/10 - 1;
    }
    public int getRow() {
        return i%10 - 1;
    }

    public final int numVal() {
        return i;
    }
    public static List<Square> getAllSquaresBetweenInLine(Square sq1, Square sq2) {
        List<Square> output = new LinkedList<>();
        int sq1Row = sq1.getRow(), sq1Column = sq1.getColumn(), sq2Row = sq2.getRow()
                , sq2Column = sq2.getColumn();
        if (sq1 == sq2) return output;
        // check for straight line
        if (sq1Row == sq2Row) {
            List<Integer> between = getNumbersBetween(sq1Column, sq2Column);
            for (int column: between) output.add(toSquare(sq1Row, column));
        } else if (sq1.getColumn() == sq2.getColumn()) {
            List<Integer> between = getNumbersBetween(sq1Row, sq2Row);
            for (int row: between) output.add(toSquare(row, sq1Column));
        // check for diagnoal line
        } else if (sq1Row - sq2Row == sq1Column - sq2Column
    || sq1Row - sq2Row == -(sq1Column - sq2Column)){

            List<Integer> betweenRow = getNumbersBetween(sq1Row, sq2Row);
            List<Integer> betweenColumn = getNumbersBetween(sq1Column, sq2Column);

            for (int i=0;i<betweenRow.size();i++)
                output.add(toSquare(betweenRow.get(i),betweenColumn.get(i)));
            // TODO TEST IF WORKS
        }
        return output;
    }
    static List<Integer> getNumbersBetween(int bigger, int smaller) {
        if (bigger < smaller) return getNumbersBetween(smaller,bigger);
        List<Integer> output = new ArrayList<>();
        for (int i=smaller + 1;i<bigger;i++)
            output.add(i);
        return output;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
