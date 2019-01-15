package com.github.romualdrousseau.shuju.util;

import java.util.List;
import java.util.HashMap;
import java.util.Map.Entry;

import com.github.romualdrousseau.shuju.StatisticClass;

public class Election<T extends StatisticClass> {
    public Winner<T> vote(List<T> candidates) {
        Winner<T> winner = null;
        HashMap<T, Double> polls = new HashMap<T, Double>();
        Double maxPoll = 0.0;

        for (T candidate : candidates) {
            Double poll = polls.get(candidate);

            if (poll == null) {
                poll = 1.0;
            } else {
                poll += 1.0;
            }

            if (poll > maxPoll) {
                winner = new Winner<T>(candidate, 0.0);
                maxPoll = poll;
            }

            polls.put(candidate, poll);
        }

        return winner;
    }

    public Winner<T> voteWithRank(List<Entry<Double, T>> candidates) {
        Winner<T> winner = null;
        HashMap<T, Double> polls = new HashMap<T, Double>();
        Double maxPoll = 0.0;

        for (Entry<Double, T> entry : candidates) {
            Double rank = entry.getKey();
            T candidate = entry.getValue();

            Double poll = polls.get(candidate);
            if (poll == null) {
                poll = weightedFunc(rank);
            } else {
                poll += weightedFunc(rank);
            }
            polls.put(candidate, poll);

            if (poll > maxPoll) {
                double probability = (winner == null) ? 1.0 - rank : Math.min(1.0 - rank, winner.getProbability());
                winner = new Winner<T>(candidate, probability);
                maxPoll = poll;
            }
        }

        return winner;
    }

    private static double weightedFunc(double x) {
        return 1 - 1 / (1 + Math.exp(-15 * (x - 0.2)));
    }
}
