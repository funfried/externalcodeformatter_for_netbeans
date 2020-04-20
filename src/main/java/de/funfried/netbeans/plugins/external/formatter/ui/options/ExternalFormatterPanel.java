/*
 * Copyright (c) 2020 bahlef.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 * bahlef
 */
package de.funfried.netbeans.plugins.external.formatter.ui.options;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.spi.options.OptionsPanelController.Keywords;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.Mnemonics;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import de.funfried.netbeans.plugins.external.formatter.FormatterService;
import de.funfried.netbeans.plugins.external.formatter.MimeType;
import de.funfried.netbeans.plugins.external.formatter.ui.customizer.VerifiableConfigPanel;

/**
 * The options panel for this plugin.
 *
 * @author markiewb
 * @author bahlef
 */
@Keywords(location = "Editor", tabTitle = "External Formatter", keywords = { "eclipse", "google", "spring", "java", "external", "format", "formatter", "eclipse formatter", "google formatter",
		"spring formatter", "external formatter" })
public class ExternalFormatterPanel extends JPanel implements VerifiableConfigPanel, ChangeListener {
	/** The unique serial version ID. */
	private static final long serialVersionUID = 1L;

	/** {@link Logger} of this class. */
	private static final Logger log = Logger.getLogger(ExternalFormatterPanel.class.getName());

	/** Holds all {@link FormatterOptionsPanel}s with thier fromatter ID in their supported mime type. */
	private transient final Map<MimeType, Map<String, FormatterOptionsPanel>> formatterOptions = new HashMap<>();

	/** Holds all {@link FormatterService}s in a {@link Map} with their supported mime type as the key. */
	private transient final Map<MimeType, List<FormatterService>> formatterIdsPerMimeType = new HashMap<>();

	/** Holder of the currently active formatter ID per mimeType. */
	private transient final Map<MimeType, String> activeFormatterId = new HashMap<>();

	/** {@link ChangeSupport} to notify about changed preference components. */
	private transient final ChangeSupport changeSupport;

	/** The {@link Preferences} modified by this options dialog. */
	private transient final Preferences preferences;

	/** Internal helper flag which is set to {@code true} while switching the mime type, so there won't be a change listener event for that. */
	private transient volatile boolean switchingMimeType = false;

	/** Flag which defines whether or not this dialog is shown globally or project specific. */
	private final boolean showsProjectSettings;

	/** Internal flag which defines whether or not the selection of another formatter should be handled or not. */
	private transient boolean formatterSelectionActive = true;

	/**
	 * Creates a new instance of {@link ExternalFormatterPanel}.
	 *
	 * @param preferences          the {@link Preferences}
	 * @param showsProjectSettings {@code true} if the panel is shown as a project specific options panel, otherwise {@code false}
	 */
	public ExternalFormatterPanel(Preferences preferences, boolean showsProjectSettings) {
		this.preferences = preferences;
		this.showsProjectSettings = showsProjectSettings;

		this.changeSupport = new ChangeSupport(this);

		Collection<? extends FormatterService> formatterServices = Lookup.getDefault().lookupAll(FormatterService.class);
		for (FormatterService formatterService : formatterServices) {
			MimeType mimeType = formatterService.getSupportedMimeType();

			List<FormatterService> formatterServicesOfMimeType = formatterIdsPerMimeType.get(mimeType);
			if (formatterServicesOfMimeType == null) {
				formatterServicesOfMimeType = new ArrayList<>();
			}

			formatterServicesOfMimeType.add(formatterService);

			formatterIdsPerMimeType.put(mimeType, formatterServicesOfMimeType);
		}

		initComponents();
		initMimeTypes();

		updateEnabledState();
	}

	/**
	 * Returns the {@link Preferences} modified by this option dialog.
	 *
	 * @return the {@link Preferences} modified by this option dialog
	 */
	public Preferences getPreferences() {
		return preferences;
	}

