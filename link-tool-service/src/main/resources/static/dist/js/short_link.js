var shortLinkList;
var deepLinkList;
var landingPageList;
var shortLinkDataTable;
var originalLinkList;
var currentShortLink;

var domain = window.location.host;
//var context-path = "/deeplink-tool";

var deleteBizPermission = true;
var statusList = ["ACTIVE", "OFF"];

var btnCopyShortLink = new ClipboardJS("#shortLinkCopyButton");
var shortLinkQRCode = new QRCode("shortLinkQRInfoDiv");


// 点击add按钮
function addShortLink(data) {
	$('#shortLinkIdInput').val('');
	$('#shortLinkUTMSourceInput').val('');
	$('#shortLinkUTMMediumInput').val('');
	$('#shortLinkUTMCampaignInput').val('');
	$('#shortLinkUTMContentInput').val('');
	$('#shortLinkPlatformSelect').val('');
	$('#shortLinkNameInput').val('');
	$('#shortLinkLandingPageSelect').val('');
	$('#shortLinkAddAlarmMessage').hide();
	$('#shortLinkAdd').modal('show');
}

// 发送update请求
function updateShortLink() {
    var id = $('#shortLinkIdInput').val();
    var status = $('#shortLinkStatusSelect').val();
    var code = $('#shortLinkCodeInput').val();
    var linkType = $('#shortLinkLinkTypeInput').val();
    var name = $('#shortLinkNameInput').val();
    var expiredTime = $('#shortLinkExpiredTimeInput').val();

    var param = {};
    param["id"] = id;
    param["status"] = status;
    param["linkType"] = linkType;
    param["name"] = name;
    param["expiredTime"] = expiredTime;
    param["originalLinkVoList"] = currentShortLink.originalLinkVoList;

    $.ajaxSetup({
        contentType : 'application/json;charset=utf-8'
    });

    $.post("/deeplink-tool/short_link_business/updateShortLink", JSON.stringify(param), function(data){
        if (null != data && "" != data && data.code == 200) {
            $('#shortLinkUpdate').modal('hide');
            queryShortLinkList();
        } else {
            showErrorMessage(data);
        }
    }, "json");
}

// 发送save请求
function saveShortLink() {
	var name = $('#shortLinkNameInput').val();
	var link = $('#shortLinkLinkInput').val();
	var expiredTime = $('#shortLinkExpireTimeInput').val();


	if (isEmpty(utmSource) || isEmpty(utmMedium) || isEmpty(utmCampaign) || isEmpty(utmContent) || isEmpty(name) || isEmpty(platform) || isEmpty(landingPageId)) {
		return;
	}

    var param = {};
    param["utmSource"] = utmSource;
    param["utmMedium"] = utmMedium;
    param["utmCampaign"] = utmCampaign;
    param["utmContent"] = utmContent;
    param["platform"] = platform;
    param["name"] = name;
    param["landingPageId"] = landingPageId;
    param["paramMap"] = landingPageParam;

    $.ajaxSetup({
        contentType : 'application/json;charset=utf-8'
    });

	$.post("/deeplink-tool/short_link/add", JSON.stringify(param), function(data){
		if (null != data && "" != data && data.code == 200) {
			$('#shortLinkAdd').modal('hide');
			queryShortLinkList();
		} else {
			showErrorMessage(data);
		}
	}, "json");

}

// 点击delete按钮
function deleteShortLink(id) {
    if (!confirm("Are you sure？")){
    		return;
    }
    $.getJSON("/deeplink-tool/short_link/delete?id=" + id, function(response){
        if (null != response && "" != response && response.code == 200) {
            queryShortLinkList();
        } else {
            showErrorMessage(response.message);
        }
    });
}

// 查询所有的ShortLink
function queryShortLinkList() {
	$.getJSON("/deeplink-tool/short_link/list", function(response){
	        if (response.code != 200) {
	            return;
	        }
			shortLinkList = response.data;
			drawShortLinkListResult();
	});
}

