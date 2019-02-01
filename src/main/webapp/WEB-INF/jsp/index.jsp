<%@page import="main.java.util.FileUploadEnums, main.java.web.Application" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<html>

<head>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<link rel="stylesheet" hreg="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-modal/2.2.6/css/bootstrap-modal.css">
	<link rel="stylesheet" href="css/styles.css">
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"></script>
</head>

<body>
	<form id="upload-form" name="upload-form" action="/upload-file" method="post" enctype="multipart/form-data">
	    <h1>Upload Excel File</h1>
	    <fieldset>
	        <legend>Select a File Upload Type</legend>
            <input type="radio" id="contributions-type" name="uploadType" value="<%=FileUploadEnums.UploadType.CONTRIBUTION%>" checked />
            <label for="contributions-type">Contribution</label>
            <input type="radio" id="census-type" name="uploadType" value="<%=FileUploadEnums.UploadType.CENSUS%>" />
            <label for="census-type">Census</label>
            <input type="radio" id="compliance-type" name="uploadType" value="<%=FileUploadEnums.UploadType.COMPLIANCE%>" />
            <label for="compliance-type">Compliance</label>
	    </fieldset>
	    <legend>Select a File</legend>
	    <input name="file-name" id="file-name" value="" type="text" style="width: 400px; padding: 0px 8px;">
	    <input name="file" id="file" type="file" class="hide" accept=".xls,.xlsx,.txt,.csv"></input>
	    <button id="browse-btn" class="btn" type="button">Browse</button>
	    <button id="upload-btn" class="btn" form="upload-form" type="submit">Upload</button>
	</form>
	<div id="contrib-src-reminder-modal" class="modal" tabindex="-1" role="dialog">
	  	<div class="modal-dialog" role="document">
	    	<div class="modal-content">
	      		<div class="modal-header">
	        		<h5 class="modal-title">Reminder</h5>
	        		<button id="close-btn" type="button" class="close" data-dismiss="modal" aria-label="Close">
	          			<span aria-hidden="true">&times;</span>
	        		</button>
	      		</div>
		      	<div class="modal-body">
		        	<p>Make sure you have at least one Contribution Source on your file. 
		        	<c:choose>
						<c:when test="${fn:length(Application.planSourcesMap) gt 1}">
					  		These are the sources that are available for your plan -
					  	</c:when>
					  	<c:otherwise>
					  		This is the source that is available for your plan -
					  	</c:otherwise>
					</c:choose>
					<c:forEach var="source" items="${Application.planSourcesMap}" varStatus="loop"><c:if test="${loop.last and fn:length(Application.planSourcesMap) gt 1}">and </c:if><c:out value="${source.value}"/><c:if test="${!loop.last}">, </c:if></c:forEach>.</p>
		      	</div>
		      	<div class="modal-footer">
		        	<button type="button" class="btn btn-primary" id="ok-btn">Ok</button>
		        	<button type="button" class="btn btn-secondary" data-dismiss="modal" id="cancel-btn">Cancel</button>
		      	</div>
	    	</div>
	  	</div>
	</div>
	<div id="loader">
		<div id="loader-icon"></div>
	</div>
</body>

</html>

<script>
$("#browse-btn").on('click',function(){
	let uploadTypeSelected = $("[name='uploadType']:checked").val();
	if (uploadTypeSelected == "CONTRIBUTION") {
		$("#contrib-src-reminder-modal").modal("show");
	} else {
		$('#file').click();
	}
});

$("#ok-btn, #close-btn").on('click',function(){
	$('#contrib-src-reminder-modal').modal('hide');
	$('#file').click();
});

$('input[type=file]').change(function (e) {
	var fileName = this.value.match(/[^\/\\]+$/);
	$("#file-name").val(fileName);
});

$("#upload-btn").on('click',function(){
	$('#loader').show();
});
</script>