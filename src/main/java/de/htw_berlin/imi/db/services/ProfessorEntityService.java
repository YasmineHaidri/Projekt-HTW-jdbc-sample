package de.htw_berlin.imi.db.services;


import de.htw_berlin.imi.db.entities.Professor;

import de.htw_berlin.imi.db.web.ProfessorDto;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.sql.*;
import java.util.*;

@Service

public class ProfessorEntityService extends AbstractEntityService<Professor> {

    private static final String FIND_ALL_QUERY = """
                SELECT
                    p.pers_nr          AS id
                   ,p.name             AS name
                   ,p.rang             AS rang
                   ,p.raum             AS room_id
                   ,p.gehalt           AS salary
                FROM uni.professoren p
            """;


    private static final String INSERT_QUERY = """
            INSERT INTO uni.professoren (pers_nr, name, rang, raum, gehalt)
                VALUES (?, ?, ?, ?, ?);
            """;
    private static final String UPDATE_QUERY = """
            UPDATE uni.professoren SET name = ?, rang = ?, raum = ?, gehalt = ?
                WHERE pers_nr = ?;
            """;
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE p.pers_nr = ?";

    private BueroRaumEntityService bueroRaumEntityService;
    private static final Logger log = LoggerFactory.getLogger(ProfessorEntityService.class);

    @Override
    public List<Professor> findAll() {
        final List<Professor> result = new ArrayList<>();
        log.debug("query: {}", FIND_ALL_QUERY);
        try (final Connection connection = getConnection(true);
             final Statement statement = connection.createStatement()) {
            final ResultSet resultSet = statement.executeQuery(FIND_ALL_QUERY);
            while (resultSet.next()) {
                result.add(getProfessorWithHisBuero(resultSet));
            }
        } catch (final Exception e) {
            log.error("Problem finding bueros {}", e.getMessage());
        }
        return result;
    }



    private Professor getProfessorWithHisBuero(final ResultSet resultSet) throws SQLException {
        final long id = resultSet.getLong("id");
        final Professor entity = new Professor(id);
        entity.setName(resultSet.getString("name"));
        entity.setRang(resultSet.getString("rang"));
        entity.setGehalt(resultSet.getInt("salary"));
        final long roomId = resultSet.getLong("room_id");
        if (roomId != 0) {
            bueroRaumEntityService
                    .findById(roomId)
                    .ifPresent(r -> entity.setRaum(r));
        }
       return entity;
    }

    @Override
    public Optional<Professor> findById(final long id) {
        log.debug("query: {}", FIND_BY_ID_QUERY + id);
        try (final Connection connection = getConnection(true);
             final PreparedStatement basePreparedStatement = getPreparedStatement(connection, FIND_BY_ID_QUERY)) {
            basePreparedStatement.setLong(1, id);
            final ResultSet resultSet = basePreparedStatement.executeQuery();
            while (resultSet.next()) {
                return Optional.of(getProfessorWithHisBuero(resultSet));
            }

        } catch (final Exception e) {
            log.error("Problem finding professor by id {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Professor create() {
        return new Professor(idGenerator.generate());
    }

    @Override
    public void save(final Professor e) {
        log.debug("insert: {}", INSERT_QUERY);
        try (final Connection connection = getConnection(false)) {
            connection.setAutoCommit(false);
            try (final PreparedStatement basePreparedStatement = getPreparedStatement(connection, INSERT_QUERY)) {
                basePreparedStatement.setLong(1, e.getId());
                basePreparedStatement.setString(2, e.getName());
                basePreparedStatement.setString(3, e.getRang());
                if (e.getRaum() == null) {
                    basePreparedStatement.setNull(4, Types.INTEGER);
                } else {
                    basePreparedStatement.setLong(4, e.getRaum().getId());
                }
                basePreparedStatement.setInt(5, e.getGehalt());
                final int update = basePreparedStatement.executeUpdate();
                if (update != 1) {
                    throw new SQLException("Could not create  part");
                }
                connection.commit();
            } catch (final SQLException ex) {
                log.error("Error creating , aborting {}", ex.getMessage());
                connection.rollback();
                throw new RuntimeException(ex);
            }
        } catch (final SQLException ex) {
            log.error("Could not get connection.");
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(final Professor e) {
        log.debug("update: {}", e);
        final double start = System.currentTimeMillis();
        try (final Connection connection = getConnection(false)) {
            connection.setAutoCommit(false);
            try (final PreparedStatement basePreparedStatement = getPreparedStatement(connection, UPDATE_QUERY)) {
                basePreparedStatement.setString(1, e.getName());
                basePreparedStatement.setString(2, e.getRang());
                basePreparedStatement.setLong(3, e.getRaum().getId());
                basePreparedStatement.setInt(4, e.getGehalt());
                basePreparedStatement.setLong(5, e.getId());
                final int update = basePreparedStatement.executeUpdate();
                if (update != 1) {
                    throw new SQLException("Could not update id");
                }
                connection.commit();
            } catch (final SQLException ex) {
                log.error("Error creating floor, aborting {}", ex.getMessage());
                connection.rollback();
                throw new RuntimeException(ex);
            }
        } catch (final SQLException ex) {
            log.error("Could not get connection.");
            throw new RuntimeException(ex);
        }
        log.info("Update finished in {} ms.", System.currentTimeMillis() - start);
    }

    @Override
    public void delete(final Professor entity) {
        final double start = System.currentTimeMillis();
        log.debug("delete: {}", entity);
        try (final Connection connection = getConnection(false)) {
            connection.setAutoCommit(false);
            try (final PreparedStatement basePreparedStatement =
                         getPreparedStatement(connection, "DELETE FROM uni.professoren WHERE pers_nr = ?")) {
                basePreparedStatement.setLong(1, entity.getId());
                basePreparedStatement.executeUpdate();
                connection.commit();
            } catch (final SQLException ex) {
                log.error("Error deleting professor, aborting {}", ex.getMessage());
                connection.rollback();
                throw new RuntimeException(ex);
            }
        } catch (final SQLException ex) {
            log.error("Could not get connection.");
            throw new RuntimeException(ex);
        }
        log.info("Delete finished in {} ms.", System.currentTimeMillis() - start);
    }

    public Professor createFrom(final ProfessorDto template) {
        final Professor prof = create();
        prof.setName(template.getName());
        prof.setRang(template.getRang());
        prof.setGehalt(template.getGehalt());
        save(prof);
        return prof;
    }


}
