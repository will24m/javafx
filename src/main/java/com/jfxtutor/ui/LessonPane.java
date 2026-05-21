package com.jfxtutor.ui;

import com.jfxtutor.data.curriculum.Lesson;
import com.jfxtutor.util.AppLog;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Document;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.parser.Parser;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

/**
 * Middle-left lesson reading pane.
 *
 * Lessons are stored as Markdown, but this app is intentionally pure JavaFX
 * instead of a WebView-based reader. This class parses CommonMark nodes and
 * translates the subset we use into Labels, TextFlows, and VBoxes styled by CSS.
 */
public class LessonPane extends VBox {

    private static final Parser MD = Parser.builder().build();

    private final VBox content;

    public LessonPane() {
        AppLog.info("lesson-pane", "Creating Markdown lesson renderer.");
        getStyleClass().add("lesson-pane");
        setId("lessonPane");

        this.content = new VBox(10);
        content.getStyleClass().add("lesson-content");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("lesson-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().add(scroll);
    }

    public void showLesson(Lesson lesson) {
        AppLog.info("lesson-pane", "Rendering lesson body for " + lesson.meta.id + ".");
        content.getChildren().clear();
        content.getChildren().addAll(buildNodes(lesson.meta.title, lesson.markdownBody));
    }

    /** Fallback when no lesson is loaded (empty curriculum or smoke test). */
    public void showLessonStub(String id, String titleText) {
        content.getChildren().clear();
        Label title = new Label(titleText);
        title.getStyleClass().add("lesson-title");
        title.setWrapText(true);
        content.getChildren().add(title);
        content.getChildren().add(plainParagraph("(Lesson body from " + id + ".md)"));
    }

    // ── rendering ────────────────────────────────────────────────────────────

    private List<javafx.scene.Node> buildNodes(String title, String markdown) {
        List<javafx.scene.Node> nodes = new ArrayList<>();

        // The title is rendered from frontmatter first so every lesson has a
        // consistent heading even if its Markdown body starts differently.
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("lesson-title");
        titleLabel.setWrapText(true);
        nodes.add(titleLabel);

        // CommonMark gives us an AST. We walk only top-level blocks here, and
        // render inline children later when building each TextFlow.
        Document doc = (Document) MD.parse(markdown);
        Node child = doc.getFirstChild();
        boolean skippedDuplicateTitle = false;
        while (child != null) {
            // Many Markdown files begin with "# Same Title". Since the app
            // already renders the frontmatter title, skip one matching H1 to
            // avoid showing a duplicate title.
            if (!skippedDuplicateTitle && isDuplicateTitleHeading(child, title)) {
                skippedDuplicateTitle = true;
                child = child.getNext();
                continue;
            }
            javafx.scene.Node rendered = renderBlock(child);
            if (rendered != null) nodes.add(rendered);
            child = child.getNext();
        }
        return nodes;
    }

    private javafx.scene.Node renderBlock(Node node) {
        // Block rendering answers "which JavaFX container should represent this
        // Markdown block?" Inline styling is handled by collectInline(...).
        if (node instanceof Heading h) {
            TextFlow flow = new TextFlow();
            flow.getStyleClass().add("md-heading");
            flow.setMaxWidth(Double.MAX_VALUE);
            double size = switch (h.getLevel()) { case 1 -> 18.0; case 2 -> 16.0; default -> 14.0; };
            collectInline(h.getFirstChild(), flow, size, FontWeight.BOLD, FontPosture.REGULAR);
            return flow;
        }
        if (node instanceof Paragraph p) {
            return inlineFlow(p.getFirstChild(), "md-paragraph");
        }
        if (node instanceof FencedCodeBlock fcb) {
            // Code blocks are rendered as a single Text node inside a TextFlow;
            // CSS gives the block its monospaced background and padding.
            Text t = new Text(fcb.getLiteral());
            t.getStyleClass().add("md-code-block");
            t.setFont(Font.font("Menlo", 12));
            TextFlow flow = new TextFlow(t);
            flow.getStyleClass().add("md-code-flow");
            return flow;
        }
        if (node instanceof BulletList bl) {
            // The lesson content currently uses simple one-paragraph list items.
            // Each list item becomes its own TextFlow so wrapping remains neat.
            VBox list = new VBox(4);
            Node item = bl.getFirstChild();
            while (item instanceof ListItem li) {
                TextFlow itemFlow = inlineFlow(
                        li.getFirstChild() instanceof Paragraph p ? p.getFirstChild() : null,
                        "md-list-item");
                itemFlow.getChildren().add(0, new Text("• "));
                list.getChildren().add(itemFlow);
                item = item.getNext();
            }
            return list;
        }
        if (node instanceof OrderedList ol) {
            // Ordered lists preserve CommonMark's explicit starting number.
            VBox list = new VBox(4);
            int number = ol.getMarkerStartNumber() == null ? 1 : ol.getMarkerStartNumber();
            Node item = ol.getFirstChild();
            while (item instanceof ListItem li) {
                TextFlow itemFlow = inlineFlow(
                        li.getFirstChild() instanceof Paragraph p ? p.getFirstChild() : null,
                        "md-list-item");
                itemFlow.getChildren().add(0, new Text(number++ + ". "));
                list.getChildren().add(itemFlow);
                item = item.getNext();
            }
            return list;
        }
        if (node instanceof BlockQuote quote) {
            // Block quotes can contain nested paragraphs/lists/headings, so they
            // recurse through renderBlock and collect the rendered children.
            VBox block = new VBox(6);
            block.getStyleClass().add("md-block-quote");
            Node child = quote.getFirstChild();
            while (child != null) {
                javafx.scene.Node rendered = renderBlock(child);
                if (rendered != null) block.getChildren().add(rendered);
                child = child.getNext();
            }
            return block;
        }
        return null;
    }

    private TextFlow inlineFlow(Node firstInline, String styleClass) {
        TextFlow flow = new TextFlow();
        flow.getStyleClass().add(styleClass);
        flow.setMaxWidth(Double.MAX_VALUE);
        collectInline(firstInline, flow, 13, FontWeight.NORMAL, FontPosture.REGULAR);
        return flow;
    }

    private void collectInline(Node node,
                               TextFlow into,
                               double size,
                               FontWeight weight,
                               FontPosture posture) {
        // Inline rendering carries font weight/posture through recursion.
        // For example, StrongEmphasis calls back in with BOLD while preserving
        // the current italic state.
        while (node != null) {
            if (node instanceof org.commonmark.node.Text t) {
                Text fx = new Text(t.getLiteral());
                fx.setFont(Font.font(null, weight, posture, size));
                fx.getStyleClass().add("md-text");
                into.getChildren().add(fx);
            } else if (node instanceof Code c) {
                Text fx = new Text(c.getLiteral());
                fx.setFont(Font.font("Menlo", FontWeight.NORMAL, FontPosture.REGULAR, size));
                fx.getStyleClass().add("md-inline-code");
                into.getChildren().add(fx);
            } else if (node instanceof Emphasis em) {
                collectInline(em.getFirstChild(), into, size, weight, FontPosture.ITALIC);
            } else if (node instanceof StrongEmphasis se) {
                collectInline(se.getFirstChild(), into, size, FontWeight.BOLD, posture);
            } else if (node instanceof Link link) {
                // The app currently renders links visually but does not navigate
                // to URLs. That keeps lessons self-contained inside the desktop app.
                Text fx = new Text(plainText(link.getFirstChild()));
                fx.setFont(Font.font(null, weight, posture, size));
                fx.setUnderline(true);
                fx.getStyleClass().add("md-link");
                into.getChildren().add(fx);
            } else if (node instanceof SoftLineBreak || node instanceof HardLineBreak) {
                into.getChildren().add(new Text(" "));
            }
            node = node.getNext();
        }
    }

    private TextFlow plainParagraph(String text) {
        Text t = new Text(text);
        t.getStyleClass().add("md-text");
        return new TextFlow(t);
    }

    private boolean isDuplicateTitleHeading(Node node, String title) {
        // Match the literal visible text, not Markdown syntax, so "# **Title**"
        // still counts as the same heading.
        return node instanceof Heading h
                && h.getLevel() == 1
                && plainText(h.getFirstChild()).trim().equals(title);
    }

    private String plainText(Node node) {
        // Flatten inline CommonMark nodes into user-visible text. This helper is
        // shared by duplicate-title detection and link rendering.
        StringBuilder sb = new StringBuilder();
        while (node != null) {
            if (node instanceof org.commonmark.node.Text text) {
                sb.append(text.getLiteral());
            } else if (node instanceof Code code) {
                sb.append(code.getLiteral());
            } else if (node instanceof SoftLineBreak || node instanceof HardLineBreak) {
                sb.append(' ');
            } else if (node.getFirstChild() != null) {
                sb.append(plainText(node.getFirstChild()));
            }
            node = node.getNext();
        }
        return sb.toString();
    }
}
