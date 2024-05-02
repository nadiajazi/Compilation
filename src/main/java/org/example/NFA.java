package org.example;
import java.util.*;
import java.util.stream.Collectors;

public class Automate {

        private final Map<String, Etat> states = new HashMap<>();

        public void addState(Etat state) {
            states.put(state.getName(), state);
        }

        public Etat getState(String name) {
            return states.get(name);
        }

        public void addTransition(String sourceName, String targetName, Character symbol) {
            Etat source = states.computeIfAbsent(sourceName, Etat::new);
            Etat target = states.computeIfAbsent(targetName, Etat::new);
            source.addTransition(new Transition(source, target, symbol));
        }

        public void determinize() {
            Map<Set<Etat>, Etat> dfaStates = new HashMap<>();
            Queue<Set<Etat>> queue = new LinkedList<>();

            Etat startState = getState("Q0"); // Ensure you use the correct start state identifier
            if (startState == null) {
                System.err.println("Start state is missing in the automaton.");
                return;
            }

            Set<Etat> initialClosure = epsilonClosure(startState);
            Etat dfaStart = new Etat(setToName(initialClosure));
            dfaStart.setStart(true);
            states.clear();
            addState(dfaStart);
            dfaStates.put(initialClosure, dfaStart);
            queue.offer(initialClosure);

            while (!queue.isEmpty()) {
                Set<Etat> current = queue.poll();
                for (char symbol : getInputSymbols()) {
                    Set<Etat> moveSet = move(current, symbol);
                    Set<Etat> closure = epsilonClosure((Etat) moveSet);
                    if (!dfaStates.containsKey(closure)) {
                        Etat newState = new Etat(setToName(closure));
                        addState(newState);
                        dfaStates.put(closure, newState);
                        queue.offer(closure);
                    }
                    dfaStates.get(current).addTransition(new Transition(dfaStates.get(current), dfaStates.get(closure), symbol));
                }
            }
        }



    public Set<Etat> epsilonClosure(Etat startState) {
        Set<Etat> closure = new HashSet<>();
        Stack<Etat> stack = new Stack<>();
        stack.push(startState);
        closure.add(startState);

        while (!stack.isEmpty()) {
            Etat currentState = stack.pop();
            for (Transition transition : currentState.getTransitions()) {
                if (transition.getSymbol() == null && !closure.contains(transition.getTarget())) {
                    closure.add(transition.getTarget());
                    stack.push(transition.getTarget());
                }
            }
        }
        return closure;
    }

        private Set<Etat> move(Set<Etat> states, char symbol) {
            Set<Etat> result = new HashSet<>();
            for (Etat state : states) {
                for (Transition trans : state.getTransitions()) {
                    if (trans.getSymbol() != null && trans.getSymbol() == symbol) {
                        result.addAll(epsilonClosure(trans.getTarget()));
                    }
                }
            }
            return result;
        }

        private Set<Character> getInputSymbols() {
            Set<Character> symbols = new HashSet<>();
            for (Etat state : states.values()) {
                for (Transition t : state.getTransitions()) {
                    if (t.getSymbol() != null) symbols.add(t.getSymbol());
                }
            }
            return symbols;
        }

        private String setToName(Set<Etat> states) {
            return "{" + states.stream().map(Etat::getName).collect(Collectors.joining(",")) + "}";
        }

        // Rest of your Automate class...
    public boolean testString(String input) {
        System.out.println("Testing the string: " + input);

        // Find the start state of the DFA
        Etat currentState = findStartState();
        if (currentState == null) {
            System.out.println("Error: No start state defined.");
            return false;
        }

        // Process each character in the input string
        for (int i = 0; i < input.length(); i++) {
            char symbol = input.charAt(i);
            boolean transitionFound = false;

            // Check for a transition with the current symbol
            for (Transition transition : currentState.getTransitions()) {
                if (transition.getSymbol() != null && transition.getSymbol() == symbol) {
                    currentState = transition.getTarget();  // Move to the target state
                    transitionFound = true;
                    break;
                }
            }

            if (!transitionFound) {
                // No valid transition found for the current character
                System.out.println("No transition found for symbol: " + symbol + " from state " + currentState.getName());
                return false;
            }
        }

        // Check if the final state after processing the string is an accepting state
        if (currentState.isAccept()) {
            System.out.println("The string is accepted by the automaton.");
            return true;
        } else {
            System.out.println("The string is not accepted (ended in state " + currentState.getName() + ").");
            return false;
        }
    }

    private Etat findStartState() {
        for (Etat state : states.values()) {
            if (state.isStart()) {
                return state;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Automate:\n");
        for (Etat state : states.values()) {
            sb.append(state).append("\n");
            for (Transition transition : state.getTransitions()) {
                sb.append("  ").append(transition).append("\n");
            }
        }
        return sb.toString();
    }
}
