<%@page import="main.java.util.FileUploadEnums" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<html>

<head>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
	<link rel="stylesheet" href="css/styles.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/themify-icons/0.1.2/css/themify-icons.css">
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/datatables/1.10.19/js/jquery.dataTables.min.js"></script>
</head>

<body>
    <form id="file-results-form" name="file-results-form" action="">
        <a id="back-btn" class="btn" href="/back"><span class="ti-arrow-left"></span> Back</a>
        <h1>${fn:toUpperCase(fn:substring(fileResults.uploadType, 0, 1))}${fn:toLowerCase(fn:substring(fileResults.uploadType, 1,fn:length(fileResults.uploadType)))}- File Results</h1>
        <h3>File Info</h3>
        <div class="row">
            <div class="col-xs-3"><label>File Name</label>
                <p>${fileResults.fileName}</p>
            </div>
            <div class="col-xs-3"><label>Header Row #</label>
                <p>${fileResults.headerRowNum}</p>
            </div>
            <div class="col-xs-3"><label>Last Row #</label>
                <p>${fileResults.lastRowNum}</p>
            </div>
            <div class="col-xs-3"><label>Total Header Rows</label>
                <p>${fileResults.totalNumHeaderRows}</p>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-3"><label>Total Data Rows</label>
                <p>${fileResults.totalNumDataRows}</p>
            </div>
            <c:if test="${fileResults.uploadType eq 'CONTRIBUTION'}">
                <div class="col-xs-3"><label>File Total</label>
                    <p>
                        <fmt:formatNumber value="${fileResults.fileTotalAmt}" type="currency" />
                    </p>
                </div>
            </c:if>
            <div class="col-xs-3"><label>Time Taken on Backend</label>
                <p>${fileResults.timeTaken}</p>
            </div>
        </div>
        <h3>Header Column Mappings</h3>
        <table id="header-table" class="display nowrap" style="width:100%">
            <thead>
                <tr>
                    <th>Column</th>
                    <th>Your Field</th>
                    <th>Mapped To</th>
                    <c:if test="${fileResults.uploadType eq 'CONTRIBUTION'}">
                        <th>Source ID</th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="entry" items="${fileResults.headerList}">
                    <tr>
                        <td>${entry.columnNum}</td>
                        <td>${entry.userInputValue}</td>
                        <td>
                            <c:choose>
                                <c:when test="${entry.name == 'No Mapping Found'}">
                                    <span style="color:red;">${entry.name}</span>
                                </c:when>
                                <c:otherwise>
                                    ${entry.name}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <c:if test="${fileResults.uploadType eq 'CONTRIBUTION'}">
                            <td>${entry.sourceId}</td>
                        </c:if>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <h3>File Data</h3>
        <table id="file-upload-data-table" class="display dataTable no-footer" style="width:100%" role="grid">
            <tbody>
                <tr class="odd">
                    <td valign="top" colspan="3" class="dataTables_empty" style="border-top:1px solid #111;">No data in File</td>
                </tr>
            </tbody>
        </table>
        <h3>File Errors</h3>

        <table id="file-upload-error-table" class="display nowrap" style="width:100%">
            <thead>
                <tr>
                    <th>Row #</th>
                    <th>Column</th>
                    <th>Error Message</th>
                    <th>Severity</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="entry" items="${fileResults.uploadErrorList}" varStatus="loop">
                    <tr>
                        <td>${entry.rowNum}</td>
                        <td>${entry.excelColumnNum}</td>
                        <td>${entry.errorMsg}</td>
                        <td>
                            <c:choose>
                                <c:when test="${entry.severity == 'CRITICAL'}">
                                    <span style="color:red;">${entry.severity}</span>
                                </c:when>
                                <c:otherwise>
                                    ${entry.severity}
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <a id="make-corrections-btn" class="btn" href="/back"><span class="ti-arrow-left"></span> Make Corrections</a>
        <c:if test="${fileResults.canSubmitFile() and fileResults.totalNumDataRows != 0}">
            <a id="submit-btn" class="btn" href="">Submit File <span class="ti-arrow-right"></span></a>
        </c:if>
    </form>
</body>

<div id="loader">
	<div id="loader-icon"></div>
</div>

</html>

<script>
$("#loader").show();
var fileResultsTable;
$(document).ready(function() {
    $('#header-table').DataTable({
    	bPaginate: false,
    	bFilter: false, 
    	bInfo: false
    });

    var jsonDataArray = '${fileResults.jsonDataArray}';
    if (jsonDataArray !== '[]') { 
	    var data = JSON.parse(jsonDataArray);

	    var columns = [];
	    columns.push({data: 'Row #', title: 'Row #'});
	    <c:forEach var="entry" items="${fileResults.headerList}" varStatus="loop">
			<c:if test="${entry.name != 'No Mapping Found.'}">
			    columns.push({data: '${entry.name}', title: '${entry.name}'});
			</c:if>
		</c:forEach>

	    fileResultsTable = $('#file-upload-data-table').DataTable({
			data: data,
			columns: columns,
			pageLength: 15,
			scrollX: true
		});
	}
	$('#file-upload-error-table').DataTable({
    	bFilter: false, 
    	bInfo: false,
    	pageLength: 15,
    	oLanguage: {
        	sEmptyTable: "No File Errors"
    	}
    });
	$("#loader").hide();
});

// $(".file-error-row-num").click(function() {
// 	let rowNum = $(this).html();
// 	fileResultsTable.columns(0).search(rowNum).draw();
// 	$('input[type="search"]').val(rowNum);
// });
</script>