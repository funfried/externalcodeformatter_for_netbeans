/*
 * Copyright (c) 2013 markiewb.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    markiewb - initial API and implementation and/or initial documentation
 */
package de.markiewb.netbeans.plugins.eclipse.formatter.options;

import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.ECLIPSE_FORMATTER_ACTIVE_PROFILE;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.ECLIPSE_FORMATTER_ENABLED;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.ECLIPSE_FORMATTER_LOCATION;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.ENABLE_SAVEACTION;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.PRESERVE_BREAKPOINTS;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.PROJECT_PREF_FILE;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.SHOW_NOTIFICATIONS;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.USE_PROJECT_PREFS;
import de.markiewb.netbeans.plugins.eclipse.formatter.customizer.VerifiableConfigPanel;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.ENABLE_SAVEACTION_MODIFIEDLINESONLY;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.FEATURE_formatChangedLinesOnly;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.LINEFEED;
import static de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.SOURCELEVEL;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.ConfigReadException;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.ConfigReader;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.Profile;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.xml.sax.SAXException;

@Keywords(location = "Java", tabTitle = "Eclipse Formatter 4.4", keywords = {"eclipse", "format", "eclipse formatter"})
public class EclipseFormatterPanel44 extends javax.swing.JPanel implements VerifiableConfigPanel {

    private final Preferences preferences;
    private final boolean showsProjectSettings;

    public Preferences getPreferences() {
        return preferences;
    }

