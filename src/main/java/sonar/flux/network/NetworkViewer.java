package sonar.flux.network;

import net.minecraft.entity.player.EntityPlayer;
import sonar.flux.network.FluxNetworkCache.ViewingType;

public class NetworkViewer {
	public boolean sentFirstPacket = false;
	public final EntityPlayer player;
	public final ViewingType type;

	public NetworkViewer(EntityPlayer player, ViewingType type) {
		this.player = player;
		this.type = type;
	}

	public void sentFirstPacket() {
		this.sentFirstPacket = true;
	}
	
	public boolean equals(Object obj){		
		if(obj!=null && obj instanceof NetworkViewer){
			return ((NetworkViewer)obj).player.equals(player);
		}		
		return false;
	}
}
