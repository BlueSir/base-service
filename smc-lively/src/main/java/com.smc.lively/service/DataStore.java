package com.smc.lively.service;

import com.smc.lively.enums.LivelyItemEnum;
import com.sohu.smc.config.model.AppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.actors.threadpool.Callable;
import scala.actors.threadpool.ExecutionException;
import scala.actors.threadpool.Executors;
import scala.actors.threadpool.Future;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jingxc
 * Date: 2/25/14
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataStore {
    static final Logger LOG = LoggerFactory.getLogger(DataStore.class);
    private String dataDir = AppConfiguration.getString("smc.api.SQLite.dataDir","~/data").get();
    private String tableName;
    private String dbName;

    DataStore(String tableName) {
        this.tableName = tableName;
        this.dbName = dataDir + File.separator+tableName;
        init();
    }

    public synchronized void init() {
        File dbDir = new File(dataDir);
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }else{
            LOG.info("[init]:Db file path="+dbDir.getAbsolutePath());
        }
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            LOG.error("[init]:Throws Exception.", e);
        }
    }

    public Connection getConnection(){
        Connection conn =null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
        } catch (SQLException e) {
            LOG.error("[getConnection]:Throws Exception.", e);
        }
        return conn;
    }
    public int execute(String sql ,Connection connection){
        Statement statement;
        int result =0;
        try {
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
            result= statement.executeUpdate(sql);
        } catch (SQLException e) {
            LOG.error("[execute]:Throws Exception.", e);
        }finally {
            close(connection);
        }

        return result;
    }

    public int[] batchInsert(final List<Long> items){

        final String sql = "insert into "+this.tableName + " values(%d)";
        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                Connection connection = null;
                Statement statement = null;
                try {
                    connection.setAutoCommit(false);
                    statement = connection.createStatement();
                    for(Long each : items){
                        statement.addBatch(String.format(sql, each));
                    }
                    int[] result = statement.executeBatch();
                    connection.commit();
                    return result;
                } catch (SQLException e) {
                    LOG.error("[batchUpdate]:Throws Exception.", e);
                }finally {
                    close(connection);
                }
                return null;
            }
        };

        Future future = Executors.newSingleThreadExecutor().submit(callable);
        try {
            return (int[]) future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int[] batchDelete(final List<Long> items){
        final String sql = "delete from "+this.tableName+" where item=%d";
        Callable callable = new Callable() {
            @Override
            public int[] call() throws Exception {
                Connection connection = null;
                Statement statement = null;
                try {
                    connection.setAutoCommit(false);
                    statement = connection.createStatement();
                    for(Long each : items){
                        statement.addBatch(String.format(sql, each));
                    }
                    int[] result = statement.executeBatch();
                    connection.commit();
                    return result;
                } catch (SQLException e) {
                    LOG.error("[batchUpdate]:Throws Exception.", e);
                }finally {
                    close(connection);
                }
                return null;
            }
        };
        Future future = Executors.newSingleThreadExecutor().submit(callable);
        try {
            int[] ret = (int[]) future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void createTable(){
        if(isTableExsist()) return;
        Connection conn = getConnection();
        String sql="create table "+ this.tableName +"(item long primary key)";
        execute(sql, conn);
        close(conn);
    }

    public boolean isTableExsist(){
        //判断表是否存在
        Connection conn = getConnection();
        ResultSet rsTables = null;
        try {
            rsTables = conn.getMetaData().getTables(null, null, dbName, null);
            if(rsTables.next()){
                return true;
            }
        } catch (SQLException e) {
            LOG.error("[isTableExsist]:Throws Exception.", e);
        }finally {
            close(conn);
        }
        return false;
    }
    public Set<Long> queryAll(){
        final String sql = "select item from "+this.tableName;
        Callable callable = new Callable() {
            @Override
            public Set<Long> call() throws Exception {
                Statement statement;
                ResultSet result =null;
                Connection connection= null;
                Set<Long> activeUser = new HashSet<Long>();
                try {
                    connection = getConnection();
                    statement = connection.createStatement();
                    statement.setQueryTimeout(30);
                    result = statement.executeQuery(sql);

                    while (result.next()) {
                        activeUser.add(result.getLong("item"));
                    }
                } catch (SQLException e) {
                    LOG.error("[query]:Throws Exception.", e);
                } finally {
                    close(connection);
                }
                return activeUser;
            }
        };
        Future future = Executors.newSingleThreadExecutor().submit(callable);
        try {
            return (Set<Long>) future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int delete(long item){
        String sql = "delete from " + this.tableName +" where item=" + item;
        Connection connection = null;
        Statement statement = null;
        int result =0;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.setQueryTimeout(30);
            result= statement.executeUpdate(sql);
        } catch (SQLException e) {
            LOG.error("[delete]:Throws Exception.", e);
        } finally {
            close(connection);
        }

        return result;
    }

    public void close(Connection conn){
        try {
            if(conn != null && !conn.isClosed()){
                conn.close();
            }
        } catch (SQLException e) {
            LOG.error("[close]:Throws Exception.", e);
        }
    }

    public static void main(String[] args) throws InterruptedException, SQLException {
        DataStore dbStore = DataStoreFactory.getInstance(LivelyItemEnum.CID_1_DAY);
        Connection conn = dbStore.getConnection();
        long start = System.currentTimeMillis();
        List<String> sqls = new ArrayList<String>();
//        dbStore.createTable();
//        for(long i = 59900000l; i< 60000010l;i++){
//            sqls.add("insert into active_user values(" + i +")");
//            if(i % 10000 == 0){
//                dbStore.batchUpdate(sqls, conn);
//                System.out.println("=================== batchUpdate ==============");
//                sqls.clear();
//            }
//        }
//        if(sqls.size() > 0){
//            dbStore.batchUpdate(sqls, conn);
//        }

        System.out.println("write:" + (System.currentTimeMillis() - start) + "ms.");

//        TimeUnit.SECONDS.sleep(1);
//        start = System.currentTimeMillis();
//        ResultSet rs = dbStore.query("select item from active_user", conn);
//        Set<Long> activeUser = new HashSet<Long>();
//        while(rs.next()){
//            activeUser.add(rs.getLong("item"));
//        }
//        System.out.println("read:" + (System.currentTimeMillis() - start) + "ms. record size=" + activeUser.size());


        for(long i = 50020000l; i< 50030000l;i++){
            sqls.add("delete from active_user where item=" + i);
        }
//        int[] rt = dbStore.batchInsert(sqls);
//        System.out.println("delete:" + (System.currentTimeMillis() - start) + "ms. size=" + rt.length);

    }
}
