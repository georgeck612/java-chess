package moveTools;

public class Converter {

    /**
     * Transforms a move in chess algebraic notation (such as a5 or c4) into a coordinate.
     *
     * @param notation A chess move.
     * @return The move as a coordinate, represented by an array of two integers.
     */
    public static int[] notationToCoord(String notation) {
        char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        char[] brokenNotation = notation.toCharArray();
        int[] boardCoord = new int[2];
        for (int i = 0; i < letters.length; i++) {
            if (brokenNotation[0] == letters[i]) {
                boardCoord[1] = i;
                break;
            }
        }
        boardCoord[0] = 8 - Character.getNumericValue(brokenNotation[1]);
        return boardCoord;
    }

    /**
     * Transforms a coordinate into chess algebraic notation.
     * @param rank The rank on the board of the coordinate (the first element).
     * @param file The file on the board of the coordinate (the second element).
     * @return The move in chess algebraic notation.
     */
    public static String coordToNotation(int rank, int file) {
        String letters = "abcdefgh";
        String num = String.valueOf(8 - rank);
        String letter = letters.substring(file, file + 1);
        return letter + num;
    }
}
