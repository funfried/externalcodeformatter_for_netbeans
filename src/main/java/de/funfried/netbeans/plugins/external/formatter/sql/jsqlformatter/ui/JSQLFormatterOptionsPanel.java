/*
 * Copyright (c) 2021 Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 */

package de.funfried.netbeans.plugins.external.formatter.sql.jsqlformatter.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.netbeans.api.project.Project;

import com.manticore.jsqlformatter.JSQLFormatter;
import com.manticore.jsqlformatter.JSQLFormatter.FormattingOption;
import com.manticore.jsqlformatter.JSQLFormatter.OutputFormat;
import com.manticore.jsqlformatter.JSQLFormatter.Separation;
import com.manticore.jsqlformatter.JSQLFormatter.Spelling;
import com.manticore.jsqlformatter.JSQLFormatter.SquaredBracketQuotation;

import de.funfried.netbeans.plugins.external.formatter.ui.options.AbstractFormatterOptionsPanel;

/**
 * JSQLFormatter implementation of the {@link AbstractFormatterOptionsPanel}.
 *
 * @author Andreas Reichel <a href="mailto:andreas@manticore-projects.com">andreas@manticore-projects.com</a>
 */

public class JSQLFormatterOptionsPanel extends AbstractFormatterOptionsPanel {
	private static final Logger LOGGER = Logger.getLogger(JSQLFormatterOptionsPanel.class.getName());

	private final JComboBox<OutputFormat> outputFormatBox = new JComboBox<>(OutputFormat.values());

	private final JComboBox<Spelling> keywordSpellingBox = new JComboBox<>(Spelling.values());

	private final JComboBox<Spelling> functionSpellingBox = new JComboBox<>(Spelling.values());

	private final JComboBox<Spelling> objectSpellingBox = new JComboBox<>(Spelling.values());

	private final JComboBox<SquaredBracketQuotation> squaredBracketQuotationBox = new JComboBox<>(SquaredBracketQuotation.values());

	private final JComboBox<Separation> separationBox = new JComboBox<>(Separation.values());

	private final JTextField indentWidthField = new JTextField(3);

	private final GridBagLayout layout = new GridBagLayout();

	private final Color validBackgroundColor = indentWidthField.getBackground();

	/**
	 * Creates new form {@link JSQLFormatterOptionsPanel}.
	 *
	 * @param project the {@link Project} if the panel is used to modify project
	 *        specific settings, otherwise {@code null}
	 */
	public JSQLFormatterOptionsPanel(Project project) {
		super(project);

		buildUI();
	}

