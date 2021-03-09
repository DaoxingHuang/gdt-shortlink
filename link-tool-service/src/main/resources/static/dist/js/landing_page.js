var landingPageList;
var landingPageDataTable;

var deleteBizPermission = true;
var platformList = ["SAA", "GH"];
var moduleList;
var saaschemeList;

function addLandingPage(data) {
	$('#landingPageIdInput').val('')
	$('#landingPagePlatformSelect').val('');
	$('#landingPageNameInput').val('');
	$('#landingPageModuleSelect').val('');
	$('#landingPageSchemeInput').val('');
	$('#landingPageSchemeSelect').val('');
	$('#landingPagePathInput').val('');
	$('#landingPathAddAlarmMessage').hide();
	$('#landingPageAdd').modal('show');
}


function saveLandingPage() {
	var platform = $('#landingPagePlatformSelect').val();
	var name = $('#landingPageNameInput').val();
	var module = $('#landingPageModuleSelect').val();
	var saaschemeName = $("#landingPageSchemeSelect").val();
	var path = $('#landingPagePathInput').val();

	if (null == platform) {
		return;
	}

	if (platform == "SAA" && (null == saaschemeName || "" == saaschemeName)) {
	    return;
	}

	if (platform == "GH") {
	    saaschemeName = "";
	}

	if (platform == "SAA") {
	    saaschemeName = $("#landingPageSchemeSelect").val();
        var saascheme = null;
        for (var i = 0; i < saaschemeList.length; i++) {
            if (saaschemeName == saaschemeList[i].scheme) {
                saascheme = saaschemeList[i];
            }
        }
        if (saascheme.native) {
            path = "";
            var saaSchemeInputList = $('#saaSchemaParamDiv input');
            var saaSchemeValueList = [];
            for (var i = 0; i < saaSchemeInputList.length; i++) {
                if (i > 0) {
                    path += ",";
                }
                var inputValue = saaSchemeInputList[i].value;
                if (null != inputValue && "" != inputValue) {
                    path = path + "{" + saaSchemeInputList[i].value + "}";
                }
            }
        }
	}

    var param = {};
    param["platform"] = platform;
    param["name"] = name;
    param["module"] = module;
    param["schemeName"] = saaschemeName;
    param["pathTemplate"] = path;

    $.ajaxSetup({

    contentType : 'application/json;charset=utf-8'

    });

	$.post("/deeplink-tool/landing_page/add", JSON.stringify(param), function(data){
		if (null != data && "" != data && data.code == 200) {
			$('#landingPageAdd').modal('hide');
			queryLandingPageList();
		} else {
			showErrorMessage(data);
		}
	}, "json");

}

function deleteLandingPage(id) {
    if (!confirm("Are you sure？")){
    		return;
    }
    $.post("/deeplink-tool/landing_page/delete?id=" + id, function(data){
        if (null != data && "" != data && data.code == 200) {
            queryLandingPageList();
        } else {
            showErrorMessage(data);
        }
    });
}

function queryLandingPageList() {
	$.getJSON("/deeplink-tool/landing_page/list", function(response){
	        if (response.code != 200) {
	            return;
	        }
			landingPageList = response.data.list;

//			$.each(landingPageList, function (name, value) {
//			    var id = value.id;
//			    var platform = value.platform;
//			    var name = value.name;
//			    var module = value.module;
//			    var schema = value.schema;
//			    var path = value.path;
//			});
			drawListResult();
	});
}

