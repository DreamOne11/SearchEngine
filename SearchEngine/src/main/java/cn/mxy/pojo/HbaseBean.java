package cn.mxy.pojo;

public class HbaseBean {
    private int tableId;
    private String table;
    private String rowKey;
    private String columnFamily;
    private String columnName;

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return "HbaseBean [table=" + table + ", rowKey=" + rowKey
                + ", columnFamily=" + columnFamily + ", columnName="
                + columnName + "]";
    }
}
