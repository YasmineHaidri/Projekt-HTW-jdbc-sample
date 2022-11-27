package de.htw_berlin.imi.db.services;

import de.htw_berlin.imi.db.entities.BueroRaum;
import de.htw_berlin.imi.db.entities.Stockwerk;
import de.htw_berlin.imi.db.entities.Student;
import de.htw_berlin.imi.db.web.BueroDto;
import de.htw_berlin.imi.db.web.StudentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implements the DAO (data access object) pattern for BueroRaum.
 * <p>
 * Classes annotated with @Service can be injected using @Autowired in other Spring components.
 * <p>
 * Classes annotated with @Slf4j have access to loggers.
 */
@Service
@Slf4j
public class StudentEntityService extends AbstractEntityService<Student> {

    @Autowired
    protected MatrikelNrGenerator matrikelNrGenerator;

    private static final String FIND_ALL_QUERY = """
                SELECT
                   id
                   ,matr_nr
                   ,name
                   ,vorname
                   ,geburtsdatum
                   ,geburtsort
                   ,anzahl_semester
                   ,studienbeginn
                FROM uni.studierende
            """;

    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE id = ?;";

    private static final String INSERT_STUDENT_QUERY = """
            INSERT INTO uni.studierende (id, matr_nr, name, vorname, geburtsdatum, geburtsort, anzahl_semester, studienbeginn)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """;

    private static final String UPDATE_STUDENT_QUERY = """
            UPDATE uni.studierende SET matr_nr = ?, name = ?, vorname = ?, geburtsdatum = ?, geburtsort = ?, anzahl_semester = ?, studienbeginn = ? WHERE id = ?;
            """;
    @Override
    public Student create() {
        return new Student(idGenerator.generate(), matrikelNrGenerator.generate());
    }

    @Override
    public void save(Student entity) {

        log.debug("insert: {}", INSERT_STUDENT_QUERY);
        try (final Connection connection = getConnection(false)) {
            connection.setAutoCommit(false);
            try (final PreparedStatement statement = getPreparedStatement(connection, INSERT_STUDENT_QUERY)) {
                setSaveStudentStatementProperties(entity, statement);
                connection.commit();
            } catch (final SQLException ex) {
                log.error("Error creating office, aborting {}", ex.getMessage());
                connection.rollback();
                throw new RuntimeException(ex);
            }
        } catch (final SQLException ex) {
            log.error("Could not get connection.");
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(final Student student) {
        log.debug("update: {}", student);
        final double start = System.currentTimeMillis();
        try (final Connection connection = getConnection(false)) {
            connection.setAutoCommit(false);
            try (final PreparedStatement statement = getPreparedStatement(connection, UPDATE_STUDENT_QUERY)) {
                setUpdateStudentStatementProperties(student, statement);
                connection.commit();
            } catch (final SQLException ex) {
                log.error("Error creating student, aborting {}", ex.getMessage());
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
    public void delete(final Student student) {
        final double start = System.currentTimeMillis();
        log.debug("delete: {}", student);
        try (final Connection connection = getConnection(false)) {
            connection.setAutoCommit(false);
            try (final PreparedStatement basePreparedStatement =
                         getPreparedStatement(connection, "DELETE FROM uni.studierende WHERE id = ?")) {
                basePreparedStatement.setLong(1, student.getId());
                basePreparedStatement.executeUpdate();
                connection.commit();
            } catch (final SQLException ex) {
                log.error("Error deleting student, aborting {}", ex.getMessage());
                connection.rollback();
                throw new RuntimeException(ex);
            }
        } catch (final SQLException ex) {
            log.error("Could not get connection.");
            throw new RuntimeException(ex);
        }
        log.info("Delete finished in {} ms.", System.currentTimeMillis() - start);
    }

    private void setSaveStudentStatementProperties(final Student e, final PreparedStatement basePreparedStatement) throws SQLException {
        basePreparedStatement.setLong(1, e.getId());
        basePreparedStatement.setLong(2, e.getMatrikelNr());
        basePreparedStatement.setString(3, e.getName());
        basePreparedStatement.setString(4, e.getVorname());
        basePreparedStatement.setDate(5, e.getGeburtsdatum());
        basePreparedStatement.setString(6, e.getGeburtsort());
        basePreparedStatement.setInt(7, e.getSemester());
        basePreparedStatement.setString(8, e.getStudienbeginn());

        final int update = basePreparedStatement.executeUpdate();
        if (update != 1) {
            throw new SQLException("Could not save student");
        }
    }

    private void setUpdateStudentStatementProperties(final Student e, final PreparedStatement basePreparedStatement) throws SQLException {
        basePreparedStatement.setLong(1, e.getMatrikelNr());
        basePreparedStatement.setString(2, e.getName());
        basePreparedStatement.setString(3, e.getVorname());
        basePreparedStatement.setDate(4, e.getGeburtsdatum());
        basePreparedStatement.setString(5, e.getGeburtsort());
        basePreparedStatement.setInt(6, e.getSemester());
        basePreparedStatement.setString(7, e.getStudienbeginn());
        basePreparedStatement.setLong(8, e.getId());

        final int update = basePreparedStatement.executeUpdate();
        if (update != 1) {
            throw new SQLException("Could not update student");
        }
    }

    @Override
    public List<Student> findAll() {
        final List<Student> result = new ArrayList<>();
        try (final Connection connection = getConnection(true);
             final Statement statement = connection.createStatement()) {
            final ResultSet resultSet = statement.executeQuery(FIND_ALL_QUERY);
            while (resultSet.next()) {
                result.add(createStudent(resultSet));
            }
        } catch (final Exception e) {
            log.error("Problem finding Student {}", e.getMessage());
        }
        return result;
    }

    @Override
    public Optional<Student> findById(final long id) {
        try (final Connection connection = getConnection(true);
             final PreparedStatement basePreparedStatement = getPreparedStatement(connection, FIND_BY_ID_QUERY)) {
            basePreparedStatement.setLong(1, id);
            final ResultSet resultSet = basePreparedStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createStudent(resultSet));
            }
        } catch (final Exception e) {
            log.error("Problem finding Student by id {}", e.getMessage());
        }
        return Optional.empty();
    }


    private Student createStudent(final ResultSet resultSet) throws SQLException {
        final long id = resultSet.getInt("id");
        final Student entity = new Student(id);
        entity.setMatrikelNr(resultSet.getLong("matr_nr"));
        entity.setName(resultSet.getString("name"));
        entity.setVorname(resultSet.getString("vorname"));
        entity.setGeburtsdatum(resultSet.getDate("geburtsdatum"));
        entity.setGeburtsort(resultSet.getString("geburtsort"));
        entity.setSemester(resultSet.getInt("anzahl_semester"));
        entity.setStudienbeginn(resultSet.getString("studienbeginn"));
        return entity;
    }

    public Student createFrom(final StudentDto template) {
        final Student student = create();
        student.setName(template.getName());
        student.setVorname(template.getVorname());
        student.setGeburtsdatum(template.getGeburtsdatum());
        student.setGeburtsort(template.getGeburtsort());
        student.setSemester(template.getSemester());
        student.setStudienbeginn(template.getStudienbeginn());
        save(student);
        return student;
    }
}
