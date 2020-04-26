package com.github.romualdrousseau.shuju.math;

import java.util.function.BiFunction;

public class UFunc0 implements UFunc {

    public UFunc0(BiFunction<Float, Float, Float> fn) {
        this.fn = fn;
    }

    @Override
    public MArray call(MArray a, MArray out) {
        if (out == null) {
            out = new MArray(a.shape);
        }

        for (int i = 0; i < a.size; i++) {
            out.data[i] = this.fn.apply(a.data[i], out.data[i]);
        }

        return out;
    }

    @Override
    public MArray reduce(MArray a, int axis, MArray out) {
        assert (axis < a.shape.length) : "Axis must be less than dimension";

        if (out == null) {
            if (a.shape.length == 1) {
                out = new MArray(1);
            } else {
                int[] newShape = new int[a.shape.length - 1];
                for (int i = 0, j = 0; i < a.shape.length; i++) {
                    if (i != axis) {
                        newShape[j++] = a.shape[i];
                    }
                }
                out = new MArray(newShape);
            }
        }

        return this.reduceWalk(0, 0, 0, 0, a, axis, out);
    }

    @Override
    public MArray accumulate(MArray a, int axis, MArray out) {
        assert (axis < a.shape.length) : "Axis must be less than dimension";

        if (out == null) {
            out = new MArray(a.shape);
        }
        return this.accumulateWalk(0, 0, a, axis, out);
    }

    @Override
    public MArray inner(MArray a, MArray b, MArray out) {
        assert (a.shape.length == b.shape.length) : "Arrays must be same dimensions";

        if(out == null) {
            int[] newShape = new int[a.shape.length];
            for (int i = 0; i < a.shape.length; i++) {
                newShape[i] = Math.max(a.shape[i], b.shape[i]);
            }
            out = new MArray(newShape);
        }

        return this.innerWalk(0, 0, 0, a, b, out);
    }

    private MArray reduceWalk(final int n1, final int n2, final int o1, final int o2, final MArray arg, int axis,
            final MArray out) {
        final int arg_c = arg.shape.length - 1;
        final int arg_n = arg.shape[n1];
        final int arg_s = arg.stride[n1];

        int o1_i = o1;
        int o2_i = o2;

        if (n1 == arg_c) {
            if (n1 == axis) {
                for (int i = 0; i < arg_n; i++) {
                    out.data[o2] = fn.apply(arg.data[o1_i], out.data[o2]);
                    o1_i += arg_s;
                }
            } else {
                final int out_s = out.stride[n2];
                for (int i = 0; i < arg_n; i++) {
                    out.data[o2_i] = fn.apply(arg.data[o1_i], out.data[o2_i]);
                    o1_i += arg_s;
                    o2_i += out_s;
                }
            }
        } else {
            if (n1 == axis) {
                for (int i = 0; i < arg_n; i++) {
                    this.reduceWalk(n1 + 1, n2, o1_i, o2, arg, axis, out);
                    o1_i += arg_s;
                }
            } else {
                final int out_s = out.stride[n2];
                for (int i = 0; i < arg_n; i++) {
                    this.reduceWalk(n1 + 1, n2 + 1, o1_i, o2_i, arg, axis, out);
                    o1_i += arg_s;
                    o2_i += out_s;
                }
            }
        }

        return out;
    }

    private MArray accumulateWalk(final int n, final int o, final MArray arg, final int axis, final MArray out) {
        final int arg_c = arg.shape.length - 1;
        final int arg_n;
        final int arg_s;

        int o_i = o;

        if (n == arg_c) {
            if (axis < arg_c) {
                arg_n = arg.shape[axis];
                arg_s = arg.stride[axis];
            } else {
                arg_n = arg.shape[n];
                arg_s = arg.stride[n];
            }

            float p = 0.0f;
            for (int i = 0; i < arg_n; i++) {
                p += fn.apply(arg.data[o_i], out.data[o_i]);
                out.data[o_i] = p;
                o_i += arg_s;
            }
        } else {
            if (axis < arg_c && n == axis) {
                arg_n = arg.shape[arg_c];
                arg_s = arg.stride[arg_c];
            } else {
                arg_n = arg.shape[n];
                arg_s = arg.stride[n];
            }

            for (int i = 0; i < arg_n; i++) {
                this.accumulateWalk(n + 1, o_i, arg, axis, out);
                o_i += arg_s;
            }
        }

        return out;
    }