    private transient final Collection<ChangeListener> changeListeners = new ArrayList<>();

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public EclipseFormatterPanel44(Preferences preferences, boolean showsProjectSettings) {
        initComponents();
        enableUI();

        formatterLocField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fireChangedListener();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fireChangedListener();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fireChangedListener();
            }
        });
        rbUseEclipse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableUI();
                fireChangedListener();
            }
        });
        rbUseNetBeans.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableUI();
                fireChangedListener();
            }
        });
        formatterLocField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadEclipseFormatterFileForPreview(formatterLocField.getText(), getSelectedProfile());
                fireChangedListener();
            }
        });
        cbShowNotifications.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireChangedListener();
            }
        });
        cbEnableSaveAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireChangedListener();
                cbEnableSaveActionModifiedLinesOnly.setEnabled(cbEnableSaveAction.isSelected());
            }
        });
        cbEnableSaveActionModifiedLinesOnly.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireChangedListener();
            }
        });
        cbProfile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fireChangedListener();
            }
        });
        cbPreserveBreakpoints.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fireChangedListener();
            }
        });
        cbSourceLevel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fireChangedListener();
            }
        });
        this.preferences = preferences;
        this.showsProjectSettings = showsProjectSettings;
        this.cbEnableSaveActionModifiedLinesOnly.setVisible(FEATURE_formatChangedLinesOnly);
        this.cbEnableSaveActionModifiedLinesOnly.setEnabled(FEATURE_formatChangedLinesOnly);
    }

    private void fireChangedListener() {
        for (ChangeListener changeListener : changeListeners) {
            changeListener.stateChanged(null);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        browseButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        formatterLocField = new javax.swing.JTextField();
        errorLabel = new javax.swing.JLabel();
        lblProfile = new javax.swing.JLabel();
        cbProfile = new javax.swing.JComboBox();
        cbPreserveBreakpoints = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
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
        cbEnableSaveAction = new javax.swing.JCheckBox();
        btnDonate = new javax.swing.JLabel();
        btnVisitHomePage = new javax.swing.JLabel();
        cbEnableSaveActionModifiedLinesOnly = new javax.swing.JCheckBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        jLabel1.setLabelFor(formatterLocField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.jLabel1.text")); // NOI18N

        formatterLocField.setText(org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.formatterLocField.text")); // NOI18N

        errorLabel.setForeground(new java.awt.Color(255, 51, 51));
        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.errorLabel.text")); // NOI18N
        errorLabel.setToolTipText(org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.errorLabel.toolTipText")); // NOI18N

        lblProfile.setLabelFor(cbProfile);
        org.openide.awt.Mnemonics.setLocalizedText(lblProfile, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.lblProfile.text")); // NOI18N

        cbPreserveBreakpoints.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(cbPreserveBreakpoints, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.cbPreserveBreakpoints.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.jLabel4.text")); // NOI18N
        jLabel4.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.jLabel2.text")); // NOI18N
        jLabel2.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(cbUseProjectPref, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.cbUseProjectPref.text")); // NOI18N

        lblLinefeed.setLabelFor(cbLinefeed);
        org.openide.awt.Mnemonics.setLocalizedText(lblLinefeed, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.lblLinefeed.text")); // NOI18N
        lblLinefeed.setToolTipText(org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.lblLinefeed.toolTipText")); // NOI18N

        cbLinefeed.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "System", "\\n", "\\r\\n", "\\r" }));

        lblSourceLevel.setLabelFor(cbSourceLevel);
        org.openide.awt.Mnemonics.setLocalizedText(lblSourceLevel, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.lblSourceLevel.text")); // NOI18N

        cbSourceLevel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No override", "1.9", "1.8", "1.7", "1.6", "1.5", "1.4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(cbPreserveBreakpoints, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(cbUseProjectPref))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblSourceLevel))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblLinefeed))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblProfile)))
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(formatterLocField)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cbProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbLinefeed, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbSourceLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(12, 12, 12)
                        .addComponent(browseButton)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(formatterLocField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblProfile)
                    .addComponent(cbProfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbUseProjectPref)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbPreserveBreakpoints, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblLinefeed)
                    .addComponent(cbLinefeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbSourceLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSourceLevel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.jLabel2.AccessibleContext.accessibleName")); // NOI18N

        buttonGroup1.add(rbUseNetBeans);
        org.openide.awt.Mnemonics.setLocalizedText(rbUseNetBeans, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.rbUseNetBeans.text")); // NOI18N

        buttonGroup1.add(rbUseEclipse);
        org.openide.awt.Mnemonics.setLocalizedText(rbUseEclipse, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.rbUseEclipse.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(txtProjectSpecificHint, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.txtProjectSpecificHint.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbShowNotifications, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.cbShowNotifications.text")); // NOI18N
        cbShowNotifications.setToolTipText(org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.cbShowNotifications.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbEnableSaveAction, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.cbEnableSaveAction.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(btnDonate, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.btnDonate.text")); // NOI18N
        btnDonate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDonateMouseClicked(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(btnVisitHomePage, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.btnVisitHomePage.text")); // NOI18N
        btnVisitHomePage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnVisitHomePageMouseClicked(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cbEnableSaveActionModifiedLinesOnly, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel44.class, "EclipseFormatterPanel44.cbEnableSaveActionModifiedLinesOnly.text")); // NOI18N

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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cbEnableSaveAction)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbEnableSaveActionModifiedLinesOnly)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnDonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cbShowNotifications)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnVisitHomePage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rbUseEclipse)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbUseNetBeans)
                    .addComponent(txtProjectSpecificHint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbUseEclipse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbShowNotifications)
                    .addComponent(btnVisitHomePage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbEnableSaveAction)
                    .addComponent(btnDonate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbEnableSaveActionModifiedLinesOnly))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnDonateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDonateMouseClicked
        try {
            HtmlBrowser.URLDisplayer.getDefault().showURLExternal(new URL("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=K4CMP92RZELE2"));
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnDonateMouseClicked

    private void btnVisitHomePageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnVisitHomePageMouseClicked
        try {
            HtmlBrowser.URLDisplayer.getDefault().showURLExternal(new URL("https://github.com/markiewb/eclipsecodeformatter_for_netbeans/"));
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
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return PROJECT_PREF_FILE.equals(f.getName());
            }

            @Override
            public String getDescription() {
                return "Eclipse project settings (" + PROJECT_PREF_FILE + ")";
            }
        };
        //Now build a file chooser and invoke the dialog in one line of code
        //"user-dir" is our unique key
        File toAdd = new FileChooserBuilder("user-dir").setFileHiding(false).setFilesOnly(true).setTitle("Choose configuration ...").
        setDefaultWorkingDirectory(home).setApproveText("Choose").
        addFileFilter(fileNameExtensionFilterProjectSetting).
        addFileFilter(fileNameExtensionFilterXML).
        addFileFilter(fileNameExtensionFilterEPF).
        setFileFilter(fileNameExtensionFilterXML).
        showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (toAdd != null) {
            loadEclipseFormatterFileForPreview(toAdd.getAbsolutePath(), getSelectedProfile());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private String getSelectedProfile() {
        if (null != cbProfile.getSelectedItem()) {
            return cbProfile.getSelectedItem().toString();
        } else {
            return "";
        }
    }

    @Override
    public void load() {
        String eclipseFormatterLocation = preferences.get(ECLIPSE_FORMATTER_LOCATION, "");
        String eclipseFormatterProfile = preferences.get(ECLIPSE_FORMATTER_ACTIVE_PROFILE, "");
        boolean isEclipseFormatterEnabled = preferences.getBoolean(ECLIPSE_FORMATTER_ENABLED, false);
        boolean showNotifications = preferences.getBoolean(SHOW_NOTIFICATIONS, true);
        boolean enableSaveAction = preferences.getBoolean(ENABLE_SAVEACTION, false);
        boolean enableSaveActionModifiedLines = preferences.getBoolean(ENABLE_SAVEACTION_MODIFIEDLINESONLY, false);
        boolean preserveBreakpoints = preferences.getBoolean(PRESERVE_BREAKPOINTS, true);
        boolean useProjectPrefs = preferences.getBoolean(USE_PROJECT_PREFS, true);
        String lineFeed = preferences.get(LINEFEED, "");
        String sourceLevel = preferences.get(SOURCELEVEL, "");
        loadOptionsWindowUI(isEclipseFormatterEnabled, eclipseFormatterLocation, eclipseFormatterProfile, showNotifications, enableSaveAction, preserveBreakpoints, useProjectPrefs, enableSaveActionModifiedLines, lineFeed, sourceLevel);
        fireChangedListener();
    }

    private void loadOptionsWindowUI(boolean isEclipseFormatterEnabled, String formatterFile, String profile, boolean showNotifications, boolean enableSaveAction, boolean preserveBreakpoints, boolean useProjectPrefs, boolean enableSaveActionModifiedLines, String lineFeed, String sourceLevel) {
        loadEclipseFormatterFileForPreview(formatterFile, profile);

        if (isEclipseFormatterEnabled) {
            buttonGroup1.setSelected(rbUseEclipse.getModel(), true);
        } else {
            buttonGroup1.setSelected(rbUseNetBeans.getModel(), true);
        }

        cbShowNotifications.setSelected(showNotifications);
        cbEnableSaveAction.setSelected(enableSaveAction);
        cbEnableSaveActionModifiedLinesOnly.setSelected(enableSaveActionModifiedLines);
        cbPreserveBreakpoints.setSelected(preserveBreakpoints);
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
        enableUI();
    }

    @NbBundle.Messages(
            "ChooseProfile=--Choose profile--")
    private void loadEclipseFormatterFileForPreview(String formatterFile, String activeProfile) {

        formatterLocField.setText(formatterFile);
        final File file = new File(formatterFile);

        cbProfile.setEnabled(false);
        lblProfile.setEnabled(false);

        cbProfile.removeAllItems();
        if (file.exists()) {
            try {
                final FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(file));

                //only xml configurations contain profiles
                if (de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.isXMLConfigurationFile(fo.getNameExt())) {
                    List<Profile> profiles = new ConfigReader().read(file);
                    cbProfile.addItem(Bundle.ChooseProfile());

                    String entryToSelect = null;
                    for (Profile profile : profiles) {
                        cbProfile.addItem(profile.getName());
                        if (activeProfile != null && activeProfile.equals(profile.getName())) {
                            entryToSelect = profile.getName();
                        }
                    }
                    selectProfileOrFallback(entryToSelect, profiles);
                    cbProfile.setEnabled(true);
                    lblProfile.setEnabled(true);
                }
            } catch (IOException | SAXException | ConfigReadException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void selectProfileOrFallback(String entryToSelect, List<Profile> profiles) {
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

    @Override
    public void store() {
        preferences.put(ECLIPSE_FORMATTER_LOCATION, formatterLocField.getText());
        preferences.putBoolean(ECLIPSE_FORMATTER_ENABLED, rbUseEclipse.isSelected());
        preferences.putBoolean(SHOW_NOTIFICATIONS, cbShowNotifications.isSelected());
        preferences.putBoolean(ENABLE_SAVEACTION, cbEnableSaveAction.isSelected());
        if (FEATURE_formatChangedLinesOnly) {
            preferences.putBoolean(ENABLE_SAVEACTION_MODIFIEDLINESONLY, cbEnableSaveActionModifiedLinesOnly.isSelected());
        }
        preferences.put(ECLIPSE_FORMATTER_ACTIVE_PROFILE, getSelectedProfile());
        preferences.putBoolean(PRESERVE_BREAKPOINTS, cbPreserveBreakpoints.isSelected());
        preferences.putBoolean(USE_PROJECT_PREFS, cbUseProjectPref.isSelected());
        preferences.put(LINEFEED, getLinefeed());
        if (cbSourceLevel.getSelectedIndex() >= 1) {
            preferences.put(SOURCELEVEL, "" + cbSourceLevel.getSelectedItem());
        } else {
            preferences.put(SOURCELEVEL, "");
        }
    }

    boolean valid() {
        errorLabel.setText(" ");
        if (rbUseEclipse.isSelected()) {
            final String fileName = formatterLocField.getText();
            final File file = new File(fileName);
            final boolean isXML = de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.isXMLConfigurationFile(file.getName());
            final boolean isEPF = de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.isWorkspaceMechanicFile(file.getName());
            final boolean isProjectSetting = de.markiewb.netbeans.plugins.eclipse.formatter.options.Preferences.isProjectSetting(file.getName());
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
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel btnDonate;
    private javax.swing.JLabel btnVisitHomePage;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbEnableSaveAction;
    private javax.swing.JCheckBox cbEnableSaveActionModifiedLinesOnly;
    private javax.swing.JComboBox<String> cbLinefeed;
    private javax.swing.JCheckBox cbPreserveBreakpoints;
    private javax.swing.JComboBox cbProfile;
    private javax.swing.JCheckBox cbShowNotifications;
    private javax.swing.JComboBox<String> cbSourceLevel;
    private javax.swing.JCheckBox cbUseProjectPref;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JTextField formatterLocField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblLinefeed;
    private javax.swing.JLabel lblProfile;
    private javax.swing.JLabel lblSourceLevel;
    private javax.swing.JRadioButton rbUseEclipse;
    private javax.swing.JRadioButton rbUseNetBeans;
    private javax.swing.JLabel txtProjectSpecificHint;
    // End of variables declaration//GEN-END:variables

    private void enableUI() {
        final boolean isEnabled = rbUseEclipse.isSelected();
        jLabel1.setEnabled(isEnabled);
        browseButton.setEnabled(isEnabled);
        formatterLocField.setEnabled(isEnabled);
        if (-1 != cbProfile.getSelectedIndex()) {
            lblProfile.setEnabled(isEnabled);
            cbProfile.setEnabled(isEnabled);
        } else {
            lblProfile.setEnabled(false);
            cbProfile.setEnabled(false);
        }
        cbPreserveBreakpoints.setEnabled(isEnabled);
        cbUseProjectPref.setEnabled(isEnabled);
        cbLinefeed.setEnabled(isEnabled);
        lblLinefeed.setEnabled(isEnabled);
        cbSourceLevel.setEnabled(isEnabled);
        lblSourceLevel.setEnabled(isEnabled);

        txtProjectSpecificHint.setVisible(!showsProjectSettings);
    }

    @Override
    public boolean holdsValidConfig() {
        return valid();
    }

    String getLinefeed() {
        if (0 == cbLinefeed.getSelectedIndex()) {
            return "";
        }
        return cbLinefeed.getSelectedItem().toString();
    }

}
