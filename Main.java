import controller.Controller;
import model.Game;
import view.CommandLineView;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        
        // create model instance
        Game game = new Game();
        // create view instance
        CommandLineView view = new CommandLineView();
        // create controller instance with access to the model and the view
        Controller controller = new Controller(game, view);
        // scanner for recieving inputs
        Scanner scanner = new Scanner(System.in);

        // main game loop
        while(true) {
            // draw the game
            view.draw(game.getLastState());
            // request new command
            System.out.print("CH> ");
            String input = scanner.nextLine().trim();
            // process command
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Thanks for playing :) Bye!");
                return;
            }
            controller.executeCmd(input);
        }
    }
}
