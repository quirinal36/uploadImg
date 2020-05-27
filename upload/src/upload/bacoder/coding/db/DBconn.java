package upload.bacoder.coding.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import org.json.simple.JSONArray;

import upload.bacoder.coding.bean.Person;
import upload.bacoder.coding.bean.Photo;

public class DBconn {
	Logger logger = Logger.getLogger(DBconn.class.getSimpleName());
	
	private String userName 	= "pps";
	private String password 	= "qhdghkdtp31!";
	private String dbms 		= "mysql";
	private String dbName 		= "new_schema";
	private String serverName 	= "hsbong.synology.me";
	private int portNumber 		= 3307;
	
	public Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	    Connection conn = null;
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", this.userName);
	    connectionProps.put("password", this.password);

	    if (this.dbms.equals("mysql")) {
	        conn = DriverManager.getConnection(
	                   "jdbc:" + this.dbms + "://" +
	                   this.serverName +
	                   ":" + this.portNumber + "/" +
	                   this.dbName + "?" +
	                   "useSSL=false",
	                   connectionProps);
	    }
	    return conn;
	}
	
	
	public int updatePerson(Person person) {
		int result = 0;
		try(Connection conn = getConnection()){
			String sql = "UPDATE Person "
					+ "SET name=?,address=?,email=?,photo=? WHERE phone= ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, person.getName());
			pstmt.setString(2, person.getAddress());
			pstmt.setString(3, person.getEmail());
			pstmt.setString(4, person.getPhoto());
			pstmt.setString(5, person.getPhone());
			result= pstmt.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	public int insertPerson(Person person) {
		int result = 0;
		try(Connection conn = getConnection()){
			String sql = "INSERT INTO Person "
					+ "(name, email, phone, password, uniqueId, photo, department) "
					+ "VALUES (?,?,?,?,?,?,?)";
			logger.info(sql.toString());
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, person.getName());
			pstmt.setString(2, person.getEmail());
			pstmt.setString(3, person.getPhone());
			pstmt.setString(4, person.getPassword());
			pstmt.setString(5, person.getUniqueId());
			pstmt.setString(6, person.getPhoto());
			pstmt.setString(7, person.getDepartment());
			result= pstmt.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int addPhotoInfo(Photo photoInfo) {
		int result = 0;
		try(Connection conn = getConnection()){
			String sql = "INSERT INTO PhotoInfo "
					+ "(patientId, photoUrl, classification, doctor, date, uploader, comment, accessLv, sync, thumbnailFilename, size, thumbnailSize, contentType) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			pstmt.setInt(1, photoInfo.getPatientId());			
			pstmt.setString(2, photoInfo.getPhotoUrl());
			pstmt.setString(3, photoInfo.getClassification());
			pstmt.setString(4, photoInfo.getDoctor());
			pstmt.setString(5, photoInfo.getDate());
			pstmt.setString(6, photoInfo.getUploader());
			pstmt.setString(7, photoInfo.getComment());
			pstmt.setInt(8, photoInfo.getAccessLv());
			pstmt.setInt(9, photoInfo.getSync()); 
			pstmt.setString(10, photoInfo.getThumbnailFilename());
			pstmt.setInt(11, photoInfo.getSize());
			pstmt.setInt(12, photoInfo.getThumbnailSize());
			if(photoInfo.getContentType() != null && photoInfo.getContentType().length() > 0) {
				pstmt.setString(13, photoInfo.getContentType());
			} else {
				pstmt.setString(13, "image/JPEG");
			}
			
			result= pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
		        result = rs.getInt(1);
		    } else {
		    	result = -2;
		        // throw an exception from here
		    }
			logger.info("addPhoto.jsp photoId result:"+ result);
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	public JSONArray getPhotos(Photo photo) {
		JSONArray result = new JSONArray();
		try(Connection conn = getConnection()){
			String sql = "SELECT * FROM PhotoInfo WHERE patientId = ?";
			logger.info(sql);;
			
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, photo.getPatientId());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				Photo p = Photo.makePhoto(rs);
				result.add(Photo.parseJSON(p));
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
