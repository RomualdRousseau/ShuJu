package com.github.romualdrousseau.shuju.op.math;

import com.github.romualdrousseau.shuju.core.UFunc;
import com.github.romualdrousseau.shuju.core.UFunc0;
import com.github.romualdrousseau.shuju.core.UFunc0i;
import com.github.romualdrousseau.shuju.types.Scalar;

public class MathOps {

    public static final UFunc<Float> Inc = new UFunc0((x, y) -> x + 1.0f);

    public static final UFunc<Float> Dec = new UFunc0((x, y) -> x - 1.0f);

    public static final UFunc<Float> SumSparse = new UFunc0((x, y) -> x + ((y == 0.0f) ? 1.0f : 0.0f));

    public static final UFunc<Float> Add = new UFunc0((x, y) -> y + x);

    public static final UFunc<Float> Sub = new UFunc0((x, y) -> y - x);

    public static final UFunc<Float> Mul = new UFunc0((x, y) -> y * x);

    public static final UFunc<Float> Div = new UFunc0((x, y) -> y / x);

    public static final UFunc<Float> Max = new UFunc0((x, y) -> x > y ? x : y);

    public static final UFunc<Float> Min = new UFunc0((x, y) -> x < y ? x : y);

    public static final UFunc<Float> ArgMax = new UFunc0i((x, y) -> x > y ? x : y);

    public static final UFunc<Float> ArgMin = new UFunc0i((x, y) -> x < y ? x : y);

    public static final UFunc<Float> Sqrt = new UFunc0((x, y) -> Scalar.sqrt(y));

    public static final UFunc<Float> InvSqrt = new UFunc0((x, y) -> 1.0f / (Scalar.sqrt(y) + Scalar.EPSILON));

    public static final UFunc<Float> Inv = new UFunc0((x, y) -> 1.0f / (y + Scalar.EPSILON));

    public static final UFunc<Float> Square = new UFunc0((x, y) -> y * y);

    public static final UFunc<Float> Pow = new UFunc0((x, y) -> Scalar.pow(y, x));

    public static final UFunc<Float> Exp = new UFunc0((x, y) -> Scalar.exp(y));

    public static final UFunc<Float> Log = new UFunc0((x, y) -> Scalar.log(y));

    public static final UFunc<Float> SumSq = new UFunc0((x, y) -> x + y * y);

    public static final UFunc<Float> MagSq = new UFunc0((x, y) -> (y - x) * (y - x));

    public static final UFunc.Uni<Float> chop = e -> new UFunc0((x, y) -> (Scalar.abs(y) < e) ? 0.0f : y);

    public static final UFunc.Bi<Float> mutate = (r, v) -> new UFunc0((x, y) -> Scalar.mutate(r, v, y));

    public static final UFunc.Bi<Float> randomize = (a, b) -> new UFunc0((x, y) -> Scalar.random(a, b));

    public static final UFunc.Bi<Float> constrain = (a, b) -> new UFunc0((x, y) -> Scalar.constrain(y, a, b));

    public static final UFunc.Tri<Float> if_lt_then = (p, a, b) -> new UFunc0((x, y) -> Scalar.if_lt_then(y, p, a, b));
}
