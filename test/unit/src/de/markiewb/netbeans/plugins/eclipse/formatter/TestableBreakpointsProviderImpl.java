package de.markiewb.netbeans.plugins.eclipse.formatter;

import de.markiewb.netbeans.plugins.eclipse.formatter.strategies.eclipse.IBreakpointsProvider;
import java.util.Collection;
import java.util.Collections;
import org.netbeans.api.debugger.jpda.LineBreakpoint;

/**
 *
 * @author markiewb
 */
public class TestableBreakpointsProviderImpl implements IBreakpointsProvider {
    
    private Collection<LineBreakpoint> breakpoints;

    public TestableBreakpointsProviderImpl(Collection<LineBreakpoint> breakpoints) {
        this.breakpoints = breakpoints;
    }

    public static IBreakpointsProvider EMTPY() {
        return new TestableBreakpointsProviderImpl(Collections.<LineBreakpoint>emptyList());
    }

    @Override
    public Collection<LineBreakpoint> getBreakpoints() {
        return breakpoints;
    }
    
}
