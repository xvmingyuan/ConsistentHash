package hash.hash;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 添加节点负载检测代理
 * 
 * @author xmy
 * @param <T>
 * @time：2019年1月17日 下午3:10:37
 */
public class Proxy<T> extends ConsistentHash<T> {

	/**
	 * 节点访问统计
	 */
	private static ConcurrentHashMap<String, Integer> IpCount;
	private static TreeMap<Integer, String> tree;

	public Proxy(HashFunc hashFunc, int numberOfReplicas, Collection<T> nodes) {
		super(hashFunc, numberOfReplicas, nodes);
		// 创建统计登记集合
		IpCount = new ConcurrentHashMap<String, Integer>();
		for (T t : nodes) {
			IpCount.put(t.toString(), 0);
		}

	}

	public Proxy(int numberOfReplicas, Collection<T> nodes) {
		super(numberOfReplicas, nodes);
		// 创建统计登记集合
		IpCount = new ConcurrentHashMap<String, Integer>();
		for (T t : nodes) {
			IpCount.put(t.toString(), 0);
		}
	}

	@Override
	public T get(Object key) {
		T t = super.get(key);
		// 节点 访问+1
		IpCount.put(t.toString(), IpCount.get(t.toString()) + 1);
		return t;
	}

	@Override
	public void remove(T node) {
		super.remove(node);
		// 节点 访问-1
		IpCount.remove(node);
	}

	/**
	 * 打印节点负载状态列表
	 */
	public void print() {
		Set<Entry<String, Integer>> entrySet = IpCount.entrySet();
		for (Entry<String, Integer> entry : entrySet) {
			System.out.println("IP:" + entry.getKey() + "(访问量：" + entry.getValue() + ")");
		}
	}

	/**
	 * 最大访问量IP
	 */
	public void maxRequestNum() {
		tree = new TreeMap<Integer, String>();
		Set<Entry<String, Integer>> entrySet = IpCount.entrySet();
		for (Entry<String, Integer> entry : entrySet) {
			tree.put(entry.getValue(), entry.getKey());
		}
		System.out.println("最大访问量IP:" + tree.get(tree.lastKey())+"("+tree.lastKey()+")");
		tree = null;
	}
	/**
	 * 最小访问量IP
	 */
	public void minRequestNum() {
		tree = new TreeMap<Integer, String>();
		Set<Entry<String, Integer>> entrySet = IpCount.entrySet();
		for (Entry<String, Integer> entry : entrySet) {
			tree.put(entry.getValue(), entry.getKey());
		}
		System.out.println("最小访问量IP:" + tree.get(tree.firstKey())+"("+tree.firstKey()+")");
		tree = null;
	}
}
