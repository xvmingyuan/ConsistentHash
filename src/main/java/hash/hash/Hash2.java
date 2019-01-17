package hash.hash;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Hash2 {
	/**
	 * 待添加入Hash环的服务器列表
	 */
	private static String[] servers = { "192.168.0.0:1001", "192.168.0.1:1001", "192.168.0.2:1001", "192.168.0.3:1001",
			"192.168.0.4:1001", "193.168.0.5:1001", "193.168.0.6:1001", "192.168.0.10:1001", "192.168.0.11:1001",
			"192.168.0.12:1001", "192.168.0.13:1001", "192.168.0.14:1001", "193.168.0.15:1001", "193.168.0.16:1001",
			"192.168.0.20:1001", "192.168.0.21:1001", "192.168.0.22:1001", "192.168.0.23:1001", "192.168.0.24:1001",
			"193.168.0.25:1001", "193.168.0.26:1001", "192.168.0.30:1001", "192.168.0.31:1001", "192.168.0.32:1001",
			"192.168.0.33:1001", "192.168.0.34:1001", "193.168.0.35:1001", "193.168.0.36:1001", "192.168.0.40:1001",
			"192.168.0.41:1001", "192.168.0.42:1001", "192.168.0.43:1001", "192.168.0.44:1001", "193.168.0.45:1001",
			"193.168.0.46:1001", "192.168.0.50:1001", "192.168.0.51:1001", "192.168.0.52:1001", "192.168.0.53:1001",
			"192.168.0.54:1001", "193.168.0.55:1001", "193.168.0.56:1001" };

	/**
	 * 真实结点列表,考虑到服务器上线、下线的场景，即添加、删除的场景会比较频繁，这里使用LinkedList会更好
	 */
	private static List<String> realNodes = new LinkedList<String>();

	/**
	 * 虚拟节点，key表示虚拟节点的hash值，value表示虚拟节点的名称
	 */
	private static SortedMap<Integer, String> virtualNodes = new TreeMap<Integer, String>();
	private static ConcurrentHashMap<String, Integer> IpCount = new ConcurrentHashMap<String, Integer>();
	/**
	 * 虚拟节点的数目，这里写死，为了演示需要，一个真实结点对应5个虚拟节点
	 */
	private static final int VIRTUAL_NODES = 5;

	static {
		// 先把原始的服务器添加到真实结点列表中
		for (int i = 0; i < servers.length; i++) {
			realNodes.add(servers[i]);
			IpCount.put(servers[i], 0);
		}
		for (String string : realNodes) {
			for (int i = 0; i < VIRTUAL_NODES; i++) {
				String virtualNodeName = string + "&&VN" + String.valueOf(i);
				int hash = getHash(virtualNodeName);
//				System.out.println("虚拟节点[" + virtualNodeName + "]被添加，hash值为：" + hash);
				virtualNodes.put(hash, virtualNodeName);
			}
		}
//		System.out.println();
	}

	/**
	 * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
	 */
	private static int getHash(String str) {
		final int p = 16777619;
		int hash = (int) 2166136261L;
		for (int i = 0; i < str.length(); i++)
			hash = (hash ^ str.charAt(i)) * p;
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;
		// 如果算出来的值为负数则取其绝对值
		if (hash < 0)
			hash = Math.abs(hash);
		return hash;
	}

	/**
	 * 得到应当路由到的结点
	 */
	private static String getServer(String node) {
		// 得到带路由的结点的Hash值
		int hash = getHash(node);
		// 得到大于该Hash值的所有Map
		SortedMap<Integer, String> subMap = virtualNodes.tailMap(hash);
		// 第一个Key就是顺时针过去离node最近的那个结点
		Integer i;
		String virtualNode;
		if (subMap.size() > 0) {
			// 查找最近节点
			i = subMap.firstKey();
			virtualNode = subMap.get(i);
		} else {
			// 闭环返回虚拟的第一个节点
//			System.out.print("循环节点---> ");
			i = virtualNodes.firstKey();
			virtualNode = virtualNodes.get(i);
		}
		// 返回对应的虚拟节点名称，这里字符串稍微截取一下
		virtualNode = virtualNode.substring(0, virtualNode.indexOf("&&"));
		
		synchronized (node) {
			IpCount.put(virtualNode, IpCount.get(virtualNode)+1);
		}
//		System.out.println("客户IP:" + node + "/hash:" + hash + "  最近服务节点IP：" + virtualNode + "/hash:" + i+"/访问数量:"+IpCount.get(virtualNode));
		return virtualNode;
	}

	public static void main(String[] args) {
		long startTime=System.currentTimeMillis();   //获取开始时间
		String[] nodes = {};
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= 800000; i++) {
			String ip = "127.100.1.";
			list.add(ip + i);
		}
		nodes = list.toArray(nodes);
		for (String IP : nodes) {
			getServer(IP);
		}
		long endTime=System.currentTimeMillis(); //获取结束时间
		TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>();
		Set<Entry<String, Integer>> entrySet = IpCount.entrySet();
		Integer sum = 0;
		for (Entry<String, Integer> entry : entrySet) {
			treeMap.put(entry.getValue(), entry.getKey());
//			System.out.println(entry.getKey()+"/访问量:"+entry.getValue());
			synchronized (sum) {
				sum = sum +entry.getValue();
			}
		}
		System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
		System.out.println("性能测试：总访问"+sum+ " 最小访问数IP"+treeMap.get(treeMap.firstKey())+"=="+treeMap.firstKey()+" 最大访问数IP"+treeMap.get(treeMap.lastKey())+"=="+treeMap.lastKey());
		
		
	}
}
