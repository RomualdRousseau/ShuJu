package com.github.romualdrousseau.shuju.math;

public class MFuncs {

    public static final UFunc<Float> Chop = new UFunc0((x, y) -> (Scalar.abs(y) < x) ? 0.0f : x);

    public static final UFunc<Float> Inc = new UFunc0((x, y) -> x + 1.0f);

    public static final UFunc<Float> Dec = new UFunc0((x, y) -> x - 1.0f);

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

    public static final UFunc<Float> Square = new UFunc0((x, y) -> y * y);

    public static final UFunc<Float> Pow = new UFunc0((x, y) -> Scalar.pow(y, x));

    public static final UFunc<Float> SumSq = new UFunc0((x, y) -> y * y + x);

    public static final UFunc<Float> MagSq = new UFunc0((x, y) -> (y - x) * (y - x));

    public static final UFunc<Float> SumSparse = new UFunc0((x, y) -> ((y == 0.0f) ? 1.0f : 0.0f) + x);

    public static final UFunc<Float> MatMul = new MatMul((x, y) -> x * y);
}
