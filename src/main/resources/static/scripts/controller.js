app.controller("invoiceController", function($scope, $http, $req, $q, $filter, $window, $timeout, $translate) {
	$scope.ui = ts.ui;
	
	$scope.ui.ready(function() {
		
		$scope.topbar = ts.ui.get("#home-topbar");
		$scope.invoiceTable = ts.ui.get("#invoice-table");
		$scope.pop = ts.ui.Notification;
		$scope.showTab = 0;
		$scope.popup = ts.ui.Notification;
		
		$scope.queryParam = {
			stag: "inbox", 
			minIssueDate: "", 
			maxIssueDate: "", 
			createdBefore: "", 
			createdAfter: "", 
			processStates: [], 
			limit: 10, 
			page: 0,
			tzOffset: getTzOffset()
		};
		
		$scope.invoicePage = {};
		$scope.selectedRows = [];
		
		$q.all([
		    $translate(["Tab.Invoice", 
		                "Table.ID", "Table.ReceiverCompany", "Table.SenderCompany", "Table.Description", "Table.Total", "Table.Currency", "Table.IssueDate", "Table.State",
		                "Stag.Inbox", "Stag.Outbox",
		                "ProcessState.Pending", "ProcessState.Invoiced", "ProcessState.Overdue", "ProcessState.Accepted", "ProcessState.Paid", "ProcessState.Rejected", "ProcessState.Disputed",
		                "Index.Download",
		                "Table.RecordsHit"])        
		])
		.then(function(response) {
			var locale = response[0];
			
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
			.selectable(function(selected, unselected) {
				selectedRows = selected;
			})
			.buttons([
				{label: locale["Index.Download"], type:'ts-primary', onclick: () => {
					$scope.download();
				}}
			]) 
			.max(10).sort(0, true);			
			
		});
		
		$scope.submitForm = function() {
			
			$scope.queryParam.page = 0;
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
				limit: 10, 
				page: 0,
				tzOffset: getTzOffset()
			};

			$scope.invoicePage = {};
			$scope.selectedRows = [];
			$scope.invoiceTable.rows([]);
			$scope.invoiceTable.pager({pages:0});
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
		
		function searchInvoices() {
			$q.all([$req.searchInvoices($scope.queryParam)])
			.then(function(response) {
				console.log(response[0]);
				var contentType = response[0].headers('Content-Type');
				console.log('Content-Type: ' + contentType);
				if(response[0].status == 200 && contentType.indexOf('application/json') >= 0) {
					$scope.invoicePage = response[0].data;
					$scope.selectedRows = [];
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
		}
		
		function populateInvoiceTable() {
			var rows = [];
			for(var i = 0; i < $scope.invoicePage.invoices.length; i++) {
				var invoice = $scope.invoicePage.invoices[i];
				rows.push([
					invoice.id || "", 
					invoice.receiverCompanyName || "", 
					invoice.senderCompanyName || "", 
					invoice.description || "", 
					invoice.total, 
					invoice.currency || "", 
					invoice.issueDate || "", 
					invoice.state || ""
				]);
			}
			$scope.invoiceTable.rows(rows);
			$scope.invoiceTable.pager({
				pages: $scope.invoicePage.numPages,
				page: $scope.invoicePage.pageId,
				onselect: loadpage
			});
			$scope.invoiceTable.status($scope.invoicePage.itemCount + " " + locale["Table.RecordsHit"]);
		}
		
		function loadpage(index) {
			$scope.queryParam.page = index;
			searchInvoices();
		}
		
		function getTzOffset() {
			var date = new Date();
			return (date.getHours() - date.getUTCHours() + 24) % 24;
		}
	
		
	});
});