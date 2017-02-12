package comp.solver;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Parser {

    static String sender = "";

    public static String parse(String input) {

        /* Test fuer korrekten Empfang / Debug */
        //System.out.println("Input:\n" + input);

        /* Dokument ueberpruefen. Erst einfache Ueberpruefung */
        String request = "";
        // Sender also Absender wird nur bei der direkten Kommunikation benötigt
        //String sender = "";
        String instruction = "";

        int[] sudoku = {};

        // Hier ggf. noch ueberarbeiten
        String modInput = input.replace("|", "").replace(" ", "").replace("\r", "").replace("\n", "").replace("\t", "");

        JSONParser parser = new JSONParser();

        try {

            Object object = parser.parse(modInput);

            JSONObject myJSONObject = (JSONObject) object;

            request = (String) myJSONObject.get("request-id");

            //sender = (String) myJSONObject.get("sender");

            instruction = (String) myJSONObject.get("instruction");

            if (myJSONObject.containsKey("sudoku")) {
                JSONArray sudokuArray = (JSONArray) myJSONObject.get("sudoku");
                sudoku = new int[sudokuArray.size()];
                for (int i = 0; i < sudokuArray.size(); i++) {
                    sudoku[i] = Integer.valueOf(String.valueOf((sudokuArray.get(i))));
                }
            }
        } catch (ParseException e) {
            return "ERROR";
            //e.printStackTrace();
        }

        // Debug
//        System.out.println("Output:");
//        System.out.println("request=" + request);
//        System.out.println("sender=" + sender);
//        System.out.println("instruction=" + instruction);
//        System.out.println("sudoku=" + Arrays.toString(sudoku));

        switch (instruction) {
            case "ping":
                //Antworte mit pong
                return answerJSON(request, "pong", sudoku);

            case "solve":
                try {
                    SudokuSolver sodokuSolver = new SudokuSolver(sudoku);
                    int solution = sodokuSolver.search();
                    if (solution == 0) {
                        return answerJSON(request, "solved:impossible", sudoku);
                    } else if (solution == 1) {
                        return answerJSON(request, "solved:one", sudoku);
                    } else if (solution == 2) {
                        return answerJSON(request, "solved:many", sudoku);
                    }
                } catch (Exception e) {
                    return "ERROR";
                    //e.printStackTrace();
                }

            default:
                //Rest interessiert uns nicht
                return "ERROR";
        }
    }

    public static String answerJSON(String request, String instruction, int[] sudoku) {
        /*
         * Sender muss geupdated werden für Antwort (URI muss noch gemachr werden..). Instruction wurde bereits geupdated.
         *  GUID (request-id) bleibt (?).
         * Sudoku Feld ist entweder null oder bereits durch Solve-Case geupdatet.
          */

        String answer = "";

        if (sender.isEmpty()) {
            System.err.println("Bitte zuerst die SenderURI über Setter-Methode festlegen!");

        } else {
            JSONObject object = new JSONObject();
            object.put("request-id", request);
            object.put("sender", sender);
            object.put("instruction", instruction);

            if (sudoku.length != 0) {
                JSONArray sudokuArray = new JSONArray();
                for (int i = 0; i < sudoku.length; i++) {
                    sudokuArray.add(sudoku[i]);
                }
                object.put("sudoku", sudokuArray);
            }

            answer = object.toJSONString();

            // Debug
//            System.out.println("Answer:");
//            System.out.println(answer);
        }
        return answer;

    }


    public static void setSender(String senderURI) {
        if (senderURI.isEmpty()) {
            System.err.println("Der als Parameter übergebene String ist leer!");
        } else {
            sender = senderURI;
        }
    }

    public static String getSender() {
        return sender;
    }

}


