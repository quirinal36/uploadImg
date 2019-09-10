<%@page import="org.json.simple.JSONObject"%>
<%@page import="upload.bacoder.coding.dev.UploadUtil"%>
<%@page import="upload.bacoder.coding.db.DBconn"%>
<%@page import="upload.bacoder.coding.bean.Photo"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.logging.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	//final String path = getServletContext().getRealPath("/storage");
	//final String path = "/volume1/@appstore/Tomcat7/src/webapps/storage";
	final String path = "/home/phbong31/storage";

	Logger logger = Logger.getLogger("addPhoto.jsp");
	
	String imgEncodedStr = request.getParameter("image");
	String fileName = request.getParameter("filename");
	String patientId = request.getParameter("patientId");
	String patientName = request.getParameter("patientName");
	String classification = request.getParameter("classification");
	String doctor = request.getParameter("doctor");
	String uploader = request.getParameter("uploader");
	String comment = request.getParameter("comment");
	String accessLv = request.getParameter("accessLv");
	String sync = request.getParameter("sync");
	
	String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date());
	
	Photo photoInfo = new Photo();
	if(patientId != null){
		photoInfo.setPatientId(patientId);
	}
	photoInfo.setPatientName(patientName);
	photoInfo.setClassification(classification);
	photoInfo.setDoctor(doctor);
	photoInfo.setDate(timeStamp);
	photoInfo.setUploader(uploader);
	if(comment != null){
		photoInfo.setComment(comment);
	}
	if(accessLv != null){
		photoInfo.setAccessLv(accessLv);
	}
	if(sync == null){
		photoInfo.setSync(2);
	}
	
	JSONObject json = new JSONObject();
	String result = new String();
	if (imgEncodedStr != null) {
		result = new UploadUtil().setPhoto(path, imgEncodedStr, fileName, photoInfo.getPatientId());
	}
	logger.info("result : " + result);
	
	String imgUrl = result.split(";")[0];
	String thumbnailUrl = result.split(";")[1];
    int fileSize = Integer.parseInt(result.split(";")[2]);
    int thumbnailSize = Integer.parseInt(result.split(";")[3]);

	photoInfo.setPhotoUrl(imgUrl);
	photoInfo.setThumbnailFilename(thumbnailUrl);
	photoInfo.setSize(fileSize);
	if(thumbnailUrl != null && thumbnailUrl.length() > 0){
		photoInfo.setThumbnailFilename(thumbnailUrl);
	}
	photoInfo.setThumbnailSize(thumbnailSize);
	
	DBconn dbconn = new DBconn();
	
	json.put("result", dbconn.addPhotoInfo(photoInfo));
	
	out.print(json.toString());
%>
