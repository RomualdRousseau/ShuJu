package com.github.romualdrousseau.shuju;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import com.github.romualdrousseau.shuju.redux.Store;

public class Test_Redux {

    @Test
    public void testStoreNoReducerNoSbuscriber() {
        final var state = new HashMap<String, Integer>();
        final var store = new Store<HashMap<String, Integer>, String>(state);
        store.dispatch("test");
    }

    @Test
    public void testStoreNoReducerWithSubscriber() {
        final var state = new HashMap<String, Integer>();
        final var store = new Store<HashMap<String, Integer>, String>(state);
        store.addSubscriber("test", (s, a) -> assertEquals("test", a));
        store.dispatch("test");
    }

    @Test
    public void testStoreWithReducerAndSubscribers() {
        final var state = new HashMap<String, Integer>();
        state.put("counter", 0);

        final var store = new Store<HashMap<String, Integer>, String>(state);
        store.addReducer((s, a) -> {
            if (a.equals("inc")) {
                s.computeIfPresent("counter", (x, y) -> y + 1);
            }
            if (a.equals("dec")) {
                s.computeIfPresent("counter", (x, y) -> y - 1);
            }
            return s;
        });

        store.addSubscriber("inc", (s, a) -> assertEquals(Integer.valueOf(1), s.getState().get("counter")));
        store.addSubscriber("dec", (s, a) -> assertEquals(Integer.valueOf(0), s.getState().get("counter")));
        store.dispatch("inc");
        store.dispatch("dec");
    }
}
