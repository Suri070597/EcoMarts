package dao;

import db.DBContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Receiver;

public class ReceiverDAO extends DBContext {

    public List<Receiver> getAllReceivers() {
        List<Receiver> list = new ArrayList<>();
        try {
            String sql = "SELECT ReceiverID, FullName FROM Receivers";
            ResultSet rs = execSelectQuery(sql);
            while (rs.next()) {
                list.add(new Receiver(
                        rs.getInt("ReceiverID"),
                        rs.getString("FullName")
                ));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
