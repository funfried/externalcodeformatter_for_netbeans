/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * markiewb - initial API and implementation and/or initial documentation
 */
package de.funfried.netbeans.plugins.external.formatter.ui.options;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.netbeans.spi.options.OptionsPanelController.Keywords;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.xml.sax.SAXException;

import com.google.googlejavaformat.java.JavaFormatterOptions;

import de.funfried.netbeans.plugins.external.formatter.exceptions.ConfigReadException;
import de.funfried.netbeans.plugins.external.formatter.java.eclipse.EclipseJavaFormatterService;
import de.funfried.netbeans.plugins.external.formatter.java.eclipse.EclipseJavaFormatterSettings;
import de.funfried.netbeans.plugins.external.formatter.java.eclipse.xml.ConfigReader;
import de.funfried.netbeans.plugins.external.formatter.java.google.GoogleJavaFormatterService;
import de.funfried.netbeans.plugins.external.formatter.java.google.GoogleJavaFormatterSettings;
import de.funfried.netbeans.plugins.external.formatter.ui.customizer.VerifiableConfigPanel;

/**
 * The options panel for this plugin.
 *
 * @author markiewb
 */
@Keywords(location = "Java", tabTitle = "External Formatter", keywords = { "eclipse", "google", "external", "format", "formatter", "eclipse formatter", "google formatter", "external formatter" })
public class ExternalFormatterPanel extends javax.swing.JPanel implements VerifiableConfigPanel {
	/** The unique serial version ID. */
	private static final long serialVersionUID = 1L;

	/** {@link Logger} of this class. */
	private static final Logger log = Logger.getLogger(ExternalFormatterPanel.class.getName());

	/** {@link ChangeSupport} to notify about changed preference components. */
	private transient final ChangeSupport changeSupport;

	/** The {@link Preferences} modified by this options dialog. */
	private transient final Preferences preferences;

	/** Flag which defines whether or not this dialog is shown globally or project specific. */
	private final boolean showsProjectSettings;

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
		updateEnabledState();

