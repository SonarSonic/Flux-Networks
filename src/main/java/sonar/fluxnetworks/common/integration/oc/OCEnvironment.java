package sonar.fluxnetworks.common.integration.oc;
/* TODO OPEN COMPUTERS INTEGRATION
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;

public class OCEnvironment extends AbstractManagedEnvironment implements NamedBlock, ManagedPeripheral {

    public IOCPeripheral tile;

    public OCEnvironment(IOCPeripheral tile) {
        this.tile = tile;
        this.setNode(Network.newNode(this, Visibility.Network).withComponent(tile.getPeripheralName(), Visibility.Network).create());
    }

    @Override
    public String preferredName() {
        return tile.getPeripheralName();
    }

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public String[] methods() {
        return tile.getOCMethods();
    }

    @Override
    public Object[] invoke(String s, Context context, Arguments arguments) {
        return tile.invokeMethods(s, arguments);
    }
}
*/