// 绘制table
function drawShortLinkListResult() {
	var hideDiv = $("#shortlink-page-wrapper .default-hide");
	for (var i = 0; i < hideDiv.length; i++) {
		hideDiv[i].classList.remove("default-hide");
	}

	if (null != shortLinkDataTable && "" != shortLinkDataTable) {
		shortLinkDataTable.destroy();
	}

	var trList = $("#shortLinkListTable tr");
	trList.remove();

	var columns = [{"title": "ID"},{"title": "Code"}, {"title": "Name"}, {"title": "Type"}, {"title": "Status"}, {"title": "expiredTime"}, {"title": "Detail"}];
	if (deleteBizPermission) {
		columns.push({"title": "Operation"});
	}

	var newFootTr = $("<tr></tr>");
    newFootTr.appendTo("#shortLinkListTable tfoot");
    for (var i = 0; i < columns.length; i++) {
        $("<th></th>").appendTo(newFootTr);
    }

	for (var i = 0; i < shortLinkList.length; i++) {
		var newTr = $("<tr></tr>");
		newTr.appendTo("#shortLinkListTable tbody");

		$("<td>" + shortLinkList[i].id + "</td>").appendTo(newTr);
		$("<td>" + shortLinkList[i].code + "</td>").appendTo(newTr);
		$("<td>" + shortLinkList[i].name + "</td>").appendTo(newTr);
		$("<td>" + shortLinkList[i].linkType + "</td>").appendTo(newTr);
		$("<td>" + shortLinkList[i].status + "</td>").appendTo(newTr);
//		$("<td>" + shortLinkList[i].landingPageVo.name + "</td>").appendTo(newTr);
		$("<td>" + formatDate(shortLinkList[i].expiredTime) + "</td>").appendTo(newTr);
//		$("<td>" + shortLinkList[i].status + "</td>").appendTo(newTr);
		$("<td class=\"center\"> " +
//		    "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDetailShortLink('" + shortLinkList[i].id + "')\" >Show " + shortLinkList[i].deepLinkVoList.length + " DeepLink</button>" +
		    "<button class=\"btn btn-outline btn-link btn-s\" onClick=\"showDetailShortLink('" + shortLinkList[i].id + "')\" >Show Detail</button>" +
        "</td>").appendTo(newTr);
		if (deleteBizPermission) {
			$("<td class=\"center\"> " +
			"<button class=\"btn btn-outline btn-link btn-s\" onClick=\"editShortLink(" + shortLinkList[i].id + ")\" >Edit</button> " +
			" </td>").appendTo(newTr);
		}
	}

	shortLinkDataTable = $('#shortLinkListTable').DataTable({
		"columns": columns,
		"responsive": true,
		"aaSorting": [[0, 'desc']],
		"sPaginationType": "full_numbers",
		"lengthMenu": [10, 50, 100, 500],
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
		},
        initComplete: function () {
             var api = this.api();
             api.columns().indexes().flatten().each( function ( i ) {
                 if (i == 3 || i == 4) {
                     var column = api.column( i );
                     var select = $('<select><option value=""></option></select>')
                         .appendTo( $(column.footer()).empty() )
                         .on( 'change', function () {
                             var val = $.fn.dataTable.util.escapeRegex(
                                 $(this).val()
                             );
                             column
                                 .search( val ? '^'+val+'$' : '', true, false )
                                 .draw();
                         } );
                     column.data().unique().sort().each( function ( d, j ) {
                         select.append( '<option value="'+d+'">'+d+'</option>' )
                     } );
                 }
             } );
         }
	});

	$('#shortLinkListTable').on( 'length.dt', function ( e, settings, len ) {
        console.log( 'New page length: '+len );
    } );

    $('#shortLinkListTable').on( 'page.dt', function () {
        var info = shortLinkDataTable.page.info();
        console.log( 'Showing page: '+info.page+' of '+info.pages );
    } );
}

