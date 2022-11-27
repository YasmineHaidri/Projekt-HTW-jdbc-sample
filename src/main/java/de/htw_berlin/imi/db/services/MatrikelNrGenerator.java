package de.htw_berlin.imi.db.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service
@Slf4j
public class MatrikelNrGenerator extends DatabaseClient {

    private static final String NEXT_MATRIKEL_NR_QUERY = """
                SELECT nextval('uni.matrikel_nr_sequence') AS id;
            """;

    protected long generate() {
        try (final Statement statement = getConnection(false).createStatement()) {
            final ResultSet resultSet = statement.executeQuery(NEXT_MATRIKEL_NR_QUERY);
            resultSet.next();
            return resultSet.getLong("id");
        } catch (final SQLException e) {
            log.error("Could not get new id: {}", e.getMessage());
            return -1;
        }
    }
}
