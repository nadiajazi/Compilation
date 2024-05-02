package org.example;

import java.util.*;


public class NFA {
    int noState;
    List<String> states;
    int noAlphabet;
    List<String> alphabets;
    String start;
    int noFinal;
    List<String> finals;
    int noTransition;
    List<Transition> transitions;
    Map<String, List<Integer>> transitionTable;
    Map<String, Integer> statesDict;
    Map<String, Integer> alphabetsDict;

    public NFA(int noState, List<String> states, int noAlphabet, List<String> alphabets, String start,
               int noFinal, List<String> finals, int noTransition, List<Transition> transitions) {
        this.noState = noState;
        this.states = states;
        this.noAlphabet = noAlphabet;
        this.alphabets = new ArrayList<>(alphabets);
        this.alphabets.add("e"); // Adding epsilon to alphabets
        this.noAlphabet += 1;
        this.start = start;
        this.noFinal = noFinal;
        this.finals = finals;
        this.noTransition = noTransition;
        this.transitions = transitions;
        this.transitionTable = new HashMap<>();
        this.statesDict = new HashMap<>();
        this.alphabetsDict = new HashMap<>();
        for (int i = 0; i < this.noState; i++) {
            this.statesDict.put(states.get(i), i);
        }
        for (int i = 0; i < this.noAlphabet; i++) {
            this.alphabetsDict.put(this.alphabets.get(i), i);
        }
        initializeTransitionTable();
    }

    private void initializeTransitionTable() {
        for (int i = 0; i < noState; i++) {
            for (int j = 0; j < noAlphabet; j++) {
                transitionTable.put(i + "" + j, new ArrayList<>());
            }
        }
        for (Transition t : transitions) {
            int fromIndex = statesDict.get(t.from);
            int symbolIndex = alphabetsDict.get(t.symbol);
            int toIndex = statesDict.get(t.to);
            transitionTable.get(fromIndex + "" + symbolIndex).add(toIndex);
        }
    }

    public Set<Integer> getEpsilonClosure(int state) {
        Set<Integer> closure = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(state);
        closure.add(state);
        while (!queue.isEmpty()) {
            int currentState = queue.poll();
            List<Integer> epsTransitions = transitionTable.get(currentState + "" + alphabetsDict.get("e"));
            for (int nextState : epsTransitions) {
                if (!closure.contains(nextState)) {
                    closure.add(nextState);
                    queue.add(nextState);
                }
            }
        }
        return closure;
    }

    public boolean isFinalDFA(Set<Integer> stateSet) {
        for (int state : stateSet) {
            if (finals.contains(states.get(state))) {
                return true;
            }
        }
        return false;
    }

    // Helper class for transitions
    public static class Transition {
        String from, symbol, to;

        public Transition(String from, String symbol, String to) {
            this.from = from;
            this.symbol = symbol;
            this.to = to;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Number of States:");
        int noState = Integer.parseInt(scanner.nextLine());
        System.out.println("States (separated by space):");
        List<String> states = Arrays.asList(scanner.nextLine().split("\\s+"));
        System.out.println("Number of Alphabets:");
        int noAlphabet = Integer.parseInt(scanner.nextLine());
        System.out.println("Alphabets (separated by space):");
        List<String> alphabets = Arrays.asList(scanner.nextLine().split("\\s+"));
        System.out.println("Start State:");
        String start = scanner.nextLine();
        System.out.println("Number of Final States:");
        int noFinal = Integer.parseInt(scanner.nextLine());
        System.out.println("Final States (separated by space):");
        List<String> finals = Arrays.asList(scanner.nextLine().split("\\s+"));
        System.out.println("Number of Transitions:");
        int noTransition = Integer.parseInt(scanner.nextLine());
        List<Transition> transitions = new ArrayList<>();
        System.out.println("Enter Transitions (from symbol to):");
        for (int i = 0; i < noTransition; i++) {
            String[] parts = scanner.nextLine().split("\\s+");
            transitions.add(new Transition(parts[0], parts[1], parts[2]));
        }

        NFA nfa = new NFA(noState, states, noAlphabet, alphabets, start, noFinal, finals, noTransition, transitions);
        System.out.println("NFA created.");
    }
}
