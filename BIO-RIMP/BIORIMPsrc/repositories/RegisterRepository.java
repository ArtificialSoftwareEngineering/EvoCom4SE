package repositories;

import entities.Register;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by developer on 12/29/15.
 */
public class RegisterRepository extends Repository<Register> {

    public static String TABLE_NAME = "brp_register";

    public Register fetchAll(ResultSet resultSet) {
        return null;
    }

    public List<Register> fetchAll() {
        getConnection();
        List<Register> results = new ArrayList();
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + Register.COLUMN_REFACTOR;
            ResultSet resultSet = statement.executeQuery(query);

            while(resultSet.next()){
                results.add(resultEntity(resultSet));
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public Register resultEntity(ResultSet resultSet) {
        try {
            int refactor = resultSet.getInt(Register.COLUMN_REFACTOR);
            String code = resultSet.getString(Register.COLUMN_CODE);
            double value = resultSet.getDouble(Register.COLUMN_VALUE);

            return new Register(refactor, code, value);
        } catch (Exception e) {
            return null;
        }
    }

    public Register getRegister(int refactorID, String code){
        getConnection();
        Register register = new Register();
        if(connection!=null){
            try{
                String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + Register.COLUMN_REFACTOR + " = ? AND "+ Register.COLUMN_CODE + "= ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, refactorID);
                statement.setString(2, code);
                ResultSet resultSet = statement.executeQuery();

                while(resultSet.next()){
                    register = resultEntity(resultSet);
                }

                resultSet.close();
                statement.close();
                connection.close();

            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return register;
    }

    public void insertRegister(int refactorID, String code, double value){
        getConnection();
        if(connection!=null){
            try{
                String query = "INSERT INTO " + TABLE_NAME +" ("+Register.COLUMN_REFACTOR+ ","+ Register.COLUMN_CODE+","+ Register.COLUMN_VALUE+") VALUES (?,?,?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, refactorID);
                statement.setString(2, code);
                statement.setDouble(3,value);
                statement.executeUpdate();
                statement.close();
                connection.close();

            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    public void insertRegister(Register register){
        insertRegister(register.getRefactor(),register.getCode(),register.getValue());

    }

}
