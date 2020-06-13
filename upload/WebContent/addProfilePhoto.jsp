<%@page import="org.json.simple.JSONObject"%>
<%@page import="upload.bacoder.coding.control.TokenControl"%>
<%@page import="upload.bacoder.coding.dev.UploadUtil"%>
<%@page import="upload.bacoder.coding.db.DBconn"%>
<%@page import="upload.bacoder.coding.bean.Photo"%>
<%@page import="upload.bacoder.coding.bean.Person"%>

<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.logging.Logger"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	//final String path = getServletContext().getRealPath("/storage");
	//final String path = "/volume1/@appstore/Tomcat7/src/webapps/storage";
	final String path = "/home/phbong31/storage/profileImg";

	Logger logger = Logger.getLogger("addProfilePhoto.jsp");
	
	String imgEncodedStr = request.getParameter("image");
	String fileExt = request.getParameter("fileExt");
	String token = request.getHeader("authorization");
	String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date());
	
	//logger.info("image: "+imgEncodedStr);
	logger.info("token: "+token);
	logger.info("fileExt: "+fileExt);
	Photo photoInfo = new Photo();
	try {
	TokenControl control = new TokenControl();
		
	if(token != null && token.length()>0 && imgEncodedStr != null 
			&& imgEncodedStr.length() > 0) {
		Person person = control.getPersonByToken(token);
		logger.info("getUserLevel: "+person.getUserLevel());
		if(person.getUserLevel() > 0) {		
			photoInfo.setDate(timeStamp);
			photoInfo.setUploader("addProfilePhoto");
			photoInfo.setAccessLv(1);
			photoInfo.setSync(3);
		
			JSONObject json = new JSONObject();
			String result = new String();
			//logger.info("##########imgEncodedStr: "+ imgEncodedStr);
			if (imgEncodedStr != null) {
				result = new UploadUtil().setProfilePhoto(path, imgEncodedStr, fileExt, person.getUserLevel());
			}
			logger.info("result : " + result);
			
			String imgUrl = result.split(";")[0];
		    int fileSize = Integer.parseInt(result.split(";")[1]);
	
			photoInfo.setPhotoUrl(imgUrl);
			photoInfo.setSize(fileSize);
			photoInfo.setContentType("image/JPEG");
			photoInfo.setClassification("profile");
	
			DBconn dbconn = new DBconn();
			
			json.put("result", dbconn.addPhotoInfo(photoInfo));
			
			out.print(json.toString());
	
		} else {
			//userLevel error
		}
	} else {
		logger.info("token or img param null");
	
		//token not found
	}
	} catch(Exception e) {
		throw e;
	}
	
	
%>
