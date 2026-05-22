package com.jfxtutor.engine.challenge;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses assertion DSL strings into {@link ChallengeAssertion} instances.
 *
 * <p>Grammar (informal):
 * <pre>
 *   assertion  ::= funcName "(" argList? ")"
 *   argList    ::= arg ("," arg)*
 *   arg        ::= name "=" value
 *   value      ::= QUOTED_STRING | IDENTIFIER | INTEGER
 * </pre>
 *
 * <p>Examples:
 * <pre>
 *   containsNodeOfType(Button)
 *   containsLabeledWithText(text="Reset")
 *   containsLabeledInside(text="Submit", parentType=VBox)
 *   countOfType(RadioButton, n=3)
 *   parentChain(child=HBox, ancestor=VBox)
 *   cssClassPresent(selector=".btn", cssClass="primary")
 * </pre>
 */
public final class AssertionParser {

    /** Matches the outer function call: {@code funcName(raw args)} */
    private static final Pattern CALL = Pattern.compile(
            "^\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*\\((.*)\\)\\s*$",
            Pattern.DOTALL);

    /** Matches one named arg: {@code key=value} or a bare positional value. */
    private static final Pattern ARG = Pattern.compile(
            "(?:([A-Za-z_][A-Za-z0-9_]*)\\s*=\\s*)?" +
            "(?:\"((?:[^\"\\\\]|\\\\.)*)\"|([A-Za-z_][A-Za-z0-9_.]*)|(-?\\d+))");

    private AssertionParser() {}

    /**
     * Parses {@code dsl} and returns the corresponding {@link ChallengeAssertion}.
     *
     * @throws IllegalArgumentException when the string cannot be parsed or references
     *                                  an unknown function or class name
     */
    public static ChallengeAssertion parse(String dsl) {
        if (dsl == null || dsl.isBlank()) {
            throw new IllegalArgumentException("Empty assertion string.");
        }

        Matcher callMatcher = CALL.matcher(dsl.trim());
        if (!callMatcher.matches()) {
            throw new IllegalArgumentException(
                    "Assertion does not match 'funcName(...)' pattern: " + dsl);
        }

        String func = callMatcher.group(1);
        String rawArgs = callMatcher.group(2).trim();
        Map<String, String> named = new LinkedHashMap<>();
        String[] positional = parseArgs(rawArgs, named);

        return switch (func) {
            case "containsNodeOfType" -> {
                String className = positional.length > 0 ? positional[0]
                        : named.get("type");
                yield Assertions.containsNodeOfType(resolveClass(className, dsl));
            }
            case "containsLabeledWithText" -> {
                String text = named.containsKey("text") ? named.get("text")
                        : positional.length > 0 ? positional[0] : null;
                requireNotNull(text, "text", dsl);
                yield Assertions.containsLabeledWithText(text);
            }
            case "containsLabeledInside" -> {
                String text = named.containsKey("text") ? named.get("text")
                        : positional.length > 0 ? positional[0] : null;
                String parentType = named.containsKey("parentType") ? named.get("parentType")
                        : positional.length > 1 ? positional[1] : null;
                requireNotNull(text, "text", dsl);
                requireNotNull(parentType, "parentType", dsl);
                yield Assertions.containsLabeledInside(text, resolveClass(parentType, dsl));
            }
            case "cssClassPresent" -> {
                String selector = named.containsKey("selector") ? named.get("selector")
                        : positional.length > 0 ? positional[0] : null;
                String cssClass = named.containsKey("cssClass") ? named.get("cssClass")
                        : positional.length > 1 ? positional[1] : null;
                requireNotNull(selector, "selector", dsl);
                requireNotNull(cssClass, "cssClass", dsl);
                yield Assertions.cssClassPresent(selector, cssClass);
            }
            case "countOfType" -> {
                String className = named.containsKey("type") ? named.get("type")
                        : positional.length > 0 ? positional[0] : null;
                String nStr = named.containsKey("n") ? named.get("n")
                        : positional.length > 1 ? positional[1] : null;
                requireNotNull(className, "type", dsl);
                requireNotNull(nStr, "n", dsl);
                int n;
                try { n = Integer.parseInt(nStr); }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "countOfType: 'n' must be an integer, got: " + nStr);
                }
                yield Assertions.countOfType(resolveClass(className, dsl), n);
            }
            case "parentChain" -> {
                String child = named.containsKey("child") ? named.get("child")
                        : positional.length > 0 ? positional[0] : null;
                String ancestor = named.containsKey("ancestor") ? named.get("ancestor")
                        : positional.length > 1 ? positional[1] : null;
                requireNotNull(child, "child", dsl);
                requireNotNull(ancestor, "ancestor", dsl);
                yield Assertions.parentChain(
                        resolveClass(child, dsl), resolveClass(ancestor, dsl));
            }
            default -> throw new IllegalArgumentException(
                    "Unknown assertion function '" + func + "' in: " + dsl);
        };
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private static Class<?> resolveClass(String name, String dsl) {
        return ClassResolver.resolve(name).orElseThrow(() -> {
            String hint = ClassResolver.didYouMean(name);
            String msg = "Unknown class '" + name + "' in: " + dsl;
            if (!hint.isBlank()) msg += " — " + hint;
            return new IllegalArgumentException(msg);
        });
    }

    private static void requireNotNull(String value, String param, String dsl) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Missing required parameter '" + param + "' in: " + dsl);
        }
    }

    /**
     * Splits the raw argument body into positional values (array) and
     * named values (map). Returns positional values in encounter order.
     */
    private static String[] parseArgs(String raw, Map<String, String> named) {
        java.util.List<String> positional = new java.util.ArrayList<>();
        if (raw.isBlank()) return new String[0];

        Matcher m = ARG.matcher(raw);
        while (m.find()) {
            String key = m.group(1);
            // groups 2=quoted, 3=identifier, 4=integer
            String value = m.group(2) != null ? m.group(2)
                    : m.group(3) != null ? m.group(3)
                    : m.group(4);
            if (value == null) continue;
            if (key != null) {
                named.put(key, value);
            } else {
                positional.add(value);
            }
        }
        return positional.toArray(new String[0]);
    }
}