function drawListResult() {
	var hideDiv = $("#landingpage-page-wrapper .default-hide");
	for (var i = 0; i < hideDiv.length; i++) {
		hideDiv[i].classList.remove("default-hide");
	}

	if (null != landingPageDataTable && "" != landingPageDataTable) {
		landingPageDataTable.destroy();
	}

	var trList = $("#landingPageListTable tr");
	trList.remove();

	var columns = [{"title": "ID"}, {"title": "Platform"}, {"title": "Name"}, {"title": "Module"}, {"title": "Schema"}, {"title": "Path"}, {"title": "CreateTime"}];
	if (deleteBizPermission) {
		columns.push({"title": "Operation"});
	}

	for (var i = 0; i < landingPageList.length; i++) {
		var newTr = $("<tr></tr>");
		newTr.appendTo("#landingPageListTable tbody");

		$("<td>" + landingPageList[i].id + "</td>").appendTo(newTr);
		$("<td>" + landingPageList[i].platform + "</td>").appendTo(newTr);
		$("<td>" + landingPageList[i].name + "</td>").appendTo(newTr);
		$("<td>" + landingPageList[i].module + "</td>").appendTo(newTr);
		$("<td>" + landingPageList[i].schemeName + "</td>").appendTo(newTr);
		$("<td>" + landingPageList[i].pathTemplate + "</td>").appendTo(newTr);
//		$("<td>" + landingPageList[i].createTime + "</td>").appendTo(newTr);
//		$("<td>" + formatDate(landingPageList[i].updateTime) + "</td>").appendTo(newTr);
		$("<td>" + formatDate(landingPageList[i].createTime) + "</td>").appendTo(newTr);
		if (deleteBizPermission) {
			$("<td class=\"center\"> <button class=\"btn btn-outline btn-link btn-s\" onClick=\"deleteLandingPage(" + landingPageList[i].id + ")\" >Delete</button> </td>").appendTo(newTr);
		}
	}

	landingPageDataTable = $('#landingPageListTable').DataTable({
		"columns": columns,
		"responsive": true,
		"aaSorting": [[0, 'desc']],
		"sPaginationType": "full_numbers",
		"oLanguage": {
			"sProcessing": "loading......",
			"sLengthMenu": "per page _MENU_ records",
			"sZeroRecords": "Sorry, no data.",
			"sEmptyTable": "No data!",
			"sInfo": "current show _START_ to _END_ records，total _TOTAL_ records",
			"sInfoFiltered": "total _MAX_ records",
			"sSearch": "Search",
			"oPaginate": {
				"sFirst": "First",
				"sPrevious": "Previous",
				"sNext": "Next",
				"sLast": "Last"
			}
		}
	});
}


function initPlatformParams() {
    $("#landingPagePlatformSelect").find("option:selected").text("");
    $("#landingPagePlatformSelect").empty();
	for (var i = 0; i < platformList.length; i++) {
		var option = $("<option value=\"" + platformList[i] + "\" >" + platformList[i] + "</option>");
		option.appendTo($("#landingPagePlatformSelect"));
	}
}

function initModuleParams() {
    $("#landingPageModuleSelect").find("option:selected").text("");
    $("#landingPageModuleSelect").empty();
    $.getJSON("/deeplink-tool/landing_page/module", function(response){
        if (response.code != 200) {
            return;
        }
        moduleList = response.data;
        for (var i = 0; i < moduleList.length; i++) {
            var option = $("<option value=\"" + moduleList[i] + "\" >" + moduleList[i] + "</option>");
            option.appendTo($("#landingPageModuleSelect"));
        }
    });
}

function initSAASchemeParams() {
    $("#landingPageSchemeSelect").find("option:selected").text("");
    $("#landingPageSchemeSelect").empty();
    $.getJSON("/deeplink-tool/landing_page/saascheme", function(response){
        if (response.code != 200) {
            return;
        }
        saaschemeList = response.data;
        for (var i = 0; i < saaschemeList.length; i++) {
            var option = $("<option value=\"" + saaschemeList[i].scheme + "\" >" + saaschemeList[i].scheme + "</option>");
            option.appendTo($("#landingPageSchemeSelect"));
        }
    });
}

function changePlatform(item) {
    var selectedModule = $(item).val();
    if (selectedModule == "GH") {
//        $("#landingPageSchemaInput").parent().hide();
        $("#landingPageSchemeSelect").parent().hide();
        $("#saaSchemaParamDiv").hide();
        $("#landingPagePathInput").parent().show();
    } else if (selectedModule == "SAA") {
//        $("#landingPageSchemaInput").parent().show();
        $("#landingPageSchemeSelect").parent().show();
        $("#saaSchemaParamDiv").show();
        $("#landingPageSchemeSelect").trigger("change");
    }
}

