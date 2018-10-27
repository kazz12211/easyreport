app.controller("invoiceController", ($scope, $http, $req, $q, $filter, $window, $timeout, $translate) => {
	$scope.ui = ts.ui;
	$scope.showTab = 0;
	$scope.fetchLimits = {invoice:100};
	$scope.fetchLimit = $scope.fetchLimits.invoice;
	$scope.queryParam = {};
	$scope.invoices = [];
	$scope.selectedRows = [];
	var strings = [
		"Tab.Invoice", 
        "Table.ID",
        "Table.ReceiverCompany",
        "Table.SenderCompany",
        "Table.Description",
        "Table.Total",
        "Table.Currency",
        "Table.IssueDate",
        "Table.State",
        "Table.Selected",
        "Table.RecordsHit",
        "Table.Action",
        "Table.Detail",
        "Stag.Inbox",
        "Stag.Outbox",
        "ProcessState.Pending",
        "ProcessState.Invoiced",
        "ProcessState.Overdue",
        "ProcessState.Accepted",
        "ProcessState.Paid",
        "ProcessState.Rejected",
        "ProcessState.Disputed",
        "Index.Download",
        "Param.FetchLimitIs",
        "Param.Records",
        "Index.Searching",
        "Error.InvalidResponse",
        "Error.InvalidContentType",
        "Error.FailedToFetchInvoices"
       ];

	$scope.ui.ready(() => {
		
		$scope.topbar = $scope.ui.TopBar;
		$scope.popup = ts.ui.Notification;
		$scope.locale;
				
		$q.all([
		    $translate(strings),
		    $req.getParams()
		])
		.then((response) => {
			$scope.locale = response[0];
			$scope.fetchLimits = response[1].data;
			$scope.fetchLimit = $scope.fetchLimits.invoice || 100;
			
			initParams();
			initStagOptions();
			initStateOptions();
			
			$scope.ui.Header.title('Easy Report');
			$scope.topbar.tabs([{
				label: $scope.locale["Tab.Invoice"],
				id: "tab0",
				onselect: () => {
					$scope.showTab = 0;
					$scope.$apply();
					scrollTo(0, 0);
				}
			}]);
			
			
			initTable();
			
			updateDownloadButton();
		});
		
		$scope.submitForm = () => {
			$scope.queryParam.page = 0;
			searchInvoices();
		};
		
		
		$scope.clearForm = function() {
			initParams();

			$scope.selectedRows = [];
			$scope.invoiceTable.rows([]);
			$scope.invoiceTable.pager({pages:0});
			$scope.invoiceTable.status('');
			$('#stateOptions option').prop('selected', false);
			updateDownloadButton();
		};

		function initParams() {
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
		}
		
		function initStagOptions() {
			$scope.stagOptions = [
			    { value : "inbox", text : $scope.locale["Stag.Inbox"] },
			    { value : "outbox", text : $scope.locale["Stag.Outbox"] }
			];
		}
		
		function initStateOptions() {
			$scope.stateOptions = [
			    { value : "PENDING", text : $scope.locale["ProcessState.Pending"] },
			    { value : "INVOICED", text : $scope.locale["ProcessState.Invoiced"] },
			    { value : "OVERDUE", text : $scope.locale["ProcessState.Overdue"] },
			    { value : "ACCEPTED", text : $scope.locale["ProcessState.Accepted"] },
			    { value : "PAID", text : $scope.locale["ProcessState.Paid"] },
			    { value : "REJECTED", text : $scope.locale["ProcessState.Rejected"] },
			    { value : "DISPUTED", text : $scope.locale["ProcessState.Disputed"] }
			];
		}
		
		function initTable() {
			$scope.invoiceTable = ts.ui.get("#invoice-table");
			$scope.invoiceTable.cols([{
				label: $scope.locale["Table.ID"], flex: 2
			},{
				label: $scope.locale["Table.ReceiverCompany"], flex: 3
			}, {
				label: $scope.locale["Table.SenderCompany"], flex: 3
			}, {
				label: $scope.locale["Table.Description"], flex: 4
			}, {
				label: $scope.locale["Table.Total"], type: "ts-number", flex: 2
			}, {
				label: $scope.locale["Table.Currency"], flex:1
			}, {
				label: $scope.locale["Table.IssueDate"], flex:2
			}, {
				label: $scope.locale["Table.State"], flex:1
			}, { 
				label: $scope.locale["Table.Action"], flex:1
			}
			])
			.sortable(function(index, ascending) {
				$scope.invoiceTable.sort(index, ascending);
			})
			.selectable()
			.buttons([
				{label: $scope.locale["Index.Download"], type:'ts-primary', onclick: () => {
					download();
				}}
			]) 
			.max(10).sort(0, true);	
			$scope.invoiceTable.onselect = (selected, unselected) => {
				$scope.selectedRows = $scope.invoiceTable.selected();
				updateDownloadButton();
				var status = $scope.invoiceTable.rows().length + " " + $scope.locale["Table.RecordsHit"];
				if($scope.selectedRows.length > 0) {
					status = status + " " + $scope.selectedRows.length + " " + $scope.locale["Table.Selected"];
				}
				$scope.invoiceTable.status(status);
			};			
		}
		
		function loadInvoice(documentId) {
			$q.all(
				[$req.loadInvoice(documentId)]
			).then((response) => {
				return response.data;
			}, (error) => {
				$scope.pop.error(error.status);
				return null;
			});
		}
		
		
		
		function download() {
		}
		
		function updateDownloadButton() {
			var button = $scope.invoiceTable.buttons()[0];
			button.disabled = $scope.selectedRows.length === 0;
		}
		
		function retrieveInvoicePage(params, successCallback, errorCallback) {
			return $req.searchInvoices(params).then((response) => {
				var contentType = response.headers('Content-Type');
				if(response.status == 200 && contentType.indexOf('application/json') >= 0) {
					successCallback(response.data);
				} else {
					if(response.status != 200) {
						errorCallback($scope.locale['Error.InvalidResponse'] + response.status);
					} else {
						errorCallback($scope.locale['Error.InvalidContentType'] + contentType);
					}
				}
			}, (response) => {
				console.log(response);
				errorCallback($scope.locale['Error.FailedToFetchInvoices'] + JSON.stringify(response));
			});
		} 
		
		function copyParamWithPage(param, page) {
			var p = {
					stag: param.stag, 
					minIssueDate: param.minIssueDate, 
					maxIssueDate: param.maxIssueDate, 
					createdBefore: param.createdBefore, 
					createdAfter: param.createdAfter, 
					processStates: param.processStates, 
					limit: $scope.fetchLimit, 
					page: page,
					tzOffset: getTzOffset()
			};
			return p;
		}
		
		
		function blockUserInteraction(message) {
			$scope.ui.get('.ts-app', app => {
				app.blocking(message);
			});
		}
		
		function unblockUserInteraction() {
			$scope.ui.get('.ts-app', app => {
				app.done();
			});
		}
		
		function searchInvoices() {
			blockUserInteraction($scope.locale['Index.Searching']);
			
			$scope.selectedRows = [];
			$scope.numPages = 0;
			var pages = [];
			retrieveInvoicePage($scope.queryParam, (response) => {
				pages.push(response);
				var numPages = response.numPages;
				if(numPages > 1) {
					var params = [];
					for(var i = 1; i < numPages; i++) {
						var param = copyParamWithPage($scope.queryParam, i);
						params.push(param);
					}
					
					var deferred = $q.defer();
					var promise = deferred.promise;
					params.forEach((param) => {
						promise = promise.finally(() => {
							return retrieveInvoicePage(param, (response) => {
								pages.push(response);
							}, (error) => {
								$scope.pop.error(error);
							});
						});

					});
					
					promise.finally(() => {
						populateInvoiceTable(pages);
						unblockUserInteraction();
					});
					
					deferred.resolve();
				} else {
					populateInvoiceTable(pages);
					unblockUserInteraction();
				}
			}, (error) => {
				$scope.pop.error(error);
				unblockUserInteraction();
			}); 
		}
		
		function localizedStateString(state) {
			for(var i = 0; i < $scope.stateOptions.length; i++) {
				if($scope.stateOptions[i].value === state) {
					return $scope.stateOptions[i].text;
				}
			}
			return "";
		}
		
		function populateInvoices(pages) {
			$scope.invoices = [];
			for(var i = 0; i < pages.length; i++) {
				var page = pages[i];
				for(var j = 0; j < page.invoices.length; j++) {
					var invoice = page.invoices[j];
					$scope.invoices.push(invoice);
				}
			}
		}
		
		function populateInvoiceTable(pages) {
			var rows = [];

			populateInvoices(pages);

			for(var i = 0; i < $scope.invoices.length; i++) {
				const invoice = $scope.invoices[i];
				rows.push([
					invoice.id || "", 
					invoice.receiverCompanyName || "", 
					invoice.senderCompanyName || "", 
					invoice.description || "", 
					invoice.total, 
					invoice.currency || "", 
					invoice.issueDate || "", 
					localizedStateString(invoice.state),
					getbutton($scope.locale["Table.Detail"], "showDetail", invoice.documentId)
				]);
			}
			$scope.invoiceTable.rows(rows).max(10);
			var status = $scope.invoiceTable.rows().length + " " + $scope.locale["Table.RecordsHit"];
			$scope.invoiceTable.status(status);
			$scope.invoiceTable.onbutton = (name, value, rowindex, cellindex) => {
				if(name === 'showDetail') {
					$scope.invoiceDetail = loadInvoice(value);
					console.log($scope.invoiceDetail);
					$scope.ui.get('#invoiceDetailAside').open();
				}
			};

		}
	
	});
});
