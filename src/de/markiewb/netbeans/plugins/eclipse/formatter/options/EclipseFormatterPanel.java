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

import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.ECLIPSE_FORMATTER_ACTIVE_PROFILE;
import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.ECLIPSE_FORMATTER_ENABLED;
import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.ECLIPSE_FORMATTER_LOCATION;
import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.ENABLE_SAVEACTION;
import static de.markiewb.netbeans.plugins.eclipse.formatter.Preferences.SHOW_NOTIFICATIONS;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.prefs.Preferences;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.EditorKit;
import de.markiewb.netbeans.plugins.eclipse.formatter.customizer.VerifiableConfigPanel;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.ConfigReadException;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.ConfigReader;
import de.markiewb.netbeans.plugins.eclipse.formatter.xml.Profile;
import java.util.List;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.xml.sax.SAXException;

public class EclipseFormatterPanel extends javax.swing.JPanel implements VerifiableConfigPanel{
    private final Preferences preferences;
    private final boolean showsProjectSettings;
    
    public Preferences getPreferences() {
        return preferences;
    }
    
    private transient final Collection<ChangeListener> changeListeners=new ArrayList<ChangeListener>();

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public EclipseFormatterPanel(Preferences preferences, boolean showsProjectSettings) {
        initComponents();
        enableUI();
        
        
        EditorKit kit = CloneableEditorSupport.getEditorKit("text/xml");
        previewPane.setEditorKit(kit);
        
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
            }
        });
        cbProfile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fireChangedListener();
            }
        });
        this.preferences = preferences;
        this.showsProjectSettings = showsProjectSettings;
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
        jLabel2 = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        formatterLocField = new javax.swing.JTextField();
        errorLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        previewPane = new javax.swing.JEditorPane();
        jLabel3 = new javax.swing.JLabel();
        cbProfile = new javax.swing.JComboBox();
        rbUseNetBeans = new javax.swing.JRadioButton();
        rbUseEclipse = new javax.swing.JRadioButton();
        txtProjectSpecificHint = new javax.swing.JLabel();
        cbShowNotifications = new javax.swing.JCheckBox();
        cbEnableSaveAction = new javax.swing.JCheckBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        jLabel1.setLabelFor(formatterLocField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.jLabel1.text")); // NOI18N

        formatterLocField.setText(org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.formatterLocField.text")); // NOI18N

        errorLabel.setForeground(new java.awt.Color(255, 51, 51));
        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.errorLabel.text")); // NOI18N
        errorLabel.setToolTipText(org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.errorLabel.toolTipText")); // NOI18N

        previewPane.setEditable(false);
        previewPane.setFocusable(false);
        previewPane.setHighlighter(null
        );
        jScrollPane2.setViewportView(previewPane);

        jLabel3.setLabelFor(cbProfile);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.jLabel3.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbProfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(formatterLocField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton)
                                .addGap(7, 7, 7)))))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(errorLabel)
                    .addComponent(jLabel3)
                    .addComponent(cbProfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addContainerGap())
        );

        buttonGroup1.add(rbUseNetBeans);
        org.openide.awt.Mnemonics.setLocalizedText(rbUseNetBeans, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.rbUseNetBeans.text")); // NOI18N

        buttonGroup1.add(rbUseEclipse);
        org.openide.awt.Mnemonics.setLocalizedText(rbUseEclipse, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.rbUseEclipse.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(txtProjectSpecificHint, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.txtProjectSpecificHint.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbShowNotifications, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.cbShowNotifications.text")); // NOI18N
        cbShowNotifications.setToolTipText(org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.cbShowNotifications.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(cbEnableSaveAction, org.openide.util.NbBundle.getMessage(EclipseFormatterPanel.class, "EclipseFormatterPanel.cbEnableSaveAction.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rbUseNetBeans)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtProjectSpecificHint))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbEnableSaveAction)
                            .addComponent(cbShowNotifications)
                            .addComponent(rbUseEclipse))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbUseNetBeans)
                    .addComponent(txtProjectSpecificHint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbUseEclipse)
                .addGap(4, 4, 4)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbShowNotifications)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbEnableSaveAction)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home"));
        final FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("Eclipse formatter (*.xml)", "xml");
        //Now build a file chooser and invoke the dialog in one line of code
        //"user-dir" is our unique key
        File toAdd = new FileChooserBuilder("user-dir").setFilesOnly(true).setTitle("Choose Eclipse formatter file ...").
        setDefaultWorkingDirectory(home).setApproveText("Choose").
        addFileFilter(fileNameExtensionFilter).setFileFilter(fileNameExtensionFilter).
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
        loadOptionsWindowUI(isEclipseFormatterEnabled, eclipseFormatterLocation, eclipseFormatterProfile, showNotifications, enableSaveAction);
        fireChangedListener();
    }

    private void loadOptionsWindowUI(boolean isEclipseFormatterEnabled, String formatterFile, String profile, boolean showNotifications, boolean enableSaveAction) {
        loadEclipseFormatterFileForPreview(formatterFile, profile);
        
        if (isEclipseFormatterEnabled) {
            buttonGroup1.setSelected(rbUseEclipse.getModel(), true);
        }else{
            buttonGroup1.setSelected(rbUseNetBeans.getModel(), true);
        }
        
        cbShowNotifications.setSelected(showNotifications);
        cbEnableSaveAction.setSelected(enableSaveAction);
        enableUI();
    }

    @NbBundle.Messages(
    "ChooseProfile=--Choose profile--")
    private void loadEclipseFormatterFileForPreview(String formatterFile, String activeProfile) {
        
        formatterLocField.setText(formatterFile);
        final File file = new File(formatterFile);
        
        if (file.exists()) {
            final File globalNormalizedFile = FileUtil.normalizeFile(file);
            try {
                previewPane.setText(FileUtil.toFileObject(globalNormalizedFile).asText());
                List<Profile> profiles = new ConfigReader().read(file);
                cbProfile.removeAllItems();
                cbProfile.addItem(Bundle.ChooseProfile());

                String entryToSelect = null;
                for (Profile profile : profiles) {
                    cbProfile.addItem(profile.getName());
                    if (activeProfile != null && activeProfile.equals(profile.getName())) {
                        entryToSelect = profile.getName();
                    }
                }
                selectProfileOrFallback(entryToSelect, profiles);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (SAXException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ConfigReadException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void selectProfileOrFallback(String entryToSelect, List<Profile> profiles) {
        if (null != entryToSelect) {
            cbProfile.setSelectedItem(entryToSelect);
        } else {
            if (profiles.size() == 1) {
                //only one entry (excl. default) -> choose the only valid item
                cbProfile.setSelectedIndex(1);
            } else {
                //fallback: ===choose profile==
                cbProfile.setSelectedIndex(0);
            }
        }
    }

    @Override
    public void store() {
        preferences.put(ECLIPSE_FORMATTER_LOCATION, formatterLocField.getText());
        preferences.putBoolean(ECLIPSE_FORMATTER_ENABLED, rbUseEclipse.isSelected());
        preferences.putBoolean(SHOW_NOTIFICATIONS, cbShowNotifications.isSelected());
        preferences.putBoolean(ENABLE_SAVEACTION, cbEnableSaveAction.isSelected());
        preferences.put(ECLIPSE_FORMATTER_ACTIVE_PROFILE, getSelectedProfile());
    }

    boolean valid() {
        errorLabel.setText(" ");
        if (rbUseEclipse.isSelected()) {
            if (cbProfile.getSelectedIndex() == 0) {
                //"choose profile" entry is selected
                return false;
            }
            
            final String fileName = formatterLocField.getText();
            final File file = new File(fileName);
            if (file.exists() && file.getName().endsWith("xml")) {
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
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbEnableSaveAction;
    private javax.swing.JComboBox cbProfile;
    private javax.swing.JCheckBox cbShowNotifications;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JTextField formatterLocField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JEditorPane previewPane;
    private javax.swing.JRadioButton rbUseEclipse;
    private javax.swing.JRadioButton rbUseNetBeans;
    private javax.swing.JLabel txtProjectSpecificHint;
    // End of variables declaration//GEN-END:variables

    private void enableUI() {
        final boolean isEnabled = rbUseEclipse.isSelected();
        jLabel1.setEnabled(isEnabled);
        jLabel2.setEnabled(isEnabled);
        browseButton.setEnabled(isEnabled);
        formatterLocField.setEnabled(isEnabled);
        previewPane.setEnabled(isEnabled);
        jLabel3.setEnabled(isEnabled);
        cbProfile.setEnabled(isEnabled);
        
        txtProjectSpecificHint.setVisible(!showsProjectSettings);
    }

    @Override
    public boolean holdsValidConfig() {
        return valid();
    }

}
