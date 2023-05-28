package com.ll.myMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SecSql {
    private final boolean isDevMode;
    private StringBuilder sqlBits;
    private List<Object> bindingDatum;
    private ConnectionPool connectionPool;
    private static ObjectMapper om;

    static {
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public SecSql(ConnectionPool connectionPool, boolean isDevMode) {
        this.connectionPool = connectionPool;
        sqlBits = new StringBuilder();
        bindingDatum = new ArrayList<>();
        this.isDevMode = isDevMode;
    }

    public SecSql append(String sqlBit, Object... bindingDatum) {
        sqlBits.append(sqlBit + " ");

        this.bindingDatum.addAll(List.of(bindingDatum));

        return this;
    }

    public SecSql appendIn(String sqlBit, List bindingDatum) {
        String replacement = IntStream
                .range(0, bindingDatum.size())
                .boxed()
                .map(i -> "?")
                .collect(Collectors.joining(","));

        sqlBit = sqlBit.replace("?", replacement);

        sqlBits.append(sqlBit + " ");

        this.bindingDatum.addAll(bindingDatum);

        return this;
    }

    private PreparedStatement getPreparedStatement() throws SQLException {
        Connection connection = connectionPool.getConnection();

        PreparedStatement stmt = null;

        if (isInsert()) {
            stmt = connection.prepareStatement(getFormat(), Statement.RETURN_GENERATED_KEYS);
        } else {
            stmt = connection.prepareStatement(getFormat());
        }

        for (int i = 0; i < bindingDatum.size(); i++) {
            Object data = bindingDatum.get(i);
            int parameterIndex = i + 1;

            if (data instanceof Boolean) {
                stmt.setBoolean(parameterIndex, (boolean) data);
            } else if (data instanceof Integer) {
                stmt.setInt(parameterIndex, (int) data);
            } else if (data instanceof Long) {
                stmt.setLong(parameterIndex, (long) data);
            } else if (data instanceof String) {
                stmt.setString(parameterIndex, (String) data);
            }
        }

        if (isDevMode) {
            System.out.println("== rawSql ==");
            System.out.println(getRawSql());
            System.out.println();
        }

        return stmt;
    }

    private boolean isInsert() {
        return getFormat().startsWith("INSERT");
    }

    private String getFormat() {
        return sqlBits.toString().trim();
    }

    public String getRawSql() {
        String rawSql = getFormat();

        for (Object data : bindingDatum) {
            if (data instanceof Boolean) {
                data = (boolean) data ? 1 : 0;
            }

            rawSql = rawSql.replaceFirst("\\?", "'" + data + "'");
        }

        return rawSql;
    }

    public long insert() {
        long id = -1;

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = getPreparedStatement();
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                id = rs.getInt(1);
            }

        } catch (SQLException e) {
            connectionPool.closeConnection();
            throw new MyMapException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    connectionPool.closeConnection();
                    throw new MyMapException(e);
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    connectionPool.closeConnection();
                    throw new MyMapException(e);
                }
            }

        }

        return id;
    }

    public long update() {
        long affectedRows = 0;

        PreparedStatement stmt = null;

        try {
            stmt = getPreparedStatement();
            affectedRows = stmt.executeUpdate();
        } catch (SQLException e) {
            connectionPool.closeConnection();
            throw new MyMapException(e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    connectionPool.closeConnection();
                    throw new MyMapException(e);
                }
            }
        }

        return affectedRows;
    }

    public long delete() {
        return update();
    }

    private Object selectValue() {
        Map<String, Object> row = selectRow();

        for (String key : row.keySet()) {
            return row.get(key);
        }

        return null;
    }

    public Long selectLong() {
        Object value = selectValue();

        try {
            return (Long) value;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public LocalDateTime selectDatetime() {
        Object value = selectValue();

        try {
            return (LocalDateTime) value;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public String selectString() {
        Object value = selectValue();

        try {
            return (String) value;
        } catch (ClassCastException e) {
            return null;
        }
    }

    public List<Long> selectLongs() {
        List<Map<String, Object>> rows = selectRows();

        List<Long> datum = new ArrayList<>();

        rows.stream().forEach(row -> {
            for (String key : row.keySet()) {
                datum.add((long) row.get(key));
            }
        });

        return datum;
    }

    public List<String> selectStrings() {
        List<Map<String, Object>> rows = selectRows();

        List<String> datum = new ArrayList<>();

        rows.stream().forEach(row -> {
            for (String key : row.keySet()) {
                datum.add((String) row.get(key));
            }
        });

        return datum;
    }

    public List<LocalDateTime> selectDatetimes() {
        List<Map<String, Object>> rows = selectRows();

        List<LocalDateTime> datum = new ArrayList<>();

        rows.stream().forEach(row -> {
            for (String key : row.keySet()) {
                datum.add((LocalDateTime) row.get(key));
            }
        });

        return datum;
    }

    public <T> T selectRow() {
        return (T) selectRow(Map.class);
    }

    public <T> T selectRow(Class<T> cls) {
        List<T> rows = selectRows(cls);

        if (rows.size() == 0) {
            return null;
        }

        return rows.get(0);
    }

    public <T> List<T> selectRows() {
        return (List<T>) selectRows(Map.class);
    }

    public <T> List<T> selectRows(Class<T> cls) throws MyMapException {
        List<T> rows = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = getPreparedStatement();
            rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnSize = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                for (int columnIndex = 0; columnIndex < columnSize; columnIndex++) {
                    String columnName = metaData.getColumnName(columnIndex + 1);
                    Object value = rs.getObject(columnName);

                    row.put(columnName, value);
                }

                if (cls.getSimpleName().equals("Map")) {
                    rows.add((T) row);
                } else {
                    rows.add(om.convertValue(row, cls));
                }
            }
        } catch (SQLException e) {
            connectionPool.closeConnection();
            throw new MyMapException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    connectionPool.closeConnection();
                    throw new MyMapException(e);
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    connectionPool.closeConnection();
                    throw new MyMapException(e);
                }
            }
        }

        return rows;
    }
}