	/**
	 * Adds a given {@link ChangeListener} to this option dialog, which will be notified as
	 * soon as an user has changed the state of the components inside this options dialog.
	 *
	 * @param listener the {@link ChangeListener} to add
	 */
	public void addChangeListener(ChangeListener listener) {
		changeSupport.addChangeListener(listener);
	}

	/**
	 * Removes a given {@link ChangeListener} from this option dialog.
	 *
	 * @param listener the {@link ChangeListener} which should be removed
	 */
	public void removeChangeListener(ChangeListener listener) {
		changeSupport.removeChangeListener(listener);
	}

	/**
	 * Fires a change event to all registered {@link ChangeListener}s.
	 */
	private void fireChangedListener() {
		changeSupport.fireChange();
	}

	/**
	 * Initializes the mime type items of the {@link #chooseMimeTypeCmbBox}
	 * {@link JComboBox}.
	 */
	private void initMimeTypes() {
		chooseMimeTypeCmbBox.removeAllItems();

		for (MimeType mimeType : MimeType.values()) {
			chooseMimeTypeCmbBox.addItem(new ExtValue(mimeType.toString(), mimeType.getDisplayName()));
		}

		chooseMimeTypeCmbBox.setSelectedIndex(0);
	}

	/**
	 * Sets the currently active formatter for the given {@code mimeType}.
	 *
	 * @param mimeType    the mime type
	 * @param formatterId the formatter service ID
	 * @param fireChange  {@code true} to fire the change listener
	 */
	private void setActiveFormatter(MimeType mimeType, String formatterId, boolean fireChange) {
		activeFormatterId.put(mimeType, formatterId);

		if (fireChange) {
			fireChangedListener();
		}
	}

	/**
	 * Returns the {@link FormatterOptionsPanel} for the given {@code mimeType} and {@code formatterId},
	 * if this {@link FormatterOptionsPanel} is requested for the first time the preferences will also be
	 * loaded.
	 *
	 * @param mimeType    the mime type
	 * @param formatterId the formatter service ID
	 *
	 * @return the {@link FormatterOptionsPanel} for the given {@code mimeType} and {@code formatterId},
	 *         or {@code null} if it cannot be found
	 */
	private synchronized FormatterOptionsPanel getFormatterOptionsPanel(MimeType mimeType, String formatterId) {
		Map<String, FormatterOptionsPanel> formatterOptionPanels = formatterOptions.get(mimeType);
		if (formatterOptionPanels == null) {
			formatterOptionPanels = new HashMap<>();

			formatterOptions.put(mimeType, formatterOptionPanels);
		}

		FormatterOptionsPanel optionsPanel = formatterOptionPanels.get(formatterId);
		if (optionsPanel == null) {
			List<FormatterService> formatterServices = formatterIdsPerMimeType.get(mimeType);
			if (formatterServices != null) {
				for (FormatterService formatterService : formatterServices) {
					if (formatterService != null && Objects.equals(formatterId, formatterService.getId())) {
						optionsPanel = formatterService.getOptionsPanel();
						optionsPanel.load(preferences);

						formatterOptionPanels.put(formatterId, optionsPanel);

						formatterOptions.put(mimeType, formatterOptionPanels);
					}
				}
			}
		}

		return optionsPanel;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtProjectSpecificHint = new JLabel();
        cbShowNotifications = new JCheckBox();
        btnDonate = new JLabel();
        btnVisitHomePage = new JLabel();
        useIndentationSettingsChkBox = new JCheckBox();
        overrideTabSizeSpn = new JSpinner();
        overrideTabSizeChkBox = new JCheckBox();
        formatterOptionsPanel = new JPanel();
        chooseLanguageLbl = new JLabel();
        chooseMimeTypeCmbBox = new JComboBox<>();
        useFormatterLbl = new JLabel();
        chooseFormatterCmbBox = new JComboBox<>();

        Mnemonics.setLocalizedText(txtProjectSpecificHint, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.txtProjectSpecificHint.text")); // NOI18N

        Mnemonics.setLocalizedText(cbShowNotifications, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.cbShowNotifications.text")); // NOI18N
        cbShowNotifications.setToolTipText(NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.cbShowNotifications.toolTipText")); // NOI18N
        cbShowNotifications.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cbShowNotificationsActionPerformed(evt);
            }
        });

        btnDonate.setHorizontalAlignment(SwingConstants.RIGHT);
        Mnemonics.setLocalizedText(btnDonate, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.btnDonate.text")); // NOI18N
        btnDonate.setToolTipText(NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.btnDonate.toolTipText")); // NOI18N
        btnDonate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDonate.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnDonateMouseClicked(evt);
            }
        });

