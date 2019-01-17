package hash.hash;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class Hash1 {

	
	 private static String[] servers = {
			 	"192.168.0.0:1001", "192.168.0.1:1001", "192.168.0.2:1001",
	            "192.168.0.3:1001", "192.168.0.4:1001","193.168.0.5:1001","193.168.0.6:1001",
	            "192.168.0.10:1001", "192.168.0.11:1001", "192.168.0.12:1001",
	            "192.168.0.13:1001", "192.168.0.14:1001","193.168.0.15:1001","193.168.0.16:1001",
	            "192.168.0.20:1001", "192.168.0.21:1001", "192.168.0.22:1001",
	            "192.168.0.23:1001", "192.168.0.24:1001","193.168.0.25:1001","193.168.0.26:1001",
	            "192.168.0.30:1001", "192.168.0.31:1001", "192.168.0.32:1001",
	            "192.168.0.33:1001", "192.168.0.34:1001","193.168.0.35:1001","193.168.0.36:1001",
	            "192.168.0.40:1001", "192.168.0.41:1001", "192.168.0.42:1001",
	            "192.168.0.43:1001", "192.168.0.44:1001","193.168.0.45:1001","193.168.0.46:1001",
	            "192.168.0.50:1001", "192.168.0.51:1001", "192.168.0.52:1001",
	            "192.168.0.53:1001", "192.168.0.54:1001","193.168.0.55:1001","193.168.0.56:1001"};
	private static SortedMap<Integer, String> sortedMap = new TreeMap<Integer, String>();
	static {
		for (int i = 0; i < servers.length; i++) {
			int hash = getHash(servers[i]);
			System.out.println("[" + servers[i] + "]加入集合中, 其Hash值为" + hash);
			sortedMap.put(hash, servers[i]);
		}
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

	private static String getServer(String node) {
		// 得到带路由的结点的Hash值
		int hash = getHash(node);
		// 得到大于该Hash值的所有Map
		SortedMap<Integer, String> subMap = sortedMap.tailMap(hash);
		// 第一个Key就是顺时针过去离node最近的那个结点
		Integer firstKey = 0;
		if(subMap.size()>0) {
			firstKey = subMap.firstKey();
		} else{
			System.out.print("循环节点 ");
			firstKey = sortedMap.firstKey();
		}
		// 返回对应的服务器名称
		String string = sortedMap.get(firstKey);
		System.out.println("IP:"+node+"/"+hash+" 服务节点："+string+"/"+firstKey);
		return string;
	}

	public static void main(String[] args) {
		String[] nodes = {};
		List<String> list=  new ArrayList<String>();
		for(int i = 1;i<=1000;i++) {
			String ip = "127.100.1.";
			list.add(ip+i);
		}
		nodes = list.toArray(nodes);
		for (String IP : nodes) {
			getServer(IP);
		}
	}
}
