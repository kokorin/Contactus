package contactus.repository.jdbc.operation;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.Statement;

public class Delete {
    private String tableName;
    private String clause = null;

    public Delete setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Delete setClause(String clause) {
        this.clause = clause;
        return this;
    }

    @SneakyThrows
    public void execute(Connection connection) {
        String deleteQuery = "DELETE FROM " + tableName;
        if (clause != null && !clause.isEmpty()) {
            clause += " WHERE " + clause;
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(deleteQuery);
        }
    }
}
