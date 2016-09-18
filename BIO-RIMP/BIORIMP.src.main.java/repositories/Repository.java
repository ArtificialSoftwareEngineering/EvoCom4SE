package repositories;

import connection.ConnectionJDBC;
import entities.Entity;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by developer on 12/29/15.
 */
public abstract class Repository<T extends Entity> {
    protected Connection connection;

    protected void getConnection() {
        try {
            connection = ConnectionJDBC.getInstance().obtenerConexion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract T resultEntity(ResultSet resultSet);

}
