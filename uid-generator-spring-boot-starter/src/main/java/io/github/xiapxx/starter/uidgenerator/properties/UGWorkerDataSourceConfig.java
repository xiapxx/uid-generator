package io.github.xiapxx.starter.uidgenerator.properties;

/**
 * @Author xiapeng
 * @Date 2025-03-07 14:00
 */
public class UGWorkerDataSourceConfig {

    /**
     * 可以不指定, 使用SPI机制自动获取
     */
    private String driverClass;

    private String url;

    private String username;

    private String password;

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
