/*
 * ---------------------------------------------------------------------------------------------------------------------
 *                            Brigham Young University - Project MEDIA StillFace DataCenter
 * ---------------------------------------------------------------------------------------------------------------------
 * The contents of this file contribute to the ProjectMEDIA DataCenter for managing and analyzing data obtained from the
 * results of StillFace observational experiments.
 *
 * This code is free, open-source software. You may distribute or modify the code, but Brigham Young University or any
 * parties involved in the development and production of this code as downloaded from the remote repository are not
 * responsible for any repercussions that come as a result of the modifications.
 */
package com.byu.pmedia.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.lang.reflect.Field;

/**
 * NumericTextFieldTableCell
 * Provides a method of restricting the edits to a text field table cell to only numeric characters.
 *
 * @param <S> The class representing the type of data displayed in the cell
 * @param <T> The wrapper class the formatted text should be restricted to
 *
 * @author kleopatra on GitHub
 */
public class NumericTextFieldTableCell<S, T> extends TextFieldTableCell<S, T> {

    private TextFormatter<T> formatter;
    private TextField textAlias;

    public NumericTextFieldTableCell() {
        this((StringConverter<T>)null);
    }

    public NumericTextFieldTableCell(StringConverter<T> converter) {
        super(converter);
    }

    public NumericTextFieldTableCell(TextFormatter<T> formatter) {
        super(formatter.getValueConverter());
        this.formatter = formatter;
    }

    /**
     * Overridden to install the formatter. <p>
     *
     * Beware: implementation detail! super creates and configures
     * the textField lazy on first access, so have to install after
     * calling super.
     */
    @Override
    public void startEdit() {
        super.startEdit();
        installFormatter();
    }

    private void installFormatter() {
        if (formatter != null && isEditing() && textAlias == null) {
            textAlias = invokeTextField();
            textAlias.setTextFormatter(formatter);
        }
    }

    private TextField invokeTextField() {
        Class<?> clazz = TextFieldTableCell.class;
        try {
            Field field = clazz.getDeclaredField("textField");
            field.setAccessible(true);
            return (TextField) field.get(this);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
