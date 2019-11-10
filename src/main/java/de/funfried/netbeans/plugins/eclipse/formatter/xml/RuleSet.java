package de.funfried.netbeans.plugins.eclipse.formatter.xml;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.RuleSetBase;

/**
 * An Apache Commons Digester RuleSet for configuring a digester to parse the
 * Eclipse formatter config XML into objects.
 *
 * @author jecki
 * @author Matt Blanchette
 */
class RuleSet extends RuleSetBase {

    /**
     * @see
     * org.apache.commons.digester.RuleSetBase#addRuleInstances(org.apache.commons.digester.Digester)
     */
    @Override
    public void addRuleInstances(Digester digester) {
        digester.addObjectCreate("profiles", Profiles.class);
        digester.addObjectCreate("profiles/profile", Profile.class);
        digester.addObjectCreate("profiles/profile/setting", Setting.class);

        digester.addSetNext("profiles/profile", "addProfile");
        digester.addSetNext("profiles/profile/setting", "addSetting");

        digester.addSetProperties("profiles/profile", "kind", "kind");
        digester.addSetProperties("profiles/profile", "name", "name");
        digester.addSetProperties("profiles/profile", "version", "version");
        digester.addSetProperties("profiles/profile/setting", "id", "id");
        digester.addSetProperties("profiles/profile/setting", "value", "value");
    }

}