	private final ItemListener itemListener = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED)
				fireChangedListener();
		}
	};

	private void buildUI() {
		JPanel panel = new JPanel(layout);

		outputFormatBox.setSelectedItem(JSQLFormatter.getOutputFormat());
		outputFormatBox.addItemListener(itemListener);

		keywordSpellingBox.setSelectedItem(JSQLFormatter.getKeywordSpelling());
		keywordSpellingBox.addItemListener(itemListener);

		functionSpellingBox.setSelectedItem(JSQLFormatter.getFunctionSpelling());
		functionSpellingBox.addItemListener(itemListener);

		objectSpellingBox.setSelectedItem(JSQLFormatter.getObjectSpelling());
		objectSpellingBox.addItemListener(itemListener);

		indentWidthField.setText(Integer.toString(JSQLFormatter.getIndentWidth()));
		indentWidthField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				fireChangedListener();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				fireChangedListener();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				fireChangedListener();
			}

		});

		separationBox.setSelectedItem(JSQLFormatter.getSeparation());
		separationBox.addItemListener(itemListener);

		squaredBracketQuotationBox.setSelectedItem(JSQLFormatter.getSquaredBracketQuotation());
		squaredBracketQuotationBox.addItemListener(itemListener);

		indentWidthField.setInputVerifier(
				new InputVerifier() {
					@Override
					public boolean verify(JComponent input) {
						return valid();
					}
				});

		GridBagConstraints constraints = new GridBagConstraints(
				0,
				0,
				1,
				1,
				1.0,
				1.0,
				GridBagConstraints.BASELINE_TRAILING,
				GridBagConstraints.NONE,
				new Insets(4, 6, 2, 6),
				0,
				0);

		// --------------------------------------------------------------------------------------------

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 6;
		constraints.insets.left = 12;
		constraints.insets.right = 12;
		panel.add(new JSeparator(JSeparator.HORIZONTAL), constraints);

		constraints.gridy++;
		JLabel label = new JLabel("Output");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		panel.add(label, constraints);

		constraints.gridx = 0;
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets.left = 12;
		constraints.insets.right = 2;

		label = new JLabel("Format:");
		label.setLabelFor(outputFormatBox);
		panel.add(label, constraints);

		constraints.gridx++;
		constraints.anchor = GridBagConstraints.BASELINE_LEADING;
		constraints.insets.left = 0;
		panel.add(outputFormatBox, constraints);

		// --------------------------------------------------------------------------------------------

		constraints.gridx = 0;
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 6;
		constraints.insets.left = 12;
		constraints.insets.right = 12;
		panel.add(new JSeparator(JSeparator.HORIZONTAL), constraints);

		constraints.gridy++;
		label = new JLabel("Spelling");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		panel.add(label, constraints);

		constraints.gridx = 0;
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridwidth = 1;
		constraints.insets.left = 12;
		constraints.insets.right = 2;

		label = new JLabel("Keywords:");
		label.setLabelFor(keywordSpellingBox);
		panel.add(label, constraints);

		constraints.gridx++;
		constraints.anchor = GridBagConstraints.BASELINE_LEADING;
		constraints.insets.left = 0;
		panel.add(keywordSpellingBox, constraints);

		constraints.gridx++;
		constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
		constraints.insets.left = 12;

		label = new JLabel("Functions:");
		label.setLabelFor(functionSpellingBox);
		panel.add(label, constraints);

		constraints.gridx++;
		constraints.anchor = GridBagConstraints.BASELINE_LEADING;
		constraints.insets.left = 0;
		panel.add(functionSpellingBox, constraints);

		constraints.gridx++;
		constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
		constraints.insets.left = 12;

		label = new JLabel("Objects:");
		label.setLabelFor(objectSpellingBox);
		panel.add(label, constraints);

		constraints.gridx++;
		constraints.anchor = GridBagConstraints.BASELINE_LEADING;
		constraints.insets.left = 0;
		constraints.insets.right = 12;
		panel.add(objectSpellingBox, constraints);

		// --------------------------------------------------------------------------------------------

		constraints.gridx = 0;
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 6;
		constraints.insets.left = 12;
		constraints.insets.right = 12;
		panel.add(new JSeparator(JSeparator.HORIZONTAL), constraints);

		constraints.gridy++;
		label = new JLabel("Positioning");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		panel.add(label, constraints);

		constraints.gridx = 0;
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridwidth = 1;
		constraints.insets.left = 12;
		constraints.insets.right = 2;

		label = new JLabel("Indent Width:");
		label.setLabelFor(indentWidthField);
		panel.add(label, constraints);

		constraints.gridx++;
		constraints.anchor = GridBagConstraints.BASELINE_LEADING;
		constraints.insets.left = 0;
		panel.add(indentWidthField, constraints);

		constraints.gridx++;
		constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
		constraints.insets.left = 12;

		label = new JLabel("Separation:");
		label.setLabelFor(separationBox);
		panel.add(label, constraints);

		constraints.gridx++;
		constraints.anchor = GridBagConstraints.BASELINE_LEADING;
		constraints.insets.left = 0;
		panel.add(separationBox, constraints);

		// --------------------------------------------------------------------------------------------

		constraints.gridx = 0;
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 6;
		constraints.insets.left = 12;
		constraints.insets.right = 12;
		panel.add(new JSeparator(JSeparator.HORIZONTAL), constraints);

		constraints.gridy++;
		label = new JLabel("Dialect");
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		panel.add(label, constraints);

		constraints.gridx = 0;
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridwidth = 3;
		constraints.insets.left = 12;
		constraints.insets.right = 2;

		label = new JLabel("Squared Brackets Quotation:");
		label.setLabelFor(squaredBracketQuotationBox);
		panel.add(label, constraints);

		constraints.gridx += 3;
		constraints.anchor = GridBagConstraints.BASELINE_LEADING;
		constraints.insets.left = 0;
		panel.add(squaredBracketQuotationBox, constraints);

		// --------------------------------------------------------------------------------------------

		constraints.gridx = 0;
		constraints.gridy++;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth = 6;
		constraints.insets.left = 12;
		constraints.insets.right = 12;
		panel.add(new JSeparator(JSeparator.HORIZONTAL), constraints);

		setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		add(panel);
	}

	public final void setOptions(Map<String, Object> optionsMap) {
		String[] options = new String[optionsMap.size()];
		int i = 0;
		for (Entry<String, Object> e : optionsMap.entrySet()) {
			options[i] = e.getKey() + "=" + e.getValue();
			i++;
		}
		setOptions(options);
	}

	public final void setOptions(String optionStr) {
		String[] options = optionStr.split(",");
		setOptions(options);
	}

	public final void setOptions(String... options) {
		if (options != null)
			for (String s : options) {
				String[] o = s.split("=");
				if (o.length == 2) {
					LOGGER.log(Level.FINE, "Found Formatting Option {0} = {1}", o);

					String key = o[0].trim();
					String value = o[1].trim();

					if (key.equalsIgnoreCase(FormattingOption.OUTPUT_FORMAT.toString())) {
						try {
							outputFormatBox.setSelectedItem(OutputFormat.valueOf(value.toUpperCase()));
						} catch (Exception ex) {
							LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
						}

					} else if (key.equalsIgnoreCase(FormattingOption.KEYWORD_SPELLING.toString())) {
						try {
							keywordSpellingBox.setSelectedItem(Spelling.valueOf(value.toUpperCase()));
						} catch (Exception ex) {
							LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
						}

					} else if (key.equalsIgnoreCase(FormattingOption.FUNCTION_SPELLING.toString())) {
						try {
							functionSpellingBox.setSelectedItem(Spelling.valueOf(value.toUpperCase()));
						} catch (Exception ex) {
							LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
						}

					} else if (key.equalsIgnoreCase(FormattingOption.OBJECT_SPELLING.toString())) {
						try {
							objectSpellingBox.setSelectedItem(Spelling.valueOf(value.toUpperCase()));
						} catch (Exception ex) {
							LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
						}

					} else if (key.equalsIgnoreCase(FormattingOption.SEPARATION.toString())) {
						try {
							separationBox.setSelectedItem(Separation.valueOf(value.toUpperCase()));
						} catch (Exception ex) {
							LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
						}

					} else if (key.equalsIgnoreCase(FormattingOption.SQUARE_BRACKET_QUOTATION.toString())) {
						try {
							squaredBracketQuotationBox.setSelectedItem(
									SquaredBracketQuotation.valueOf(value.toUpperCase()));
						} catch (Exception ex) {
							LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
						}

					} else if (key.equalsIgnoreCase(FormattingOption.INDENT_WIDTH.toString())) {
						try {
							Integer indentWidth = Integer.valueOf(value);
							indentWidthField.setText(indentWidth.toString());
						} catch (Exception ex) {
							LOGGER.log(Level.WARNING, "Formatting Option {0} does not support {1} ", o);
						}
					} else {
						LOGGER.log(Level.WARNING, "Unknown Formatting Option {0} = {1} ", o);
					}

				} else {
					LOGGER.log(Level.WARNING, "Invalid Formatting Option {0}", s);
				}
			}
	}

	public final String getOptionString() {
		StringBuilder builder = new StringBuilder();
		String[] options = getOptions();

		for (int i = 0; i < options.length; i++) {
			if (i > 0)
				builder.append(",");

			builder.append(options[1]);
		}
		return null;
	}

	public final Map<String, Object> getOptionsMap() {
		TreeMap<String, Object> map = new TreeMap<>();

		map.put(FormattingOption.OUTPUT_FORMAT.toString(), outputFormatBox.getSelectedItem());
		map.put(FormattingOption.KEYWORD_SPELLING.toString(), keywordSpellingBox.getSelectedItem());
		map.put(FormattingOption.FUNCTION_SPELLING.toString(), functionSpellingBox.getSelectedItem());
		map.put(FormattingOption.OBJECT_SPELLING.toString(), objectSpellingBox.getSelectedItem());

		Integer indentWidth = Integer.parseInt(indentWidthField.getText());
		map.put(FormattingOption.INDENT_WIDTH.toString(), indentWidth);

		map.put(FormattingOption.SEPARATION.toString(), separationBox.getSelectedItem());
		map.put(
				FormattingOption.SQUARE_BRACKET_QUOTATION.toString(),
				squaredBracketQuotationBox.getSelectedItem());

		return map;
	}

	public final String[] getOptions() {
		Map<String, Object> map = getOptionsMap();

		String[] options = new String[map.size()];
		int i = 0;
		for (Entry<String, Object> e : map.entrySet()) {
			options[i] = e.getKey() + "=" + e.getValue();
			i++;
		}
		return options;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(Preferences preferences) {
		TreeMap<String, Object> map = new TreeMap<>();
		map.put(FormattingOption.OUTPUT_FORMAT.toString(), preferences.get(FormattingOption.OUTPUT_FORMAT.toString(), JSQLFormatter.getOutputFormat().toString()));
		map.put(FormattingOption.KEYWORD_SPELLING.toString(), preferences.get(FormattingOption.KEYWORD_SPELLING.toString(), JSQLFormatter.getKeywordSpelling().toString()));
		map.put(FormattingOption.FUNCTION_SPELLING.toString(), preferences.get(FormattingOption.FUNCTION_SPELLING.toString(), JSQLFormatter.getFunctionSpelling().toString()));
		map.put(FormattingOption.OBJECT_SPELLING.toString(), preferences.get(FormattingOption.OBJECT_SPELLING.toString(), JSQLFormatter.getObjectSpelling().toString()));
		map.put(FormattingOption.INDENT_WIDTH.toString(), preferences.getInt(FormattingOption.INDENT_WIDTH.toString(), JSQLFormatter.getIndentWidth()));
		map.put(FormattingOption.SEPARATION.toString(), preferences.get(FormattingOption.SEPARATION.toString(), JSQLFormatter.getSeparation().toString()));
		map.put(FormattingOption.SQUARE_BRACKET_QUOTATION.toString(), preferences.get(FormattingOption.SQUARE_BRACKET_QUOTATION.toString(), JSQLFormatter.getSquaredBracketQuotation().toString()));

		setOptions(map);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store(Preferences preferences) {
		Map<String, Object> map = getOptionsMap();

		int i = 0;
		for (Entry<String, Object> e : map.entrySet()) {
			preferences.put(e.getKey(), e.getValue().toString());
			i++;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean valid() {
		String text = indentWidthField.getText();
		try {
			int i = Integer.parseInt(text.trim());
			if (i >= 0 && i <= 24) {
				indentWidthField.setBackground(validBackgroundColor);
				return true;
			} else
				throw new Exception("The Indent Width must be an Integer between 0 and 24.");
		} catch (Exception ex) {
			indentWidthField.setBackground(Color.orange);
			return false;
		}
	}
}
