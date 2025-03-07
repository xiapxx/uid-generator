package io.github.xiapxx.starter.uidgenerator.worker;

import io.github.xiapxx.starter.uidgenerator.properties.UGWorkerDataSourceConfig;
import io.github.xiapxx.uid.generator.api.worker.WorkerIdAssigner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * postgres的方式生成机器id
 *
 * @Author xiapeng
 * @Date 2025-03-07 14:21
 */
public class PostgresWorkerIdAssigner implements WorkerIdAssigner {

    private static final String SQL = "select nextval('uid_generator_worker_id_seq')";

    private UGWorkerDataSourceConfig dataSource;

    public PostgresWorkerIdAssigner(UGWorkerDataSourceConfig dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public long assignWorkerId(long maxWorkerId) {
        loadDriver();
        try(Connection conn = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
            PreparedStatement preparedStatement = conn.prepareStatement(SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
        )
        {
           if(!resultSet.next()){
               throw new RuntimeException("Unable to allocate Worker ID");
           }
           return resultSet.getLong(1);
        } catch (Throwable e) {
            throw new RuntimeException(e);
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
