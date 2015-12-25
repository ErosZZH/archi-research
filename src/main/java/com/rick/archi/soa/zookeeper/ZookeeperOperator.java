package com.rick.archi.soa.zookeeper;

import java.util.Arrays;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;

public class ZookeeperOperator extends AbstractZookeeper {

	public void create(String path, byte[] data) throws KeeperException,
			InterruptedException {
		/**
		 * 此处采用的是CreateMode是PERSISTENT 表示The znode will not be automatically
		 * deleted upon client's disconnect. EPHEMERAL 表示The znode will be
		 * deleted upon the client's disconnect.
		 */
		this.zooKeeper.create(path, data, Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
	}

	public void getChild(String path) throws KeeperException,
			InterruptedException {
		try {
			List<String> list = this.zooKeeper.getChildren(path, false);
			if (list.isEmpty()) {
				System.out.println(path + "中没有节点");
			} else {
				System.out.println(path + "中存在节点");
				for (String child : list) {
					System.out.println("节点为：" + child);
				}
			}
		} catch (KeeperException.NoNodeException e) {
			throw e;
		}
	}

	public byte[] getData(String path) throws KeeperException,
			InterruptedException {
		return this.zooKeeper.getData(path, false, null);
	}

	public static void main(String[] args) {
		try {
			ZookeeperOperator zkoperator = new ZookeeperOperator();
			zkoperator.connect("127.0.0.1:2181");
			byte[] data = new byte[] { 'a', 'b', 'c', 'd' };

			zkoperator.create("/root", null);
			System.out.println(Arrays.toString(zkoperator.getData("/root")));

			zkoperator.create("/root/child1", data);
			System.out.println(Arrays.toString(zkoperator
					.getData("/root/child1")));

			zkoperator.create("/root/child2", data);
			System.out.println(Arrays.toString(zkoperator
					.getData("/root/child2")));

			String zktest = "ZooKeeper的Java API测试";
			zkoperator.create("/root/child3", zktest.getBytes());
			System.out.println("获取设置的信息："
					+ new String(zkoperator.getData("/root/child3")));

			System.out.println("节点孩子信息:");
			zkoperator.getChild("/root");

			zkoperator.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
