package org.priorityhealth.stab.pdiff.persistence;

import org.priorityhealth.stab.pdiff.service.LogService;

import java.sql.*;

/**
 * Created by bra50311 on 1/6/15.
 */
public class SqLiteService {

    public SqLiteService() {

        Connection connection = null;
        try {
            // load the sqlite-JDBC driver using the current class loader
            Class.forName("org.sqlite.JDBC");

            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo')");
            statement.executeUpdate("insert into person values(2, 'yui')");
            ResultSet rs = statement.executeQuery("select * from person");
            while(rs.next())
            {
                // read the result set
                LogService.Info(this, "name = " + rs.getString("name"));
                LogService.Info(this, "id = " + rs.getInt("id"));
            }

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try
            {
                if (connection != null) {
                    connection.close();
                }
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