		formatterLocField.getDocument().addDocumentListener(new DocumentListener() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void insertUpdate(DocumentEvent e) {
				fireChangedListener();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void removeUpdate(DocumentEvent e) {
				fireChangedListener();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void changedUpdate(DocumentEvent e) {
				fireChangedListener();
			}
		});

		cbSourceLevel.addActionListener((ActionEvent e) -> {
			fireChangedListener();
		});
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
	 * REmoves a given {@link ChangeListener} from this option dialog.
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
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        formatterBtnGrp = new javax.swing.ButtonGroup();
        googleCodeStyleBtnGrp = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        browseButton = new javax.swing.JButton();
        lblFormatterFile = new javax.swing.JLabel();
        formatterLocField = new javax.swing.JTextField();
        errorLabel = new javax.swing.JLabel();
        lblProfile = new javax.swing.JLabel();
        cbProfile = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        cbUseProjectPref = new javax.swing.JCheckBox();
        lblLinefeed = new javax.swing.JLabel();
        cbLinefeed = new javax.swing.JComboBox<>();
        lblSourceLevel = new javax.swing.JLabel();
        cbSourceLevel = new javax.swing.JComboBox<>();
        rbUseNetBeans = new javax.swing.JRadioButton();
        rbUseEclipse = new javax.swing.JRadioButton();
        txtProjectSpecificHint = new javax.swing.JLabel();
        cbShowNotifications = new javax.swing.JCheckBox();
        btnDonate = new javax.swing.JLabel();
        btnVisitHomePage = new javax.swing.JLabel();
        rbUseGoogle = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        googleCodeStyleRdBtn = new javax.swing.JRadioButton();
        aospRdBtn = new javax.swing.JRadioButton();
        googleCodeStyleLbl = new javax.swing.JLabel();
        useIndentationSettingsChkBox = new javax.swing.JCheckBox();
        overrideTabSizeSpn = new javax.swing.JSpinner();
        overrideTabSizeChkBox = new javax.swing.JCheckBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.browseButton.text")); // NOI18N
        browseButton.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.browseButton.toolTipText")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        lblFormatterFile.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFormatterFile.setLabelFor(formatterLocField);
        org.openide.awt.Mnemonics.setLocalizedText(lblFormatterFile, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.lblFormatterFile.text")); // NOI18N

        formatterLocField.setText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.formatterLocField.text")); // NOI18N
        formatterLocField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formatterLocFieldActionPerformed(evt);
            }
        });

        errorLabel.setForeground(new java.awt.Color(255, 51, 51));
        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.errorLabel.text")); // NOI18N
        errorLabel.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.errorLabel.toolTipText")); // NOI18N

        lblProfile.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblProfile.setLabelFor(cbProfile);
        org.openide.awt.Mnemonics.setLocalizedText(lblProfile, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.lblProfile.text")); // NOI18N

        cbProfile.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.cbProfile.toolTipText")); // NOI18N
        cbProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbProfileActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.jLabel2.text")); // NOI18N
        jLabel2.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(cbUseProjectPref, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.cbUseProjectPref.text")); // NOI18N
        cbUseProjectPref.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.cbUseProjectPref.toolTipText")); // NOI18N

        lblLinefeed.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLinefeed.setLabelFor(cbLinefeed);
        org.openide.awt.Mnemonics.setLocalizedText(lblLinefeed, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.lblLinefeed.text")); // NOI18N
        lblLinefeed.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.lblLinefeed.toolTipText")); // NOI18N

        cbLinefeed.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "System", "\\n", "\\r\\n", "\\r" }));
        cbLinefeed.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.cbLinefeed.toolTipText")); // NOI18N
        cbLinefeed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbLinefeedActionPerformed(evt);
            }
        });

        lblSourceLevel.setLabelFor(cbSourceLevel);
        org.openide.awt.Mnemonics.setLocalizedText(lblSourceLevel, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.lblSourceLevel.text")); // NOI18N

        cbSourceLevel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No override", "1.9", "1.8", "1.7", "1.6", "1.5", "1.4" }));
        cbSourceLevel.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.cbSourceLevel.toolTipText")); // NOI18N
        cbSourceLevel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSourceLevelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblLinefeed, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbLinefeed, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblSourceLevel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbSourceLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(formatterLocField)
                                        .addGap(12, 12, 12)
                                        .addComponent(browseButton))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cbUseProjectPref)
                                            .addComponent(cbProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 0, Short.MAX_VALUE))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblFormatterFile)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFormatterFile)
                    .addComponent(formatterLocField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(errorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbProfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProfile))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbUseProjectPref)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLinefeed)
                    .addComponent(cbLinefeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSourceLevel)
                    .addComponent(cbSourceLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.jLabel2.AccessibleContext.accessibleName")); // NOI18N

        formatterBtnGrp.add(rbUseNetBeans);
        rbUseNetBeans.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(rbUseNetBeans, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.rbUseNetBeans.text")); // NOI18N
        rbUseNetBeans.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.rbUseNetBeans.toolTipText")); // NOI18N
        rbUseNetBeans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbUseNetBeansActionPerformed(evt);
            }
        });

        formatterBtnGrp.add(rbUseEclipse);
        org.openide.awt.Mnemonics.setLocalizedText(rbUseEclipse, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.rbUseEclipse.text")); // NOI18N
        rbUseEclipse.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.rbUseEclipse.toolTipText")); // NOI18N
        rbUseEclipse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbUseEclipseActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(txtProjectSpecificHint, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.txtProjectSpecificHint.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbShowNotifications, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.cbShowNotifications.text")); // NOI18N
        cbShowNotifications.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.cbShowNotifications.toolTipText")); // NOI18N
        cbShowNotifications.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbShowNotificationsActionPerformed(evt);
            }
        });

        btnDonate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(btnDonate, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.btnDonate.text")); // NOI18N
        btnDonate.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.btnDonate.toolTipText")); // NOI18N
        btnDonate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDonate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDonateMouseClicked(evt);
            }
        });

        btnVisitHomePage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        org.openide.awt.Mnemonics.setLocalizedText(btnVisitHomePage, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.btnVisitHomePage.text")); // NOI18N
        btnVisitHomePage.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVisitHomePage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnVisitHomePageMouseClicked(evt);
            }
        });

        formatterBtnGrp.add(rbUseGoogle);
        org.openide.awt.Mnemonics.setLocalizedText(rbUseGoogle, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.rbUseGoogle.text")); // NOI18N
        rbUseGoogle.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.rbUseGoogle.toolTipText")); // NOI18N
        rbUseGoogle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbUseGoogleActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        googleCodeStyleBtnGrp.add(googleCodeStyleRdBtn);
        googleCodeStyleRdBtn.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(googleCodeStyleRdBtn, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.googleCodeStyleRdBtn.text")); // NOI18N
        googleCodeStyleRdBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                googleCodeStyleRdBtnActionPerformed(evt);
            }
        });

        googleCodeStyleBtnGrp.add(aospRdBtn);
        org.openide.awt.Mnemonics.setLocalizedText(aospRdBtn, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.aospRdBtn.text")); // NOI18N
        aospRdBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aospRdBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(googleCodeStyleLbl, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.googleCodeStyleLbl.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(googleCodeStyleLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(googleCodeStyleRdBtn)
                .addGap(18, 18, 18)
                .addComponent(aospRdBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(googleCodeStyleRdBtn)
                    .addComponent(aospRdBtn)
                    .addComponent(googleCodeStyleLbl))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.openide.awt.Mnemonics.setLocalizedText(useIndentationSettingsChkBox, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.useIndentationSettingsChkBox.text")); // NOI18N
        useIndentationSettingsChkBox.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.useIndentationSettingsChkBox.toolTipText")); // NOI18N
        useIndentationSettingsChkBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useIndentationSettingsChkBoxActionPerformed(evt);
            }
        });

        overrideTabSizeSpn.setModel(new javax.swing.SpinnerNumberModel(4, 1, 20, 1));
        overrideTabSizeSpn.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.overrideTabSizeSpn.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(overrideTabSizeChkBox, org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.overrideTabSizeChkBox.text")); // NOI18N
        overrideTabSizeChkBox.setToolTipText(org.openide.util.NbBundle.getMessage(ExternalFormatterPanel.class, "ExternalFormatterPanel.overrideTabSizeChkBox.toolTipText")); // NOI18N
        overrideTabSizeChkBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overrideTabSizeChkBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rbUseNetBeans)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtProjectSpecificHint))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbShowNotifications)
                            .addComponent(rbUseGoogle)
                            .addComponent(rbUseEclipse)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(useIndentationSettingsChkBox)
                                .addGap(18, 18, 18)
                                .addComponent(overrideTabSizeChkBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(overrideTabSizeSpn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnVisitHomePage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnDonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbUseNetBeans)
                    .addComponent(txtProjectSpecificHint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbUseGoogle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbUseEclipse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(useIndentationSettingsChkBox)
                    .addComponent(overrideTabSizeSpn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(overrideTabSizeChkBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbShowNotifications)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVisitHomePage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void btnDonateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDonateMouseClicked
		try {
			HtmlBrowser.URLDisplayer.getDefault().showURLExternal(new URL("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=926F5XBCTK2LQ&source=url"));
		} catch (MalformedURLException ex) {
			Exceptions.printStackTrace(ex);
		}
	}//GEN-LAST:event_btnDonateMouseClicked

	private void btnVisitHomePageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVisitHomePageMouseClicked
		try {
			HtmlBrowser.URLDisplayer.getDefault().showURLExternal(new URL("https://github.com/funfried/externalcodeformatter_for_netbeans/"));
		} catch (MalformedURLException ex) {
			Exceptions.printStackTrace(ex);
		}
	}//GEN-LAST:event_btnVisitHomePageMouseClicked

	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
		//The default dir to use if no value is stored
		File home = new File(System.getProperty("user.home"));
		final FileNameExtensionFilter fileNameExtensionFilterXML = new FileNameExtensionFilter("Eclipse formatter (*.xml)", "xml");
		final FileNameExtensionFilter fileNameExtensionFilterEPF = new FileNameExtensionFilter("Workspace mechanic (*.epf)", "epf");
		final FileFilter fileNameExtensionFilterProjectSetting = new FileFilter() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				return EclipseJavaFormatterSettings.PROJECT_PREF_FILE.equals(f.getName());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String getDescription() {
				return "Eclipse project settings (" + EclipseJavaFormatterSettings.PROJECT_PREF_FILE + ")";
			}
		};
		//Now build a file chooser and invoke the dialog in one line of code
		//"user-dir" is our unique key
		File toAdd = new FileChooserBuilder("user-dir").setFileHiding(false).setFilesOnly(true).setTitle("Choose configuration ...").setDefaultWorkingDirectory(home).setApproveText("Choose")
				.addFileFilter(fileNameExtensionFilterProjectSetting).addFileFilter(fileNameExtensionFilterXML).addFileFilter(fileNameExtensionFilterEPF).setFileFilter(fileNameExtensionFilterXML)
				.showOpenDialog();
		//Result will be null if the user clicked cancel or closed the dialog w/o OK
		if (toAdd != null) {
			loadEclipseFormatterFileForPreview(toAdd.getAbsolutePath(), getSelectedProfile());
		}
	}//GEN-LAST:event_browseButtonActionPerformed

    private void rbUseEclipseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbUseEclipseActionPerformed
		updateEnabledState();
		fireChangedListener();
    }//GEN-LAST:event_rbUseEclipseActionPerformed

    private void rbUseNetBeansActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbUseNetBeansActionPerformed
		updateEnabledState();
		fireChangedListener();
    }//GEN-LAST:event_rbUseNetBeansActionPerformed

    private void formatterLocFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formatterLocFieldActionPerformed
		loadEclipseFormatterFileForPreview(formatterLocField.getText(), getSelectedProfile());
		fireChangedListener();
    }//GEN-LAST:event_formatterLocFieldActionPerformed

    private void cbShowNotificationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbShowNotificationsActionPerformed
		fireChangedListener();
    }//GEN-LAST:event_cbShowNotificationsActionPerformed

    private void cbProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbProfileActionPerformed
		fireChangedListener();
    }//GEN-LAST:event_cbProfileActionPerformed

    private void cbLinefeedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbLinefeedActionPerformed
		fireChangedListener();
    }//GEN-LAST:event_cbLinefeedActionPerformed

    private void cbSourceLevelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSourceLevelActionPerformed
		fireChangedListener();
    }//GEN-LAST:event_cbSourceLevelActionPerformed

    private void rbUseGoogleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbUseGoogleActionPerformed
		updateEnabledState();
		fireChangedListener();
    }//GEN-LAST:event_rbUseGoogleActionPerformed

    private void googleCodeStyleRdBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_googleCodeStyleRdBtnActionPerformed
		updateEnabledState();
		fireChangedListener();
    }//GEN-LAST:event_googleCodeStyleRdBtnActionPerformed

    private void aospRdBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aospRdBtnActionPerformed
		updateEnabledState();
		fireChangedListener();
    }//GEN-LAST:event_aospRdBtnActionPerformed

    private void useIndentationSettingsChkBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useIndentationSettingsChkBoxActionPerformed
		updateEnabledState();
		fireChangedListener();
    }//GEN-LAST:event_useIndentationSettingsChkBoxActionPerformed

    private void overrideTabSizeChkBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overrideTabSizeChkBoxActionPerformed
		updateEnabledState();
		fireChangedListener();
    }//GEN-LAST:event_overrideTabSizeChkBoxActionPerformed

	/**
	 * Returns the selected Eclipse formatter profile.
	 *
	 * @return the selected Eclipse formatter profile
	 */
	private String getSelectedProfile() {
		if (null != cbProfile.getSelectedItem()) {
			return cbProfile.getSelectedItem().toString();
		} else {
			return "";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load() {
		String enabledFormatter = preferences.get(Settings.ENABLED_FORMATTER, Settings.DEFAULT_FORMATTER);

		boolean googleFormatterEnabled = GoogleJavaFormatterService.ID.equals(enabledFormatter);
		String googleFormatterCodeStyle = preferences.get(GoogleJavaFormatterSettings.GOOGLE_FORMATTER_CODE_STYLE, JavaFormatterOptions.Style.GOOGLE.name());
		String eclipseFormatterLocation = preferences.get(EclipseJavaFormatterSettings.ECLIPSE_FORMATTER_CONFIG_FILE_LOCATION, "");
		String eclipseFormatterProfile = preferences.get(EclipseJavaFormatterSettings.ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
		boolean eclipseFormatterEnabled = EclipseJavaFormatterService.ID.equals(enabledFormatter);
		boolean showNotifications = preferences.getBoolean(Settings.SHOW_NOTIFICATIONS, false);
		boolean useIndentationSettings = preferences.getBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, true);
		boolean overrideTabSize = preferences.getBoolean(Settings.OVERRIDE_TAB_SIZE, false);
		int overrideTabSizeValue = preferences.getInt(Settings.OVERRIDE_TAB_SIZE_VALUE, 4);
		boolean useProjectPrefs = preferences.getBoolean(EclipseJavaFormatterSettings.USE_PROJECT_PREFS, true);
		String lineFeed = preferences.get(EclipseJavaFormatterSettings.LINEFEED, "");
		String sourceLevel = preferences.get(EclipseJavaFormatterSettings.SOURCELEVEL, "");

		loadEclipseFormatterFileForPreview(eclipseFormatterLocation, eclipseFormatterProfile);

		if (eclipseFormatterEnabled) {
			formatterBtnGrp.setSelected(rbUseEclipse.getModel(), true);
		} else if (googleFormatterEnabled) {
			formatterBtnGrp.setSelected(rbUseGoogle.getModel(), true);
		} else {
			formatterBtnGrp.setSelected(rbUseNetBeans.getModel(), true);
		}

		if (JavaFormatterOptions.Style.AOSP.name().equals(googleFormatterCodeStyle)) {
			googleCodeStyleBtnGrp.setSelected(aospRdBtn.getModel(), true);
		} else {
			googleCodeStyleBtnGrp.setSelected(googleCodeStyleRdBtn.getModel(), true);
		}

		useIndentationSettingsChkBox.setSelected(useIndentationSettings);
		overrideTabSizeChkBox.setSelected(overrideTabSize);
		overrideTabSizeSpn.setValue(overrideTabSizeValue);

		cbShowNotifications.setSelected(showNotifications);

		cbUseProjectPref.setSelected(useProjectPrefs);

		if (null == lineFeed || "".equals(lineFeed)) {
			//default = system-dependend LF
			cbLinefeed.setSelectedIndex(0);
		} else {
			cbLinefeed.setSelectedItem(lineFeed);
		}

		if (null == sourceLevel || "".equals(sourceLevel)) {
			//default = No override
			cbSourceLevel.setSelectedIndex(0);
		} else {
			cbSourceLevel.setSelectedItem(sourceLevel);
		}

		updateEnabledState();

		fireChangedListener();
	}

	@NbBundle.Messages("ChooseProfile=--Choose profile--")
	private void loadEclipseFormatterFileForPreview(String formatterFile, String activeProfile) {
		formatterLocField.setText(formatterFile);
		final File file = new File(formatterFile);

		cbProfile.setEnabled(false);
		lblProfile.setEnabled(false);

		cbProfile.removeAllItems();
		if (file.exists()) {
			try {
				final FileObject fo = ConfigReader.toFileObject(file);

				//only xml configurations contain profiles
				if (EclipseJavaFormatterSettings.isXMLConfigurationFile(fo.getNameExt())) {
					List<String> profileNames = ConfigReader.getProfileNames(fo);
					cbProfile.addItem(Bundle.ChooseProfile());

					String entryToSelect = null;
					for (String profileName : profileNames) {
						cbProfile.addItem(profileName);
						if (activeProfile != null && activeProfile.equals(profileName)) {
							entryToSelect = profileName;
						}
					}
					selectProfileOrFallback(entryToSelect, profileNames);
					cbProfile.setEnabled(true);
					lblProfile.setEnabled(true);
				}
			} catch (IOException | SAXException | ConfigReadException ex) {
				log.log(Level.WARNING, "Could not parse formatter config", ex);
			}
		}
	}

	private void selectProfileOrFallback(String entryToSelect, List<String> profiles) {
		if (null != entryToSelect) {
			cbProfile.setSelectedItem(entryToSelect);
		} else if (profiles.size() == 1) {
			//only one entry (excl. default) -> choose the only valid item
			cbProfile.setSelectedIndex(1);
		} else {
			//fallback: ===choose profile==
			cbProfile.setSelectedIndex(0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store() {
		preferences.put(Settings.ENABLED_FORMATTER, rbUseGoogle.isSelected() ? GoogleJavaFormatterService.ID : rbUseEclipse.isSelected() ? EclipseJavaFormatterService.ID : Settings.DEFAULT_FORMATTER);
		preferences.put(GoogleJavaFormatterSettings.GOOGLE_FORMATTER_CODE_STYLE, googleCodeStyleRdBtn.isSelected() ? JavaFormatterOptions.Style.GOOGLE.name() : JavaFormatterOptions.Style.AOSP.name());
		preferences.put(EclipseJavaFormatterSettings.ECLIPSE_FORMATTER_CONFIG_FILE_LOCATION, formatterLocField.getText());
		preferences.putBoolean(Settings.ENABLE_USE_OF_INDENTATION_SETTINGS, useIndentationSettingsChkBox.isSelected());
		preferences.putBoolean(Settings.OVERRIDE_TAB_SIZE, overrideTabSizeChkBox.isSelected());
		preferences.putInt(Settings.OVERRIDE_TAB_SIZE_VALUE, Integer.parseInt(overrideTabSizeSpn.getValue().toString()));
		preferences.putBoolean(Settings.SHOW_NOTIFICATIONS, cbShowNotifications.isSelected());
		preferences.put(EclipseJavaFormatterSettings.ECLIPSE_FORMATTER_ACTIVE_PROFILE, getSelectedProfile());
		preferences.putBoolean(EclipseJavaFormatterSettings.USE_PROJECT_PREFS, cbUseProjectPref.isSelected());
		preferences.put(EclipseJavaFormatterSettings.LINEFEED, getLinefeed());
		if (cbSourceLevel.getSelectedIndex() >= 1) {
			preferences.put(EclipseJavaFormatterSettings.SOURCELEVEL, "" + cbSourceLevel.getSelectedItem());
		} else {
			preferences.put(EclipseJavaFormatterSettings.SOURCELEVEL, "");
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
		errorLabel.setText(" ");
		if (rbUseEclipse.isSelected()) {
			final String fileName = formatterLocField.getText();
			final File file = new File(fileName);
			final boolean isXML = EclipseJavaFormatterSettings.isXMLConfigurationFile(file.getName());
			final boolean isEPF = EclipseJavaFormatterSettings.isWorkspaceMechanicFile(file.getName());
			final boolean isProjectSetting = EclipseJavaFormatterSettings.isProjectSetting(file.getName());
			if (isXML && cbProfile.getSelectedIndex() == 0) {
				//"choose profile" entry is selected
				return false;
			}

			//use configuration from .settings
			if (cbUseProjectPref.isSelected()) {
				return true;
			}

			if (file.exists() && (isXML || isEPF || isProjectSetting)) {
				return true;
			} else {
				errorLabel.setText("Invalid file. Please enter a valid configuration file.");
				return false;
			}
		}
		return true;
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton aospRdBtn;
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel btnDonate;
    private javax.swing.JLabel btnVisitHomePage;
    private javax.swing.JComboBox<String> cbLinefeed;
    private javax.swing.JComboBox<String> cbProfile;
    private javax.swing.JCheckBox cbShowNotifications;
    private javax.swing.JComboBox<String> cbSourceLevel;
    private javax.swing.JCheckBox cbUseProjectPref;
    private javax.swing.JLabel errorLabel;
    private javax.swing.ButtonGroup formatterBtnGrp;
    private javax.swing.JTextField formatterLocField;
    private javax.swing.ButtonGroup googleCodeStyleBtnGrp;
    private javax.swing.JLabel googleCodeStyleLbl;
    private javax.swing.JRadioButton googleCodeStyleRdBtn;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblFormatterFile;
    private javax.swing.JLabel lblLinefeed;
    private javax.swing.JLabel lblProfile;
    private javax.swing.JLabel lblSourceLevel;
    private javax.swing.JCheckBox overrideTabSizeChkBox;
    private javax.swing.JSpinner overrideTabSizeSpn;
    private javax.swing.JRadioButton rbUseEclipse;
    private javax.swing.JRadioButton rbUseGoogle;
    private javax.swing.JRadioButton rbUseNetBeans;
    private javax.swing.JLabel txtProjectSpecificHint;
    private javax.swing.JCheckBox useIndentationSettingsChkBox;
    // End of variables declaration//GEN-END:variables

	private void updateEnabledState() {
		boolean isEclipseFormatterEnabled = rbUseEclipse.isSelected();
		boolean isGoogleFormatterEnabled = rbUseGoogle.isSelected();

		lblFormatterFile.setEnabled(isEclipseFormatterEnabled);
		browseButton.setEnabled(isEclipseFormatterEnabled);
		formatterLocField.setEnabled(isEclipseFormatterEnabled);

		if (cbProfile.getSelectedIndex() != -1) {
			lblProfile.setEnabled(isEclipseFormatterEnabled);
			cbProfile.setEnabled(isEclipseFormatterEnabled);
		} else {
			lblProfile.setEnabled(false);
			cbProfile.setEnabled(false);
		}

		useIndentationSettingsChkBox.setEnabled(isEclipseFormatterEnabled || isGoogleFormatterEnabled);
		overrideTabSizeChkBox.setEnabled(useIndentationSettingsChkBox.isEnabled() && useIndentationSettingsChkBox.isSelected());
		overrideTabSizeSpn.setEnabled(overrideTabSizeChkBox.isEnabled() && overrideTabSizeChkBox.isSelected());

		cbUseProjectPref.setEnabled(isEclipseFormatterEnabled);
		cbLinefeed.setEnabled(isEclipseFormatterEnabled);
		lblLinefeed.setEnabled(isEclipseFormatterEnabled);
		cbSourceLevel.setEnabled(isEclipseFormatterEnabled);
		lblSourceLevel.setEnabled(isEclipseFormatterEnabled);

		googleCodeStyleLbl.setEnabled(isGoogleFormatterEnabled);
		googleCodeStyleRdBtn.setEnabled(isGoogleFormatterEnabled);
		aospRdBtn.setEnabled(isGoogleFormatterEnabled);

		txtProjectSpecificHint.setVisible(!showsProjectSettings);
	}

	private String getLinefeed() {
		if (0 == cbLinefeed.getSelectedIndex()) {
			return "";
		}
		return cbLinefeed.getSelectedItem().toString();
	}

}
