package com.github.romualdrousseau.shuju.redux;

import java.util.function.BiFunction;

public interface Reducer<S, A> extends BiFunction<S, A, S> {}
