package sonar.flux.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.flux.api.IFluxCommon;
import sonar.flux.api.IFluxNetwork;
import sonar.flux.connection.BasicFluxNetwork;
import sonar.flux.connection.EmptyFluxNetwork;

public class CommonNetworkCache<C extends CommonNetworkCache, T extends IFluxCommon> {
	
	public ConcurrentHashMap<String, ArrayList<T>> networks = new ConcurrentHashMap<String, ArrayList<T>>();
	public ConcurrentHashMap<Integer, T> allNetworks = new ConcurrentHashMap<Integer, T>();
	
	public enum ViewingType {
		ADMIN, CLIENT, ONE_NET, CONNECTIONS;

		public boolean forceSync() {
			return this == ONE_NET;
		}
	}
	
	//network id will now be unique across all available networks to make life easier
	public int uniqueID = 1;
	public final static IFluxNetwork empty = new EmptyFluxNetwork();

	public void clearNetworks() {
		networks.clear();
		allNetworks.clear();
	}

	public int createNewUniqueID() {
		int id = uniqueID++;
		return id;
	}

	public ArrayList<T> getAllNetworks() {
		ArrayList<T> available = new ArrayList();
		for (Entry<String, ArrayList<T>> entry : networks.entrySet()) {
			available.addAll(entry.getValue());
		}
		return available;
	}

	public void addNetwork(T common) {
		networks.putIfAbsent(common.getOwnerName(), new ArrayList());
		networks.get(common.getOwnerName()).add(common);
		allNetworks.put(common.getNetworkID(), common);
	}

	public void removeNetwork(T common) {
		networks.putIfAbsent(common.getOwnerName(), new ArrayList());
		networks.get(common.getOwnerName()).remove(common);
		allNetworks.remove(common.getNetworkID());
		
	}

	public T getNetwork(int iD) {
		ArrayList<T> networks = getAllNetworks();
		for(T common : networks){
			if(!common.isFakeNetwork() && iD==common.getNetworkID()){
				return common;
			}
		}
		return (T) empty;
	}
	/*
	public static IFluxCommon getNetwork(String owner, String playerName, boolean admin, int iD) {
		ArrayList<IFluxCommon> available = getAllowedNetworks(playerName, admin);
		for (IFluxCommon network : available) {
			if (!network.isFakeNetwork()) {
				if (network.getOwnerName().equals(owner) && network.getNetworkID() == iD) {
					return network;
				}
			}
		}
		return empty;
	}
	*/

	public ArrayList<? extends IFluxCommon> fromBytes(ByteBuf buf) {
		NBTTagCompound compound = ByteBufUtils.readTag(buf);
		NBTTagList list = compound.getTagList("nets", 10);
		tags: for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound c = list.getCompoundTagAt(i);
			BasicFluxNetwork net = new BasicFluxNetwork(c);
			net.readData(c, SyncType.SAVE);
			String name = net.getOwnerName();
			((ConcurrentMap<String, ArrayList<T>>) networks).putIfAbsent(name, new ArrayList());
			for (T current : networks.get(name)) {
				if (current.getNetworkID() == net.getNetworkID()) {
					current.readData(c, SyncType.SAVE);
					continue tags;
				}
			}
			addNetwork((T) net);
		}
		return null;
	}

	public static void toBytes(ByteBuf buf, ArrayList<? extends IFluxCommon> networks) {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (IFluxCommon network : networks) {
			if (network.getNetworkID() != -1) {
				NBTTagCompound netTag = new NBTTagCompound();
				network.writeData(netTag, SyncType.SAVE);
				list.appendTag(netTag);
			}
		}
		tag.setTag("nets", list);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static IMessage onPacket(PacketFluxNetworkList message, MessageContext ctx) {
		return null;
	}
}
