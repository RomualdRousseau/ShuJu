package com.github.romualdrousseau.shuju.math;

public class MFuncs {

    public static final UFunc0 Chop = new UFunc0((x, y) -> (Scalar.abs(y) < x) ? 0.0f : x);

    public static final UFunc0 Inc = new UFunc0((x, y) -> x + 1.0f);

    public static final UFunc0 Dec = new UFunc0((x, y) -> x - 1.0f);

    public static final UFunc0 Add = new UFunc0((x, y) -> y + x);

    public static final UFunc0 Sub = new UFunc0((x, y) -> y - x);

    public static final UFunc0 Mul = new UFunc0((x, y) -> y * x);

    public static final UFunc0 Div = new UFunc0((x, y) -> y / x);

    public static final UFunc0 Max = new UFunc0((x, y) -> x > y ? x : y);

    public static final UFunc0 Min = new UFunc0((x, y) -> x < y ? x : y);

    public static final UFunc0 Sqrt = new UFunc0((x, y) -> Scalar.sqrt(y));

    public static final UFunc0 InvSqrt = new UFunc0((x, y) -> 1.0f / (Scalar.sqrt(y) + Scalar.EPSILON));

    public static final UFunc0 Pow = new UFunc0((x, y) -> Scalar.pow(y, x));

    public static final UFunc0 SumSq = new UFunc0((x, y) -> y * y + x);

    public static final UFunc0 MagSq = new UFunc0((x, y) -> (y - x) * (y - x));

    public static final UFunc0 SumSparse = new UFunc0((x, y) -> ((y == 0.0f) ? 1.0f : 0.0f) + x);
}
