app.controller("invoiceController", function($scope, $http, $req, $q, $filter, $window, $timeout, $translate) {
	$scope.ui = ts.ui;
	
	$scope.ui.ready(function() {
		
		$scope.topbar = $scope.ui.TopBar;
		$scope.invoiceTable = ts.ui.get("#invoice-table");
		$scope.pop = ts.ui.Notification;
		$scope.showTab = 0;
		$scope.popup = ts.ui.Notification;
		$scope.locale;
		$scope.fetchLimits = {invoice:500};
		$scope.fetchLimit = $scope.fetchLimits.invoice;
		
		$scope.queryParam = {
			stag: "inbox", 
			minIssueDate: "", 
			maxIssueDate: "", 
			createdBefore: "", 
			createdAfter: "", 
			processStates: [], 
			limit: $scope.fetchLimit, 
			page: 0,
			tzOffset: getTzOffset()
		};
		
		$scope.invoicePages = [];
		$scope.selectedRows = [];
		
		$q.all([
		    $translate(["Tab.Invoice", 
		                "Table.ID", "Table.ReceiverCompany", "Table.SenderCompany", "Table.Description", "Table.Total", "Table.Currency", "Table.IssueDate", "Table.State",
		                "Stag.Inbox", "Stag.Outbox",
		                "ProcessState.Pending", "ProcessState.Invoiced", "ProcessState.Overdue", "ProcessState.Accepted", "ProcessState.Paid", "ProcessState.Rejected", "ProcessState.Disputed",
		                "Index.Download",
		                "Table.RecordsHit",
		                "Param.FetchLimitIs", "Param.Records",
		                "Index.Searching"]),
		    $req.getParams()
		])
		.then(function(response) {
			var locale = response[0];
			$scope.locale = locale;
			$scope.fetchLimits = response[1].data;
			$scope.fetchLimit = $scope.fetchLimits.invoice || 500;
			
			$scope.ui.Header.title('Easy Report');
			$scope.topbar.tabs([{
				label: locale["Tab.Invoice"],
				id: "tab0",
				onselect: function() {
					$scope.showTab = 0;
					$scope.$apply();
					scrollTo(0, 0);
				}
			}]);
			
			$scope.stagOptions = [
			    { value : "inbox", text : locale["Stag.Inbox"] },
			    { value : "outbox", text : locale["Stag.Outbox"] }
			];
			$scope.stateOptions = [
			    { value : "PENDING", text : locale["ProcessState.Pending"] },
			    { value : "INVOICED", text : locale["ProcessState.Invoiced"] },
			    { value : "OVERDUE", text : locale["ProcessState.Overdue"] },
			    { value : "ACCEPTED", text : locale["ProcessState.Accepted"] },
			    { value : "PAID", text : locale["ProcessState.Paid"] },
			    { value : "REJECTED", text : locale["ProcessState.Rejected"] },
			    { value : "DISPUTED", text : locale["ProcessState.Disputed"] }
			];
			
			$scope.invoiceTable.cols([{
				label: locale["Table.ID"], flex: 2
			},{
				label: locale["Table.ReceiverCompany"], flex: 2
			}, {
				label: locale["Table.SenderCompany"], flex: 2
			}, {
				label: locale["Table.Description"], flex: 2
			}, {
				label: locale["Table.Total"], type: "ts-number", flex: 2
			}, {
				label: locale["Table.Currency"]
			}, {
				label: locale["Table.IssueDate"]
			}, {
				label: locale["Table.State"]
			}])
			.sortable(function(index, ascending) {
				$scope.invoiceTable.sort(index, ascending);
			})
			.selectable()
			.buttons([
				{label: locale["Index.Download"], type:'ts-primary', onclick: () => {
					$scope.download();
				}}
			]) 
			.max(10).sort(0, true);	
			$scope.invoiceTable.onselect = function(selected, unselected) {
				$scope.selectedRows = selected;
				updateDownloadButton();
			};
			
			updateDownloadButton();
		});
		
		$scope.submitForm = function() {
			
			$scope.queryParam.page = 0;
			$scope.selectedRows = [];
			searchInvoices();
			
		};

		$scope.clearForm = function() {
			$scope.queryParam = {
				stag: "inbox", 
				minIssueDate: "", 
				maxIssueDate: "", 
				createdBefore: "", 
				createdAfter: "", 
				processStates: [], 
				limit: $scope.fetchLimit, 
				page: 0,
				tzOffset: getTzOffset()
			};

			$scope.invoicePage = {};
			$scope.selectedRows = [];
			$scope.invoiceTable.rows([]);
			$scope.invoiceTable.pager({pages:0});
			$scope.invoiceTable.status('');
			$('#stateOptions option').prop('selected', false);
			updateDownloadButton();
		};
		
		$scope.download = function() {
			$q.all([
			    $req.downloadInvoice("1")
			])
			.then(function(response) {
				console.log(response.data);			
				if(response.status == 200) {
					
				} else {
					$scope.pop.error(response.statusText);
				}
			}, function(error) {
				console.log(error);
				$scope.pop.error(error.statusText);
			});
		};
		
		function updateDownloadButton() {
			var button = $scope.invoiceTable.buttons()[0];
			button.disabled = $scope.selectedRows.length === 0;
		}
		
		function retrieveInvoicePage(params, successCallback, errorCallback) {
			$req.searchInvoices(params).then((response) => {
				var contentType = response.headers('Content-Type');
				if(response.status == 200 && contentType.indexOf('application/json') >= 0) {
					successCallback(response.data);
				} else {
					if(response.status != 200) {
						errorCallback('Failed to get response. HTTP Status: ' + response.status);
					} else {
						errorCallback('Failed to get JSON response. Content-Type: ' + contentType);
					}
				}
			}, (response) => {
				console.log(response);
				errorCallback('Failed to get invoices');
			});
		} 
		
		function searchInvoices() {
			var main = $('main').first();
			main.attr('data-ts.busy', $scope.locale['Index.Searching']);
			$scope.invoicePages = [];
			$scope.numPages = 0;
			
			retrieveInvoicePage($scope.queryParam, (response) => {
				$scope.invoicePages.push(response);
				$scope.numPages = response.numPages;
				if($scope.numPages > 1) {
					var promise = $q.all([]);
					var exit = false;
					for(var i = 1; i < $scope.numPages && exit == false; i++) {
						$scope.queryParam.page = i;
						promise = promise.then(() => {
							return $timeout(() => {
								retrieveInvoicePage($scope.queryParam, (response) => {
									$scope.invoicePages.push(response);
								}, (error) => {
									$scope.pop.error(error);
									exit = true;
								});
							}, 10);
						});
					}
					promise.finally(() => {
						populateInvoiceTable();
						main.attr('data-ts.busy', '');
					});
				} else {
					populateInvoiceTable();
					main.attr('data-ts.busy', '');
				}
			}, (error) => {
				$scope.pop.error(error);
				main.attr('data-ts.busy', '');
			}); 
			/*
			$q.all([$req.searchInvoices($scope.queryParam)])
			.then(function(response) {
				var contentType = response[0].headers('Content-Type');
				if(response[0].status == 200 && contentType.indexOf('application/json') >= 0) {
					$scope.invoicePage = response[0].data;
					populateInvoiceTable();
					if($scope.invoicePage.invoices.length == 0) {
						$scope.pop.info("No records found match the criteria.");
					}
				} else {
					if(response.status != 200) {
						$scope.pop.error('Failed to get response. HTTP Status: ' + response.status);
					} else {
						$scope.pop.error('Failed to get JSON response. Content-Type: ' + contentType);
					}
				}
				
			}, function(error) {
				console.log(error);
				$scope.pop.error(error.statusText);
			});
			*/
		}
		
		function localizedStateString(state) {
			for(var i = 0; i < $scope.stateOptions.length; i++) {
				if($scope.stateOptions[i].value === state) {
					return $scope.stateOptions[i].text;
				}
			}
			return "";
		}
		
		function populateInvoiceTable() {
			var rows = [];
			for(var i = 0; i < $scope.invoicePages.length; i++) {
				var page = $scope.invoicePages[i];
				for(var j = 0; j < page.invoices.length; j++) {
					var invoice = page.invoices[j];
					rows.push([
						invoice.id || "", 
						invoice.receiverCompanyName || "", 
						invoice.senderCompanyName || "", 
						invoice.description || "", 
						invoice.total, 
						invoice.currency || "", 
						invoice.issueDate || "", 
						localizedStateString(invoice.state)
					]);
				}				
			}
			$scope.invoiceTable.status(rows.length + " " + $scope.locale["Table.RecordsHit"]);
			$scope.invoiceTable.rows(rows).max(10);
			/*
			$scope.invoiceTable.pager({
				pages: $scope.invoicePage.numPages,
				page: $scope.invoicePage.pageId,
				onselect: loadpage
			});
			*/
		}
		
	
		function loadpage(index) {
			$scope.invoiceTable.pager().page = index;
			//$scope.queryParam.page = index;
			//searchInvoices();
		}
		
		function getTzOffset() {
			var date = new Date();
			return (date.getHours() - date.getUTCHours() + 24) % 24;
		}
	
		
	});
});