package edu.kit.kastel.game.decision;

import edu.kit.kastel.game.console.Console;
import edu.kit.kastel.game.console.Message;

/**
 * Decision maker for debug mode. Provides deterministic decision-making through user input.
 *
 * @author uupyx
 */
public final class DebugDecisionMaker implements DecisionMaker {

    @Override
    public boolean decideYesNo(double probability, String context) {
        
        while (true) {
            Console.writeMessage(Message.DECIDE_YES_NO, context);
            String input = Console.readLine();
            if (input.equals("y")) {
                return true;
            }
            if (input.equals("n")) {
                return false;
            }
            Console.writeMessage(Message.ERROR_DEBUG_Y_N);
        }
    }

    @Override
    public double decideDouble(double min, double max, String context) {
        while (true) {
            try {
                Console.writeMessage(Message.DECIDE_DOUBLE, context, min, max);
                
                double value = Double.parseDouble(Console.readLine());
                if (value >= min && value <= max) {
                    return value;
                }
                Console.writeMessage(Message.ERROR_DEBUG_OUT_OF_RANGE);
            } catch (NumberFormatException e) {
                Console.writeMessage(Message.ERROR_DEBUG_INVALID_NUMBER);
            }
        }
    }

    @Override
    public int decideInt(int min, int max, String context) {
        while (true) {
            try {
                Console.writeMessage(Message.DECIDE_INTEGER, context, min, max);
                
                int value = Integer.parseInt(Console.readLine());
                if (value >= min && value <= max) {
                    return value;
                }
                Console.writeMessage(Message.ERROR_DEBUG_OUT_OF_RANGE);
            } catch (NumberFormatException e) {
                Console.writeMessage(Message.ERROR_DEBUG_INVALID_NUMBER);
            }
        }
    }

}
