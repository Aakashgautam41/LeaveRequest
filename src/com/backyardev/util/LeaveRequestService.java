package com.backyardev.util;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class LeaveRequestService {

	public static boolean loginService(String email, String pass) {
		
		String hash;
		boolean returnBool = false;
		
		if(!validateMail(email)) {
			returnBool = false;
		} else {
			
			ResultSet rs = DatabaseQueries.getAuth(email);
			
			try {
				
				if(!rs.next()) {
					returnBool = false;
				} else {
					EmployeesObjectClass obj = new EmployeesObjectClass();
					obj.setEcode(rs.getString("ecode"));
					obj.setDesignation(rs.getString("designation"));
					hash = rs.getString("pass_hash");
					obj.setName(rs.getString("name"));
					obj.setProject(rs.getString("project"));
					obj.setTeamLead(rs.getString("team_lead"));
					obj.setProjectManager(rs.getString("project_manager"));
					if (Password.check(pass, hash)) {	
						returnBool = true;
					} else {
						returnBool = false;
					}
				}
			} catch(Exception ex) {
				returnBool = false;
				ex.printStackTrace();
			}
		}
		DatabaseQueries.closeConnection();
		return returnBool;
	}
	
	public static String leaveService(LeaveReqObject obj) {
		String returnString = null;
		try {
			if(DatabaseQueries.insertLeaveRequest(obj)) {
				if (LeaveMail.prepareMail(obj)){
					returnString = "true";
				} else {
					returnString = "mail_not_sent";
				}
			} else {
				returnString = "insertion_error";
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		DatabaseQueries.closeConnection();
		return returnString;
	}
	
	public static String compOffService(CompoffReqObject obj) {
		String returnString = null;
		try {
			if(DatabaseQueries.insertCompoffRequest(obj)) {
				returnString = "true";	
				if (CompoffMail.prepareMail(obj)){
					returnString = "true";
				} else {
					returnString = "mail_not_sent";
				}
			} else {
				returnString = "insertion_error";
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		DatabaseQueries.closeConnection();
		return returnString;
	}

	public static boolean setLeaveStatus(int id, String action) {
		boolean returnBool = false;
		int status = 0;
		int comp_id = 0;
		if(action.equals("approve")) {
			status = 1;
		}else if(action.equals("reject")){
			status = -1;
		}
		
		if(DatabaseQueries.setLeaveStatus(status, id)) {
			
			comp_id = DatabaseQueries.getCompId(id);
			if(!(comp_id == 0)) {
				if(DatabaseQueries.setCompOffStatus(comp_id,status)) {
					returnBool = true;
				}
			} else {
				returnBool = true;
			}
		} else {
			returnBool = false;
		}
		DatabaseQueries.closeConnection();
		return returnBool;
	}
	
	public static String setCompStatus(int id, String action, String reason, String reviewer ) {
		String returnString = null;
		int status = 0;
		
		if(action.equals("approve")) {
			status = 1;
		}else if(action.equals("reject")){
			status = -1;
		}
		
		if(DatabaseQueries.setCompStatus(status, id, reason, reviewer)) {
			returnString = "true";
		} else {
			returnString = "false";
		}
		//Run a method to update status of compoff leave 
		DatabaseQueries.setAvailStatus(id,status);
		DatabaseQueries.closeConnection();
		return returnString;
	}
	
	public static ArrayList<LeaveReqObject> populateLeaveTable(String desg, String tl_name, String ecode){
		
		ArrayList<LeaveReqObject> al = new ArrayList<>();
		ResultSet rs = DatabaseQueries.getLeaveTable(desg, tl_name, ecode);
		
		try {
			while (rs.next()) {
				LeaveReqObject obj = new LeaveReqObject();
				if(rs.getInt("status")== 0) {
					obj.setStatus("Pending");
				} else if(rs.getInt("status")== 1) {
					obj.setStatus("Approved");
				} else {
					obj.setStatus("Rejected");
				}
				
				if(rs.getInt("half_day_leave") == 1) {
					obj.setHalfDayLeave(1);
				} else if(rs.getInt("full_day_leave") == 1) {
					obj.setFullDayLeave(1);
				} 
				
				obj.setId(rs.getInt("id"));
				obj.setEcode(rs.getString("ecode"));
				obj.setName(rs.getString("name"));
				obj.setTeamLead(rs.getString("team_lead"));
				obj.setProjectManager(rs.getString("project_manager"));
				obj.setProjectName(rs.getString("project"));
				obj.setStartDate(rs.getString("leave_start_date"));
				obj.setEndDate(rs.getString("leave_end_date"));
				obj.setNumberOfDays(rs.getInt("number_of_days"));
				obj.setLeaveType(rs.getString("leave_type"));
				obj.setLeaveDesc(rs.getString("leave_desc"));
				al.add(obj);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		DatabaseQueries.closeConnection();
		return al;
	}

	public static ArrayList<CompoffReqObject> populateCompoffTable(String desg, String tl_name, String ecode){
		
		ArrayList<CompoffReqObject> al = new ArrayList<>();
		ResultSet rs = DatabaseQueries.getCompoffTable(desg, tl_name, ecode);
	
		try {
	
			while(rs.next()) {
				CompoffReqObject obj = new CompoffReqObject();

				if(rs.getString("reviewed_time") !=null) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				    java.util.Date parsedDate = dateFormat.parse(rs.getString("reviewed_time"));
				    Timestamp timestamp = new Timestamp(parsedDate.getTime());
				    Date date= new Date(timestamp.getTime());  
					obj.setReviewTime(date.toString());
					
				}else {
					obj.setReviewTime("Not Reviewed");
				}
				
				if(rs.getString("request_timestamp") != null) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				    java.util.Date parsedDate = dateFormat.parse(rs.getString("request_timestamp"));
				    Timestamp timestamp = new Timestamp(parsedDate.getTime());
				    Date date= new Date(timestamp.getTime());  
					obj.setRequestTime(date.toString());	
				}else {
					obj.setRequestTime("Not Reviewed");	
				}
								
				if(rs.getString("reviewed_by") != null) {
					obj.setReviewedBy(rs.getString("reviewed_by"));	
				}else {
					obj.setReviewedBy("Not Reviewed");	
				}
				
				if(rs.getInt("status")== 0) {
					obj.setStatus("Pending");
				} else if(rs.getInt("status")== 1) {
					obj.setStatus("Approved");
				} else {
					obj.setStatus("Rejected");
				}
				
				obj.setId(rs.getInt("id"));
				obj.setEcode(rs.getString("ecode"));
				obj.setName(rs.getString("name"));
				obj.setTeamLead(rs.getString("team_lead"));
				obj.setCompDate(rs.getString("comp_date"));
				obj.setDesc(rs.getString("description"));
				obj.setProject(rs.getString("project"));
				obj.setManager(rs.getString("project_manager"));
				obj.setNightShift(rs.getString("night_shift"));
				obj.setTicket(rs.getString("ticket_scr"));
				al.add(obj);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		DatabaseQueries.closeConnection();
		return al;
	}
	
	public static LeaveReqObject getLeave(String id){
		LeaveReqObject obj = new LeaveReqObject();
		ResultSet rs = DatabaseQueries.getLeave(id);
		try {
			if (rs.next()) {
				
				obj.setId(rs.getInt("id"));
				obj.setStatus(rs.getString("status"));
				obj.setEcode(rs.getString("ecode"));
				obj.setName(rs.getString("name"));
				obj.setTeamLead(rs.getString("team_lead"));
				obj.setProjectManager(rs.getString("project_manager"));
				obj.setProjectName(rs.getString("project"));
				obj.setStartDate(rs.getString("leave_start_date"));
				obj.setEndDate(rs.getString("leave_end_date"));
				obj.setNumberOfDays(rs.getInt("number_of_days"));
				obj.setDayLeave(rs.getInt("full_day_leave"));
				obj.setLeaveType(rs.getString("leave_type"));
				obj.setCompId(rs.getString("comp_id"));
				obj.setLeaveDesc(rs.getString("leave_desc"));
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return obj;
	}
	
	public static CompoffReqObject getCompoff(String id){
		CompoffReqObject obj = new CompoffReqObject();
		ResultSet rs = DatabaseQueries.getCompLeave(id);
		try {
			if (rs.next()) {
				
				obj.setId(rs.getInt("id"));
				obj.setEcode(rs.getString("ecode"));
				obj.setCompDate(rs.getString("comp_date"));
				obj.setDesc(rs.getString("description"));
				obj.setTicket(rs.getString("ticket_scr"));
				obj.setNightShift(rs.getString("night_shift"));
				obj.setStatus(rs.getString("status"));
				obj.setName(rs.getString("name"));
				obj.setProject(rs.getString("project"));
				obj.setTeamLead(rs.getString("team_lead"));
				obj.setManager(rs.getString("project_manager"));
			
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return obj;
	}
	
	private static boolean validateMail(String mail){
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+ 
    			"[a-zA-Z0-9_+&*-]+)*@" + 
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" + 
                "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		if(mail == null) {
		return false;
		}
		if (pat.matcher(mail).matches()) {
			if(mail.indexOf("@trantorinc.com", mail.length() - "@trantorinc.com".length()) != -1){
	            return true;
	        } else {
	        	return false;
	        }
		} else {
			return false;
		}
	}
	
}