        btnVisitHomePage.setHorizontalAlignment(SwingConstants.RIGHT);
        Mnemonics.setLocalizedText(btnVisitHomePage, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.btnVisitHomePage.text")); // NOI18N
        btnVisitHomePage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVisitHomePage.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                btnVisitHomePageMouseClicked(evt);
            }
        });

        useIndentationSettingsChkBox.setSelected(true);
        Mnemonics.setLocalizedText(useIndentationSettingsChkBox, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.useIndentationSettingsChkBox.text")); // NOI18N
        useIndentationSettingsChkBox.setToolTipText(NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.useIndentationSettingsChkBox.toolTipText")); // NOI18N
        useIndentationSettingsChkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                useIndentationSettingsChkBoxActionPerformed(evt);
            }
        });

        overrideTabSizeSpn.setModel(new SpinnerNumberModel(4, 1, 20, 1));
        overrideTabSizeSpn.setToolTipText(NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.overrideTabSizeSpn.toolTipText")); // NOI18N
        overrideTabSizeSpn.setEnabled(false);
        overrideTabSizeSpn.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                overrideTabSizeSpnStateChanged(evt);
            }
        });

        Mnemonics.setLocalizedText(overrideTabSizeChkBox, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.overrideTabSizeChkBox.text")); // NOI18N
        overrideTabSizeChkBox.setToolTipText(NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.overrideTabSizeChkBox.toolTipText")); // NOI18N
        overrideTabSizeChkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                overrideTabSizeChkBoxActionPerformed(evt);
            }
        });

        formatterOptionsPanel.setLayout(new BorderLayout());

        Mnemonics.setLocalizedText(chooseLanguageLbl, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.chooseLanguageLbl.text")); // NOI18N

        chooseMimeTypeCmbBox.setRenderer(new MimeTypesListCellRenderer());
        chooseMimeTypeCmbBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                chooseMimeTypeCmbBoxItemStateChanged(evt);
            }
        });

        Mnemonics.setLocalizedText(useFormatterLbl, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.useFormatterLbl.text")); // NOI18N

        chooseFormatterCmbBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                chooseFormatterCmbBoxItemStateChanged(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(formatterOptionsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(txtProjectSpecificHint))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVisitHomePage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDonate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(cbShowNotifications)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(useIndentationSettingsChkBox)
                                .addGap(18, 18, 18)
                                .addComponent(overrideTabSizeChkBox)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(overrideTabSizeSpn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chooseLanguageLbl)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chooseMimeTypeCmbBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(useFormatterLbl)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chooseFormatterCmbBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 115, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(txtProjectSpecificHint)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(chooseLanguageLbl)
                    .addComponent(chooseMimeTypeCmbBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(useFormatterLbl)
                    .addComponent(chooseFormatterCmbBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(formatterOptionsPanel, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(useIndentationSettingsChkBox)
                    .addComponent(overrideTabSizeSpn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(overrideTabSizeChkBox))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbShowNotifications)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDonate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVisitHomePage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void btnDonateMouseClicked(MouseEvent evt) {//GEN-FIRST:event_btnDonateMouseClicked
		try {
			HtmlBrowser.URLDisplayer.getDefault().showURLExternal(new URL("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=926F5XBCTK2LQ&source=url"));
		} catch (MalformedURLException ex) {
			Exceptions.printStackTrace(ex);
		}
	}//GEN-LAST:event_btnDonateMouseClicked

	private void btnVisitHomePageMouseClicked(MouseEvent evt) {//GEN-FIRST:event_btnVisitHomePageMouseClicked
		try {
			HtmlBrowser.URLDisplayer.getDefault().showURLExternal(new URL("https://github.com/funfried/externalcodeformatter_for_netbeans/"));
		} catch (MalformedURLException ex) {
			Exceptions.printStackTrace(ex);
		}
	}//GEN-LAST:event_btnVisitHomePageMouseClicked

    private void useIndentationSettingsChkBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_useIndentationSettingsChkBoxActionPerformed
		updateEnabledState();
    }//GEN-LAST:event_useIndentationSettingsChkBoxActionPerformed

    private void chooseMimeTypeCmbBoxItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_chooseMimeTypeCmbBoxItemStateChanged
		try {
			switchingMimeType = true;

			if (ItemEvent.SELECTED == evt.getStateChange()) {
				chooseFormatterCmbBox.removeAllItems();

				formatterSelectionActive = false;

				MimeType selectedMimeType = MimeType.valueOf(getSelectedValue(chooseMimeTypeCmbBox));

				List<FormatterService> formatterServices = formatterIdsPerMimeType.get(selectedMimeType);
				if (formatterServices != null) {
					ExtValue selected = new ExtValue(Settings.DEFAULT_FORMATTER, "Internal NetBeans formatter");
					chooseFormatterCmbBox.addItem(selected);

					for (FormatterService formatterService : formatterServices) {
						String formatterId = formatterService.getId();
						ExtValue value = new ExtValue(formatterId, formatterService.getDisplayName());

						chooseFormatterCmbBox.addItem(value);

						if (Objects.equals(activeFormatterId.get(selectedMimeType), formatterId)) {
							selected = value;
						}
					}

					formatterSelectionActive = true;

					chooseFormatterCmbBox.setSelectedItem(selected);
				} else {
					formatterSelectionActive = true;
				}
			}
		} finally {
			switchingMimeType = false;
		}
    }//GEN-LAST:event_chooseMimeTypeCmbBoxItemStateChanged

    private void chooseFormatterCmbBoxItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_chooseFormatterCmbBoxItemStateChanged
		if (!formatterSelectionActive) {
			return;
		}

		if (ItemEvent.DESELECTED == evt.getStateChange()) {
			formatterOptionsPanel.setBorder(null);
			formatterOptionsPanel.removeAll();
		} else if (ItemEvent.SELECTED == evt.getStateChange()) {
			MimeType selectedMimeType = MimeType.valueOf(getSelectedValue(chooseMimeTypeCmbBox));
			String selectedFormatterId = getSelectedValue(chooseFormatterCmbBox);

			FormatterOptionsPanel optionsPanel = getFormatterOptionsPanel(selectedMimeType, selectedFormatterId);
			if (optionsPanel != null) {
				formatterOptionsPanel.setBorder(BorderFactory.createEtchedBorder());
				formatterOptionsPanel.add(optionsPanel.getComponent(), BorderLayout.CENTER);

				optionsPanel.addChangeListener(WeakListeners.change(this, optionsPanel));
			}

			setActiveFormatter(selectedMimeType, selectedFormatterId, !switchingMimeType);
		}
    }//GEN-LAST:event_chooseFormatterCmbBoxItemStateChanged

    private void overrideTabSizeChkBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_overrideTabSizeChkBoxActionPerformed
		updateEnabledState();
		fireChangedListener();
    }//GEN-LAST:event_overrideTabSizeChkBoxActionPerformed

    private void overrideTabSizeSpnStateChanged(ChangeEvent evt) {//GEN-FIRST:event_overrideTabSizeSpnStateChanged
		fireChangedListener();
    }//GEN-LAST:event_overrideTabSizeSpnStateChanged

    private void cbShowNotificationsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbShowNotificationsActionPerformed
		fireChangedListener();
    }//GEN-LAST:event_cbShowNotificationsActionPerformed

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		MimeType selectedMimeType = MimeType.valueOf(getSelectedValue(chooseMimeTypeCmbBox));
		String javaMimeType = JavaTokenId.language().mimeType();

		for (MimeType mimeType : formatterIdsPerMimeType.keySet()) {
			String activeFormatter = preferences.get(Settings.ENABLED_FORMATTER_PREFIX + mimeType.toString(), Settings.DEFAULT_FORMATTER);
			if (Settings.DEFAULT_FORMATTER.equals(activeFormatter) && ArrayUtils.contains(mimeType.getMimeTypes(), javaMimeType)) {
				activeFormatter = preferences.get(Settings.ENABLED_FORMATTER, Settings.DEFAULT_FORMATTER);

				preferences.remove(Settings.ENABLED_FORMATTER);
			}

			setActiveFormatter(mimeType, activeFormatter, false);

			if (Objects.equals(selectedMimeType, mimeType)) {
				chooseFormatterCmbBox.setSelectedItem(new ExtValue(activeFormatter, null));
			}
		}

		boolean showNotifications = preferences.getBoolean(Settings.SHOW_NOTIFICATIONS, false);
		boolean useIndentationSettings = preferences.getBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, true);
		boolean overrideTabSize = preferences.getBoolean(Settings.OVERRIDE_TAB_SIZE, false);
		int overrideTabSizeValue = preferences.getInt(Settings.OVERRIDE_TAB_SIZE_VALUE, 4);

		useIndentationSettingsChkBox.setSelected(useIndentationSettings);
		overrideTabSizeChkBox.setSelected(overrideTabSize);
		overrideTabSizeSpn.setValue(overrideTabSizeValue);

		cbShowNotifications.setSelected(showNotifications);

		updateEnabledState();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store() {
		for (MimeType mimeType : formatterIdsPerMimeType.keySet()) {
			preferences.put(Settings.ENABLED_FORMATTER_PREFIX + mimeType.toString(), activeFormatterId.get(mimeType));

			Map<String, FormatterOptionsPanel> options = formatterOptions.get(mimeType);
			if (options != null) {
				for (FormatterOptionsPanel option : options.values()) {
					option.store(preferences);
				}
			}
		}

		preferences.putBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, useIndentationSettingsChkBox.isSelected());
		preferences.putBoolean(Settings.OVERRIDE_TAB_SIZE, overrideTabSizeChkBox.isSelected());
		preferences.putInt(Settings.OVERRIDE_TAB_SIZE_VALUE, Integer.parseInt(overrideTabSizeSpn.getValue().toString()));
		preferences.putBoolean(Settings.SHOW_NOTIFICATIONS, cbShowNotifications.isSelected());

		try {
			preferences.flush();
		} catch (BackingStoreException ex) {
			log.log(Level.WARNING, "Could not flush formatter settings", ex);
		}

		ExternalFormatterPreferencesChangeSupport editorPreferencesChangeSupport = Lookup.getDefault().lookup(ExternalFormatterPreferencesChangeSupport.class);
		if (editorPreferencesChangeSupport != null) {
			editorPreferencesChangeSupport.fireChange();
		} else {
			log.warning("Could not find ExternalFormatterPreferencesChangeSupport in lookup!");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean valid() {
		for (MimeType mimeType : formatterOptions.keySet()) {
			String activeId = activeFormatterId.get(mimeType);
			if (StringUtils.isNotBlank(activeId) && !Objects.equals(Settings.DEFAULT_FORMATTER, activeId)) {
				FormatterOptionsPanel optionsPanel = getFormatterOptionsPanel(mimeType, activeId);
				if (optionsPanel != null && !optionsPanel.valid()) {
					return false;
				}
			}
		}

		return true;
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel btnDonate;
    private JLabel btnVisitHomePage;
    private JCheckBox cbShowNotifications;
    private JComboBox<ExtValue> chooseFormatterCmbBox;
    private JLabel chooseLanguageLbl;
    private JComboBox<ExtValue> chooseMimeTypeCmbBox;
    private JPanel formatterOptionsPanel;
    private JCheckBox overrideTabSizeChkBox;
    private JSpinner overrideTabSizeSpn;
    private JLabel txtProjectSpecificHint;
    private JLabel useFormatterLbl;
    private JCheckBox useIndentationSettingsChkBox;
    // End of variables declaration//GEN-END:variables

	/**
	 * Updates the enabled state of all components regarding the lastest selections made by the user.
	 */
	private void updateEnabledState() {
		overrideTabSizeChkBox.setEnabled(useIndentationSettingsChkBox.isSelected());
		overrideTabSizeSpn.setEnabled(overrideTabSizeChkBox.isEnabled() && overrideTabSizeChkBox.isSelected());

		txtProjectSpecificHint.setVisible(!showsProjectSettings);
	}

	/**
	 * Returns the selected item of a given {@link JComboBox}. If the selected item is an instance of {@link ExtValue} the {@link ExtValue#getValue()} will be returned.
	 *
	 * @param comboBox the {@link JComboBox}
	 *
	 * @return the selected item of a given {@link JComboBox}. If the selected item is an instance of {@link ExtValue} the {@link ExtValue#getValue()} will be returned
	 */
	private String getSelectedValue(JComboBox comboBox) {
		Object selectedItem = comboBox.getSelectedItem();
		if (selectedItem != null) {
			if (selectedItem instanceof ExtValue) {
				return ((ExtValue) selectedItem).getValue();
			} else {
				return selectedItem.toString();
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		fireChangedListener();
	}

	/**
	 * A renderer for the {@link #chooseMimeTypeCmbBox} which shows every entry in bold which has an external formatter select.
	 */
	private class MimeTypesListCellRenderer extends DefaultListCellRenderer {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (value != null) {
				String mimeType = value.toString();
				if (value instanceof ExtValue) {
					mimeType = ((ExtValue) value).getValue();
				}

				if (!Objects.equals(Settings.DEFAULT_FORMATTER, activeFormatterId.get(MimeType.valueOf(mimeType)))) {
					Font standardFont = comp.getFont();

					comp.setFont(new Font(standardFont.getName(), Font.BOLD, standardFont.getSize()));
				}
			}

			return comp;
		}
	}

	/**
	 * A Java bean for separating between visual and logical values.
	 */
	private static class ExtValue {

		/**
		 * Holder of the logical value.
		 */
		private final String value;

		/**
		 * Holder of the visual value.
		 */
		private final String visualValue;

		/**
		 * Constructor of this class.
		 *
		 * @param value logical value
		 * @param visualValue visual value
		 */
		public ExtValue(String value, String visualValue) {
			this.value = value;
			this.visualValue = visualValue;
		}

		/**
		 * Returns the logical value.
		 *
		 * @return the logical value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Returns the visual value.
		 *
		 * @return the visual value
		 *
		 * @see #toString()
		 */
		public String getVisualValue() {
			return visualValue;
		}

		/**
		 * Returns the {@link #visualValue} if not {@code null}, otherwise the logical {@link #value}.
		 *
		 * @return the {@link #visualValue} if not {@code null}, otherwise the logical {@link #value}
		 */
		@Override
		public String toString() {
			return StringUtils.isBlank(visualValue) ? value : visualValue;
		}

		/**
		 * Returns {@code true} if {@code obj} is also an {@link ExtValue} with the same logical value or if {@code obj} is a String with the same characters as the logical {@link #value}, otherwise
		 * {@code false}.
		 *
		 * @return {@code true} if {@code obj} is also an {@link ExtValue} with the same logical value or if {@code obj} is a String with the same characters as the logical {@link #value}, otherwise
		 * {@code false}
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			} else if (obj instanceof ExtValue) {
				ExtValue other = (ExtValue) obj;

				return Objects.equals(this.value, other.value);
			} else {
				return Objects.equals(this.value, obj);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			int hash = 7;
			hash = 97 * hash + Objects.hashCode(this.value);
			return hash;
		}
	}
}
