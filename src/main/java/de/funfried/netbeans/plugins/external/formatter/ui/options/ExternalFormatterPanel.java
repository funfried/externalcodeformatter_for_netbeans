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

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.tuple.Pair;
import org.netbeans.spi.options.OptionsPanelController.Keywords;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.Mnemonics;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.funfried.netbeans.plugins.external.formatter.base.FormatterService;
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

	/**
	 * Holds all shown {@link FormatterOptionsPanel} together with their created {@link JRadioButton}
	 * and with their {@link FormatterService} ID as the key.
	 */
	private transient final Map<String, Pair<FormatterOptionsPanel, JRadioButton>> formatterOptions = new HashMap<>();

	/** {@link CellConstraints} for the dynamically build {@link FormLayout}. */
	private transient final CellConstraints cc = new CellConstraints();

	/** {@link ChangeSupport} to notify about changed preference components. */
	private transient final ChangeSupport changeSupport;

	/** The {@link Preferences} modified by this options dialog. */
	private transient final Preferences preferences;

	/** Flag which defines whether or not this dialog is shown globally or project specific. */
	private final boolean showsProjectSettings;

	/** Holder of the currently active formatter ID. */
	private transient String activeFormatterId = null;

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

		initComponents();
		initFormatterOptions();

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
	 * Initializes the individual formatter options panels.
	 */
	private void initFormatterOptions() {
		FormLayout formLayout = new FormLayout("28px,pref:grow", "");

		formatterOptionsPanel.setLayout(formLayout);

		int row = 1;

		Collection<? extends FormatterService> formatterServices = Lookup.getDefault().lookupAll(FormatterService.class);
		for (FormatterService formatterService : formatterServices) {
			if (row > 1) {
				formLayout.appendRow(RowSpec.decode("3dlu"));
			}

			formLayout.appendRow(RowSpec.decode("pref"));
			formLayout.appendRow(RowSpec.decode("2dlu"));
			formLayout.appendRow(RowSpec.decode("pref"));

			FormatterOptionsPanel optionsPanel = formatterService.getOptionsPanel();

			JRadioButton rdBtn = new JRadioButton(NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.use") + " " + formatterService.getDisplayName());
			rdBtn.addActionListener((ActionEvent e) -> {
				setActiveFormatter(formatterService.getId());
			});

			formatterOptions.put(formatterService.getId(), Pair.of(optionsPanel, rdBtn));

			optionsPanel.addChangeListener(WeakListeners.change(this, optionsPanel));

			formatterBtnGrp.add(rdBtn);

			JPanel formatterPanel = optionsPanel.getComponent();
			formatterPanel.setBorder(BorderFactory.createEtchedBorder());

			formatterOptionsPanel.add(rdBtn, cc.xyw(1, row, 2));
			formatterOptionsPanel.add(formatterPanel, cc.xy(2, row + 2));

			row += 4;
		}
	}

	private void setActiveFormatter(String formatterId) {
		activeFormatterId = formatterId;

		Set<String> formatterIds = formatterOptions.keySet();
		for (String id : formatterIds) {
			Pair<FormatterOptionsPanel, JRadioButton> optionsPair = formatterOptions.get(id);
			optionsPair.getLeft().setActive(Objects.equals(activeFormatterId, id));
		}

		updateEnabledState();
		fireChangedListener();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        formatterBtnGrp = new ButtonGroup();
        rbUseNetBeans = new JRadioButton();
        txtProjectSpecificHint = new JLabel();
        cbShowNotifications = new JCheckBox();
        btnDonate = new JLabel();
        btnVisitHomePage = new JLabel();
        useIndentationSettingsChkBox = new JCheckBox();
        overrideTabSizeSpn = new JSpinner();
        overrideTabSizeChkBox = new JCheckBox();
        formatterOptionsPanel = new JPanel();

        formatterBtnGrp.add(rbUseNetBeans);
        rbUseNetBeans.setSelected(true);
        Mnemonics.setLocalizedText(rbUseNetBeans, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.rbUseNetBeans.text")); // NOI18N
        rbUseNetBeans.setToolTipText(NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.rbUseNetBeans.toolTipText")); // NOI18N
        rbUseNetBeans.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rbUseNetBeansActionPerformed(evt);
            }
        });

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

        Mnemonics.setLocalizedText(useIndentationSettingsChkBox, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.useIndentationSettingsChkBox.text")); // NOI18N
        useIndentationSettingsChkBox.setToolTipText(NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.useIndentationSettingsChkBox.toolTipText")); // NOI18N
        useIndentationSettingsChkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                useIndentationSettingsChkBoxActionPerformed(evt);
            }
        });

        overrideTabSizeSpn.setModel(new SpinnerNumberModel(4, 1, 20, 1));
        overrideTabSizeSpn.setToolTipText(NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.overrideTabSizeSpn.toolTipText")); // NOI18N

        Mnemonics.setLocalizedText(overrideTabSizeChkBox, NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.overrideTabSizeChkBox.text")); // NOI18N
        overrideTabSizeChkBox.setToolTipText(NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.overrideTabSizeChkBox.toolTipText")); // NOI18N
        overrideTabSizeChkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                overrideTabSizeChkBoxActionPerformed(evt);
            }
        });

        formatterOptionsPanel.setLayout(null);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(cbShowNotifications)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(useIndentationSettingsChkBox)
                        .addGap(18, 18, 18)
                        .addComponent(overrideTabSizeChkBox)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(overrideTabSizeSpn, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rbUseNetBeans)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtProjectSpecificHint))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVisitHomePage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDonate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(formatterOptionsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rbUseNetBeans)
                    .addComponent(txtProjectSpecificHint))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(formatterOptionsPanel, GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
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

    private void rbUseNetBeansActionPerformed(ActionEvent evt) {//GEN-FIRST:event_rbUseNetBeansActionPerformed
		setActiveFormatter(Settings.DEFAULT_FORMATTER);
    }//GEN-LAST:event_rbUseNetBeansActionPerformed

    private void cbShowNotificationsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbShowNotificationsActionPerformed
		fireChangedListener();
    }//GEN-LAST:event_cbShowNotificationsActionPerformed

    private void useIndentationSettingsChkBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_useIndentationSettingsChkBoxActionPerformed
		updateEnabledState();
		fireChangedListener();
    }//GEN-LAST:event_useIndentationSettingsChkBoxActionPerformed

    private void overrideTabSizeChkBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_overrideTabSizeChkBoxActionPerformed
		updateEnabledState();
		fireChangedListener();
    }//GEN-LAST:event_overrideTabSizeChkBoxActionPerformed

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		activeFormatterId = preferences.get(Settings.ENABLED_FORMATTER, Settings.DEFAULT_FORMATTER);

		boolean showNotifications = preferences.getBoolean(Settings.SHOW_NOTIFICATIONS, false);
		boolean useIndentationSettings = preferences.getBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, true);
		boolean overrideTabSize = preferences.getBoolean(Settings.OVERRIDE_TAB_SIZE, false);
		int overrideTabSizeValue = preferences.getInt(Settings.OVERRIDE_TAB_SIZE_VALUE, 4);

		Pair<FormatterOptionsPanel, JRadioButton> activePair = formatterOptions.get(activeFormatterId);
		if (activePair != null) {
			formatterBtnGrp.setSelected(activePair.getRight().getModel(), true);
		} else {
			formatterBtnGrp.setSelected(rbUseNetBeans.getModel(), true);
		}

		useIndentationSettingsChkBox.setSelected(useIndentationSettings);
		overrideTabSizeChkBox.setSelected(overrideTabSize);
		overrideTabSizeSpn.setValue(overrideTabSizeValue);

		cbShowNotifications.setSelected(showNotifications);

		Collection<Pair<FormatterOptionsPanel, JRadioButton>> options = formatterOptions.values();
		for (Pair<FormatterOptionsPanel, JRadioButton> option : options) {
			option.getLeft().load(preferences);
		}

		setActiveFormatter(activeFormatterId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store() {
		preferences.put(Settings.ENABLED_FORMATTER, activeFormatterId);
		preferences.putBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, useIndentationSettingsChkBox.isSelected());
		preferences.putBoolean(Settings.OVERRIDE_TAB_SIZE, overrideTabSizeChkBox.isSelected());
		preferences.putInt(Settings.OVERRIDE_TAB_SIZE_VALUE, Integer.parseInt(overrideTabSizeSpn.getValue().toString()));
		preferences.putBoolean(Settings.SHOW_NOTIFICATIONS, cbShowNotifications.isSelected());

		Collection<Pair<FormatterOptionsPanel, JRadioButton>> optionsPanels = formatterOptions.values();
		for (Pair<FormatterOptionsPanel, JRadioButton> optionsPanel : optionsPanels) {
			optionsPanel.getLeft().store(preferences);
		}

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
		Pair<FormatterOptionsPanel, JRadioButton> optionsPanel = formatterOptions.get(activeFormatterId);
		if (optionsPanel != null) {
			return optionsPanel.getLeft().valid();
		}

		return true;
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel btnDonate;
    private JLabel btnVisitHomePage;
    private JCheckBox cbShowNotifications;
    private ButtonGroup formatterBtnGrp;
    private JPanel formatterOptionsPanel;
    private JCheckBox overrideTabSizeChkBox;
    private JSpinner overrideTabSizeSpn;
    private JRadioButton rbUseNetBeans;
    private JLabel txtProjectSpecificHint;
    private JCheckBox useIndentationSettingsChkBox;
    // End of variables declaration//GEN-END:variables

	/**
	 * Updates the enabled state of all components regarding the lastest selections made by the user.
	 */
	private void updateEnabledState() {
		useIndentationSettingsChkBox.setEnabled(!rbUseNetBeans.isSelected());
		overrideTabSizeChkBox.setEnabled(useIndentationSettingsChkBox.isEnabled() && useIndentationSettingsChkBox.isSelected());
		overrideTabSizeSpn.setEnabled(overrideTabSizeChkBox.isEnabled() && overrideTabSizeChkBox.isSelected());

		txtProjectSpecificHint.setVisible(!showsProjectSettings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		fireChangedListener();
	}
}