function changeSAAScheme(item) {
    var saaschemeName = $("#landingPageSchemeSelect").val();
    var saascheme = null;
    for (var i = 0; i < saaschemeList.length; i++) {
        if (saaschemeName == saaschemeList[i].scheme) {
            saascheme = saaschemeList[i];
        }
    }
    if (saascheme.native) {
        $("#landingPagePathInput").parent().hide();
        showSAASchemeParam();
    } else {
        $("#landingPagePathInput").parent().show();
        hideSAASchemeParam();
    }
}

function hideSAASchemeParam() {
    $('#saaSchemaParamDiv').hide();
}

function showSAASchemeParam() {
//    $('#saaSchemaParamDiv div').remove();
//
//    var saaSchemeParam = $("<div class=\"jupiter-form-group\"><label></label></div>");
//    $("<input class=\"jupiter-form-control\" name=\"\" value=\"\" />").appendTo(saaSchemeParam);
//    $("<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\" onClick=\"removeSAASchemeParam(this)\"></span>").appendTo(saaSchemeParam);
//    saaSchemeParam.appendTo($('#saaSchemaParamDiv'));

//    $("<div class=\"jupiter-form-group\"><label></label><span class=\"glyphicon glyphicon-plus\" aria-hidden=\"true\" onClick=\"addSAASchemeParam(this)\"></span></div>").appendTo($('#saaSchemaParamDiv'));

    $('#saaSchemaParamDiv').show();
//    if (null != shortLink.deepLinkVoList && shortLink.deepLinkVoList.length > 0) {
//        for (var i = 0; i < shortLink.deepLinkVoList.length; i++) {
//            var deepLinkInputDiv = $("<div class=\"jupiter-form-group\"><label></label></div>");
////            $("<input class=\"jupiter-form-control\" readonly name=\"\" value=\"" + shortLink.deepLinkVoList[i].id + "\" >" + shortLink.deepLinkVoList[i].name + "</input>").appendTo(deepLinkInputDiv);
//
//            var deepLinkSelect = $("<select class=\"jupiter-form-control\" onChange=\"changeDeepLinkSelect(this)\" disable ></select>");
//            for (var j = 0; j < deepLinkList.length; j++) {
//                if (deepLinkList[j].id == shortLink.deepLinkVoList[i].id) {
//                    $("<option selected value=\"" + deepLinkList[j].id + "\">" + deepLinkList[j].name + "</option>").appendTo(deepLinkSelect);
//                } else {
//                    $("<option value=\"" + deepLinkList[j].id + "\">" + deepLinkList[j].name + "</option>").appendTo(deepLinkSelect);
//                }
//            }
//
//            deepLinkSelect.appendTo(deepLinkInputDiv);
//            $("<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\" onClick=\"removeDeepLinkInput(this)\"></span>").appendTo(deepLinkInputDiv);
//
//            deepLinkInputDiv.appendTo($('#shortLinkDeepLinkInput'));
//        }
//    }
//
//    $("<div class=\"jupiter-form-group\"><label></label><span class=\"glyphicon glyphicon-plus\" aria-hidden=\"true\" onClick=\"addDeepLinkSelect(this)\"></span></div>").appendTo($('#shortLinkDeepLinkInput'));

}

function addSAASchemeParam(item) {
    var saaSchemeParam = $("<div class=\"jupiter-form-group\"><label></label></div>");
    $("<input class=\"jupiter-form-control\" name=\"\" value=\"\" />").appendTo(saaSchemeParam);
    $("<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\" onClick=\"removeSAASchemeParam(this)\"></span>").appendTo(saaSchemeParam);
    saaSchemeParam.insertBefore(item.parentNode);
}

function removeSAASchemeParam(item) {
    item.parentNode.remove();
}

function showErrorMessage(data) {
	if (null != data.message) {
		alert(data.message);
	} else {
		alert("system error");
	}
}