// 点击Show Detail
function showDetailShortLink(shortLinkId) {
    var shortLink;
    for (var i = 0; i < shortLinkList.length; i++) {
        if (shortLinkList[i].id == shortLinkId) {
            shortLink = shortLinkList[i];
            break;
        }
    }

    $("#shortLinkInfoDiv input").attr("readonly", "true");

    $('#shortLinkStatusDiv span').remove();
    $('#shortLinkNameDiv span').remove();
    $('#shortLinkCodeDiv span').remove();
    $('#shortLinkLinkDiv span').remove();
    $('#shortLinkLinkTypeDiv span').remove();
    $('#shortLinkExpiredTimeDiv span').remove();
    $('#shortLinkUpdateTimeDiv span').remove();
    $('#shortLinkDeepLinkDiv span').remove();
    $('#shortLinkDeepLinkDiv br').remove();

    $("<span>" + shortLink.status + "</span>").appendTo($('#shortLinkStatusDiv'));
    $("<span>" + shortLink.name + "</span>").appendTo($('#shortLinkNameDiv'));
    $("<span>" + shortLink.code + "</span>").appendTo($('#shortLinkCodeDiv'));
    $("<span>" + shortLink.link + "</span>").appendTo($('#shortLinkLinkDiv'));
    $("<span>" + shortLink.linkType + "</span>").appendTo($('#shortLinkLinkTypeDiv'));
    $("<span>" + shortLink.expiredTime + "</span>").appendTo($('#shortLinkExpiredTimeDiv'));
    $("<span>" + shortLink.updateTime + "</span>").appendTo($('#shortLinkUpdateTimeDiv'));

    if (null != shortLink.originalLinkVoList && shortLink.originalLinkVoList.length > 0) {
        for (var i = 0; i < shortLink.originalLinkVoList.length; i++) {
            $("<span>" + shortLink.originalLinkVoList[i].originalLink + "</span>").appendTo($('#shortLinkDeepLinkDiv'));
            $("<br>").appendTo($('#shortLinkDeepLinkDiv'));
        }
    }

    $("#shortLinkCopyButton").attr("data-clipboard-text", shortLink.link);
    shortLinkQRCode.clear();
    shortLinkQRCode.makeCode(shortLink.link);

    $('#shortLinkInfo').modal('show');
}

