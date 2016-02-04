package com.rick.archi.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;


public class HbaseTest {
    public static Configuration configuration;
    public static Connection connection;
    public static Admin admin;

    public static void main(String[] args) throws IOException {
//        createTable("testByJava", new String[]{"col1", "col2"});
//        insertRow("testByJava", "1-1-20160204", "col1", "des1", "Rick is cute.");
//        getData("testByJava", "1-1-20160204", "col1", "des1");
        scanData("testByJava", "1-1-20160204", "1-1-20160204");
//        deleRow("testByJava", "1-1-20160204", "col1", "des1");
//        deleteTable("testByJava");
    }

    //初始化链接
    public static void init() {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "192.168.6.45");
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
//        configuration.set("zookeeper.znode.parent", "/hbase");
        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            if (null != admin) admin.close();
            if (null != connection) connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(String tn, String[] cols) throws IOException {
        init();
        TableName tableName = TableName.valueOf(tn);
        if (admin.tableExists(tableName)) {
            System.out.println("table is exists!");
        } else {
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
            for (String col : cols) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(col);
                hTableDescriptor.addFamily(hColumnDescriptor);
            }
            admin.createTable(hTableDescriptor);
        }
        close();
    }

    public static void deleteTable(String tableName) throws IOException {
        init();
        TableName tn = TableName.valueOf(tableName);
        if (admin.tableExists(tn)) {
            admin.disableTable(tn);
            admin.deleteTable(tn);
        }
        close();
    }

    public static void listTables() throws IOException {
        init();
        HTableDescriptor hTableDescriptors[] = admin.listTables();
        for (HTableDescriptor hTableDescriptor : hTableDescriptors) {
            System.out.println(hTableDescriptor.getNameAsString());
        }
        close();
    }

    public static void insertRow(String tableName, String rowkey, String colFamily, String col, String val) throws IOException {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(rowkey));
        put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(val));
        table.put(put);            /* List<Put> putList = new ArrayList<Put>();        puts.add(put);        table.put(putList);*/
        table.close();
        close();
    }

    public static void deleRow(String tableName, String rowkey, String colFamily, String col) throws IOException {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowkey));
        //delete.addFamily(Bytes.toBytes(colFamily));
        //delete.addColumn(Bytes.toBytes(colFamily),Bytes.toBytes(col));
        //table.delete(delete);
       /* List<Delete> deleteList = new ArrayList<Delete>();        deleteList.add(delete);        table.delete(deleteList);*/
        table.close();
        close();
    }

    public static void getData(String tableName, String rowkey, String colFamily, String col) throws IOException {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowkey));
        //get.addFamily(Bytes.toBytes(colFamily));
        //get.addColumn(Bytes.toBytes(colFamily),Bytes.toBytes(col));
        Result result = table.get(get);
        showCell(result);
        table.close();
        close();
    }

    public static void showCell(Result result) {
        Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
            System.out.println("RowName:" + new String(CellUtil.cloneRow(cell)) + " ");
            System.out.println("Timetamp:" + cell.getTimestamp() + " ");
            System.out.println("column Family:" + new String(CellUtil.cloneFamily(cell)) + " ");
            System.out.println("row Name:" + new String(CellUtil.cloneQualifier(cell)) + " ");
            System.out.println("value:" + new String(CellUtil.cloneValue(cell)) + " ");
        }
    }

    public static void scanData(String tableName, String startRow, String stopRow) throws IOException {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRow));
        scan.setStopRow(Bytes.toBytes(stopRow));
        ResultScanner resultScanner = table.getScanner(scan);
        for (Result result : resultScanner) {
            showCell(result);
        }
        table.close();
        close();
    }
}