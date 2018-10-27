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
		$scope.invoices = [];
		$scope.selectedRows = [];
		
		$q.all([
		    $translate(["Tab.Invoice", 
		                "Table.ID", "Table.ReceiverCompany", "Table.SenderCompany", "Table.Description", "Table.Total", "Table.Currency", "Table.IssueDate", "Table.State",
		                "Stag.Inbox", "Stag.Outbox",
		                "ProcessState.Pending", "ProcessState.Invoiced", "ProcessState.Overdue", "ProcessState.Accepted", "ProcessState.Paid", "ProcessState.Rejected", "ProcessState.Disputed",
		                "Index.Download",
		                "Table.RecordsHit",
		                "Param.FetchLimitIs", "Param.Records",
		                "Index.Searching",
		                "Error.InvalidResponse", "Error.InvalidContentType", "Error.FailedToFetchInvoices",
		                "Table.Selected", "Table.Action", "Table.Detail"]),
		    $req.getParams()
		])
		.then(function(response) {
			var locale = response[0];
			$scope.locale = locale;
			$scope.fetchLimits = response[1].data;
			$scope.fetchLimit = $scope.fetchLimits.invoice || 100;
			
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
				label: locale["Table.ReceiverCompany"], flex: 3
			}, {
				label: locale["Table.SenderCompany"], flex: 3
			}, {
				label: locale["Table.Description"], flex: 4
			}, {
				label: locale["Table.Total"], type: "ts-number", flex: 2
			}, {
				label: locale["Table.Currency"], flex:1
			}, {
				label: locale["Table.IssueDate"], flex:2
			}, {
				label: locale["Table.State"], flex:1
			}, { 
				label: locale["Table.Action"], flex:1
			}
			])
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
				$scope.selectedRows = $scope.invoiceTable.selected();
				updateDownloadButton();
				var status = $scope.invoiceTable.rows().length + " " + $scope.locale["Table.RecordsHit"];
				if($scope.selectedRows.length > 0) {
					status = status + " " + $scope.selectedRows.length + " " + $scope.locale["Table.Selected"];
				}
				$scope.invoiceTable.status(status);
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
		
		
		$scope.download = function() {
			var docIds = [];
			for(var i = 0; i < $scope.selectedRows.length; i++) {
				invoice = $scope.invoices[$scope.selectedRows[i]];
				docIds.push(invoice.documentId);
			}
			
			// test code start
			
			$q.all([
				$req.downloadInvoice(docIds[0])
			]).then((response) => {
				console.log(response);
			}, (error) => {
				$scope.pop.error(error.statusText);
			});
			
			// test code end
			
			/*
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
			*/
		};
		
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
			
			$scope.invoicePages = [];
			$scope.numPages = 0;
			
			retrieveInvoicePage($scope.queryParam, (response) => {
				$scope.invoicePages.push(response);
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
								$scope.invoicePages.push(response);
							}, (error) => {
								$scope.pop.error(error);
							});
						});

					});
					
					promise.finally(() => {
						populateInvoiceTable();
						unblockUserInteraction();
					});
					
					deferred.resolve();
				} else {
					populateInvoiceTable();
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
		
		function populateInvoiceTable() {
			var rows = [];
			$scope.invoices = [];
			for(var i = 0; i < $scope.invoicePages.length; i++) {
				var page = $scope.invoicePages[i];
				for(var j = 0; j < page.invoices.length; j++) {
					var invoice = page.invoices[j];
					$scope.invoices.push(invoice);
					rows.push([
						invoice.id || "", 
						invoice.receiverCompanyName || "", 
						invoice.senderCompanyName || "", 
						invoice.description || "", 
						invoice.total, 
						invoice.currency || "", 
						invoice.issueDate || "", 
						localizedStateString(invoice.state),
						getButton(locale["Table.Detail"], "Detail", invoice.documentId)
					]);
				}				
			}
			$scope.invoiceTable.rows(rows).max(10);
			var status = $scope.invoiceTable.rows().length + " " + $scope.locale["Table.RecordsHit"];
			$scope.invoiceTable.status(status);
			$scope.invoiceTable.onbutton = function(name, value, rowindex, cellindex) {
				if(name === 'Detail') {
					$scope.invoiceDetail = loadInvoice(value);
					console.log($scope.invoiceDetail);
					$scope.ui.get('#invoiceDetailAside').open();
				}
			};

		}
		
	
		function getTzOffset() {
			var date = new Date();
			return (date.getHours() - date.getUTCHours() + 24) % 24;
		}
	
		function getbutton(label, name, value) {
			return {
				item: 'Button',
				type: 'ts-secondary ts-micro',
				label: label,
				name: name,
				value: value
			};
		}
	);
	});
});
