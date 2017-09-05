package sonar.flux.api.tiles;

import sonar.core.listener.ISonarListenable;
import sonar.core.listener.PlayerListener;

public interface IFluxListenable extends IFlux, ISonarListenable<PlayerListener> {
}