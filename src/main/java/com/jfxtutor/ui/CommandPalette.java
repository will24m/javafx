package com.jfxtutor.ui;

import com.jfxtutor.data.curriculum.Lesson;
import com.jfxtutor.util.AppLog;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Keyboard-driven lesson picker opened by Cmd+Shift+P.
 *
 * Implemented as a {@link Popup} that positions itself near the top-center of
 * the owner window. Typing filters the lesson list in real time; Enter or a
 * double-click opens the selected lesson. Escape closes without action.
 */
public class CommandPalette {

    private static final int MAX_RESULTS = 12;
    private static final double PALETTE_WIDTH = 520;

    private final Popup popup;
    private final TextField input;
    private final ListView<Lesson> results;
    private List<Lesson> allLessons = List.of();
    private Consumer<Lesson> onSelect = l -> {};

    public CommandPalette() {
        this.input = new TextField();
        input.setPromptText("Go to lesson…");
        input.getStyleClass().add("palette-input");
        input.setAccessibleRole(AccessibleRole.TEXT_FIELD);
        input.setAccessibleText("Lesson search");
        input.setAccessibleHelp("Type to search lessons by title, ID, or tier. Press Enter to open.");

        this.results = new ListView<>();
        results.getStyleClass().add("palette-results");
        results.setPrefHeight(220);
        results.setAccessibleRole(AccessibleRole.LIST_VIEW);
        results.setAccessibleText("Matching lessons");

        results.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Lesson lesson, boolean empty) {
                super.updateItem(lesson, empty);
                if (empty || lesson == null) {
                    setText(null);
                } else {
                    setText(String.format("%03d   %s   [%s]",
                            lesson.meta.order, lesson.meta.title, lesson.meta.tier));
                    setAccessibleText(lesson.meta.title + ", " + lesson.meta.tier + " tier");
                }
            }
        });

        Label hint = new Label("↑↓ navigate   Enter open   Esc dismiss");
        hint.getStyleClass().add("palette-hint");
        hint.setAccessibleRole(AccessibleRole.TEXT);

        VBox box = new VBox(6, input, results, hint);
        box.getStyleClass().add("command-palette");
        box.setPadding(new Insets(10));
        box.setPrefWidth(PALETTE_WIDTH);

        this.popup = new Popup();
        popup.setAutoHide(true);
        popup.setAutoFix(true);
        popup.getContent().add(box);
        popup.setOnHiding(e -> input.clear());

        // Filtering
        input.textProperty().addListener((obs, old, val) -> updateResults(val));

        // Keyboard navigation inside the text field
        input.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case DOWN -> {
                    int sel = results.getSelectionModel().getSelectedIndex();
                    if (sel < results.getItems().size() - 1) {
                        results.getSelectionModel().select(sel + 1);
                        results.scrollTo(sel + 1);
                    }
                    e.consume();
                }
                case UP -> {
                    int sel = results.getSelectionModel().getSelectedIndex();
                    if (sel > 0) {
                        results.getSelectionModel().select(sel - 1);
                        results.scrollTo(sel - 1);
                    }
                    e.consume();
                }
                case ENTER -> {
                    Lesson sel = results.getSelectionModel().getSelectedItem();
                    if (sel != null) open(sel);
                    e.consume();
                }
                case ESCAPE -> {
                    popup.hide();
                    e.consume();
                }
                default -> {}
            }
        });

        // Double-click
        results.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Lesson sel = results.getSelectionModel().getSelectedItem();
                if (sel != null) open(sel);
            }
        });

        // Enter in the list
        results.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                Lesson sel = results.getSelectionModel().getSelectedItem();
                if (sel != null) open(sel);
                e.consume();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                popup.hide();
                e.consume();
            }
        });
    }

    /** Provide the full lesson list used for fuzzy search. */
    public void setLessons(List<Lesson> lessons) {
        this.allLessons = lessons != null ? lessons : List.of();
    }

    /** Called when the user picks a lesson. */
    public void setOnSelect(Consumer<Lesson> callback) {
        this.onSelect = callback != null ? callback : l -> {};
    }

    /** Show the palette anchored to the top-center of {@code owner}. */
    public void show(Window owner) {
        if (popup.isShowing()) { popup.hide(); return; }
        updateResults("");
        double x = owner.getX() + (owner.getWidth() - PALETTE_WIDTH) / 2.0;
        double y = owner.getY() + 80;
        popup.show(owner, x, y);
        Platform.runLater(input::requestFocus);
        AppLog.info("palette", "Command palette shown.");
    }

    public void hide() {
        popup.hide();
    }

    public boolean isShowing() {
        return popup.isShowing();
    }

    // ── internals ─────────────────────────────────────────────────────────────

    private void updateResults(String query) {
        List<Lesson> filtered;
        if (query == null || query.isBlank()) {
            filtered = allLessons.size() <= MAX_RESULTS
                    ? allLessons
                    : allLessons.subList(0, MAX_RESULTS);
        } else {
            String needle = query.toLowerCase(Locale.ROOT).trim();
            filtered = allLessons.stream()
                    .filter(l -> l.meta.title.toLowerCase(Locale.ROOT).contains(needle)
                            || l.meta.id.toLowerCase(Locale.ROOT).contains(needle)
                            || l.meta.tier.toLowerCase(Locale.ROOT).contains(needle))
                    .limit(MAX_RESULTS)
                    .toList();
        }
        results.getItems().setAll(filtered);
        if (!filtered.isEmpty()) {
            results.getSelectionModel().selectFirst();
        }
    }

    private void open(Lesson lesson) {
        popup.hide();
        AppLog.info("palette", "Opening lesson from palette: " + lesson.meta.id);
        onSelect.accept(lesson);
    }
}
