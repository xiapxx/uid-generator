package io.github.xiapxx.starter.uidgenerator.worker;

import io.github.xiapxx.starter.uidgenerator.properties.UGWorkerDataSourceConfig;
import io.github.xiapxx.uid.generator.api.worker.WorkerIdAssigner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @Author xiapeng
 * @Date 2025-03-07 15:08
 */
public class MysqlWorkerIdAssigner implements WorkerIdAssigner {

    private static final String SQL = "insert into uid_generator_worker_id(id) values(null)";

    private UGWorkerDataSourceConfig dataSource;

    public MysqlWorkerIdAssigner(UGWorkerDataSourceConfig dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public long assignWorkerId(long maxWorkerId) {
        loadDriver();
        ResultSet rs = null;
        try(Connection conn = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
            PreparedStatement preparedStatement = conn.prepareStatement(SQL, new String[]{"id"});
        )
        {
            preparedStatement.execute();
            rs = preparedStatement.getGeneratedKeys();
            if(!rs.next()){
                throw new RuntimeException("Unable to allocate Worker ID");
            }
            return rs.getLong(1);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            if(rs != null){
                try {
                    rs.close();
                } catch (Throwable e) {
                }
            }
        }
    }

    /**
     * 加载驱动
     */
    private void loadDriver() {
        try {
            Class.forName(dataSource.getDriverClass());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