    private MArray innerWalk(final int n, final int o1, final int o2, final MArray arg1, final MArray arg2, final MArray out) {
        switch (arg1.shape.length - n) {
            case 1:
                if (arg1.shape[n] == arg2.shape[n]) {
                    final int c_i = arg1.shape[n];
                    final int s_i = arg1.stride[n];
                    int o1_i = o1;
                    int o2_i = o2;
                    for (int i = 0; i < c_i; i++) {
                        out.data[o1_i] = fn.apply(arg1.data[o1_i], arg2.data[o2_i]);
                        o1_i += s_i;
                        o2_i += s_i;
                    }
                } else {
                    final int c_i = Math.max(arg1.shape[n], arg2.shape[n]);
                    for (int i = 0; i < c_i; i++) {
                        final int o1_i = o1 + (i % arg1.shape[n]) * arg1.stride[n];
                        final int o2_i = o2 + (i % arg2.shape[n]) * arg2.stride[n];
                        out.data[o1_i] = fn.apply(arg1.data[o1_i], arg2.data[o2_i]);
                    }
                }
                break;

            case 2:
                if (arg1.shape[n] == arg2.shape[n] && arg1.shape[n + 1] == arg2.shape[n + 1]) {
                    final int c_i = arg1.shape[n];
                    final int c_ij = arg1.shape[n + 1];
                    final int s_i = arg1.stride[n];
                    final int s_ij = arg1.stride[n + 1];
                    int o1_i = o1;
                    int o2_i = o2;
                    for (int i = 0; i < c_i; i++) {
                        int o1_ij = o1_i;
                        int o2_ij = o2_i;
                        for (int j = 0; j < c_ij; j++) {
                            out.data[o1_ij] = fn.apply(arg1.data[o1_ij], arg2.data[o2_ij]);
                            o1_ij += s_ij;
                            o2_ij += s_ij;
                        }
                        o1_i += s_i;
                        o2_i += s_i;
                    }
                } else {
                    final int c_i = Math.max(arg1.shape[n], arg2.shape[n]);
                    final int c_ij = Math.max(arg1.shape[n + 1], arg2.shape[n + 1]);
                    for (int i = 0; i < c_i; i++) {
                        final int o1_i = o1 + (i % arg1.shape[n]) * arg1.stride[n];
                        final int o2_i = o2 + (i % arg2.shape[n]) * arg2.stride[n];
                        for (int j = 0; j < c_ij; j++) {
                            final int o1_ij = o1_i + (j % arg1.shape[n + 1]) * arg1.stride[n + 1];
                            final int o2_ij = o2_i + (j % arg2.shape[n + 1]) * arg2.stride[n + 1];
                            out.data[o1_ij] = fn.apply(arg1.data[o1_ij], arg2.data[o2_ij]);
                        }
                    }
                }
                break;

            default:
                final int c_i = Math.max(arg1.shape[n], arg2.shape[n]);
                for (int i = 0; i < c_i; i++) {
                    final int o1_i = o1 + (i % arg1.shape[n]) * arg1.stride[n];
                    final int o2_i = o2 + (i % arg2.shape[n]) * arg2.stride[n];
                    this.innerWalk(n + 1, o1_i, o2_i, arg1, arg2, out);
                }
        }

        return out;
    }

    private BiFunction<Float, Float, Float> fn;
}