// 点击edit
function editShortLink(shortLinkId) {
    var shortLink;
    for (var i = 0; i < shortLinkList.length; i++) {
        if (shortLinkList[i].id == shortLinkId) {
            shortLink = shortLinkList[i];
            break;
        }
    }
    currentShortLink = jQuery.extend(true, {}, shortLink);;

    $('#shortLinkIdInput').val(shortLink.id);

    $('#shortLinkStatusSelect option').remove();
    for (var i = 0; i < statusList.length; i++) {
        if (statusList[i] == shortLink.status) {
            $("<option selected value=\"" + statusList[i] + "\">" + statusList[i] + "</option>").appendTo($('#shortLinkStatusSelect'));
        } else {
            $("<option value=\"" + statusList[i] + "\">" + statusList[i] + "</option>").appendTo($('#shortLinkStatusSelect'));
        }
    }

    $('#shortLinkCodeInput').val(shortLink.code);
    $('#shortLinkLinkTypeInput').val(shortLink.linkType);
    $('#shortLinkNameInput').val(shortLink.name);
    $('#shortLinkExpiredTimeInput').val(shortLink.expiredTime);
    $("#shortLinkExpiredTimeInput").datetimepicker({
        value: shortLink.expiredTime,
        format:'Y-m-d H:i:s'
    });

    $('#shortLinkOriginalLinkInput div').remove();

    if ("common" == shortLink.linkType) {
        if (null != shortLink.originalLinkVoList && shortLink.originalLinkVoList.length > 0) {
            for (var i = 0; i < shortLink.originalLinkVoList.length; i++) {
                var originalLinkInputDiv = $("<div class=\"jupiter-form-group\"><label></label></div>");
                var originalLinkSelect = $("<input class=\"jupiter-form-control\" value=\"" + shortLink.originalLinkVoList[i].originalLink + "\" disabled />");
                originalLinkSelect.appendTo(originalLinkInputDiv);
                $("<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\" onClick=\"removeOriginalLinkInput(this)\"></span>").appendTo(originalLinkInputDiv);
                originalLinkInputDiv.appendTo($('#shortLinkOriginalLinkInput'));
            }
            $("<div class=\"jupiter-form-group\"><label></label><span class=\"glyphicon glyphicon-plus\" aria-hidden=\"true\" onClick=\"addOriginalLinkInput(this)\"></span></div>").appendTo($('#shortLinkOriginalLinkInput'));
            $('#shortLinkUpdate').modal('show');
        }
    } else {
        if (null != shortLink.originalLinkVoList && shortLink.originalLinkVoList.length > 0) {
            for (var i = 0; i < shortLink.originalLinkVoList.length; i++) {
                  var detailInfoUri;

                  if ("deeplink" == shortLink.linkType) {
                      detailInfoUri = "/deeplink-tool/deep_link/detail?id=" + shortLink.originalLinkVoList[i].originalId;
                  } else if ("oneLink" == shortLink.linkType) {
                        detailInfoUri = "/deeplink-tool/one_link/detail?id=" + shortLink.originalLinkVoList[i].originalId;
                  }
                  $.ajaxSettings.async = false;
                  $.getJSON(detailInfoUri, function(response){
                      if (response.code != 200) {
                          return;
                      }
                      shortLink.originalLinkVoList[i]["name"] = response.data.name;

                  });
                  $.ajaxSettings.async = true;

                  var originalLinkInputDiv = $("<div class=\"jupiter-form-group\"><label></label></div>");
      //            $("<input class=\"jupiter-form-control\" readonly name=\"\" value=\"" + shortLink.deepLinkVoList[i].id + "\" >" + shortLink.deepLinkVoList[i].name + "</input>").appendTo(deepLinkInputDiv);

                  var originalLinkSelect = $("<select class=\"jupiter-form-control\" onChange=\"changeOriginalLinkSelect(this)\" disabled ></select>");

                  if ("common" == shortLink.linkType) {
                      $("<option selected value=\"\">" + shortLink.originalLinkVoList[i].originalLink + "</option>").appendTo(originalLinkSelect);
                  } else {
                      $("<option selected value=\"" + shortLink.originalLinkVoList[i].originalId + "\">(id:" + shortLink.originalLinkVoList[i].originalId + ") " + shortLink.originalLinkVoList[i].name + "</option>").appendTo(originalLinkSelect);
                  }


                  originalLinkSelect.appendTo(originalLinkInputDiv);
                  $("<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\" onClick=\"removeOriginalLinkSelect(this)\"></span>").appendTo(originalLinkInputDiv);

                  originalLinkInputDiv.appendTo($('#shortLinkOriginalLinkInput'));
            }
        }

        var queryOtherLinkUri;
        if ("deeplink" == shortLink.linkType) {
            queryOtherLinkUri = "/deeplink-tool/short_link_business/listDeepLinkWithoutRelation";
        } else if ("oneLink" == shortLink.linkType) {
            queryOtherLinkUri = "/deeplink-tool/short_link_business/listGDTOneLinkWithoutRelation";
        }

        $.ajaxSettings.async = false;
        $.getJSON(queryOtherLinkUri, function(response) {
            if (response.code != 200) {
                return;
            }
            originalLinkList = response.data;
        });
        $.ajaxSettings.async = true;

        $("<div class=\"jupiter-form-group\"><label></label><span class=\"glyphicon glyphicon-plus\" aria-hidden=\"true\" onClick=\"addOriginalLinkSelect(this)\"></span></div>").appendTo($('#shortLinkOriginalLinkInput'));
        $('#shortLinkUpdate').modal('show');
    }

}

// 点击OriginalLink添加按钮
function addOriginalLinkSelect(item) {
    if (null == originalLinkList || originalLinkList.length == 0) {
        alert("All link of this type has relate to one ShortLink.");
        return;
    }
    var originalLinkSelectList = $('#shortLinkOriginalLinkInput select');
    for (var i = 0; i < originalLinkSelectList.length; i++) {
        if (!originalLinkSelectList[i].value) {
            originalLinkSelectAlter(originalLinkSelectList[i], true);
            return;
        } else {
            originalLinkSelectAlter(originalLinkSelectList[i], false);
        }
    }

    var originalLinkInputDiv = $("<div class=\"jupiter-form-group\"><label></label></div>");
    var originalLinkSelect = $("<select class=\"jupiter-form-control\" onChange=\"changeOriginalLinkSelect(this)\" ><option value=\"\">Please choice</option></select>");
    for (var i = 0; i < originalLinkList.length; i++) {
        $("<option value=\"" + originalLinkList[i].id + "\">(id:" + originalLinkList[i].id + ") " + originalLinkList[i].name + "</option>").appendTo(originalLinkSelect);
    }

    originalLinkSelect.appendTo(originalLinkInputDiv);
    $("<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\" value=\"\" onClick=\"removeOriginalLinkSelect(this)\"></span>").appendTo(originalLinkInputDiv);
    originalLinkInputDiv.insertBefore(item.parentNode);

    printAllOriginalLink();
}
// 点击OriginalLink添加按钮 For common
function addOriginalLinkInput(item) {
    var originalLinkSelectList = $('#shortLinkOriginalLinkInput input');
    for (var i = 0; i < originalLinkSelectList.length; i++) {
        if (!originalLinkSelectList[i].value) {
            originalLinkSelectAlter(originalLinkSelectList[i], true);
            return;
        } else {
            originalLinkSelectAlter(originalLinkSelectList[i], false);
        }
    }

    var originalLinkInputDiv = $("<div class=\"jupiter-form-group\"><label></label></div>");
    var originalLinkSelect = $("<input class=\"jupiter-form-control\" onBlur=\"changeOriginalLinkInput(this)\" />");

    originalLinkSelect.appendTo(originalLinkInputDiv);
    $("<span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\" value=\"\" onClick=\"removeOriginalLinkInput(this)\"></span>").appendTo(originalLinkInputDiv);
    originalLinkInputDiv.insertBefore(item.parentNode);

    printAllOriginalLink();
}

