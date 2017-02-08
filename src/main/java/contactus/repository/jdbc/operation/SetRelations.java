package contactus.repository.jdbc.operation;

import javafx.util.Pair;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

public class SetRelations {
    private String tableName;
    private List<Integer> ownerIds = new ArrayList<>();
    private Set<Pair<Integer, Integer>> relations = new HashSet<>();
    private String ownerIdField;
    private String relationIdField;

    public SetRelations setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public SetRelations setRelation(Integer ownerId, List<Integer> relationIds) {
        ownerIds.add(ownerId);
        for (Integer relationId : relationIds) {
            relations.add(new Pair<>(ownerId, relationId));
        }
        return this;
    }

    public SetRelations setOwnerIdField(String ownerIdField) {
        this.ownerIdField = ownerIdField;
        return this;
    }

    public SetRelations setRelationIdField(String relationIdField) {
        this.relationIdField = relationIdField;
        return this;
    }

    @SneakyThrows
    public void execute(Connection connection) {
        String ownerIdClause = Util.createInClauseByIds(ownerIds, ownerIdField);
        Set<Pair<Integer, Integer>> existingRelations = new Select<Pair<Integer, Integer>>()
                .setTableName(tableName)
                .setFields(ownerIdField, relationIdField)
                .setClause(ownerIdClause)
                .setParser(SetRelations::parseRealation)
                .execute(connection);

        List<Pair<Integer, Integer>> toInsert = new ArrayList<>(relations);
        toInsert.removeAll(existingRelations);

        List<Pair<Integer, Integer>> toDelete = new ArrayList<>(existingRelations);
        toDelete.removeAll(relations);

        new Insert<Pair<Integer, Integer>>().setTableName(tableName)
                .setColumnNames(ownerIdField, relationIdField)
                .setItems(toInsert)
                .setGetValues(r -> Arrays.asList(r.getKey(), r.getValue()))
                .execute(connection);

        new DeleteItems<Pair<Integer, Integer>>().setTableName(tableName)
                .setItems(toDelete)
                .setColumnNames(ownerIdField, relationIdField)
                .setGetValues(r -> Arrays.asList(r.getKey(), r.getValue()))
                .execute(connection);
    }


    @SneakyThrows
    private static Pair<Integer, Integer> parseRealation(ResultSet resultSet) {
        return new Pair<>(resultSet.getInt(1), resultSet.getInt(2));
    }
}
