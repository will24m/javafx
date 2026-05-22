package com.jfxtutor.ui;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.AccessibleRole;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Editable code area with Java syntax highlighting. Exposes {@link #textProperty()}
 * so the host can wire it to a {@link com.jfxtutor.engine.runtime.SnippetRunner}.
 */
public class EditorPane extends VBox {

    private static final String[] KEYWORDS = {
            "abstract", "assert", "boolean", "break", "byte", "case", "catch",
            "char", "class", "const", "continue", "default", "do", "double",
            "else", "enum", "extends", "final", "finally", "float", "for",
            "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "package", "private",
            "protected", "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "true", "false", "null", "var"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "[()]";
    private static final String BRACE_PATTERN = "[{}]";
    private static final String BRACKET_PATTERN = "[\\[\\]]";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")");

    private final CodeArea codeArea;
    private final ReadOnlyStringWrapper text = new ReadOnlyStringWrapper("");

    public EditorPane() {
        getStyleClass().add("editor-pane");
        setId("editorPane");

        // RichTextFX's CodeArea gives us a real code editor surface with line
        // numbers and per-token styling, while still being a normal JavaFX Node.
        this.codeArea = new CodeArea();
        codeArea.getStyleClass().add("editor-textarea");
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setAccessibleRole(AccessibleRole.TEXT_AREA);
        codeArea.setAccessibleText("Java code editor");
        codeArea.setAccessibleHelp("Edit the Java snippet here. The preview updates automatically as you type.");

        // Keep our lightweight read-only text property in sync with the editor.
        // InteractiveIde listens to this property and hands changes to SnippetRunner.
        codeArea.textProperty().addListener((obs, old, val) -> {
            text.set(val);
            codeArea.setStyleSpans(0, computeHighlighting(val));
        });

        // VirtualizedScrollPane is the standard RichTextFX wrapper; it keeps
        // rendering fast for longer snippets by only laying out visible paragraphs.
        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(codeArea);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        getChildren().add(scrollPane);
    }

    /** Replace the editor contents. Used when switching lessons. */
    public void setText(String code) {
        codeArea.replaceText(code == null ? "" : code);
        codeArea.moveTo(0);
    }

    public ReadOnlyStringProperty textProperty() { return text.getReadOnlyProperty(); }
    public String getText() { return text.get(); }
    public CodeArea getCodeArea() { return codeArea; }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        // The combined regex walks the code once and labels each token with the
        // CSS class that app.css turns into syntax colors.
        Matcher matcher = PATTERN.matcher(text);
        int lastEnd = 0;
        StyleSpansBuilder<Collection<String>> spans = new StyleSpansBuilder<>();
        while (matcher.find()) {
            // Anything between two matches is plain source text, so it receives
            // an empty style list. Matched tokens receive exactly one style.
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" : null;
            spans.add(Collections.emptyList(), matcher.start() - lastEnd);
            spans.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastEnd = matcher.end();
        }
        // Finish with a trailing plain span after the final regex match.
        spans.add(Collections.emptyList(), text.length() - lastEnd);
        return spans.create();
    }
}
