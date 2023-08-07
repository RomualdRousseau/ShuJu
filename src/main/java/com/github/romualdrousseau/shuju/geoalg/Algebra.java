package com.github.romualdrousseau.shuju.geoalg;

import com.github.romualdrousseau.shuju.graph.Graph;

public interface Algebra {
    default Graph graph(Graph graph, AlgebraGraphFunction inline) {
        return graph.apply(inline.apply(this));
    }

    default void inline(AlgebraInlineFunction inline) {
        inline.apply(this);
    }

    default float[] set(float... v) {
        return v;
    }

    public abstract float[] rev(float[] a);

    public abstract float[] conj(float[] a);

    public abstract float[] dual(float[] a);

    public abstract float[] add(float[] a, float b);

    public abstract float[] add(float a, float[] b);

    public abstract float[] add(float[] a, float[] b);

    public abstract float[] sub(float[] a, float b);

    public abstract float[] sub(float a, float[] b);

    public abstract float[] sub(float[] a, float[] b);

    public abstract float[] mul(float a, float[] b);

    public abstract float[] mul(float[] a, float b);

    public abstract float[] mul(float[] a, float[] b);

    public abstract float[] div(float a, float[] b);

    public abstract float[] div(float[] a, float b);

    public abstract float[] div(float[] a, float[] b);

    public abstract float[] dot(float[] a, float[] b);

    public abstract float[] join(float[] a, float[] b);

    public abstract float[] meet(float[] a, float[] b);

    public abstract float norm(float[] a);

    public abstract String toString(float[] a);
}
