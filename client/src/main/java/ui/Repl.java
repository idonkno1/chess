package ui;

import ui.websocket.ServerMessageHandler;
import webSocketMessages.serverMessages.*;
import webSocketMessages.serverMessages.Error;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements ServerMessageHandler{

    private final Client client;

    public Repl(String serverUrl) {client = new Client(serverUrl, this);}

    public void run() {
        System.out.println("Welcome to 240 Chess. Type Help to get started.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    @Override
    public void notify(ServerMessage serverMessage, String playerCol) {
        if (serverMessage instanceof LoadGame) {
            handleLoadGame((LoadGame) serverMessage, playerCol);
        } else if (serverMessage instanceof Error) {
            handleError((Error) serverMessage);
        } else if (serverMessage instanceof Notification) {
            handleNotification((Notification) serverMessage);
        } else if (serverMessage instanceof HighlightMessage) {
            handleHighlight((HighlightMessage) serverMessage, playerCol);
        }else {
            System.out.println(RED + "Unhandled server message type: " + serverMessage.getServerMessageType());
        }
        printPrompt();
    }

    private void handleHighlight(HighlightMessage message, String playerCol) {
        System.out.println(RED + "Highlighted move: ");
        var game = message.getGame().getBoard();
        var moves = message.getMoves();
        CreateBoard.printBoard(game, playerCol, moves);
    }

    private void handleLoadGame(LoadGame message, String playerCol) {
        System.out.println(RED + "Game Loaded: ");
        var game = message.getGame().getBoard();
        CreateBoard.printBoard(game, playerCol, null);
    }

    private void handleError(Error message) {
        System.out.println(RED + message.getErrorDescription());
    }

    private void handleNotification(Notification message) {
        System.out.println(RED + "Notification: " + message.getNotification());
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}