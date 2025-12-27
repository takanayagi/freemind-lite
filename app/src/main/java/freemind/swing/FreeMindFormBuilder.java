/*
 * This file is based on JGoodies DefaultFormBuilder, but heavily modified for FreeMind.
 *
 * Original Copyright (c) 2001-2005 JGoodies Karsten Lentzsch. All Rights Reserved.
 */

package freemind.swing;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import com.jgoodies.forms.FormsSetup;
import com.jgoodies.forms.internal.AbstractFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

/**
 * A FreeMind-specific FormBuilder that simplifies the construction of forms.
 *
 * @see com.jgoodies.forms.builder.DefaultFormBuilder
 */
public class FreeMindFormBuilder extends AbstractFormBuilder<FreeMindFormBuilder> {

	/**
	 * Constructs a {@code FreeMindFormBuilder} for the given layout.
	 *
	 * @param layout the {@code FormLayout} to be used
	 *
	 * @throws NullPointerException if {@code layout} is {@code null}
	 */
	public FreeMindFormBuilder(FormLayout layout) {
		super(layout, new JPanel(null));
		opaque(FormsSetup.getOpaqueDefault());
	}

	/**
	 * Returns the panel used to build the form. Intended to return the panel in build methods.
	 *
	 * @return the panel used by this builder to build the form
	 *
	 * @see com.jgoodies.forms.builder.PanelBuilder#build()
	 */
	@Override
	public JPanel build() {
		return getPanel();
	}

	/**
	 * Adds a component to the panel using the default constraints with a column span of 1. Then
	 * proceeds to the next data column.
	 *
	 * @param component the component to add
	 *
	 * @see com.jgoodies.forms.builder.DefaultFormBuilder#append(Component)
	 */
	public void append(Component component) {
		append(component, 1);
	}

	/**
	 * Adds a component to the panel using the default constraints with the given columnSpan.
	 * Proceeds to the next data column.
	 *
	 * @param component the component to append
	 * @param columnSpan the column span used to add
	 *
	 * @see com.jgoodies.forms.builder.DefaultFormBuilder#append(Component, int)
	 */
	public void append(Component component, int columnSpan) {
		ensureCursorColumnInGrid();
		ensureHasGapRow(FormSpecs.LINE_GAP_ROWSPEC);
		ensureHasComponentLine();

		add(component, createLeftAdjustedConstraints(columnSpan));
		nextColumn(columnSpan + 1);
	}

	/**
	 * Adds a text label to the panel and proceeds to the next column.
	 *
	 * @param textWithMnemonic the label's text - may mark a mnemonic
	 * @return the added label
	 *
	 * @see com.jgoodies.forms.builder.DefaultFormBuilder#append(String)
	 */
	public JLabel append(String textWithMnemonic) {
		JLabel label = getComponentFactory().createLabel(textWithMnemonic);
		append(label);
		return label;
	}

	/**
	 * Adds a text label and component to the panel. Then proceeds to the next data column.
	 * <p>
	 *
	 * The created label is labeling the given component; so the component gets the focus if the
	 * (optional) label mnemonic is pressed.
	 *
	 * @param textWithMnemonic the label's text - may mark a mnemonic
	 * @param component the component to add
	 * @return the added label
	 *
	 * @see com.jgoodies.forms.builder.DefaultFormBuilder#append(String, Component)
	 */
	public JLabel append(String textWithMnemonic, Component component) {
		JLabel label = append(textWithMnemonic);
		label.setLabelFor(component);
		append(component, 1);
		return label;
	}

	/**
	 * Adds a separator without text that spans all columns.
	 *
	 * @return the added titled separator
	 *
	 * @see com.jgoodies.forms.builder.DefaultFormBuilder#appendSeparator()
	 */
	public JComponent appendSeparator() {
		return appendSeparator("");
	}

	/**
	 * Adds a separator with the given text that spans all columns.
	 *
	 * @param textWithMnemonic the separator title text
	 * @return the added titled separator
	 *
	 * @see com.jgoodies.forms.builder.DefaultFormBuilder#appendSeparator(String)
	 */
	public JComponent appendSeparator(String textWithMnemonic) {
		ensureCursorColumnInGrid();
		ensureHasGapRow(FormSpecs.PARAGRAPH_GAP_ROWSPEC);
		ensureHasComponentLine();

		setColumn(getLeadingColumn());
		int columnSpan = getColumnCount();
		setColumnSpan(columnSpan);
		JComponent titledSeparator =
				getComponentFactory().createSeparator(textWithMnemonic, SwingConstants.LEFT);
		CellConstraints constraints = createLeftAdjustedConstraints(columnSpan);
		add(titledSeparator, constraints);
		setColumnSpan(1);
		nextColumn(columnSpan);
		return titledSeparator;
	}

	/**
	 * Ensures that the cursor is in the grid. In case it's beyond the form's right hand side, the
	 * cursor is moved to the leading column of the next line.
	 *
	 * @see com.jgoodies.forms.builder.DefaultFormBuilder#ensureCursorColumnInGrid()
	 */
	private void ensureCursorColumnInGrid() {
		if (isLeftToRight() && getColumn() > getColumnCount()
				|| !isLeftToRight() && getColumn() < 1) {
			nextLine();
		}
	}

	/**
	 * Ensures that we have a gap row before the next component row. Checks if the current row is
	 * the given {@code RowSpec} and appends this row spec if necessary.
	 *
	 * @param gapRowSpec the row specification to check for
	 *
	 * @see com.jgoodies.forms.builder.DefaultFormBuilder#ensureHasGapRow(RowSpec)
	 */
	private void ensureHasGapRow(RowSpec gapRowSpec) {
		if (getRow() == 1 || getRow() <= getRowCount()) {
			return;
		}
		if (getRow() <= getRowCount()) {
			RowSpec rowSpec = getLayout().getRowSpec(getRow());
			if (rowSpec == gapRowSpec) {
				return;
			}
		}
		appendRow(gapRowSpec);
		nextLine();
	}

	/**
	 * Ensures that the form has a component row. Adds a component row if the cursor is beyond the
	 * form's bottom.
	 *
	 * @see com.jgoodies.forms.builder.DefaultFormBuilder#ensureHasComponentLine()
	 */
	private void ensureHasComponentLine() {
		if (getRow() <= getRowCount()) {
			return;
		}
		appendRow(FormSpecs.PREF_ROWSPEC);
	}

}
