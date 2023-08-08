package com.github.romualdrousseau.shuju.geoalg;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class AlgebraBuilder {

    public Algebra build(String className, final int p, final int q, final int r) {
        className += "Class"; // Avoid name collision

        final String[] b = generateBasis(p + q + r);
        final int[] g = generateGrades(b);
        final int[] qf = generateQuadratricForm(p, q, r);
        final String[][] m = generateCayleyMatrix(qf, b);
        final String algebra = generateAlgebra(className, b, g, m);

        return compileAlgebra(className, algebra);
    }

    private String generateAlgebra(final String className, final String[] basis, final int[] grades,
            final String[][] cayley) {
        final StringBuffer source = new StringBuffer("public class " + className + " implements GA2D.Algebra {\n");
        compile_rev(source, grades);
        compile_conj(source, grades);
        compile_dual(source, grades);
        compile_ops(source, basis, '+', "add", true, true);
        compile_ops(source, basis, '-', "sub", true, true);
        compile_ops(source, basis, '*', "mul", true, false);
        compile_mul(source, basis, cayley);
        compile_ops(source, basis, '/', "div", true, false);
        compile_div(source, basis, cayley);
        compile_dot(source, basis, cayley);
        compile_join(source, basis, cayley);
        compile_meet(source, basis, cayley);
        compile_norm(source);
        compile_toString(source, basis);
        source.append("}");
        return source.toString();
    }

    private Algebra compileAlgebra(final String className, final String source) {
        try {
            final Path javaFile = File.createTempFile(className, ".java").toPath();
            Files.write(javaFile, source.getBytes(StandardCharsets.UTF_8));

            final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, javaFile.toFile().getAbsolutePath());
            final Path javaClass = javaFile.getParent().resolve(className + ".class");

            final URL classUrl = javaClass.getParent().toFile().toURI().toURL();
            final URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { classUrl });
            final Class<?> clazz = Class.forName(className, true, classLoader);
            return (Algebra) clazz.getDeclaredConstructor().newInstance();
        } catch (final Exception x) {
            throw new RuntimeException(x);
        }
    }

    private void compile_ops(final StringBuffer source, final String[] basis, final char op, final String name,
            final boolean commutatif,
            final boolean scalarOnly) {

        source.append("\n");
        source.append("\t").append("public float[] " + name + "(float[] a, float b) {").append("\n");
        source.append("\t\t").append("float[] r = new float[" + basis.length + "]").append(";\n");
        for (int i = 0; i < basis.length; i++) {
            source.append("\t\t").append("r[" + i + "] = a[" + i + "] " + op + " b").append(";\n");
        }
        source.append("\t\t").append("return r").append(";\n");
        source.append("\t").append("}").append("\n");

        if (commutatif) {
            source.append("\n");
            source.append("\t").append("public float[] " + name + "(float a, float[] b) {").append("\n");
            source.append("\t\t").append("float[] r = new float[" + basis.length + "]").append(";\n");
            for (int i = 0; i < basis.length; i++) {
                source.append("\t\t").append("r[" + i + "] = a " + op + " b[" + i + "]").append(";\n");
            }
            source.append("\t\t").append("return r").append(";\n");
            source.append("\t").append("}").append("\n");
        }

        if (scalarOnly) {
            source.append("\n");
            source.append("\t").append("public float[] " + name + "(float[] a, float[] b) {").append("\n");
            source.append("\t\t").append("float[] r = new float[" + basis.length + "]").append(";\n");
            for (int i = 0; i < basis.length; i++) {
                source.append("\t\tr").append("[" + i + "] = a[" + i + "] " + op + " b[" + i + "]").append(";\n");
            }
            source.append("\t\t").append("return r").append(";\n");
            source.append("\t").append("}").append("\n");
        }
    }

    private void compile_rev(final StringBuffer source, final int[] grades) {
        final String[] code = new String[grades.length];

        for (int i = 0; i < grades.length; i++) {

            final int n = grades[i] * (grades[i] - 1) / 2;

            if (code[i] == null) {
                code[i] = "r[" + i + "] = ";
            }
            if (n % 2 == 0) {
                code[i] += "a[" + i + "]";
            } else {
                code[i] += "-a[" + i + "]";
            }
        }

        source.append("\n");
        source.append("\t").append("public float[] rev(float[] a) {").append("\n");
        source.append("\t\t").append("float[] r = new float[" + grades.length + "]").append(";\n");
        for (final String line : code) {
            source.append("\t\t").append(line).append(";\n");
        }
        source.append("\t\t").append("return r").append(";\n");
        source.append("\t").append("}").append("\n");
    }

    private void compile_conj(final StringBuffer source, final int[] grades) {
        final String[] code = new String[grades.length];

        for (int i = 0; i < grades.length; i++) {

            final int n = grades[i] * (grades[i] - 1) / 2;

            if (code[i] == null) {
                code[i] = "r[" + i + "] = ";
            }
            if (grades[i] % 2 == n % 2) {
                code[i] += "a[" + i + "]";
            } else {
                code[i] += "-a[" + i + "]";
            }
        }

        source.append("\n");
        source.append("\t").append("public float[] conj(float[] a) {").append("\n");
        source.append("\t\t").append("float[] r = new float[" + grades.length + "]").append(";\n");
        for (final String line : code) {
            source.append("\t\t").append(line).append(";\n");
        }
        source.append("\t\t").append("return r").append(";\n");
        source.append("\t").append("}").append("\n");
    }

    private void compile_dual(final StringBuffer source, final int[] grades) {
        final String[] code = new String[grades.length];

        for (int i = 0; i < grades.length; i++) {
            if (code[i] == null) {
                code[i] = "r[" + i + "] = ";
            }
            code[i] += "a[" + (grades.length - i - 1) + "]";
        }

        source.append("\n");
        source.append("\t").append("public float[] dual(float[] a) {").append("\n");
        source.append("\t\t").append("float[] r = new float[" + grades.length + "]").append(";\n");
        for (final String line : code) {
            source.append("\t\t").append(line).append(";\n");
        }
        source.append("\t\t").append("return r").append(";\n");
        source.append("\t").append("}").append("\n");
    }

    private void compile_mul(final StringBuffer source, final String[] basis, final String[][] cayley) {
        final String[] code = new String[basis.length];

        for (int i = 0; i < cayley.length; i++) {
            for (int j = 0; j < cayley[i].length; j++) {

                String symbol = cayley[i][j];
                if (symbol.equals("0")) {
                    continue;
                }

                float c = 1;
                if (symbol.startsWith("-")) {
                    symbol = symbol.substring(1);
                    c *= -1;
                }

                final int k = java.util.Arrays.asList(basis).indexOf(symbol);

                if (code[k] == null) {
                    code[k] = "r[" + k + "] = ";
                    if (c < 1) {
                        code[k] += "-";
                    }
                } else {
                    if (c < 1) {
                        code[k] += " - ";
                    } else {
                        code[k] += " + ";
                    }
                }
                code[k] += "a[" + i + "] * b[" + j + "]";
            }
        }

        source.append("\n");
        source.append("\t").append("public float[] mul(float[] a, float[] b) {").append("\n");
        source.append("\t\t").append("float[] r = new float[" + basis.length + "]").append(";\n");
        for (final String line : code) {
            source.append("\t\t").append(line).append(";\n");
        }
        source.append("\t\t").append("return r").append(";\n");
        source.append("\t").append("}").append("\n");
    }

    private void compile_div(final StringBuffer source, final String[] basis, final String[][] cayley) {
        source.append("\n");
        source.append("\tpublic float[] div(float[] a, float[] b) {").append("\n");
        source.append("\t\tthrow new RuntimeException(\"Not implemented\");").append("\n");
        source.append("\t}").append("\n");
    }

    private void compile_join(final StringBuffer source, final String[] basis, final String[][] cayley) {
        final String[] code = new String[basis.length];

        for (int i = 0; i < cayley.length; i++) {
            for (int j = 0; j < cayley[i].length; j++) {

                if (!basis[i].equals("1") && !basis[j].equals("1") && is_axis_colinear(basis[i], basis[j])) {
                    continue;
                }

                String symbol = cayley[i][j];
                if (symbol.equals("0")) {
                    continue;
                }

                float c = 1;
                if (symbol.startsWith("-")) {
                    symbol = symbol.substring(1);
                    c *= -1;
                }

                final int k = java.util.Arrays.asList(basis).indexOf(symbol);

                if (code[k] == null) {
                    code[k] = "r[" + k + "] = ";
                    if (c < 1) {
                        code[k] += "-";
                    }
                } else {
                    if (c < 1) {
                        code[k] += " - ";
                    } else {
                        code[k] += " + ";
                    }
                }
                code[k] += "a[" + i + "] * b[" + j + "]";
            }
        }

        source.append("\n");
        source.append("\t").append("public float[] join(float[] a, float[] b) {").append("\n");
        source.append("\t\t").append("float[] r = new float[" + basis.length + "]").append(";\n");
        for (final String line : code) {
            source.append("\t\t").append(line).append(";\n");
        }
        source.append("\t\t").append("return r").append(";\n");
        source.append("\t").append("}").append("\n");
    }

    private void compile_dot(final StringBuffer source, final String[] basis, final String[][] cayley) {
        final String[] code = new String[basis.length];

        for (int i = 0; i < cayley.length; i++) {
            for (int j = 0; j < cayley[i].length; j++) {

                if (!basis[i].equals("1") && !basis[j].equals("1") && is_axis_orthogonal(basis[i], basis[j])) {
                    continue;
                }

                String symbol = cayley[i][j];
                if (symbol.equals("0")) {
                    continue;
                }

                float sign = 1;
                if (symbol.startsWith("-")) {
                    symbol = symbol.substring(1);
                    sign *= -1;
                }

                final int k = java.util.Arrays.asList(basis).indexOf(symbol);
                if (code[k] == null) {
                    code[k] = "r[" + k + "] = " + ((sign < 1) ? "-" : "");
                } else {
                    code[k] += (sign < 1) ? " - " : " + ";
                }
                code[k] += "a[" + i + "] * b[" + j + "]";
            }
        }

        source.append("\n");
        source.append("\t").append("public float[] dot(float[] a, float[] b) {").append("\n");
        source.append("\t\t").append("float[] r = new float[" + basis.length + "]").append(";\n");
        for (final String line : code) {
            source.append("\t\t").append(line).append(";\n");
        }
        source.append("\t\t").append("return r").append(";\n");
        source.append("\t").append("}").append("\n");
    }

    private void compile_meet(final StringBuffer source, final String[] basis, final String[][] cayley) {
        final String[] code = new String[basis.length];

        for (int i = 0; i < cayley.length; i++) {
            for (int j = 0; j < cayley[i].length; j++) {

                if (!basis[i].equals("1") && !basis[j].equals("1") && is_axis_colinear(basis[i], basis[j])) {
                    continue;
                }

                String symbol = cayley[i][j];
                if (symbol.equals("0")) {
                    continue;
                }

                float sign = 1;
                if (symbol.startsWith("-")) {
                    symbol = symbol.substring(1);
                    sign *= -1;
                }

                final int k = java.util.Arrays.asList(basis).indexOf(symbol);
                if (code[k] == null) {
                    code[k] = "r[" + (basis.length - k - 1) + "] = " + ((sign < 1) ? "-" : "");
                } else {
                    code[k] += (sign < 1) ? " - " : " + ";
                }
                code[k] += "a[" + (basis.length - i - 1) + "] * b[" + (basis.length - j - 1) + "]";
            }
        }

        source.append("\n");
        source.append("\t").append("public float[] meet(float[] a, float[] b) {").append("\n");
        source.append("\t\t").append("float[] r = new float[" + basis.length + "]").append(";\n");
        for (final String line : code) {
            source.append("\t\t").append(line).append(";\n");
        }
        source.append("\t\t").append("return r").append(";\n");
        source.append("\t").append("}").append("\n");
    }

    private void compile_norm(final StringBuffer source) {
        source.append("\n");
        source.append("\t").append("public float norm(float[] a) {").append("\n");
        source.append("\t\t").append("return (float) Math.sqrt(mul(a, conj(a))[0])").append(";\n");
        source.append("\t").append("}").append("\n");
    }

    private void compile_toString(final StringBuffer source, final String[] basis) {

        source.append("\n");
        source.append("\t").append("private final static String[] BASIS = { ");
        boolean firstPass = true;
        for (final String token : basis) {
            if (firstPass) {
                source.append("\"" + token + "\"");
                firstPass = false;
            } else {
                source.append(", \"" + token + "\"");
            }
        }
        source.append(" }").append(";\n");

        source.append("\t").append("public String toString(float[] a) {").append("\n");
        source.append("\t\t").append("StringBuffer output = new StringBuffer()").append(";\n");
        source.append("\t\t").append("boolean firstPass = true").append(";\n");
        source.append("\t\t").append("for (int i = 0; i < a.length; i++) {").append("\n");
        source.append("\t\t\t").append("if (a[i] != 0) {").append("\n");
        source.append("\t\t\t\t").append("if (firstPass) {").append("\n");
        source.append("\t\t\t\t\t").append("output.append(a[i] + BASIS[i])").append(";\n");
        source.append("\t\t\t\t\t").append("firstPass = false").append(";\n");
        source.append("\t\t\t\t").append("} else {").append("\n");
        source.append("\t\t\t\t\t").append("output.append(\" \" + a[i] + BASIS[i])").append(";\n");
        source.append("\t\t\t\t").append("}").append("\n");
        source.append("\t\t\t").append("}").append("\n");
        source.append("\t\t").append("}").append("\n");
        source.append("\t\t").append("return output.toString()").append(";\n");
        source.append("\t").append("}").append("\n");
    }

    private String[] generateBasis(final int n) {
        final int k = (int) Math.floor(Math.pow(2, n));

        final String[] result = new String[k];

        for (int i = 0; i < k; i++) {
            result[i] = "";
            for (int j = 0; j < n; j++) {
                result[i] += (((i >> j) & 1) == 1) ? Integer.toString(j + 1) : "";
            }
        }

        Arrays.sort(result, 1, k, (a, b) -> Integer.parseInt(a) - Integer.parseInt(b));

        result[0] = "1";
        for (int i = 1; i < k; i++) {
            result[i] = "e" + result[i];
        }

        return result;
    }

    private int[] generateGrades(final String[] basis) {
        final int[] result = new int[basis.length];

        result[0] = 0;
        for (int i = 1; i < basis.length; i++) {
            result[i] = basis[i].length() - 1;
        }

        return result;
    }

    private int[] generateQuadratricForm(final int p, final int q, final int r) {
        final int[] result = new int[p + q + r];

        for (int i = 0; i < r; i++) {
            result[i] = 0;
        }
        for (int i = r; i < r + p; i++) {
            result[i] = 1;
        }
        for (int i = r + p; i < r + p + q; i++) {
            result[i] = -1;
        }

        return result;
    }

    private String[][] generateCayleyMatrix(final int[] quadraticForm, final String[] basis) {
        final String[][] result = new String[basis.length][basis.length];

        for (int i = 0; i < basis.length; i++) {
            for (int j = 0; j < basis.length; j++) {
                result[i][j] = basis[i].equals("1") ? basis[j]
                        : basis[j].equals("1") ? basis[i] : simplify(basis[i], basis[j], quadraticForm);
            }
        }

        return result;
    }

    private String simplify(final String a, final String b, final int[] quadraticForm) {
        final char[] cmps = (a.substring(1) + b.substring(1)).toCharArray();
        int sign = 1;

        for (int i = 0; i < cmps.length; i++) {
            for (int j = i + 1; j < cmps.length; j++) {
                if (cmps[i] > cmps[j]) {
                    final char temp = cmps[i];
                    cmps[i] = cmps[j];
                    cmps[j] = temp;
                    sign *= -1;
                }
            }
        }

        for (int i = 1; i < cmps.length; i++) {
            final int x = cmps[i - 1] - '1';
            final int y = cmps[i] - '1';
            if (x == y) {
                if (quadraticForm[x] == 0) {
                    sign = 0;
                } else if (quadraticForm[x] == -1) {
                    sign *= -1;
                }
                i++;
            }
        }

        if (sign == 0) {
            return "0";
        } else {
            final String s = new String(cmps).replaceAll("(.)\\1", "");
            return ((sign == 1) ? "" : "-") + (s.equals("") ? "1" : "e" + s);
        }
    }

    private boolean is_axis_colinear(String a, String b) {

        a = a.substring(1);
        b = b.substring(1);

        if (a.equals(b)) {
            return true;
        }

        boolean found = false;
        if (a.length() < b.length()) {
            for (int i = 0; i < a.length(); i++) {
                found |= b.indexOf(a.charAt(i)) >= 0;
            }
        } else {
            for (int i = 0; i < b.length(); i++) {
                found |= a.indexOf(b.charAt(i)) >= 0;
            }
        }
        return found;
    }

    private boolean is_axis_orthogonal(String a, String b) {

        a = a.substring(1);
        b = b.substring(1);

        if (a.equals(b)) {
            return false;
        }

        boolean found = true;
        if (a.length() < b.length()) {
            for (int i = 0; i < a.length(); i++) {
                found &= b.indexOf(a.charAt(i)) >= 0;
            }
        } else {
            for (int i = 0; i < b.length(); i++) {
                found &= a.indexOf(b.charAt(i)) >= 0;
            }
        }
        return !found;
    }
}
