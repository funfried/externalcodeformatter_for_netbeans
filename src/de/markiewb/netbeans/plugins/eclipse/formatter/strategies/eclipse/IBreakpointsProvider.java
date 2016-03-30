package de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse;

import java.util.Collection;
import org.netbeans.api.debugger.jpda.LineBreakpoint;

/**
 *
 * @author markiewb
 */
public interface IBreakpointsProvider {

    Collection<LineBreakpoint> getBreakpoints();
    
}
