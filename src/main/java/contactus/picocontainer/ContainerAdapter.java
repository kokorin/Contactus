package contactus.picocontainer;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

import java.lang.reflect.Type;

/**
 * Allows injecting of PicoContainer into components
 */
public class ContainerAdapter implements ComponentAdapter<PicoContainer>{
    @Override
    public Object getComponentKey() {
        return PicoContainer.class;
    }

    @Override
    public Class<? extends PicoContainer> getComponentImplementation() {
        return PicoContainer.class;
    }

    @Override
    public PicoContainer getComponentInstance(PicoContainer container) throws PicoCompositionException {
        return container;
    }

    @Override
    public PicoContainer getComponentInstance(PicoContainer container, Type into) throws PicoCompositionException {
        return container;
    }

    @Override
    public void verify(PicoContainer container) throws PicoCompositionException {

    }

    @Override
    public void accept(PicoVisitor visitor) {

    }

    @Override
    public ComponentAdapter<PicoContainer> getDelegate() {
        return null;
    }

    @Override
    public <U extends ComponentAdapter> U findAdapterOfType(Class<U> adapterType) {
        return null;
    }

    @Override
    public String getDescriptor() {
        return "ContainerAdapter";
    }
}
