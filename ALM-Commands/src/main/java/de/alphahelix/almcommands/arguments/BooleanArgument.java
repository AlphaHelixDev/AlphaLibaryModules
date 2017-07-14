package de.alphahelix.almcommands.arguments;

import java.util.ArrayList;

public class BooleanArgument extends Argument<Boolean> {

    private ArrayList<String> trueStrings = new ArrayList<>();
    private ArrayList<String> falseStrings = new ArrayList<>();

    public BooleanArgument() {
        addBooleans("true", "false");
        addBooleans("yes", "no");
        addBooleans("1", "0");
        addBooleans("y", "n");
    }

    @Override
    public boolean matches() {
        return trueStrings.contains(getEnteredArgument().toLowerCase()) || falseStrings.contains(getEnteredArgument().toLowerCase());
    }

    @Override
    public Boolean fromArgument() {
        return matches() && trueStrings.contains(getEnteredArgument().toLowerCase());
    }

    public void addBooleans(String trueString, String falseString) {
        if (!trueStrings.contains(trueString.toLowerCase())) {
            trueStrings.add(trueString.toLowerCase());
        }
        if (!falseStrings.contains(falseString.toLowerCase())) {
            falseStrings.add(falseString.toLowerCase());
        }
    }
}
