package hash.hash;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.swing.internal.plaf.synth.resources.synth_zh_CN;

/**
 * 一致性算法代理类 测试
 */
public class HashTest2 {

	private static String[] servers = { "192.168.0.0:1001", "192.168.0.1:1001", "192.168.0.2:1001", "192.168.0.3:1001",
			"192.168.0.4:1001", "193.168.0.5:1001", "193.168.0.6:1001", "192.168.0.10:1001", "192.168.0.11:1001",
			"192.168.0.12:1001", "192.168.0.13:1001", "192.168.0.14:1001", "193.168.0.15:1001", "193.168.0.16:1001",
			"192.168.0.20:1001", "192.168.0.21:1001", "192.168.0.22:1001", "192.168.0.23:1001", "192.168.0.24:1001",
			"193.168.0.25:1001", "193.168.0.26:1001", "192.168.0.30:1001", "192.168.0.31:1001", "192.168.0.32:1001",
			"192.168.0.33:1001", "192.168.0.34:1001", "193.168.0.35:1001", "193.168.0.36:1001", "192.168.0.40:1001",
			"192.168.0.41:1001", "192.168.0.42:1001", "192.168.0.43:1001", "192.168.0.44:1001", "193.168.0.45:1001",
			"193.168.0.46:1001", "192.168.0.50:1001", "192.168.0.51:1001", "192.168.0.52:1001", "192.168.0.53:1001",
			"192.168.0.54:1001", "193.168.0.55:1001", "193.168.0.56:1001" };
	private static List<String> realNodes = new LinkedList<String>();

	public static void main(String[] args) {
		long startTime=System.currentTimeMillis();   //获取开始时间
		for (int i = 0; i < servers.length; i++) {
			realNodes.add(servers[i]);
		}
		Proxy proxy = new Proxy(10, realNodes);
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= 500000; i++) {
			String ip = "127.100.1.";
			list.add(ip + i);
		}
		String[] nodes = {};
		nodes = list.toArray(nodes);
		for (String IP : nodes) {
			proxy.get(IP);
		}
		long endTime=System.currentTimeMillis(); //获取结束时间
		System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
		proxy.print();
		proxy.maxRequestNum();
		proxy.minRequestNum();
		
		proxy.remove("193.168.0.45:1001");
		System.out.println();
		proxy.print();
		proxy.maxRequestNum();
		proxy.minRequestNum();

	}
}
