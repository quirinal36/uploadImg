<%@page import="upload.bacoder.coding.dev.UploadUtil"%>
<%@page import="upload.bacoder.coding.db.DBconn"%>
<%@page import="upload.bacoder.coding.bean.Photo"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONString"%>
<%@page import="org.json.JSONException"%>


<%@page import="java.util.logging.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	//final String path = getServletContext().getRealPath("/storage");
	//final String path = "/volume1/@appstore/Tomcat7/src/webapps/storage";
	final String path = "/home/phbong31/storage";

	Logger logger = Logger.getLogger("##########addPhotos.jsp");
	
   	String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).format(new Date());
	String jsonString = request.getParameter("attachment");
	
	String imgEncodedStr = "";
	String fileName = "";
	String classification = "";
	String uploader = "";

	//logger.info("####param : " + jsonString);
	
	JSONArray jsArray = new JSONArray();
	
	try {
		JSONArray jsonArray = new JSONArray(jsonString);
		//logger.info("####befor iterator");
		for(int i = 0; i < jsonArray.length(); i++ ){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			//logger.info("####jsonObj : " + jsonObject.toString());
			
	        imgEncodedStr = jsonObject.getString("encoded");
			fileName = jsonObject.getString("fileName");
			classification = jsonObject.getString("classification");
			uploader = jsonObject.getString("uploader");
		
			//logger.info("####fileName : " + fileName);
			
			Photo photoInfo = new Photo();

			photoInfo.setClassification(classification);
			photoInfo.setDate(timeStamp);
			photoInfo.setUploader(uploader);
			photoInfo.setSync(2);
			
			String result = new String();
			//logger.info("##########imgEncodedStr: "+ imgEncodedStr);
			if (imgEncodedStr != null) {
				result = new UploadUtil().setPhoto(path, imgEncodedStr, fileName, photoInfo.getPatientId());
			}
			//logger.info("###result : " + result);
			
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
			
			jsArray.put(i, dbconn.addPhotoInfo(photoInfo));

		}
	} catch (Exception e) {
		e.printStackTrace();
	}

	//logger.info("####end");
	logger.info("###photoIdArray : " + jsArray.toString());

	out.print(jsArray.toString());
%>
