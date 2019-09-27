<%@page import="com.backyardev.util.LeaveRequestService"%>
<%@page import="com.backyardev.util.CompoffReqObject"%>
<%@page import=" java.util.ArrayList"%>
<jsp:include page="/WEB-INF/layout.jsp"></jsp:include>
<div class="main-div">
	<table class="table hover nowrap  mt-5"  id="data-table">
	   <thead>
	      <tr>
	         <th scope="col" >Ecode <i class="fas fa-sort ml-1" onclick="sort()"></i></th>
	         <th scope="col">Name <i class="fas fa-sort ml-1" onclick="sort()"></i></th>
	         <th scope="col">TL <i class="fas fa-sort ml-1" onclick="sort()"></i></th>
	         <th scope="col">Ticket/Scr <i class="fas fa-sort ml-1" onclick="sort()"></i></th>
	         <th scope="col">Date <i class="fas fa-sort ml-1" onclick="sort()"></i></th>
	         <th scope="col">Night Shift <i class="fas fa-sort ml-1" onclick="sort()"></i></th>
       		 <th scope="col">Requested on <i class="fas fa-sort ml-1" onclick="sort()"> </i></th>
	         <th scope="col">Reviewed By <i class="fas fa-sort ml-1" onclick="sort()"> </i></th>
       		 <th scope="col">Reviewed 	on <i class="fas fa-sort ml-1" onclick="sort()"> </i></th>
	        
		     <% if(session.getAttribute("desg").equals("Developer")){ %>
	            <th scope="col">Status <i class="fas fa-sort ml-1" onclick="sort()"> </i></th>
	         <% }
		     else { %>     
	            <th scope="col">Action <i class="fas fa-sort ml-1" onclick="sort()"> </i></th>
	         <% } %>
	
	      </tr>
	   </thead>
	   <tbody>
	      <%
	      	String ecode = (String) session.getAttribute("ecode");
	        String desg = (String) session.getAttribute("desg");
	        String name = (String) session.getAttribute("name");
	        ArrayList<CompoffReqObject> al = LeaveRequestService.populateCompoffTable(desg,name,ecode);
	      %>
	      <% for(int i = 0; i < al.size(); i+=1) { %>
	      <tr  id=" <%=al.get(i).getId()%>" class="targetRow">
	         <td><%=al.get(i).getEcode() %></td>
	         <td><%=al.get(i).getName() %></td>
	         <td><%=al.get(i) .getTeamLead()%></td>
	         <td><%=al.get(i) .getTicket()%></td>
	         <td><%=al.get(i) .getCompDate()%></td>
	         <td><%=al.get(i) .getNightShift()%></td>
	         <td><%=al.get(i).getRequestTime()%></td>
	         <td><%=al.get(i).getReviewedBy()%></td>
	         <td><%=al.get(i).getReviewTime()%></td>
	    
	    	<!-- changes action/status column view based on designation --> 
	         <% if(session.getAttribute("desg").equals("Developer")) { %>			 	<!-- designation is developer -->
			         <% if ((al.get(i).getStatus()).equals("Pending")) { %> 
			         <td class="text-pending font-weight-bold"><%=al.get(i).getStatus()%></td>
			         <% } %>
			         <% if((al.get(i).getStatus()).equals("Approved")) { %>
			         <td class="text-approved  font-weight-bold"><%=al.get(i).getStatus()%></td>
			         <% } %>
			         <% if ((al.get(i).getStatus()).equals("Rejected")) { %>
			         <td class="text-rejected font-weight-bold"><%=al.get(i).getStatus()%></td>
			         <% } %>
         	<% } else { %>																															<!-- designation is !developer-->	
	         <!-- changes action/status column view based on status-->
			         <% if ((al.get(i).getStatus()).equals("Approved")) { %>	<!-- status == approved -->	
			   			<td class="text-approved font-weight-bold" > Approved </td>
					<% } else if ((al.get(i).getStatus()).equals("Rejected")) { %>	<!-- status == rejected -->	
			         <td class="text-rejected font-weight-bold" > Rejected </td>
			         <% } else { %>	<!-- status == pending -->	
			         <td>
			            <button type="button" class=" btn-check" id="<%=al.get(i).getId()%>"><i class="fas fa-check "></i></button>
			            <button type="button" class=" btn-reject" id="<%=al.get(i).getId()%>"><i class="fas fa-times"></i></button>
			         </td>
			         <% } %>
	            <% } %>
	      </tr>
	      <% } %>
	   </tbody>
	</table>
	 <div class="modal fade" id="rejectReqModal" tabindex="-1" role="dialog" aria-labelledby="rejectReqModalTitle" aria-hidden="true">
	 <div class="modal-dialog modal-dialog-centered" role="document">
 	    <div class="modal-content">
	        <div class="modal-header">
	            <h4 class="modal-title" id="ModalTitle">Reject Request?</h4>
	            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	                <span aria-hidden="true">&times;</span>
                </button>
	        </div>
	        <div class="modal-body">
	            <form class="form">
	            <div class="form-group">
	             <label for="reason">Rejection reason</label>
	             <input class="form-control" id="reason" name="reason" type="text" required placeholder="Reason for rejection"/>
	            </div>
	            </form>
	            <div style="text-align: center; display: none;" id="progress">
	                <img src="static/loading.gif" alt="loading..">
	            </div>
	        </div>
	        <div class="modal-footer">
	            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
	            <button value="Add" class="btn btn-primary reject-btn">Reject</button>
	            </div>
	        </div>
	    </div>
	</div>
</div>
<script src="/LeaveRequest/static/compDisplayJs.js"></script>
<script>
	$(document).ready(function() {
		$('#all-comp').css('color', '#f1f1f1');
		$('#all-comp').css('font-size', '1.2rem');
		$('#data-table').DataTable({
			"ordering": false,
			"pagingType": "full_numbers",
			 fixedHeader: true
		}
		);
	});
</script>

</body>
</html>