var req_id = 0;

$('.btn-check').click(function(s) {
	s.stopPropagation();
    var url = "ActionCompLeaveServlet";
    var method = "post";
    var id = $(this).attr("id")
    console.log(id);

    $.ajax({
        type: method,
        url: url,
        data: {
            'id': id,
            'action': 'approve'
        },
        success: function(data) {
            if (data == "true") {
                window.location.replace('comp');
            } else {
                alert('Error!');
            }
        }
    });
});
	
$('.btn-reject').click(function(s) {
	s.stopPropagation();
	req_id = $(this).attr("id")
	$('#rejectReqModal').modal('show');
});

$('.reject-btn').click(function(){
	console.log('workss!');
	var url = "ActionCompLeaveServlet";
    var method = "post";
    var reason = $('#reason').val();
    $.ajax({
        type: method,
        url: url,
        data: {
            'id': req_id,
            'action': 'reject',
            'reason': reason
        },
        success: function(data) {
            if (data == "true") {
                window.location.replace('comp');
            } else {
                alert(data);
            }
        }
    });
});

function sort(){
	 $('#data-table').DataTable({
	 		destroy: true,
	    	"ordering": true,
	    	fixedHeader: true,
	    },
	    {
	        "pagingType": "scrolling"
	    });
	 $(".fa-sort").hide()
}

$('.targetRow').click(function() {
	var url = "comp/"+$(this).attr("id").trim()
	console.log(url);
	window.location.replace(url);
});