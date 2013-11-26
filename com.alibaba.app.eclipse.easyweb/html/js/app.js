var function_apps_data = {};

function initAppData(data) {
	for (var i = data.length - 1; i >= 0; i--) {
		var item = data[i];
		function_apps_data[item.name] = item;
	};
}

function initApps(data) {
	var dom = $('#app_nav');
	$('#app_list').tmpl(data).appendTo(dom);
}

function initJetty(appName, jettyStarted) {
	var result = function_apps_data[appName];

	$('#jvm').val(result.jvm);

	var started = result.jettyStarted;
	if (jettyStarted != undefined) {
		started = jettyStarted;
	}

	if (started) {
		$('#app_start').hide();
		$('#app_stop').show();
		// $('#jetty_status').html('已启动');
	} else {
		$('#app_start').show();
		$('#app_stop').hide();
		// $('#jetty_status').html('未启动');
	}
}

function initAppPrj(appName) {
	var result = function_apps_data[appName];
	$('#app_port').val(result.appPort);
	var dom = $('#app_prj tbody');
	// dom.empty();

	// $('#app_project_list').tmpl(result).appendTo(dom);
}

function sync() {
	changeStatusLine("function:sync");
}

!(function($) {
	$(document).on('click', '.app-name', function() {
				$('#page_main').show();
				$('#page_left .nav-list li').removeClass('active');
				var _this = $(this);
				_this.parent().addClass('active');
				var appName = _this.data('name');

				$('#page_main').load("app-main.html?" + now, function() {
							$('a').tooltip();
							initJetty(appName);
							initAppPrj(appName);
						});
				return false;
			});

	$(document).on('click', '#app_start', function() {
				var appName = $('#app_nav li.active').text();
				changeStatusLine("function:srartApp|" + appName);
				initJetty(appName, true);
				return false;
			});

	$(document).on('click', '#app_stop', function() {
				var appName = $('#app_nav li.active').text();
				changeStatusLine("function:stopApp|" + appName);
				initJetty(appName, false);
				return false;
			});

	$(document).on('click', '#app_autoconf', function() {
				var appName = $('#app_nav li.active').text();
				changeStatusLine("function:autoconf|" + appName);
			});

	$(document).on('click', '#app_buildclasspath', function() {
				var appName = $('#app_nav li.active').text();
				changeStatusLine("function:buildclasspath|" + appName);
			});

	$(document).on('click', '#jvm_btn', function() {
				var appName = $('#app_nav li.active').text();
				changeStatusLine("function:jvm|" + appName + "|"
						+ $('#jvm').val());

				$('#jvm_modal').modal('toggle');

				return false;
			});

	$(document).on('keyup', '#app_port', function() {
		var appName = $('#app_nav li.active').text();
		changeStatusLine("function:jettyport|" + appName + "|"
				+ $('#app_port').val());
		return false;
	});

	$(document).on('click', '#module_select', function(e) {
				e.preventDefault();
				var appName = $('#app_nav li.active').text();

				var callback = Java("selectProject", appName);

				if (callback) {
					var dataObj=eval("("+callback+")");
					var names = "";
					for(var i=0;i<dataObj.length;i++){
						names += dataObj[i].name +" ";
					}
					alert("成功的添加了模块"+names+"到应用中 !");
				}

				return false;
			});

	$(document).on('click', '.module-project-connect', function(e) {
				e.preventDefault();
				var appName = $('#app_nav li.active').text();
				var callback = Java("selectProject", appName,$(this).data('name'));
				if (callback) {
					var dataObj=eval("("+callback+")")
					alert("关联项目成功 !");
				}else{
					
				}

				return false;
			});

})(jQuery)