// 更新OriginalLinkSelect控件内容
function changeOriginalLinkSelect(item) {
    var newOriginalLinkId = item.value;

    var originalLinkSelectList = $('#shortLinkOriginalLinkInput select');
    for (var i = 0; i < originalLinkSelectList.length; i++) {
        if (item == originalLinkSelectList[i]) {
            continue;
        }
        if (newOriginalLinkId == originalLinkSelectList[i].value) {
            originalLinkSelectAlter(item, true);
            return;
        }
    }
    originalLinkSelectAlter(item, false);

    var originalLinkVo = {};
    originalLinkVo["originalId"] = item.parentNode.children[1].value;
    var index = 0;
    if (null != currentShortLink.originalLinkVoList) {
        index = currentShortLink.originalLinkVoList.length;
    }
    currentShortLink.originalLinkVoList[index] = originalLinkVo;
}

// 更新OriginalLinkInput内容
function changeOriginalLinkInput(item) {
    var newOriginalLinkId = item.value;
    if (!newOriginalLinkId) {
        return;
    }

    var originalLinkSelectList = $('#shortLinkOriginalLinkInput input');
    for (var i = 0; i < originalLinkSelectList.length; i++) {
        if (item == originalLinkSelectList[i]) {
            continue;
        }
        if (newOriginalLinkId == originalLinkSelectList[i].value) {
            originalLinkSelectAlter(item, true);
            return;
        }
    }
    originalLinkSelectAlter(item, false);

    var originalLinkVo = {};
    originalLinkVo["originalLink"] = item.parentNode.children[1].value;
    var index = 0;
    if (null != currentShortLink.originalLinkVoList) {
        index = currentShortLink.originalLinkVoList.length;
    }
    currentShortLink.originalLinkVoList[index] = originalLinkVo;
}

function removeOriginalLinkSelect(item) {
    var currentOriginalId = item.parentNode.children[1].value;
    for (var i = 0; i < currentShortLink.originalLinkVoList.length; i++) {
        if(currentShortLink.originalLinkVoList[i].originalId == currentOriginalId) {
            currentShortLink.originalLinkVoList.splice(i, 1);
            break;
        }
    }
    item.parentNode.remove();
}
function removeOriginalLinkInput(item) {
    var currentOriginalLink = item.parentNode.children[1].value;
    for (var i = 0; i < currentShortLink.originalLinkVoList.length; i++) {
        if(currentShortLink.originalLinkVoList[i].originalLink == currentOriginalLink) {
            currentShortLink.originalLinkVoList.splice(i, 1);
            break;
        }
    }
    item.parentNode.remove();
}

function printAllOriginalLink() {
    var originalLinkInputList = $('#shortLinkOriginalLinkInput input');
    for (var i = 0; i < originalLinkInputList.length; i++) {
        console.log(originalLinkInputList[i].value);
    }
}

function originalLinkSelectAlter(item, shouldAlter) {
    if (!shouldAlter) {
        item.classList.remove("alarm-border");
    } else {
        item.classList.add("alarm-border");
    }
}