package contactus.repository.jdbc.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    private Util(){}

    public static String createInClauseByIds(List<Integer> ids, String idField) {
        List<String> subClauses = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += 500) {
            int end = Math.min(i + 500, ids.size());
            List<Integer> subIds = ids.subList(i, end);
            String subIdsString = subIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            subClauses.add(idField + " IN (" + subIdsString + ")");
        }
        return "(" + String.join(" OR ", subClauses) + ")";
    }

}
